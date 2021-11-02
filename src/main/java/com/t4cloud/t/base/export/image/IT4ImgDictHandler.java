package com.t4cloud.t.base.export.image;

import cn.hutool.core.util.StrUtil;
import com.t4cloud.t.base.utils.SpringContextUtil;
import com.t4cloud.t.service.service.IT4CommonService;

public interface IT4ImgDictHandler {

    default String toName(String code, Object value) {

        IT4CommonService service = SpringContextUtil.getBean(IT4CommonService.class);
        try {

            //空值不翻译
            if (value == null || StrUtil.isBlank(value + StrUtil.EMPTY) || StrUtil.isBlank(code)) {
                return StrUtil.EMPTY;
            }

            String val = "";
            String[] dictInfo = code.split("\\|\\|");
            if (dictInfo.length == 3) {
                String tmpCode = dictInfo[0];
                String table = dictInfo[1];
                String prop = dictInfo[2];
                val = service.queryDictText(tmpCode, (String) value, table, prop);
            } else {
                val = service.queryDictText(code, value + "");
            }

            return StrUtil.isBlank(val) ? value + "" : val;

        } catch (Exception e) {
            return "";
        }

    }

    default String toValue(String code, Object value) {

        IT4CommonService service = SpringContextUtil.getBean(IT4CommonService.class);
        if (!StrUtil.isBlank(value + "") && !StrUtil.isBlank(code)) {
            String key = service.queryDictKey(code, (String) value);
            return key;
        } else {
            return "";
        }

    }
}
