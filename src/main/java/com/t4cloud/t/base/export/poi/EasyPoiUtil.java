package com.t4cloud.t.base.export.poi;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.t4cloud.t.base.constant.ResultConstant;
import com.t4cloud.t.base.exception.T4CloudException;
import com.t4cloud.t.base.export.poi.entity.MultiSheet;
import com.t4cloud.t.base.utils.SpringContextUtil;
import com.t4cloud.t.base.utils.UserUtil;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;


/**
 * easypoi工具类
 * <p>
 * --------------------
 *
 * @author Qiming
 * @date 2020/0221
 */
@Component
public class EasyPoiUtil {

    public static Integer bigData;
    @Autowired
    private Environment env;

    /**
     * 导出EXCEL，会自动判断是否采用内存优化的大数据模式
     *
     * @param title       一级标题
     * @param secondTitle 副标题
     * @param pojoClass   实体类
     * @param autoColList 自定义列
     * @throws T4CloudException
     */
    public static <T, S extends IService<T>> void exportExcel(String title, String secondTitle, Class<?> pojoClass, List<ExcelExportEntity> autoColList, int count, S service, QueryWrapper queryWrapper) throws T4CloudException {

        //定义基础配置
        ExportParams exportParams = createDefaultExportParams(title, secondTitle);

        Workbook workbook = null;

        //判断是否需要大数据优化性能
        Boolean bigDateFlag = count > bigData;
        //是否自定义列
        Boolean autoColListFlag = autoColList != null;

        //处理图片列
        if (autoColListFlag) {
            getPicCol(exportParams, autoColList);
        } else {
            getPicCol(exportParams, pojoClass);
        }

        //大数据自定义列导出
        if (bigDateFlag && autoColListFlag) {
            workbook = ExcelExportUtil.exportBigExcel(exportParams, autoColList, (IExcelExportServer) service, queryWrapper);
        }

        //大数据实体类导出
        if (bigDateFlag && !autoColListFlag) {
            workbook = ExcelExportUtil.exportBigExcel(exportParams, pojoClass, (IExcelExportServer) service, queryWrapper);
        }

        //普通自定义列导出
        if (!bigDateFlag && autoColListFlag) {
            workbook = ExcelExportUtil.exportExcel(exportParams, autoColList, service.list(queryWrapper));
        }

        //普通实体类导出
        if (!bigDateFlag && !autoColListFlag) {
            workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, service.list(queryWrapper));
        }

