package com.t4cloud.t.base.export.image.entity;

import lombok.Data;

@Data
public class ImageConfig {

    /**
     * 图片宽度
     */
    private int width;
    /**
     * 图片高度
     */
    private int height;
    /**
     * 标题行样式
     */
    private LineStyle titleStyle = LineStyle.TITLE;
    /**
     * 副标题行样式
     */
    private LineStyle subTitleStyle = LineStyle.SUB_TITLE;
    /**
     * 内容行样式
     */
    private LineStyle contentStyle = LineStyle.DEFAULT;
    /**
     * 内容双行样式
     */
    private LineStyle doubleContentStyle = null;

}
