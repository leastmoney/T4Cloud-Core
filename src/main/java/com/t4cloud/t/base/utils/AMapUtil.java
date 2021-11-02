package com.t4cloud.t.base.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * AMapUtil
 * <p>
 * 高德地图 WEP API对接
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2021/3/24 21:39
 */
@Component
public class AMapUtil {

    @Autowired
    private Environment env;

    private static String KEY;

    /**
     * 通过地址获取对应的GPS坐标
     *
     * @param address 语义化地址
     *                <p>
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2021/3/24 21:47
     */
    public static String geo(String address) {
        String url = "https://restapi.amap.com/v3/geocode/geo?address=" + address + "&key=" + KEY;
        String response = HttpUtil.get(url);

        JSONObject res = JSONUtil.parseObj(response);

        //解析失败
        if (res.getInt("status") != 1) {
            return null;
        }

        JSONArray geocodes = res.getJSONArray("geocodes");
        JSONObject geo = geocodes.get(0, JSONObject.class);

        return geo.getStr("location");
    }

    @PostConstruct
    public void readConfig() {
        KEY = env.getProperty("t4cloud.amap.key", String.class);
    }

//    public static void main(String[] args) {
//        String geo = AMapUtil.geo("上海");
//        System.out.println(geo);
//    }

}