        //开始对外输出excel文件
        downLoadExcel(title, workbook);
    }

    /**
     * 导出EXCEL，使用已经查询到的数据集
     *
     * @param title     一级标题
     * @param pojoClass 实体类
     * @param dataList  数据集合
     * @throws T4CloudException
     */
    public static <T> void exportExcel(String title, Class<T> pojoClass, List<T> dataList) throws T4CloudException {

        String username = UserUtil.getCurrentUser() == null ? "未登录" : UserUtil.getCurrentUser().getUsername();
        StringBuilder secondTitle = new StringBuilder().append("导出人:").append(username).append("  导出时间:").append(DateUtil.now());

        //定义基础配置
        ExportParams exportParams = createDefaultExportParams(title, secondTitle.toString());

        Workbook workbook = null;

        //处理图片列
        getPicCol(exportParams, pojoClass);

        //准备workbook
        workbook = ExcelExportUtil.exportExcel(exportParams, pojoClass, dataList);

        //开始对外输出excel文件
        downLoadExcel(title, workbook);
    }

    /**
     * 导出多sheet表的EXCEL，使用已经查询到的数据集
     *
     * @param dataList 多sheet表专用数据集
     * @throws T4CloudException
     */
    public static <T> void exportMultiSheetExcel(List<MultiSheet> dataList, String fileName) throws T4CloudException {

        String username = UserUtil.getCurrentUser() == null ? "未登录" : UserUtil.getCurrentUser().getUsername();
        StringBuilder secondTitle = new StringBuilder().append("导出人:").append(username).append("  导出时间:").append(DateUtil.now());

        //将暴露给外部的严格模式，转换成内部的松散对象,并完善exportParams信息
        List<Map<String, Object>> list = new ArrayList<>();
        for (MultiSheet multiSheet : dataList) {
            //完善exportParams
            ExportParams exportParams = createDefaultExportParams(multiSheet.getTitle(), secondTitle.toString());
            //处理图片列
            getPicCol(exportParams, multiSheet.getEntity());

            //进行转换
            Map<String, Object> param = new HashMap<>();
            param.put("title", exportParams);
            param.put("entity", multiSheet.getEntity());
            param.put("data", multiSheet.getData());

            list.add(param);
        }

        //准备workbook
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);

        //开始对外输出excel文件
        downLoadExcel(fileName, workbook);
    }

    /**
     * 导入excel，将文件解析成实体类列表
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> pojoClass) {
        if (file == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(2);
        params.setHeadRows(1);
        params.setDictHandler(SpringUtil.getBean("t4ExcelDictHandler"));
        List<T> list = null;
        try {
            list = ExcelImportUtil.importExcel(file.getInputStream(), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new T4CloudException("excel文件不能为空");
        } catch (IOException e) {
            throw new T4CloudException("不支持该文件，请确认使用标准excel文件");
        } catch (Exception e) {
            throw new T4CloudException(e.getMessage());
        }
        return list;
    }

    private static void downLoadExcel(String fileName, Workbook workbook) {
        try {
            HttpServletResponse response = SpringContextUtil.getHttpServletResponse();
            String encodeFileName = URLUtil.encode(fileName + ".xls", "UTF-8");
            response.setContentType("application/vnd.ms-excel;application/force-download;charset=UTF-8;");
            response.addHeader(ResultConstant.ACCESS_CONTROL_EXPOSE_HEADERS, ResultConstant.FILENAME);
            response.addHeader(ResultConstant.FILENAME, encodeFileName);
            response.addHeader("Content-Disposition", "attachment;filename=" + encodeFileName);
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            throw new T4CloudException("导出excel失败");
        }
    }

    /**
     * 自定义导出列
     *
     * @param colKeys
     * @param clazz
     * @return
     */
    public static List<ExcelExportEntity> genExcelExportLst(List colKeys, Class clazz) {
        List<ExcelExportEntity> excelExportEntityLst = new ArrayList<>();

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            //如果定义的key中有该字段，就获取该字段上的excel的值， 将他加入到自定义中
            if (colKeys.contains(declaredField.getName())) {
                ExcelExportEntity excelExportEntity = new ExcelExportEntity();
                excelExportEntity.setKey(declaredField.getName());
                Excel excelAno = declaredField.getAnnotation(Excel.class);
                //如果没有注解，默认使用名字
                String name = declaredField.getName();
                //如果ano属性为空，或者没有注解，尝试获取<code>ApiModelProperty</code>中的value值，如果value值也不存在，则导出字段也使用用字段名
//                if (excelAno == null || StrUtil.isBlank(excelAno.name())) {
                //不存在上述StrUtil.isBlank(excelAno.name())这个情况，因为该注解要求必须有name属性 -by TeaR  -2020/2/24-0:53
                if (excelAno == null) {
                    ApiModelProperty apiModelProperty = declaredField.getAnnotation(ApiModelProperty.class);
                    if (!StrUtil.isBlank(apiModelProperty.value())) {
                        name = apiModelProperty.value();
                    }
                    excelExportEntity.setName(name);

                    //如果是date类型，且exportFormat为空，则自动转换类型
                    if (declaredField.getType().getName().equals("java.util.Date")) {
                        excelExportEntity.setFormat("yyyy-MM-dd HH:ss:mm");
                        excelExportEntity.setWidth(20);
                    }

                } else {
                    //如果存在excel注解
                    if (!excelAno.isColumnHidden()) {
                        excelExportEntity.setColumnHidden(excelAno.isColumnHidden());
                    }
                    if (!excelAno.needMerge()) {
                        excelExportEntity.setNeedMerge(excelAno.needMerge());
                    }
                    if (!StrUtil.isEmpty(excelAno.dict())) {
                        excelExportEntity.setDict(excelAno.dict());
                    }

                    if (!StrUtil.isEmpty(excelAno.orderNum())) {
                        excelExportEntity.setOrderNum(Integer.valueOf(excelAno.orderNum()));
                    }
                    //如果是date类型，且exportFormat为空，则自动转换类型
                    if (declaredField.getGenericType().toString().equals(("class java.util.Date"))) {
                        if (!StrUtil.isBlank(excelAno.format())) {
                            excelExportEntity.setFormat(excelAno.format());
                        } else if (!StrUtil.isBlank(excelAno.exportFormat())) {
                            excelExportEntity.setFormat(excelAno.exportFormat());
                        } else {
                            excelExportEntity.setFormat("yyyy-MM-dd HH:ss:mm");
                        }
                        if (!StrUtil.isBlank(excelAno.databaseFormat())) {
                            excelExportEntity.setDatabaseFormat(excelAno.databaseFormat());
                        }
                    }
                    excelExportEntity.setName(excelAno.name());
                    excelExportEntity.setExportImageType(excelAno.imageType());
                    excelExportEntity.setHeight(excelAno.height());
                    excelExportEntity.setWidth(excelAno.width());
                    excelExportEntity.setType(excelAno.type());
                    if (!ArrayUtil.isEmpty(excelAno.replace())) {
                        excelExportEntity.setReplace(excelAno.replace());
                    }
                }
                excelExportEntityLst.add(excelExportEntity);
            }
        }
        return excelExportEntityLst;
    }

    /**
     * 获取需要处理的图片资源列
     *
     * @param exportParams
     * @param clazz
     * @return
     */
    private static void getPicCol(ExportParams exportParams, Class clazz) {
        T4ExcelPicHandler<T> t4ExcelPicHandler = new T4ExcelPicHandler<>();
        List picColList = new ArrayList();
        Field[] fields = FieldUtils.getFieldsWithAnnotation(clazz, Excel.class);
        for (Field field : fields) {
            Excel excel = field.getAnnotation(Excel.class);
            if (excel.type() == 2) {
                picColList.add(excel.name());
            }
        }
        String[] picCol = new String[picColList.size()];
        picColList.toArray(picCol);
        t4ExcelPicHandler.setNeedHandlerFields(picCol);
        exportParams.setDataHandler(t4ExcelPicHandler);
    }

    /**
     * 获取需要处理的图片资源列
     *
     * @param exportParams
     * @param colList
     * @return
     */
    private static void getPicCol(ExportParams exportParams, List<ExcelExportEntity> colList) {
        T4ExcelPicHandler<T> t4ExcelPicHandler = new T4ExcelPicHandler<>();
        List picColList = new ArrayList();
        for (ExcelExportEntity excelExportEntity : colList) {
            if (excelExportEntity.getType() == 2) {
                picColList.add(excelExportEntity.getName());
            }
        }
        String[] picCol = new String[picColList.size()];
        picColList.toArray(picCol);
        t4ExcelPicHandler.setNeedHandlerFields(picCol);
        exportParams.setDataHandler(t4ExcelPicHandler);
    }

    /**
     * 生成默认的 ExportParams 配置
     *
     * @param title       一级标题和sheet名
     * @param secondTitle 二级标题
     *                    <p>
     * @return ExportParams
     * --------------------
     * @author TeaR
     * @date 2020/9/25 11:56
     */
    private static ExportParams createDefaultExportParams(String title, String secondTitle) {
        //定义基础配置
        ExportParams exportParams = new ExportParams(title, secondTitle, title);
        exportParams.setStyle(T4ExcelStyler.class);
        exportParams.setSecondTitleHeight((short) 8);
        exportParams.setHeaderHeight(120.0D);
        exportParams.setDictHandler(SpringUtil.getBean("t4ExcelDictHandler"));
        return exportParams;
    }

    @PostConstruct
    public void readConfig() {
        bigData = env.getProperty("t4cloud.excel.big-data", Integer.class);
        if (bigData == null) {
            bigData = 5000;
        }
    }

}
