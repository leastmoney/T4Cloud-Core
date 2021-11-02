package com.t4cloud.t.base.export.image.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * ImageCol
 * <p>
 * 图片导出每列参数
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2021/5/27 15:46
 */
@Data
public class ImageCol {

    private Excel excel;
    private Object object;
    private Object value;

}
