package com.t4cloud.t.base.config;

import lombok.Data;

/**
 * GenConfig 代码生成器自定义的属性
 *
 * <p>
 *
 * @author TeaR
 * @return --------------------
 * @date 2020/2/8 16:28
 */
@Data
public class GenCodeConfig {

    /**
     * 表前缀
     */
    private String tablePrefix = "";

    /**
     * 需要生成的表名
     */
    private String[] tableName;

    /**
     * 生成到哪个模块
     */
    private String moduleName;

    /**
     * Client的服务名称
     */
    private String serverName;

    /**
     * modal是否为drawer方式，默认为modal
     */
    private Boolean drawer = false;

    /**
     * 是否需要生成feignClient&DTO
     */
    private Boolean feignClient;

    /**
     * 是否为树结构
     */
    private Boolean tree = false;
}
