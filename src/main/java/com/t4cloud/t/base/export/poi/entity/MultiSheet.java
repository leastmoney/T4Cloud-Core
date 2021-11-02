package com.t4cloud.t.base.export.poi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * MultiSheetData
 * <p>
 * 多sheet导出数据类
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2020/9/25 12:11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MultiSheet<T> {

    /**
     * 一级标题和sheet名
     */
    private String title;
    /**
     * 导出的配置类
     */
    private Class<T> entity;
    /**
     * 导出的数据集合
     */
    private List<T> data;

}
