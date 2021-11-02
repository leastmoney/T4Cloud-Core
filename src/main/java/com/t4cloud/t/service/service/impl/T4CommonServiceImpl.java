package com.t4cloud.t.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.t4cloud.t.base.annotation.cache.DataRuleCacheable;
import com.t4cloud.t.base.annotation.cache.DictCacheable;
import com.t4cloud.t.base.constant.CacheConstant;
import com.t4cloud.t.base.entity.SystemLogRequest;
import com.t4cloud.t.base.entity.T4Log;
import com.t4cloud.t.base.exception.T4CloudValidException;
import com.t4cloud.t.base.service.impl.T4ServiceImpl;
import com.t4cloud.t.service.entity.*;
import com.t4cloud.t.service.mapper.*;
import com.t4cloud.t.service.service.IT4CommonService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户表 服务实现类
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-12
 */
@Slf4j
@Service
@AllArgsConstructor
public class T4CommonServiceImpl extends T4ServiceImpl<CommonUserMapper, SysUser> implements IT4CommonService {


    @Autowired
    private CommonDictMapper dictMapper;

    @Autowired
    private CommonLogMapper logMapper;

    @Autowired
    private CommonPermissionMapper permissionMapper;

    @Autowired
    private CommonCompanyMapper companyMapper;

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
    @Override
    @DictCacheable
    public List<SysDictValue> queryDict(String code) {
        log.debug(String.format("从数据库查询字典组！CODE: %s。", code));
        return dictMapper.queryDict(code);
    }

    /**
     * 查询多个字典组
     *
     * @param codes 多个字典code，用逗号拼接
     *              <p>
     * @return java.util.Map<java.lang.String, java.util.List < com.t4cloud.t.service.entity.SysDictValue>>
     * --------------------
     * @author TeaR
     * @date 2021/7/30 21:47
     */
    @Override
    public Map<String, List<SysDictValue>> queryDictMap(String codes) {

        log.debug(String.format("从数据库查询多个字典集合！CODES: %s。", codes));

        //准备结果集
        Map<String, List<SysDictValue>> result = new HashMap<>();

        //获取字典集
        String[] split = codes.split(",");
        for (String code : split) {
            result.put(code, queryDict(code));
        }

        //返回结果
        return result;
    }

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
    @Override
    @Cacheable(value = CacheConstant.SYS_DICT_TEMP, key = "#table + ':' + #code + ':' + #key + ':' + #prop", unless = "#result == null")
    public String queryDictText(String code, String key, String table, String prop) {
        try {
            if (StrUtil.isBlank(key) || StrUtil.isBlank(code)) {
                return StrUtil.EMPTY;
            }
            if (StrUtil.isBlank(table) || StrUtil.isBlank(prop)) {
                //从字典表查询
                String text = dictMapper.queryDictText(code, key);
                log.debug(String.format("从数据库查询字典！CODE: %s, KEY: %s, TEXT: %s。", code, key, text));
                return text;
            } else {
                //自定义查询
                String text = dictMapper.superQueryDictText(code, key, table, prop);
                log.debug(String.format("从数据库查询自定义属性！CODE: %s, KEY: %s, TABLE: %s, PROP: %s, TEXT: %s。", code, key, table, prop, text));
                return text;
            }
        } catch (Exception e) {
            log.error(String.format("字典查询失败！CODE: %s, KEY: %s, TABLE: %s, PROP: %s", code, key, table, prop), e);
            return StrUtil.EMPTY;
        }
    }

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
    @Override
    @Cacheable(value = CacheConstant.SYS_DICT, key = "#code + ':value:' + #key")
//    @Cacheable(value = CacheConstant.SYS_DICT, key = "#p0 + ':value:' + #p1", unless = "#result == null")
    public String queryDictText(String code, String key) {
        try {
            if (StrUtil.isBlank(key) || StrUtil.isBlank(code)) {
                return StrUtil.EMPTY;
            }
            //从字典表查询
            String text = dictMapper.queryDictText(code, key);
            log.debug(String.format("从数据库查询字典！CODE: %s, KEY: %s, TEXT: %s。", code, key, text));
            return text;
        } catch (Exception e) {
            log.error(String.format("字典查询失败！CODE: %s, KEY: %s", code, key), e);
            return StrUtil.EMPTY;
        }
    }

    /**
     * 查询字典的翻译内容
     *
     * @param code 字典code
     * @param text key对应的值
     *             <p>
     * @return java.lang.String
     * --------------------
     * @author Qiming
     * @date 2020/2/22
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DICT, key = "#code + ':key:' + #text")
//    @Cacheable(value = CacheConstant.SYS_DICT, key = "#p0 + ':key:' + #p1", unless = "#result == null")
    public String queryDictKey(String code, String text) {
        if (StrUtil.isBlank(text) || StrUtil.isBlank(code)) {
            return StrUtil.EMPTY;
        }
        String key = dictMapper.queryDictKey(code, text);
        log.debug(String.format("从数据库查询字典！CODE: %s, value: %s, KEY: %s", code, text, key));
        return key;
    }

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
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean saveLog(T4Log log) {
        //转换日志对象
        SysLog sysLog = new SysLog();
        BeanUtils.copyProperties(log, sysLog);

        return logMapper.insert(sysLog) == 1;
    }

    /**
     * 统计至今天的请求总量，正常量，失败量
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
    @Override
    public SystemLogRequest countSystemLog(String startDate, String endDate, String tenantIds) {
        return logMapper.countSystemLog(startDate, endDate, tenantIds);
    }

    /**
     * 统计至今天的请求总量，正常量，失败量
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
    @Override
    public SystemLogRequest countSystemLogWithType(String startDate, String endDate, String tenantIds, String resultType) {
        return logMapper.countSystemLogWithType(startDate, endDate, tenantIds, resultType);
    }

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
    @Override
    public List<String> getUserRoles(String userId) {
        // 查询用户拥有的角色集合
        List<String> roles = baseMapper.getRoleByUserId(userId);
        log.info("-------通过数据库读取用户拥有的角色Rules------userId： " + userId + ",Roles size: " + (roles == null ? 0 : roles.size()));
        return roles;
    }

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
    @Override
    public List<String> getUserPermissions(String userId) {
        List<String> permissions = new ArrayList<>();
        List<SysPermission> permissionList = permissionMapper.queryByUser(userId);
        for (SysPermission po : permissionList) {
            if (StringUtils.isNotEmpty(po.getPerms())) {
                permissions.add(po.getPerms());
            }
        }
        log.info("-------通过数据库读取用户拥有的权限Perms------userId： " + userId + ",Perms size: " + permissions.size());
        return permissions;
    }

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
    @Override
    @DataRuleCacheable
    public List<SysCompany> getUserDataRule(String userId) {
        List<SysCompany> dataRules = companyMapper.queryByUserId(userId);
        log.info("-------通过数据库读取用户拥有的数据权限权限 dataRules------userId： " + userId + ",dataRules size: " + dataRules.size());
        return dataRules;
    }

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
    @Override
    public void valid(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new T4CloudValidException(bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(StrUtil.COMMA)));
        }
    }

}

















































