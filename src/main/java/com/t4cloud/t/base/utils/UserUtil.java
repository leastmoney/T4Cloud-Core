package com.t4cloud.t.base.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.t4cloud.t.base.authc.config.JwtToken;
import com.t4cloud.t.base.constant.CacheConstant;
import com.t4cloud.t.base.entity.LoginUser;
import com.t4cloud.t.base.exception.T4CloudValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.t4cloud.t.base.constant.CacheConstant.SYS_USERS_COMPANY_CACHE;
import static com.t4cloud.t.base.constant.RequestConstant.T_ACCESS_TOKEN;
import static com.t4cloud.t.base.constant.RequestConstant.T_TENANT;

/**
 * UserUtil 用户工具类
 * <p>
 * 获取当前操作的用户信息
 * <p>
 * ---------------------
 *
 * @author Terry
 * @date 2020/2/10 17:07
 */
@Slf4j
@Component
public class UserUtil {

    /**
     * 获取当前登录的用户信息
     *
     * <p>
     *
     * @return com.t4cloud.t.base.entity.LoginUser
     * --------------------
     * @author TeaR
     * @date 2020/2/10 17:09
     */
    public static LoginUser getCurrentUser() {
        try {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            return loginUser;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前登录的用户信息
     *
     * <p>
     *
     * @return java.util.List<java.lang.String>
     * --------------------
     * @author TeaR
     * @date 2020/4/14 11:51
     */
    public static List<String> getDataRuleList() {
        List<String> dataRuleList = new ArrayList<>();
        try {
            LoginUser user = getCurrentUser();
            log.debug("user:" + JSONUtil.toJsonStr(user));
            if (user != null) {
                //优先从已登录对象中
                List<String> userDataRule = (List<String>) RedisUtil.get(SYS_USERS_COMPANY_CACHE + user.getId());
                if (CollectionUtil.isEmpty(userDataRule)) {
                    dataRuleList.add(user.getTenantId());
                } else {
                    dataRuleList = userDataRule;
                }
            } else {
                //未登录则尝试从Request中获取
                HttpServletRequest request = null;
                try {
                    request = SpringContextUtil.getHttpServletRequest();
                } catch (Exception e) {
                    throw new T4CloudValidException("无法获取正确的租户ID");
                }
                //尝试解析token
                if (StrUtil.isNotBlank(request.getHeader(T_ACCESS_TOKEN))) {
                    String userId = JwtUtil.getUserId(request.getHeader(T_ACCESS_TOKEN));
                    List<String> userDataRule = (List<String>) RedisUtil.get(SYS_USERS_COMPANY_CACHE + userId);
                    if (CollectionUtil.isNotEmpty(userDataRule)) {
                        dataRuleList = userDataRule;
                    }
                }
                if (CollectionUtil.isEmpty(dataRuleList)) {
                    //最后直接使用租户参数
                    String tanentId = request.getHeader(T_TENANT);
                    if (StrUtil.isBlank(tanentId)) {
                        throw new T4CloudValidException("无法获取正确的租户ID");
                    }
                    dataRuleList.add(tanentId);
                }

            }

            log.debug("user-dataRuleList:" + JSONUtil.toJsonStr(dataRuleList));

            return dataRuleList;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 不通过Shiro拦截器，手动登录当前token
     *
     * <p>
     *
     * @return com.t4cloud.t.base.entity.LoginUser
     * --------------------
     * @author TeaR
     * @date 2020/9/15 10:34
     */
    public static LoginUser login(String token) {
        try {
            //手动登录
            SecurityUtils.getSubject().login(new JwtToken(token));
            //重新获取用户信息
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            return loginUser;
        } catch (Exception e) {
            //登录失败会爆出异常
            return null;
        }
    }

    /**
     * 退出当前登录状态
     * WEB请求中才可以使用
     *
     * @param all 是否清退该用户所有的登录
     *            <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/6/16 11:31 上午
     */
    public static void logout(String token, boolean all) {
        //先登录一下
        SecurityUtils.getSubject().login(new JwtToken(token));
        //获取用户ID
        String userId = getCurrentUser().getId();
        //退出登录
        SecurityUtils.getSubject().logout();
        //删除用户当前对应的登录Token缓存
        RedisUtil.del(CacheConstant.SYS_USERS_TOKEN + userId + "-" + token);
        //清退所有记录
        if (all) {
            logout(userId);
        }
    }

    /**
     * 清退指定ID的用户登录
     *
     * @param userId 指定的用户ID
     *               <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/6/16 2:58 下午
     */
    public static void logout(String userId) {

        //所有token
        Set<String> keys = RedisUtil.keys(CacheConstant.SYS_USERS_TOKEN + userId + "-*");

        String[] keyArray = {};
        keyArray = keys.toArray(keyArray);
        RedisUtil.del(keyArray);

        //删除用户信息
        RedisUtil.del(CacheConstant.SYS_USERS_CACHE + userId);

        //删除数据权限信息
        RedisUtil.del(CacheConstant.SYS_USER_DataRules + "::" + userId);
        RedisUtil.del(CacheConstant.SYS_USERS_COMPANY_CACHE + userId);

        //删除shiro信息
        RedisUtil.del(CacheConstant.SHIRO_USER_AUTHORIZATION_INFO + CacheConstant.T4CLOUD_SHIRO_USER + "::" + userId);

        //权限和角色
        RedisUtil.del(CacheConstant.SYS_USER_PERMISSIONS + "::" + userId);
        RedisUtil.del(CacheConstant.SYS_USER_ROLES + "::" + userId);

    }

}
