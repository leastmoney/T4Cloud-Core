package com.t4cloud.t.base.query;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.t4cloud.t.base.annotation.Dict;
import com.t4cloud.t.base.utils.SqlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.t4cloud.t.base.constant.DictConstant.DICT_TEXT_SUFFIX;

/**
 * query增强生成器，可根据实体类自动的生成查询条件
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/2/8 17:25
 */
@Slf4j
public class T4Query {

    /**
     * 高级查询后缀
     */
    private static final String GE = "_ge";
    private static final String LE = "_le";
    private static final String GT = "_gt";
    private static final String LT = "_lt";
    private static final String IN = "_in";
    private static final String NE = "_ne";

    /**
     * 排序列
     */
    private static final String ORDER_COLUMN = "column";
    /**
     * 排序方式
     */
    private static final String ORDER_TYPE = "order";
    private static final String ORDER_TYPE_ASC = "ASC";

    /**
     * 时间格式化
     */
    private static final ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>();

    private static SimpleDateFormat getTime() {
        SimpleDateFormat time = local.get();
        if (time == null) {
            time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            local.set(time);
        }
        return time;
    }

    /**
     * 根据查询实体和req中的参数，构造通用的查询条件
     *
     * @param searchObj    查询实体
     * @param parameterMap request.getParameterMap()
     *                     <p>
     * @return com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<T>
     * --------------------
     * @author TeaR
     * @date 2020/2/8 17:32
     */
    public static <T> QueryWrapper<T> initQuery(T searchObj, Map<String, String[]> parameterMap) {
        long start = System.currentTimeMillis();
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        initQuery(queryWrapper, searchObj, parameterMap);
        log.debug("---查询条件构造器初始化完成,耗时:" + (System.currentTimeMillis() - start) + "毫秒----");
        return queryWrapper;
    }

