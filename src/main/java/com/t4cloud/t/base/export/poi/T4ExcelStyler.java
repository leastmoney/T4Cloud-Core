package com.t4cloud.t.base.export.poi;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.params.ExcelForEachParams;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import org.apache.poi.ss.usermodel.*;


public class T4ExcelStyler implements IExcelExportStyler {
    private static final short STRING_FORMAT = (short) BuiltinFormats.getBuiltinFormat("TEXT");
    private static final short FONT_SIZE_TEN = 10;
    private static final short FONT_SIZE_TITLE = 12;
    private static final short FONT_SIZE_HEADER = 18;
    /**
     * 大标题样式
     */
    private CellStyle headerStyle;
    /**
     * 每列标题样式
     */
    private CellStyle titleStyle;
    /**
     * 数据行样式
     */
    private CellStyle styles;
    /**
     * 数据行样式
     */
    private CellStyle stylesDouble;

    public T4ExcelStyler(Workbook workbook) {
        this.init(workbook);
    }

    /**
     * 初始化样式
     *
     * @param workbook
     */
    private void init(Workbook workbook) {
        this.headerStyle = initHeaderStyle(workbook);
        this.titleStyle = initTitleStyle(workbook);
        this.styles = initStyles(workbook);
        this.stylesDouble = initStylesDouble(workbook);
    }

    /**
     * 大标题样式
     *
     * @param color
     * @return
     */
    @Override
    public CellStyle getHeaderStyle(short color) {
        return headerStyle;
    }

    /**
     * 每列标题样式
     *
     * @param color
     * @return
     */
    @Override
    public CellStyle getTitleStyle(short color) {
        return titleStyle;
    }

    /**
     * 数据行样式
     *
     * @param parity 可以用来表示奇偶行
     * @param entity 数据内容
     * @return 样式
     */
    @Override
    public CellStyle getStyles(boolean parity, ExcelExportEntity entity) {
        if (parity) {
            return styles;
        } else {
            return stylesDouble;
        }
    }

    /**
     * 获取样式方法
     *
     * @param dataRow 数据行
     * @param obj     对象
     * @param data    数据
     */
    @Override
    public CellStyle getStyles(Cell cell, int dataRow, ExcelExportEntity entity, Object obj, Object data) {
        return getStyles(dataRow % 2 == 0, entity);
    }

    /**
     * 模板使用的样式设置
     */
    @Override
    public CellStyle getTemplateStyles(boolean isSingle, ExcelForEachParams excelForEachParams) {
        return null;
    }

    /**
     * 初始化--大标题样式
     *
     * @param workbook
     * @return
     */
    private CellStyle initHeaderStyle(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);
        Font font = getFont(workbook, FONT_SIZE_HEADER, true);
        font.setUnderline((byte) 2);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * 初始化--每列标题样式
     *
     * @param workbook
     * @return
     */
    private CellStyle initTitleStyle(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);
        style.setFont(getFont(workbook, FONT_SIZE_TITLE, false));
        //背景色
        style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE1.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * 初始化--数据行样式
     *
     * @param workbook
     * @return
     */
    private CellStyle initStyles(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);
        style.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(getFont(workbook, FONT_SIZE_TEN, false));
        style.setDataFormat(STRING_FORMAT);
        return style;
    }

    /**
     * 初始化--数据行样式
     *
     * @param workbook
     * @return
     */
    private CellStyle initStylesDouble(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(getFont(workbook, FONT_SIZE_TEN, false));
        style.setDataFormat(STRING_FORMAT);
        return style;
    }

    /**
     * 基础样式
     *
     * @return
     */
    private CellStyle getBaseCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //下边框
        style.setBorderBottom(BorderStyle.THIN);
        //左边框
        style.setBorderLeft(BorderStyle.THIN);
        //上边框
        style.setBorderTop(BorderStyle.THIN);
        //右边框
        style.setBorderRight(BorderStyle.THIN);
        //水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        //上下居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置自动换行
        style.setWrapText(true);
        return style;
    }

    /**
     * 字体样式
     *
     * @param size   字体大小
     * @param isBold 是否加粗
     * @return
     */
    private Font getFont(Workbook workbook, short size, boolean isBold) {
        Font font = workbook.createFont();
        //字体样式
        font.setFontName("宋体");
        //是否加粗
        font.setBold(isBold);
        //字体大小
        font.setFontHeightInPoints(size);
        return font;
    }
}
