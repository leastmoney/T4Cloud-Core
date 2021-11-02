package com.t4cloud.t.base.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.nio.charset.Charset;

/**
 * @description:  调用 echarts 提供的地图压缩方法压缩地图
 *
 *                <p>
 * --------------------
 * @author: Qian
 * @date: 2021/8/6 18:05
 */
@Component
public class EchartGeoZipUtil {
    public static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    static {
        try {
            //注意这里的 encode.js 路径
            engine.eval(ResourceUtil.readStr("encode.js", Charset.defaultCharset()));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public static final Invocable invocable = (Invocable) engine;

    /**
     * 压缩为 echarts 格式的地图
     *
     * @param geoJson
     * @param fileName
     * @param type     json 或 其他（按js处理）
     * @return
     */
    public static String convert2Echarts(String geoJson, String fileName, String type) {
        try {
            return (String) invocable.invokeFunction("convert2Echarts", geoJson, fileName, type);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
