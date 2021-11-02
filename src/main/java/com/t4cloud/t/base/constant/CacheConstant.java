package com.t4cloud.t.base.constant;

/**
 * 缓存前缀字典
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/16 11:00
 */
public interface CacheConstant {

    /**
     * 默认缓存路径
     */
    String SYS_TEMP_DIR = "/tmp/t4cloud";
    /**
     * 默认缓存文件过期时间
     */
    Integer SYS_TEMP_CLEAN = 30;
    /**
     * 字典信息缓存
     */
    String SYS_DICT = "dict";
    /**
     * 自定义字典信息临时缓存
     */
    String SYS_DICT_TEMP = "dict_temp";

    /**
     * 缓存用户信息
     */
    String SYS_USERS_CACHE = "user:";

    /**
     * 缓存用户公司信息
     */
    String SYS_USERS_COMPANY_CACHE = "user_company:";

    /**
     * 缓存用户TOKEN信息
     */
    String SYS_USERS_TOKEN = "token:";

    /**
     * 登录用图片验证码
     */
    String SYS_USERS_CHECK_CODE = "check_code:";

    /**
     * 绑定用动态验证码
     */
    String SYS_USERS_BIND_CODE = "bind_code:";

    /**
     * 字典信息缓存
     */
    String SYS_REDIS_MONITOR = "redis_monitor:";

    /**
     * Jvm信息缓存
     */
    String SYS_JVM_MONITOR = "jvm_monitor:";

    /**
     * sysLog请求统计信息缓存
     */
    String SYS_LOG_MONITOR = "sys_log_monitor:";

    /**
     * shiro 用户权限缓存前缀
     */
    String SHIRO_USER_AUTHORIZATION_INFO = "shiro:cache:com.t4cloud.t.base.authc.ShiroRealm.authorizationCache:";
    String T4CLOUD_SHIRO_USER = "userId";

    /**
     * 用户权限缓存前缀
     */
    String SYS_USER_PERMISSIONS = "permissions";
    /**
     * 用户角色缓存前缀
     */
    String SYS_USER_ROLES = "roles";

    /**
     * 用户数据权限缓存前缀
     */
    String SYS_USER_DataRules = "dataRules";

    /**
     * 用户临时授权码
     */
    String AK_USER_CODE = "AK_USER_CODE::";


}
