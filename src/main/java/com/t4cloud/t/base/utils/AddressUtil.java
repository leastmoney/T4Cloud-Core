package com.t4cloud.t.base.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * AddressUtil
 * <p>
 * 获取地址中的省、市、区
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2021/3/24 20:56
 */
public class AddressUtil {

    /**
     * 从资源文件中加载省市区全集
     */
    public static JSONArray addressList = JSONUtil.parseArray(ResourceUtil.readStr("city.json", Charset.defaultCharset()));


    /**
     * 从地址中获取省信息（可能匹配中多个）
     *
     * @param addr 地址
     *             <p>
     * @return java.util.List<java.lang.String>
     * --------------------
     * @author TeaR
     * @date 2021/3/24 21:11
     */
    public static List<String> getProvince(String addr) {

        //准备结果集
        List<String> result = new LinkedList<>();

        //验证参数
        if (StrUtil.isBlank(addr)) {
            return result;
        }

        //准备数据集
        List<JSONObject> provinceList = addressList.toList(JSONObject.class);

        //开始匹配
        for (JSONObject province : provinceList) {

            String name = province.getStr("name").replaceAll(" ", "");
            if (addr.contains(name)) {
                result.add(name);
                continue;
            }

            //尝试去除尾缀匹配
            if (name.length() > 2 && addr.contains(name.substring(0, name.length() - 1))) {
                result.add(name);
                continue;
            }

            //开始匹配市，看是否缩写了省，但能匹配上市 TODO -by TeaR  -2021/3/24-21:26

        }

        //过滤结果
        filter(result);

        return result;

    }

    /**
     * 去除意料之外的匹配结果
     *
     * @param result 地址结果集
     *               <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/3/24 21:20
     */
    public static void filter(List<String> result) {

        result.remove("其他");
        result.remove("郊区");
        result.remove("城区");

    }

    public static void main(String[] args) {
        List<String> province = AddressUtil.getProvince("上海");
        System.out.println(JSONUtil.toJsonStr(province));
    }
}
