package com.t4cloud.t.base.utils;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.t4cloud.t.base.annotation.Dict;
import com.t4cloud.t.base.constant.DictConstant;
import com.t4cloud.t.service.service.IT4CommonService;
import io.swagger.annotations.ApiModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 字典处理工具类 工具类
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/7/10 15:05
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DictUtil {

    private final IT4CommonService service;


    /**
     * 针对List进行翻译
     *
     * @param entityList 需要翻译的Lis对象
     * @param dictTemp   字典缓存
     *                   <p>
     * @return java.util.List<java.lang.Object>
     * --------------------
     * @author TeaR
     * @date 2020/7/10 16:28
     */
    public List<Object> parseListDict(List<Object> entityList, Map<String, String> dictTemp) {
        List<Object> items = new ArrayList<>();
        for (Object record : entityList) {
            //处理属性翻译
            items.add(parseEntityDict(record, dictTemp));
        }
        return items;
    }

    /**
     * 针对单个实体类进行翻译
     *
     * @param entity   需要翻译的实体类对象
     * @param dictTemp 字典缓存
     *                 <p>
     * @return java.lang.Object
     * --------------------
     * @author TeaR
     * @date 2020/7/10 15:08
     */
    public Object parseEntityDict(Object entity, Map<String, String> dictTemp) {
        try {
            //判断是否需要翻译 && 是否可以翻译
            if (entity == null || entity.getClass().getAnnotation(ApiModel.class) == null) {
                return entity;
            }

            //开始翻译，转换成JSON
            String json = "{}";
            try {
                //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
                json = JSONUtil.toJsonStr(entity);
            } catch (Exception e) {
                log.error("json解析失败" + e.getMessage(), e);
                return entity;
            }
            JSONObject item = JSONObject.parseObject(json);

            //遍历实体类属性
            for (Field field : ReflectUtil.getFields(entity.getClass())) {
                //还原原本的属性
                Object prop = ReflectUtil.getFieldValue(entity, field);

                //递归查找子数据
                if (prop != null && prop.getClass().getAnnotation(ApiModel.class) != null) {
                    item.put(field.getName(), parseEntityDict(prop, dictTemp));
                }

                //处理List属性
                if (prop instanceof List) {
                    item.put(field.getName(), parseListDict((List) prop, dictTemp));
                }

                //获取字典注解
                Dict[] dictArray = field.getAnnotationsByType(Dict.class);

                //开始尝试翻译
                for (Dict dict : dictArray) {

                    //不用翻译
                    if (prop == null || dict.onlyQuery()) {
                        continue;
                    }

                    //获取字典参数
                    String key = String.valueOf(prop);
                    String code = dict.code();
                    String table = dict.table();
                    String text = dict.prop();

                    //组装后缀
                    String suffix = (StrUtil.isBlank(table) ? "" : "_" + text) + DictConstant.DICT_TEXT_SUFFIX;

                    //补充默认字典
                    if (StrUtil.isBlank(table)) {
                        table = "sys_dict";
                        text = "text";
                    }
                    String value = null;
                    String tempValue = dictTemp.get(table + code + key + text);
                    if (dictTemp.containsKey(table + code + key + text)) {
                        value = tempValue;
                    } else {
                        //翻译字典值对应的txt
                        value = translateDictValue(dict, key);
                        dictTemp.put(table + code + key + text, value);
                        log.debug(" __数据库 翻译字典字段__ " + field.getName() + suffix + "： " + value);
                    }

                    //将字典值写入返回值
                    item.put(field.getName() + suffix, value);

                    /** 下面这一块是兼容以前的，不然以前的很多接口都需要重新对接
                     *
                     * -by TeaR  -2021/7/31-0:19
                     */
                    item.put(field.getName() + DictConstant.DICT_TEXT_SUFFIX, value);
                }

                //date类型默认转换string格式化日期,并增加对应时间戳
                if (field.getType().getName().equals("java.util.Date") && prop != null) {
                    translateDateValue(field, item);
                }

            }

            return item;

        } catch (Exception e) {
            log.error("翻译失败：" + e.getMessage());
            e.printStackTrace();
            //取消翻译，返回原来的
            return entity;
        }
    }


    // ----------------------------------------------- 基础方法 -----------------------------------------------


    /**
     * 处理时间属性
     *
     * @param field 当前属性
     * @param item  实体类对象
     *              <p>
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2020/7/10 16:16
     */
    private void translateDateValue(Field field, JSONObject item) {
        SimpleDateFormat simpleDateFormat = null;
        try {
            if (field.getAnnotation(JsonFormat.class) == null) {
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            } else {
                simpleDateFormat = new SimpleDateFormat(field.getAnnotation(JsonFormat.class).pattern());
                //补充时区
                if (StrUtil.isNotBlank(field.getAnnotation(JsonFormat.class).timezone())) {
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(field.getAnnotation(JsonFormat.class).timezone()));
                }
            }
            item.put(field.getName(), simpleDateFormat.format(new Date((Long) item.get(field.getName()))));
            item.put(field.getName() + DictConstant.DATE_UNIX_SUFFIX, item.getDate(field.getName()).getTime());
        } catch (Exception e) {
            //时间戳优化转换异常
        }
    }

    /**
     * 翻译字典文本
     *
     * @param dict 字典注解
     * @param key  字典KEY值
     * @return
     */
    private String translateDictValue(Dict dict, String key) {
        String code = dict.code();
        String table = dict.table();
        String prop = dict.prop();
        //翻译字典值对应的txt
        return translateDictValue(code, key, table, prop);
    }

    /**
     * 翻译字典文本
     *
     * @param code  字典code
     * @param key   值对应的KEY
     * @param table 指定表名
     * @param prop  指定属性字段
     * @return
     */
    private String translateDictValue(String code, String key, String table, String prop) {
        if (StringUtils.isEmpty(key) || StrUtil.NULL.equalsIgnoreCase(key)) {
            return null;
        }
        StringBuffer textValue = new StringBuffer();
        String[] keys = key.split(",");
        for (String k : keys) {
            String tmpValue = null;
            log.debug(" 字典 code : " + code + " 字典 key : " + k);
            if (k.trim().length() == 0) {
                continue; //跳过循环
            }

            //区分不同的调用方法，自定义字典不走缓存，影响性能
            if (StrUtil.isBlank(table) || StrUtil.isBlank(prop)) {
                tmpValue = service.queryDictText(code, k.trim());
            } else {
                tmpValue = service.queryDictText(code, k.trim(), table, prop);
            }

            if (tmpValue != null) {
                if (!"".equals(textValue.toString())) {
                    textValue.append(",");
                }
                textValue.append(tmpValue);
            }

        }
        return textValue.toString();
    }

}
