package com.t4cloud.t.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.t4cloud.t.base.annotation.Dict;
import com.t4cloud.t.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 公司表 实体类
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @since 2020-04-15
 */
@Data
@TableName("sys_company")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "SysCompany对象", description = "公司表")
public class SysCompany extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 公司名
     */
    @ApiModelProperty(value = "公司名")
    @NotNull(message = "公司名不允许为空")
    private String name;
    /**
     * 英文名
     */
    @ApiModelProperty(value = "英文名")
    private String nameEn;
    /**
     * 公司名缩写
     */
    @ApiModelProperty(value = "公司名缩写")
    private String nameAbbr;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
    /**
     * 联系方式
     */
    @ApiModelProperty(value = "联系方式")
    private String phone;
    /**
     * 传真
     */
    @ApiModelProperty(value = "传真")
    private String fax;
    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @Dict(code = "common_status")
    private Integer status;

    public SysCompany(String id) {
        this.id = id;
    }


}
