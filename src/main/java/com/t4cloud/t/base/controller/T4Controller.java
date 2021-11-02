package com.t4cloud.t.base.controller;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import com.t4cloud.t.base.constant.RequestConstant;
import com.t4cloud.t.base.exception.T4CloudServiceException;
import com.t4cloud.t.base.export.image.ExportImageUtil;
import com.t4cloud.t.base.export.image.entity.ImageConfig;
import com.t4cloud.t.base.export.image.entity.LineStyle;
import com.t4cloud.t.base.export.poi.EasyPoiUtil;
import com.t4cloud.t.base.utils.SpringContextUtil;
import com.t4cloud.t.base.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * T4Controller
 * <p>
 * Controller基础类
 * <p>
 * ---------------------
 *
 * @author Terry
 * @date 2020/1/15 12:39
 */
@Slf4j
public class T4Controller<T, S extends IService<T>> {

    @Autowired
    public S service;

    private Class<T> modelClass;

    public T4Controller() {
        Type type = this.getClass().getGenericSuperclass();
        this.modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    /**
     * 获取对象ID
     *
     * @return
     */
    protected String getId(T item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前请求携带的token
     *
     * <p>
     *
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2020/2/10 16:54
     */
    protected String getToken() {
        String token = SpringContextUtil.getHttpServletRequest().getHeader(RequestConstant.T_ACCESS_TOKEN);
        return token;
    }

    /**
     * 获取当前请求携带的 uuid
     *
     * <p>
     *
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2020/11/27 14:10
     */
    protected String getUuid() {
        String uuid = SpringContextUtil.getHttpServletRequest().getHeader(RequestConstant.T_UUID);
        return uuid;
    }

    /**
     * 导出数据
     *
     * @param type            excel|image 导出类型
     * @param title           标题|文件名
     * @param title           文件名
     * @param selectedColKeys 自定义列
     * @param queryWrapper    查询条件
     *                        <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/7/15 13:18
     */
    protected void export(String type, String title, String selectedColKeys, QueryWrapper queryWrapper) {
        switch (type) {
            case "excel": {
                exportExl(title, selectedColKeys, queryWrapper);
                break;
            }
            case "image": {
                exportImg(title, selectedColKeys, queryWrapper);
                break;
            }
            default: {
                throw new T4CloudServiceException("不支持的导出类型");
            }
        }
    }

    /**
     * 导出excel
     *
     * @param title           文件名
     * @param selectedColKeys 自定义列
     * @param queryWrapper    查询条件
     *                        <p>
     * @return void
     * --------------------
     * @author QiMing
     * @date 2020/2/24 1:28
     */
    private void exportExl(String title, String selectedColKeys, QueryWrapper queryWrapper) {
        //统计导出的数据量
        int count = service.count(queryWrapper);

        //如果selectedColMap  不为空，则导出自定义列
        List<ExcelExportEntity> beanList = null;
        if (selectedColKeys != null) {
            String[] colKeys = selectedColKeys.split(",");
            //反射获取列key和value
            beanList = EasyPoiUtil.genExcelExportLst(Lists.newArrayList(colKeys), modelClass);
        }
        //生成副标题
        String username = UserUtil.getCurrentUser().getUsername();
        String subTitle = "导出人:" + username + "  导出时间:" + DateUtil.now();

        //开始导出
        EasyPoiUtil.exportExcel(title, subTitle, modelClass, beanList, count, service, queryWrapper);
    }

    /**
     * 导出 Image
     *
     * @param title           文件名
     * @param selectedColKeys 自定义列
     * @param queryWrapper    查询条件
     *                        <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/7/14 2:23 下午
     */
    private void exportImg(String title, String selectedColKeys, QueryWrapper queryWrapper) {
        //统计导出的数据量
        List list = service.list(queryWrapper);

        //生成副标题
        String username = UserUtil.getCurrentUser().getUsername();
        String subTitle = "导出人:" + username + "  导出时间:" + DateUtil.now();

        //自定义配置
        ImageConfig config = new ImageConfig();
        LineStyle doubleLineStyle = LineStyle.DEFAULT;
        doubleLineStyle.setColor(Color.LIGHT_GRAY);
        config.setDoubleContentStyle(doubleLineStyle);

        //开始导出
        ExportImageUtil.export(title, subTitle, selectedColKeys, modelClass, list, config);
    }


    /**
     * 检查实体类某一项是否已存在
     *
     * @param key   属性名
     * @param value 属性值
     *              <p>
     * @return java.lang.Boolean
     * --------------------
     * @author TeaR
     * @date 2020/3/16 15:17
     */
    protected Boolean checkProp(String key, String value) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StrUtil.toUnderlineCase(key), value);
        List<T> users = service.list(queryWrapper);
        if (CollectionUtil.isEmpty(users)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