    /**
     * 根据查询实体和req中的参数，构造通用的查询条件
     *
     * <p>使用此方法 需要有如下几点注意:
     * <br>1.使用QueryWrapper 而非LambdaQueryWrapper;
     * <br>2.实例化QueryWrapper时不可将实体传入参数
     * <br>错误示例:如QueryWrapper<JeecgDemo> queryWrapper = new QueryWrapper<JeecgDemo>(jeecgDemo);
     * <br>正确示例:QueryWrapper<JeecgDemo> queryWrapper = new QueryWrapper<JeecgDemo>();
     * <br>3.也可以不使用这个方法直接调用 {@link #initQuery}直接获取实例
     * --------------------
     *
     * @author TeaR
     * @date 2020/2/8 17:36
     */
    public static <T> void initQuery(QueryWrapper<?> queryWrapper, T searchObj, Map<String, String[]> parameterMap) {

        //获取所有的属性
        PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(searchObj);

        String name, type;
        for (int i = 0; i < origDescriptors.length; i++) {
            //aliasName = origDescriptors[i].getName();  mybatis  不存在实体属性 不用处理别名的情况
            name = origDescriptors[i].getName();
            type = origDescriptors[i].getPropertyType().toString();

            //判断是否需要入库查询
            Field field = FieldUtils.getField(searchObj.getClass(), name, true);
            if (field != null && field.getAnnotation(TableField.class) != null && !field.getAnnotation(TableField.class).exist()) {
                //忽略
                continue;
            }

            try {

                if (isIgnoreParam(name) || !PropertyUtils.isReadable(searchObj, name)) {
                    //是特殊属性就忽略
                    continue;
                }

                //是否已经匹配规则
                boolean hasRule = false;

                //高级字典查询
                String dictValue;
                if (parameterMap != null && parameterMap.containsKey(name + DICT_TEXT_SUFFIX)) {
                    dictValue = parameterMap.get(name + DICT_TEXT_SUFFIX)[0].trim();

                    String inSql = "";
                    Dict[] dicts = field.getAnnotationsByType(Dict.class);

                    for (Dict dict : dicts) {

                        String code = dict.code();
                        String table = dict.table();
                        String prop = dict.prop();

                        if (StrUtil.isBlank(table) || StrUtil.isBlank(prop)) {
                            //内部字典处理
                            if (!hasRule) {
                                //刚开始处理，处理SQL SELECT部分
                                inSql = "select t.value from sys_dict_value t where " +
                                        " where t.dict_id = (select id from sys_dict where code = '" + code + "') ";
                                //处理WHERE条件
                                inSql = inSql + " and (t.text LIKE '%" + dictValue + "%'";
                            } else {
                                //二次添加条件
                                inSql = inSql + " or t.text LIKE '%" + dictValue + "%'";
                            }

                        } else {
                            //自定义字典

                            if (!hasRule) {
                                //刚开始处理，处理SQL SELECT部分
                                inSql = "select " + code + " from " + table + " where ";
                                //处理WHERE条件
                                inSql = inSql + " ( " + prop + " LIKE '%" + dictValue + "%'";
                            } else {
                                //二次添加条件
                                inSql = inSql + " or " + prop + " LIKE '%" + dictValue + "%'";
                            }

                        }

                        hasRule = true;
                    }

                    //补全末尾括号
                    inSql = inSql + " ) ";

                    queryWrapper.inSql(StrUtil.toUnderlineCase(name), inSql);
                }

                /** TODO 模糊匹配暂时不做，现在定义的字符串全局模糊匹配，其他全部EQ，目前唯一遗漏的就是字符串类型的指定EQ。
                 *
                 * -by TeaR  -2020/3/31-14:17
                 */

                //匹配IN查询
                String inValue;
                if (parameterMap != null && parameterMap.containsKey(name + IN)) {
                    inValue = parameterMap.get(name + IN)[0].trim();
                    addQueryByRule(queryWrapper, name, type, inValue, QueryRuleEnum.IN);
                    hasRule = true;
                }

                //匹配 != （NE）
                String neValue;
                if (parameterMap != null && parameterMap.containsKey(name + NE)) {
                    neValue = parameterMap.get(name + NE)[0].trim();
                    addQueryByRule(queryWrapper, name, type, neValue, QueryRuleEnum.NE);
                    hasRule = true;
                }

                // 判断 >= <=(GE/LE)
                String leValue = null, geValue = null;
                if (parameterMap != null && parameterMap.containsKey(name + GE)) {
                    geValue = parameterMap.get(name + GE)[0].trim();
                    addQueryByRule(queryWrapper, name, type, geValue, QueryRuleEnum.GE);
                    hasRule = true;
                }
                if (parameterMap != null && parameterMap.containsKey(name + LE)) {
                    leValue = parameterMap.get(name + LE)[0].trim();
                    addQueryByRule(queryWrapper, name, type, leValue, QueryRuleEnum.LE);
                    hasRule = true;
                }

                //匹配 > < (GT/LT)
                String ltValue = null, gtValue = null;
                if (parameterMap != null && parameterMap.containsKey(name + GT)) {
                    gtValue = parameterMap.get(name + GT)[0].trim();
                    addQueryByRule(queryWrapper, name, type, gtValue, QueryRuleEnum.GT);
                    hasRule = true;
                }
                if (parameterMap != null && parameterMap.containsKey(name + LT)) {
                    ltValue = parameterMap.get(name + LT)[0].trim();
                    addQueryByRule(queryWrapper, name, type, ltValue, QueryRuleEnum.LT);
                    hasRule = true;
                }

                //处理正常查询
                //获取对应的值
                Object value = PropertyUtils.getSimpleProperty(searchObj, name);
                if (value == null) {
                    //如果值为空
                    continue;
                }

                //字符串额外匹配IN规则
                if (!hasRule && "class java.lang.String".equals(type) && value.toString().contains(StrUtil.COMMA)) {
                    addEasyQuery(queryWrapper, name, QueryRuleEnum.IN, value);
                    hasRule = true;
                }

                //字符串兜底规则
                if (!hasRule && "class java.lang.String".equals(type) && !name.contains("id") && !name.contains("Id")) {
                    addEasyQuery(queryWrapper, name, QueryRuleEnum.LIKE, value);
                    hasRule = true;
                }

                //其他兜底规则
                if (!hasRule) {
                    addEasyQuery(queryWrapper, name, QueryRuleEnum.EQ, value);
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        // 排序逻辑 处理
        doMultiFieldsOrder(queryWrapper, parameterMap);

    }

    /**
     * 处理排序方式
     *
     * <p>
     *
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/2/8 18:09
     */
    public static void doMultiFieldsOrder(QueryWrapper<?> queryWrapper, Map<String, String[]> parameterMap) {
        String column = null, order = null;
        if (parameterMap != null && parameterMap.containsKey(ORDER_COLUMN)) {
            column = parameterMap.get(ORDER_COLUMN)[0];
        }
        if (parameterMap != null && parameterMap.containsKey(ORDER_TYPE)) {
            order = parameterMap.get(ORDER_TYPE)[0];
        }
        log.debug("排序规则>>列:" + column + ",排序方式:" + order);

        if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(order)) {
            //判断XSS风险
            SqlUtil.filterContent(column);
            if (order.equalsIgnoreCase(ORDER_TYPE_ASC)) {
                queryWrapper.orderByAsc(StrUtil.toUnderlineCase(column));
            } else {
                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(column));
            }
        }
    }

    /**
     * 解析值（主要是IN方法）
     *
     * @param value 值
     * @param rule  规则
     * @param func  解析方法
     *              <p>
     * @return java.lang.Object 解析完成的值
     * --------------------
     * @author TeaR
     * @date 2020/4/1 18:10
     */
    private static Object parseValue(String value, QueryRuleEnum rule, ParseFunction func) {
        Object result;
        if (rule.equals(QueryRuleEnum.IN)) {
            String[] split = value.split(",");
            ArrayList tempList = new ArrayList<>();
            for (String s : split) {
                tempList.add(func.parse(s));
            }
            result = tempList;
        } else {
            result = func.parse(value);
        }
        return result;
    }

    /**
     * 根据判断类型添加querywrapper中的条件
     *
     * @param queryWrapper queryWrapper对象
     * @param value        参数值
     * @param rule         判断条件
     *                     <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/2/8 18:21
     */
    private static void addQueryByRule(QueryWrapper<?> queryWrapper, String name, String type, String value, QueryRuleEnum rule) throws ParseException {
        if (!"".equals(value)) {
            Object temp;
            switch (type) {
                case "class java.lang.Integer":
                    temp = parseValue(value, rule, Integer::parseInt);
                    break;
                case "class java.math.BigDecimal":
                    temp = parseValue(value, rule, BigDecimal::new);
                    break;
                case "class java.lang.Short":
                    temp = parseValue(value, rule, Short::parseShort);
                    break;
                case "class java.lang.Long":
                    temp = parseValue(value, rule, Long::parseLong);
                    break;
                case "class java.lang.Float":
                    temp = parseValue(value, rule, Float::parseFloat);
                    break;
                case "class java.lang.Double":
                    temp = parseValue(value, rule, Double::parseDouble);
                    break;
                case "class java.util.Date":
                    temp = getDateQueryByRule(value, rule);
                    break;
                default:
                    temp = value;
                    break;
            }
            addEasyQuery(queryWrapper, name, rule, temp);
        }
    }

    /**
     * 获取日期类型的值
     *
     * @param value
     * @param rule
     * @return
     * @throws ParseException
     */
    private static Date getDateQueryByRule(String value, QueryRuleEnum rule) throws ParseException {
        Date date = null;
        //Date
        if (value.length() == 10) {
            date = DateUtil.parseDate(value);
        }
        //Time
        if (value.length() == 8) {
            date = DateUtil.parseTime(value);
        }
        //DateTIme
        if (date == null) {
            date = DateUtil.parse(value);
        }
        return date;
    }

    /**
     * 根据规则走不同的查询
     *
     * @param queryWrapper QueryWrapper
     * @param name         字段名字
     * @param rule         查询规则
     * @param value        查询条件值
     */
    private static void addEasyQuery(QueryWrapper<?> queryWrapper, String name, QueryRuleEnum rule, Object value) {
        if (value == null || rule == null) {
            return;
        }
        name = StrUtil.toUnderlineCase(name);
        log.info("--查询规则-->" + name + " " + rule.getValue() + " " + value);
        switch (rule) {
            case GT:
                queryWrapper.gt(name, value);
                break;
            case GE:
                queryWrapper.ge(name, value);
                break;
            case LT:
                queryWrapper.lt(name, value);
                break;
            case LE:
                queryWrapper.le(name, value);
                break;
            case EQ:
                queryWrapper.eq(name, value);
                break;
            case NE:
                queryWrapper.ne(name, value);
                break;
            case IN:
                if (value instanceof String) {
                    queryWrapper.in(name, (Object[]) value.toString().split(StrUtil.COMMA));
                } else if (value instanceof String[]) {
                    queryWrapper.in(name, (Object[]) value);
                } else if (value instanceof List) {
                    queryWrapper.in(name, ((List) value).toArray());
                } else {
                    queryWrapper.in(name, value);
                }
                break;
            case LIKE:
                queryWrapper.like(name, value);
                break;
            case LEFT_LIKE:
                queryWrapper.likeLeft(name, value);
                break;
            case RIGHT_LIKE:
                queryWrapper.likeRight(name, value);
                break;
            default:
                log.info("--查询规则未匹配到---");
                break;
        }
    }

    /**
     * 忽略基类中的无用参数
     *
     * @param name 参数名
     *             <p>
     * @return boolean
     * --------------------
     * @author TeaR
     * @date 2020/2/8 17:46
     */
    private static boolean isIgnoreParam(String name) {
        return "class".equals(name) || "ids".equals(name)
                || "page".equals(name) || "rows".equals(name)
                || "sort".equals(name) || "order".equals(name);
    }


    /**
     * 定义接受的方法
     *
     * <p>
     *
     * @author TeaR
     * @return --------------------
     * @date 2020/4/1 18:20
     */
    @FunctionalInterface
    interface ParseFunction {
        Object parse(String value);
    }


}
