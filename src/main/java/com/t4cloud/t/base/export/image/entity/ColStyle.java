package com.t4cloud.t.base.export.image.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.text.SimpleDateFormat;

@Data
@Accessors(chain = true)
public class ColStyle {
    private double width;
    private int order;
    private int type;
    private SimpleDateFormat format;
    private String name;
    private String propertyName;
    private String dict;
//    private Field field;//只有为日期的时候需要，其他类型为null

}
