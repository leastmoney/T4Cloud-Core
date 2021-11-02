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
 * 字典详细值 实体类
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-09
 */
@Data
@TableName("sys_dict_value")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "SysDictValue对象", description = "SysDictValue对象")
public class SysDictValue extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 字典id
     */
    @ApiModelProperty(value = "字典id")
    private String dictId;
    /**
     * 字典项文本
     */
    @ApiModelProperty(value = "字典项文本")
    private String text;
    /**
     * 字典项值
     */
    @ApiModelProperty(value = "字典项值")
    private String value;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer position;
    /**
     * 状态（1启用 0不启用）
     */
    @ApiModelProperty(value = "状态（1启用 0不启用）")
    private Integer status;
    public SysDictValue(String id) {
        this.id = id;
    }


}
