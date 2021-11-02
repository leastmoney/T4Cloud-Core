package com.t4cloud.t.base.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.t4cloud.t.base.exception.T4CloudDecryptException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * 加解密工具类
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/3/9 21:40
 */
@Slf4j
@Component
public class RSAUtil {

    public static String privateKey;
    public static String publicKey;
    public static RSA rsa;
    @Autowired
    private Environment env;

    /**
     * 加密
     *
     * @param str 原始字符串
     *            <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/3/9 21:47
     */
    public static String encrypt(String str) {
        if (StrUtil.isBlank(str) || rsa == null) {
            return str;
        }
        return rsa.encryptBase64(str, KeyType.PublicKey);
    }

    /**
     * 解密
     *
     * @param str 加密后的字符串
     *            <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/3/9 21:47
     */
    public static String decrypt(String str) {
        try {
            if (StrUtil.isBlank(str) || rsa == null) {
                return str;
            }
            return rsa.decryptStr(str, KeyType.PrivateKey);
        } catch (Exception e) {
            throw new T4CloudDecryptException("RSA解析异常！请检查秘钥是否相同，或参数是否加密。密文： " + str + ",异常详情：" + e.getMessage());
        }
    }

    @PostConstruct
    public void readConfig() {
        privateKey = env.getProperty("t4cloud.private-key", String.class);
        publicKey = env.getProperty("t4cloud.public-key", String.class);
        if (StrUtil.isBlank(privateKey)) {
            log.info("T4Cloud无私钥配置，RSA自动解密不启用");
            rsa = null;
        } else {
            rsa = new RSA(privateKey, publicKey);
        }
    }

}
