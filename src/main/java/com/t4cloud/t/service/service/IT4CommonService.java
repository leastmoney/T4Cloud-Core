package com.t4cloud.t.service.service;


import com.t4cloud.t.base.entity.SystemLogRequest;
import com.t4cloud.t.base.entity.T4Log;
import com.t4cloud.t.base.service.T4Service;
import com.t4cloud.t.service.entity.SysCompany;
import com.t4cloud.t.service.entity.SysDictValue;
import com.t4cloud.t.service.entity.SysUser;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

/**
 * 公用 服务类
 * <p>
 * 所有common包下的数据库交互都放在这里，不按实体类区分
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-12
 */
public interface IT4CommonService extends T4Service<SysUser> {


    // ----------------------------------------------- 字典 -----------------------------------------------

    /**
     * 查询单个字典组
     *
     * @param code 字典code
     *             <p>
     * @return java.util.List<com.t4cloud.t.service.entity.SysDictValue>
     * --------------------
     * @author TeaR
     * @date 2021/7/30 21:33
     */
    List<SysDictValue> queryDict(String code);

    /**
     * 查询多个字典组
     *
     * @param codes 多个字典code，用逗号拼接
     * <p>
     * @return java.util.Map<java.lang.String,java.util.List<com.t4cloud.t.service.entity.SysDictValue>>
     * --------------------
     * @author TeaR
     * @date 2021/7/30 21:47
     */
    Map<String, List<SysDictValue>> queryDictMap(String codes);

    /**
     * 查询字典的翻译内容
     *
     * @param code  字典code
     * @param key   值对应的KEY
     * @param table 指定表名
     * @param prop  指定属性字段
     *              <p>
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2020/2/9 13:09
     */
    String queryDictText(String code, String key, String table, String prop);

    /**
     * 查询字典的翻译内容(从字典表查询）
     *
     * @param code 字典code
     * @param key  值对应的KEY
     *             <p>
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2020/2/9 13:09
     */
    String queryDictText(String code, String key);


    /**
     * 查询字典值对应的key
     *
     * @param code  字典code
     * @param value 值对应的KEY
     *              <p>
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2020/2/9 13:09
     */
    String queryDictKey(String code, String value);

    // ----------------------------------------------- 日志 -----------------------------------------------

    /**
     * 保存系统日志
     *
     * @param log 日志对象
     *            <p>
     * @return boolean
     * --------------------
     * @author TeaR
     * @date 2020/2/12 12:04
     */
    boolean saveLog(T4Log log);

    /**
     * 统计今天的请求总量
     *
     * @param startDate 今日开始天数
     * @param endDate   今日结束天数
     * @param tenantIds 租户ids
     *
     *                  <p>
     * @return SystemLogRequest
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/17 14:48
     */
    SystemLogRequest countSystemLog(String startDate, String endDate, String tenantIds);

    /**
     * 统计今天的请求正常量，失败量
     *
     * @param startDate  今日开始天数
     * @param endDate    今日结束天数
     * @param tenantIds  租户ids
     * @param resultType 正常异常标识 0-异常，1-正常
     *
     *                   <p>
     * @return SystemLogRequest
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/17 14:48
     */
    SystemLogRequest countSystemLogWithType(String startDate, String endDate, String tenantIds, String resultType);

    // ----------------------------------------------- 用户 -----------------------------------------------

    /**
     * 用户角色列表
     *
     * @param userId 用户ID
     *               <p>
     * @return java.util.List<java.lang.String>
     * --------------------
     * @author TeaR
     * @date 2020/2/12 11:47
     */
    List<String> getUserRoles(String userId);

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     *               <p>
     * @return java.util.List<java.lang.String>
     * --------------------
     * @author TeaR
     * @date 2020/2/12 14:54
     */
    List<String> getUserPermissions(String userId);

    /**
     * 获取用户数据权限
     *
     * @param userId 用户ID
     *               <p>
     * @return java.util.List<java.lang.String>
     * --------------------
     * @author TeaR
     * @date 2020/2/12 14:54
     */
    List<SysCompany> getUserDataRule(String userId);

    /**
     * 校验参数的正确性
     *
     * @param bindingResult 检验结果
     *                      <p>
     * @return viod
     * --------------------
     * @author TeaR
     * @date 2020/2/21 21:39
     */
    void valid(BindingResult bindingResult);

}
