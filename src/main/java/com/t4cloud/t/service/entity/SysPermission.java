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
 * 菜单权限表 实体类
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-12
 */
@Data
@TableName("sys_permission")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "SysPermission对象", description = "菜单权限表")
public class SysPermission extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 父id
     */
    @ApiModelProperty(value = "父id")
    private String parentId;
    /**
     * 菜单标题
     */
    @ApiModelProperty(value = "菜单标题")
    private String name;
    /**
     * 路径
     */
    @ApiModelProperty(value = "路径")
    private String url;
    /**
     * 打开方式（0-框架内打开，1-新的页面打开）
     */
    @ApiModelProperty(value = "打开方式（0-框架内打开，1-新的页面打开）")
    private Integer openType;
    /**
     * 组件
     */
    @ApiModelProperty(value = "组件")
    private String component;
    /**
     * 菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)
     */
    @ApiModelProperty(value = "菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)")
    private Integer menuType;
    /**
     * 权限编码
     */
    @ApiModelProperty(value = "权限编码")
    private String perms;
    /**
     * 菜单排序
     */
    @ApiModelProperty(value = "菜单排序")
    private Double sortNo;
    /**
     * 菜单图标
     */
    @ApiModelProperty(value = "菜单图标")
    private String icon;
    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;
    /**
     * 隐藏路由: 0-隐藏,1-展示
     */
    @ApiModelProperty(value = "隐藏路由: 0-隐藏,1-展示")
    private Integer hidden;
    /**
     * 按钮权限状态(0无效1有效)
     */
    @ApiModelProperty(value = "按钮权限状态(0无效1有效)")
    private String status;
    public SysPermission(String id) {
        this.id = id;
    }


}
