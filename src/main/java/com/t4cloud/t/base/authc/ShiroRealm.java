package com.t4cloud.t.base.authc;

import cn.hutool.core.util.StrUtil;
import com.t4cloud.t.base.authc.config.JwtToken;
import com.t4cloud.t.base.constant.CacheConstant;
import com.t4cloud.t.base.constant.RequestConstant;
import com.t4cloud.t.base.constant.ResultConstant;
import com.t4cloud.t.base.entity.LoginUser;
import com.t4cloud.t.base.exception.T4CloudException;
import com.t4cloud.t.base.utils.IPUtil;
import com.t4cloud.t.base.utils.JwtUtil;
import com.t4cloud.t.base.utils.RedisUtil;
import com.t4cloud.t.base.utils.SpringContextUtil;
import com.t4cloud.t.service.service.IT4CommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

import static com.t4cloud.t.base.constant.CacheConstant.T4CLOUD_SHIRO_USER;


/**
 * 实现用户登录逻辑
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 21:32
 */
@Component
@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private IT4CommonService service;

    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 权限信息认证(包括角色以及权限)是用户访问controller的时候才进行验证(redis存储的此处权限信息)
     * 触发检测用户权限时才会调用此方法，例如checkRole,checkPermission
     *
     * @param principals 身份信息
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.debug("===============Shiro权限认证开始============ [ roles、permissions]==========");
        String userId = null;
        if (principals != null) {
            LoginUser sysUser = (LoginUser) principals.getPrimaryPrincipal();
            userId = sysUser.getId();
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        // 设置用户拥有的角色集合，比如“admin,test”
//        Set<String> userRoles = new HashSet<>();
//        userRoles.add("admin");
//        userRoles.add("test");
        List<String> userRoles = service.getUserRoles(userId);
        info.setRoles(new HashSet<>(userRoles));

        // 设置用户拥有的权限集合，比如“sys:role:add,sys:user:add”
//        Set<String> userPermissions = new HashSet<>();
//        userPermissions.add("sys:role:add");
//        userPermissions.add("sys:user:add");
        List<String> userPermissions = service.getUserPermissions(userId);
        info.addStringPermissions(new HashSet<>(userPermissions));
        log.debug("===============Shiro权限认证成功==============");
        return info;
    }

    /**
     * 用户信息认证是在用户进行登录的时候进行验证(不存redis)
     * 也就是说验证用户输入的账号和密码是否正确，错误抛出异常
     *
     * @param auth 用户登录的账号密码信息
     * @return 返回封装了用户信息的 AuthenticationInfo 实例
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        String token = (String) auth.getCredentials();
        if (StringUtils.isEmpty(token)) {
            String url = SpringContextUtil.getHttpServletRequest().getRequestURL().toString();
            log.error("————————身份认证失败——————————IP地址:  " + IPUtil.getIpAddr(SpringContextUtil.getHttpServletRequest()) + ",URL:" + url);
            /** TODO 此处可以进行日志入库，对鉴权失败的请求需要特别关注
             *
             * -by TeaR  -2020/2/12-11:38
             */
            throw new AuthenticationException("该接口需要登录才能调用，token为空!URL:" + url);
        }
        // 校验token有效性
        LoginUser loginUser = this.checkUserTokenIsEffect(token);
        return new SimpleAuthenticationInfo(loginUser, token, getName());
    }

    /**
     * 自定义rediskey，主要是为了兼容Spring的Cache注解
     *
     * @param principals <p>
     * @return java.lang.Object
     * --------------------
     * @author TeaR
     * @date 2020/2/21 17:28
     */
    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        return T4CLOUD_SHIRO_USER + "::" + ((LoginUser) principals.getPrimaryPrincipal()).getId();
    }


    /**
     * 校验token的有效性
     *
     * @param token
     */
    public LoginUser checkUserTokenIsEffect(String token) throws AuthenticationException {
        // 解密获得username，用于和数据库进行对比
        String userId = JwtUtil.getUserId(token);
        if (userId == null) {
            throw new AuthenticationException("token非法！");
        }

        // 校验token有效性
        log.debug(" ———— 校验token是否有效 ———— " + token);
        String cacheToken = null;
        try {
            cacheToken = (String) RedisUtil.get(CacheConstant.SYS_USERS_TOKEN + userId + "-" + token);
        } catch (Exception e) {
            throw new T4CloudException("Redis连接异常！请检查Redis");
        }

        if (StrUtil.isBlank(cacheToken)) {
            throw new AuthenticationException("无法从Redis中获取有效token，请检查Redis连接状态或token已失效。URL:" + SpringContextUtil.getHttpServletRequest().getRequestURL().toString());
        }

        if (token == null || cacheToken == null || !token.equalsIgnoreCase(cacheToken)) {
            throw new AuthenticationException("token已失效，请重新登录！URL:" + SpringContextUtil.getHttpServletRequest().getRequestURL().toString());
        }

        //获取用户信息
        LoginUser loginUser = (LoginUser) RedisUtil.get(CacheConstant.SYS_USERS_CACHE + userId);

        if (loginUser == null) {
            throw new AuthenticationException("用户登录已失效!");
        }

        /** 此处刷新token有一个问题，所以做以下处理，对接口进行判断
         *  TOKEN刚过期且处于允许刷新获取新的token，且正好调用的接口正好是退出接口，那么也会刷新，
         *  导致退出成功了，老的TOKEN失效，但是响应头中依然会带有新的TOKEN
         *
         * -by TeaR  -2020/2/10-17:16
         */

        String url = SpringContextUtil.getHttpServletRequest().getRequestURL().toString();
        if (!url.endsWith("login/logout")) {
            // 刷新token
            jwtTokenRefresh(cacheToken, loginUser);
        }

        return loginUser;
    }

    /**
     * JWTToken刷新生命周期 （实现： 用户在线操作不掉线功能）
     *
     * @param token     redis中存的token
     * @param loginUser 对应的用户信息
     * @return
     */
    public boolean jwtTokenRefresh(String token, LoginUser loginUser) {
        //token为空不处理，不刷新
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        // 校验token有效性
        if (JwtUtil.verify(token, loginUser.getPassword())) {
            //验证通过，说明没过期，不用刷新
            return false;
        }

        Long expireTime = JwtUtil.getExpireTime(token);

        if (expireTime == null) {
            expireTime = JwtUtil.EXPIRE_TIME;
        }

        //说明过期了，需要刷新
        String newToken = JwtUtil.sign(loginUser.getId(), loginUser.getUsername(), loginUser.getPassword(), expireTime);

        // 设置新的token缓存有效时间
        RedisUtil.set(CacheConstant.SYS_USERS_TOKEN + loginUser.getId() + "-" + newToken, newToken, expireTime * 2 / 1000);
        RedisUtil.set(CacheConstant.SYS_USERS_CACHE + loginUser.getId(), loginUser, expireTime * 2 / 1000);
        log.debug(" ———————————————— 用户在线操作，更新token保证不掉线 ———————————————— " + token);

        //输出新的TOKEN到外部
        SpringContextUtil.getHttpServletResponse().setHeader(RequestConstant.T_ACCESS_TOKEN, newToken);
        SpringContextUtil.getHttpServletResponse().setHeader(ResultConstant.ACCESS_CONTROL_EXPOSE_HEADERS, RequestConstant.T_ACCESS_TOKEN);
        return true;
    }

    /**
     * 清除当前用户的权限认证缓存
     *
     * @param principals 权限信息
     */
    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

}
