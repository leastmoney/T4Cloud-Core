package com.t4cloud.t.base.export.image.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Builder;
import lombok.Data;

import java.awt.*;

/**
 * LineStyle
 * <p>
 * 定义行的样式
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2021/5/27 14:52
 */
@Data
@Builder
public class LineStyle {

    // ----------------------------------------------- 常量值 -----------------------------------------------
    //ALIGN属性
    public static String ALIGN_CENTER = "center";
    public static String ALIGN_LEFT = "left";
    public static String ALIGN_RIGHT = "right";
    //字体属性
    public static String FONT_SimSun = "宋体";
    public static String FONT_SimHei = "黑体";
    public static String FONT_KaiTi = "楷体";
    public static String FONT_YaHei = "微软雅黑";
    public static String FONT_FangSong = "仿宋";
    //字体style属性
    public static int FONT_PLAIN = Font.PLAIN;//正常
    public static int FONT_BOLD = Font.BOLD;//加粗
    public static int FONT_ITALIC = Font.ITALIC;//斜体
    public static int FONT_BOLD_ITALIC = Font.BOLD + Font.ITALIC;//加粗+斜体
    // ----------------------------------------------- 常用样式 -----------------------------------------------

    public static LineStyle DEFAULT = LineStyle.builder().fontSize(15).height(60).font(FONT_KaiTi).build();
    public static LineStyle TITLE = LineStyle.builder().fontSize(30).height(80).font(FONT_SimHei).build();
    public static LineStyle SUB_TITLE = LineStyle.builder().fontSize(10).height(40).font(FONT_SimHei).align(ALIGN_RIGHT).build();

    // ----------------------------------------------- 属性 -----------------------------------------------

    /**
     * 行背景色
     */
    private Color color;
    /**
     * 行高
     */
    private int height;

    /**
     * 行水平方向
     */
    private String align;

    /**
     * 字体
     */
    private String font;
    /**
     * 字体颜色
     */
    private Color fontColor;
    /**
     * 字体大小
     */
    private int fontSize;
    /**
     * 字体粗细
     */
    private int fontWeight;

    //默认属性的初始化要在get方法中获取
    public Color getColor() {
        return color == null ? Color.WHITE : color;
    }

    public int getHeight() {
        return height == 0 ? 80 : height;
    }

    public String getAlign() {
        return StrUtil.isBlank(align) ? ALIGN_CENTER : align;
    }

    public String getFont() {
        return StrUtil.isBlank(font) ? FONT_SimSun : font;
    }

    public Color getFontColor() {
        return fontColor == null ? Color.BLACK : fontColor;
    }

    public int getFontSize() {
        return fontSize == 0 ? 30 : fontSize;
    }
}
