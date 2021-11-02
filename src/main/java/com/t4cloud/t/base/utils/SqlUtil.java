package com.t4cloud.t.base.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.t4cloud.t.base.exception.T4CloudException;
import lombok.extern.slf4j.Slf4j;

/**
 * sql注入处理工具类
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/2/8 18:13
 */
@Slf4j
public class SqlUtil {

    final static String xss = "'|and |exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|,";

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     *
     * @param value 需要检验的内容
     *              <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/2/8 18:15
     */
    public static void filterContent(String value) {
        if (value == null || "".equals(value)) {
            return;
        }
        // 统一转为小写
        value = value.toLowerCase();
        String[] xssArr = xss.split("\\|");
        for (int i = 0; i < xssArr.length; i++) {
            if (value.indexOf(xssArr[i]) > -1) {
                log.error("请注意，值可能存在SQL注入风险!---> {}", value);
                throw new T4CloudException("请注意，值可能存在SQL注入风险!--->" + value);
            }
        }
        return;
    }


    /**
     * 通过用户角色code过滤数据
     *
     * @param wrapper wapper对象
     * @param value   用户的角色code
     * @param userCol 表中用户ID对应的字段
     *
     *                <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/9/23 17:28
     */
    public static void addUserRoleFilter(QueryWrapper wrapper, String value, String userCol) {
        wrapper.inSql("user_id",
                "SELECT\n" +
                        "\tsys_user.id\n" +
                        "FROM\n" +
                        "\t`sys_user`\n" +
                        "LEFT JOIN sys_user_role ON sys_user.id = sys_user_role.user_id\n" +
                        "LEFT JOIN sys_role ON sys_user_role.role_id = sys_role.id\n" +
                        "WHERE\n" +
                        "\tsys_role.role_code = '" + value + "'");
    }

    /**
     * 通过用户角色code过滤数据（默认用户ID列名为user_id）
     *
     * @param wrapper wapper对象
     * @param value   用户的角色code
     *
     *                <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/9/23 17:28
     */
    public static void addUserRoleFilter(QueryWrapper wrapper, String value) {
        addUserRoleFilter(wrapper, value, "user_id");
    }


}
