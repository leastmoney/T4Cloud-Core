package com.t4cloud.t.base.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.t4cloud.t.base.query.T4SqlParser;
import com.t4cloud.t.base.utils.SpringContextUtil;
import com.t4cloud.t.base.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * MybatisPlusConfig
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 22:12
 */
@Slf4j
@Configuration
@MapperScan(basePackages = "com.t4cloud.t.**.mapper")
public class MybatisPlusConfig {


    /**
     * 不做租户的表
     */
    private static List<String> tables;

    /** 初始化需要过滤的表 */
    static {
        tables = new ArrayList<>();

        //通用硬编码
        tables.add("sup_region");
        tables.add("sys_user_role");
        tables.add("sys_role_permission");
        tables.add("sys_permission");
        tables.add("sys_dict");
        tables.add("sys_dict_value");
        tables.add("sys_user_company");
    }

    @Value("${t4cloud.tenant.open:false}")
    private Boolean tenant;
    @Value("${t4cloud.tenant.key:false}")
    private String tenantKey;
    @Value("#{'${t4cloud.tenant.filter-table: }'.split(',')}")
    private List<String> filterTable;
    @Value("#{'${t4cloud.tenant.filter-url: }'.split(',')}")
    private List<String> filterUrl;

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置sql的limit为无限制，默认是500
        paginationInterceptor.setLimit(-1);

        /*
         * 【测试多租户】 SQL 解析处理拦截器<br>
         * 这里固定写成住户 1 实际情况需要DataRule处理器
         */

        List<ISqlParser> sqlParserList = new ArrayList<>();
        T4SqlParser tenantSqlParser = new T4SqlParser();

        tenantSqlParser.setTenantHandler(new TenantHandler() {

            @Override
            public Expression getTenantId(boolean where) {
                //获取用户数据权限列表
                List<String> dataRuleList = UserUtil.getDataRuleList();

                final boolean multipleTenantIds = CollectionUtil.isNotEmpty(dataRuleList) && dataRuleList.size() > 1;//这里切换单个tenantId和多个tenantId

                log.debug("dataRuleList:" + JSONUtil.toJsonStr(dataRuleList));

                if (where && multipleTenantIds) {
                    //多个权限门采用IN方式
                    final InExpression inExpression = new InExpression();
                    inExpression.setLeftExpression(new Column(getTenantIdColumn()));
                    final ExpressionList itemsList = new ExpressionList();
                    final List<Expression> inValues = new ArrayList<>(dataRuleList.size());
                    dataRuleList.forEach(item -> {
                        inValues.add(new StringValue(item));
                    });
                    itemsList.setExpressions(inValues);
                    inExpression.setRightItemsList(itemsList);
                    return inExpression;
                } else if (CollectionUtil.isEmpty(dataRuleList)) {
                    //没有权限的人
                    return new StringValue("-7");
                } else {
                    //单个权限，采用=方式
                    return new StringValue(dataRuleList.get(0));
                }
            }

            @Override
            public String getTenantIdColumn() {
                //指定租户字段
                return "tenant_id";
            }

            /**
             * 是否执行租户
             * false 代表需要执行租户
             * true 代表不需要执行租户
             * <p>
             * @return boolean
             * --------------------
             * @author TeaR
             * @date 2020/3/25 16:29
             */
            @Override
            public boolean doTableFilter(String tableName) {

                //系统不需要开启租户
                if (!tenant) {
                    return true;
                } else if (!"T4Cloud".equalsIgnoreCase(tenantKey)) {
                    //假设 做一下多租户功能权限验证，就是判断key是不是登录T4Cloud
                    return true;
                }

                if (tables.contains(tableName) || filterTable.contains(tableName)) {
                    return true;
                }

                //获取req对象，若不存在，则不开启租户
                HttpServletRequest request = SpringContextUtil.getHttpServletRequest();
                if (request == null) {
                    return true;
                }

                //排除配置文件中配置的过滤接口
                if (request != null && CollectionUtil.isNotEmpty(filterUrl) &&
                        filterUrl.stream().filter(item -> item.equalsIgnoreCase(request.getRequestURI()) || (item.endsWith("**") && request.getRequestURI().startsWith(item.replace("**", ""))))
                                .count() > 0) {
                    return true;
                }

                //其他情况不过滤
                return false;
            }
        });

        sqlParserList.add(tenantSqlParser);
        paginationInterceptor.setSqlParserList(sqlParserList);
        return paginationInterceptor;
    }


    /**
     * 乐观锁插件
     *
     * @return com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor
     * --------------------
     * @author TeaR
     * @date 2020/9/17 10:54
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

}
