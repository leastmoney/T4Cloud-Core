package com.t4cloud.t.base.utils;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Base加解密 处理
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2021/1/27 17:20
 */
public class BaseUtil {

    // ----------------------------------------------- Base 64 -----------------------------------------------

    /**
     * 字符集
     */
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    /**
     * 初始化字符集对应关系
     */
    private static HashMap<Character, Integer> CHARS_MAP;


    // ----------------------------------------------- Base 32 -----------------------------------------------

    static {
        CHARS_MAP = new HashMap<Character, Integer>();
        for (int i = 0; i < CHARS.length; i++) {
            CHARS_MAP.put(CHARS[i], i);
        }
    }

    /**
     * 提供Base 64 加密的方法
     *
     * @param text 字符串
     *             <p>
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2021/1/25 16:09
     */
    public static String encodeBase64(String text) {

        //校验参数
        if (StringUtils.isBlank(text)) {
            return null;
        }

        try {
            return new String(Base64.encodeBase64(text.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //加密异常
            return null;
        }
    }

    /**
     * 提供Base 64 加密的方法
     *
     * @param text Base64 字符串
     *             <p>
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2021/1/25 16:09
     */
    public static String decodeBase64(String text) {

        //校验参数
        if (StringUtils.isBlank(text)) {
            return null;
        }

        try {
            return new String(Base64.decodeBase64(text.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //解密异常
            return null;
        }
    }

    /**
     * @param base32
     * @return
     * @Author:lulei
     * @Description: 将五位二进制转化为base32
     */
    public static char getBase32Char(boolean[] base32) {
        if (base32 == null || base32.length != 5) {
            return ' ';
        }
        int num = 0;
        for (boolean bool : base32) {
            num <<= 1;
            if (bool) {
                num += 1;
            }
        }
        return CHARS[num % CHARS.length];
    }

    /**
     * 将geo hash还原成二进制字符串
     */
    public static String hash2BinaryStr(String hash) {

        if (StrUtil.isBlank(hash)) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hash.length(); i++) {
            char c = hash.charAt(i);
            if (CHARS_MAP.containsKey(c)) {
                String cStr = int2Base32(CHARS_MAP.get(c));
                if (cStr != null) {
                    sb.append(cStr);
                }
            }
        }

        return sb.toString();
    }

    // ----------------------------------------------- private -----------------------------------------------


    /**
     * 获取数字对应的二进制字符串
     */
    private static String int2Base32(int i) {
        if (i < 0 || i > 31) {
            return null;
        }
        String str = Integer.toBinaryString(i + 32);
        return str.substring(1);
    }

}