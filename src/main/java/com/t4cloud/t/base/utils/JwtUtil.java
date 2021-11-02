package com.t4cloud.t.base.utils;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * JWT工具类
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/16 10:54
 */
public class JwtUtil {

    // Token过期时间30分钟（用户登录过期时间是此时间的两倍，以token在reids缓存时间为准）
    public static final long EXPIRE_TIME = 30 * 60 * 1000;

    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @param secret 用户的密码
     * @return 是否正确
     */
    public static boolean verify(String token, String secret) {
        try {
            // 根据密码生成JWT效验器
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            // 效验TOKEN
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        try {
            if (StrUtil.isBlank(token)) {
                return null;
            }
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户ID
     */
    public static String getUserId(String token) {
        try {
            if (StrUtil.isBlank(token)) {
                return null;
            }
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asString();
//        } catch (JWTDecodeException e) {
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的有效期
     */
    public static Long getExpireTime(String token) {
        try {
            if (StrUtil.isBlank(token)) {
                return null;
            }
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("expireTime").asLong();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成签名
     *
     * @param username 用户名
     * @param secret   用户的密码
     * @return 加密的token
     */
    public static String sign(String userId, String username, String secret) {
        return sign(userId, username, secret, EXPIRE_TIME);
    }

    /**
     * 生成签名
     *
     * @param username   用户名
     * @param secret     用户的密码
     * @param expireTime token过期时间
     * @return 加密的token
     */
    public static String sign(String userId, String username, String secret, long expireTime) {
        Date date = new Date(System.currentTimeMillis() + expireTime);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 附带username信息
        return JWT.create().withClaim("username", username).withClaim("userId", userId).withClaim("expireTime", expireTime).withExpiresAt(date).sign(algorithm);
    }

//    public static void main(String[] args) {
//        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MDE0NzI0NDQsInVzZXJJZCI6IjAwMWZkNmIzNmYxMWJhMjhkNDdmM2VjZTZiMzJlMmY5IiwidXNlcm5hbWUiOiLlvKDlm73msJEtMDcyMzM4YTI3NDE0MzAyYjEzYzI1ZjVmMDcyMyJ9.qAmrxFjNzml-06_dJnCpYpyq-VXrHM5QPAF7IYReuPA";
//        System.out.println(JwtUtil.getUsername(token));
//        boolean flag = verify(token, "e53155b1e0e2e7d6cf8afac07a7fd8ce1f90d958430b1e0a54849171b62fb58855e27e6cf4cb906b");
//        System.out.println(flag);
//    }
}
