package com.t4cloud.t.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.t4cloud.t.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 字典 实体类
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-09
 */
@Data
@TableName("sys_dict")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "SysDict对象", description = "SysDict对象")
public class SysDict extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 字典名称
     */
    @ApiModelProperty(value = "字典名称")
    private String name;
    /**
     * 字典编码
     */
    @ApiModelProperty(value = "字典编码")
    private String code;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
    /**
     * 字典类型0为string,1为number
     */
    @ApiModelProperty(value = "字典类型0为string,1为number")
    private Integer type;
    /**
     * 删除状态
     */
    @ApiModelProperty(value = "删除状态")
    private Integer status;
    public SysDict(String id) {
        this.id = id;
    }


}
