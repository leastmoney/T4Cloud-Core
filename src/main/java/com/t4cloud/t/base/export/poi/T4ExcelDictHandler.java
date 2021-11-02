package com.t4cloud.t.base.export.poi;

import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.hutool.core.util.StrUtil;
import com.t4cloud.t.service.service.IT4CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * excel导入导出,字典自定义 翻译
 *
 * <p>
 * --------------------
 *
 * @author qiming
 * @since 2020-02-22
 */
@Configuration
public class T4ExcelDictHandler implements IExcelDictHandler {

    @Autowired
    private IT4CommonService service;

    /**
     * excel 导出
     *
     * @return
     */
    @Override
    public String toName(String code, Object o, String name, Object value) {
        //空值不翻译
        if (value == null || StrUtil.isBlank(value + StrUtil.EMPTY) || StrUtil.isBlank(code)) {
            return StrUtil.EMPTY;
        }

        String val = StrUtil.EMPTY;
        String[] dictInfo = code.split("\\|\\|");
        if (dictInfo.length == 3) {
            //从指定表查询
            String tmpCode = dictInfo[0];
            String table = dictInfo[1];
            String prop = dictInfo[2];
            val = service.queryDictText(tmpCode, (String) value, table, prop);
        } else {
            //从系统字典表查询
            val = service.queryDictText(code, value + StrUtil.EMPTY);
        }

        if (StrUtil.isBlank(val)) {
            return value + StrUtil.EMPTY;
        }
        return val;
    }

    /**
     * excel 导入
     *
     * @return
     */
    @Override
    public String toValue(String code, Object o, String s1, Object value) {
        if (StrUtil.isBlank(value + StrUtil.EMPTY) || StrUtil.isBlank(code)) {
            return StrUtil.EMPTY;
        }
        String key = service.queryDictKey(code, (String) value);
        //导入的时候，如果反向无法查询到KEY时，保持null，避免数据库插入出错。例如value：男性，无法有KEY值，但是此时如果将男性插入到tinyint显然是不合理的。会因此报错
//        if(StrUtil.isBlank(key)){
//            return (String)value;
//        }
        return key;
    }


}
