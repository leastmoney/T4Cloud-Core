package com.t4cloud.t.base.query;

import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 修正多租户时WHERE的异常
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/4/17 15:05
 */
public class T4SqlParser extends TenantSqlParser {

    /**
     * 目前仅支持：in, between, >, <, =, !=等比较操作，处理多租户的字段加上表别名
     *
     * @param expression
     * @param table
     * @return
     */
    protected Expression processTableAlias(Expression expression, Table table) {
        String tableAliasName;
        if (table.getAlias() == null) {
            tableAliasName = table.getName();
        } else {
            tableAliasName = table.getAlias().getName();
        }
        if (expression instanceof InExpression) {
            InExpression in = (InExpression) expression;
            if (in.getLeftExpression() instanceof Column) {
                setTableAliasNameForColumn((Column) in.getLeftExpression(), tableAliasName);
            }
        } else if (expression instanceof BinaryExpression) {
            BinaryExpression compare = (BinaryExpression) expression;
            if (compare.getLeftExpression() instanceof Column) {
                setTableAliasNameForColumn((Column) compare.getLeftExpression(), tableAliasName);
            } else if (compare.getRightExpression() instanceof Column) {
                setTableAliasNameForColumn((Column) compare.getRightExpression(), tableAliasName);
            }
        } else if (expression instanceof Between) {
            Between between = (Between) expression;
            if (between.getLeftExpression() instanceof Column) {
                setTableAliasNameForColumn((Column) between.getLeftExpression(), tableAliasName);
            }
        }
        return expression;
    }

    private void setTableAliasNameForColumn(Column column, String tableAliasName) {
        column.setColumnName(tableAliasName + "." + column.getColumnName());
    }

    /**
     * 重写该方法，实现save的时候，如果存在手动配置的tenant id，则不进行框架自动补全
     *
     * @param insert
     * <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/8/20 11:30 上午
     */
    @Override
    public void processInsert(Insert insert) {
        //不存在手动指定，走原有的租户处理逻辑。如果手动指定了，则忽略租户处理
        if(!insert.getColumns().stream().map(Column::getColumnName).collect(Collectors.toList()).contains(super.getTenantHandler().getTenantIdColumn())){
            super.processInsert(insert);
        }
    }

    /**
     * 重写该方法，修正IN查询时多个条件的异常
     *
     * <p>
     *
     * @return net.sf.jsqlparser.expression.BinaryExpression
     * --------------------
     * @author TeaR
     * @date 2020/4/21 11:41
     */
    @Override
    protected BinaryExpression andExpression(Table table, Expression where) {
        EqualsTo equalsTo = new EqualsTo();
        equalsTo.setLeftExpression(this.getAliasColumn(table));
        Expression tenantId = this.getTenantHandler().getTenantId(true);
        if (tenantId instanceof StringValue) {
            equalsTo.setRightExpression(tenantId);
            if (null != where) {
                return where instanceof OrExpression ? new AndExpression(equalsTo, new Parenthesis(where)) : new AndExpression(equalsTo, where);
            } else {
                return equalsTo;
            }
        } else {
            if (null != where) {
                return where instanceof OrExpression ? new AndExpression(tenantId, new Parenthesis(where)) : new AndExpression(tenantId, where);
            } else {
                equalsTo.setRightExpression(tenantId);
                return equalsTo;
            }
        }

    }

    /**
     * 重写该方法，修正select方法在多租户解析的时候获取到的where为false的异常
     *
     * <p>
     *
     * @return net.sf.jsqlparser.expression.Expression
     * --------------------
     * @author TeaR
     * @date 2020/4/29 13:20
     */
    @Override
    protected Expression builderExpression(Expression currentExpression, Table table) {
        final Expression tenantExpression = this.getTenantHandler().getTenantId(true);
        Expression appendExpression = this.processTableAlias4CustomizedTenantIdExpression(tenantExpression, table);

//        final Expression tenantExpression = this.getTenantHandler().getTenantId(Objects.nonNull(currentExpression));
//        Expression appendExpression;
        if (!(tenantExpression instanceof SupportsOldOracleJoinSyntax)) {
            appendExpression = new EqualsTo();
            ((EqualsTo) appendExpression).setLeftExpression(this.getAliasColumn(table));
            ((EqualsTo) appendExpression).setRightExpression(tenantExpression);
        } else {
            appendExpression = processTableAlias(tenantExpression, table);
        }
        if (currentExpression == null) {
            return appendExpression;
        }
        if (currentExpression instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) currentExpression;
            if (binaryExpression.getLeftExpression() instanceof FromItem) {
                processFromItem((FromItem) binaryExpression.getLeftExpression());
            }
            if (binaryExpression.getRightExpression() instanceof FromItem) {
                processFromItem((FromItem) binaryExpression.getRightExpression());
            }
        } else if (currentExpression instanceof InExpression) {
            InExpression inExp = (InExpression) currentExpression;
            ItemsList rightItems = inExp.getRightItemsList();
            if (rightItems instanceof SubSelect) {
                processSelectBody(((SubSelect) rightItems).getSelectBody());
            }
        }
        if (currentExpression instanceof OrExpression) {
            return new AndExpression(new Parenthesis(currentExpression), appendExpression);
        } else {
            return new AndExpression(currentExpression, appendExpression);
        }
    }

    @Override
    protected void processPlainSelect(PlainSelect plainSelect, boolean addColumn) {
        FromItem fromItem = plainSelect.getFromItem();
        if (fromItem instanceof Table) {
            Table fromTable = (Table) fromItem;
            if (!this.getTenantHandler().doTableFilter(fromTable.getName())) {
                plainSelect.setWhere(builderExpression(plainSelect.getWhere(), fromTable));
                if (addColumn) {
                    plainSelect.getSelectItems().add(new SelectExpressionItem(new Column(this.getTenantHandler().getTenantIdColumn())));
                }
            }
        } else {
            processFromItem(fromItem);
        }
        List<Join> joins = plainSelect.getJoins();
        if (joins != null && joins.size() > 0) {
            joins.forEach(j -> {
                processJoin(j);
                processFromItem(j.getRightItem());
            });
        }
    }
}