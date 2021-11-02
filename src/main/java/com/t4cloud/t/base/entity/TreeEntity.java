package com.t4cloud.t.base.entity;

import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 树状结构实体类 基类
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2021/8/4 4:11 下午
 */
@ExcelTarget("TreeEntity")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreeEntity<T extends TreeEntity> extends BaseEntity<T> {

    protected static final long serialVersionUID = 1L;

    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id")
    private String parentId;

    /**
     * 树状结构中的子集
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "子集")
    private List<T> children;

    /**
     * 是否包含子集
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "是否有子集")
    private Boolean hasChild;

    public String getParentId() {
        return parentId;
    }

    public T setParentId(String parentId) {
        this.parentId = parentId;
        return (T) this;
    }

    public List<T> getChildren() {
        return children;
    }

    public T setChildren(List<T> children) {
        this.children = children;
        return (T) this;
    }

    public Boolean getHasChild() {
        return hasChild;
    }

    public T setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
        return (T) this;
    }
}
