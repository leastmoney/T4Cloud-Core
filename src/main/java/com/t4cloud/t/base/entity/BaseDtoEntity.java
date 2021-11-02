package com.t4cloud.t.base.entity;

import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 实体类 DTO基类
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 22:22
 */
@ExcelTarget("BaseDtoEntity")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDtoEntity<T extends BaseDtoEntity> extends T4Entity {

    protected static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty(value = "数据标识，主键")
    protected String id;

//    /**
//     * 删除状态（0，正常，1已删除）
//     */
//    @TableLogic
//    @ApiModelProperty(value = "删除状态（0，正常，1已删除）")
//    protected Integer flag;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人（用户名，唯一）")
    protected String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(timezone = "GMT+8" , pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人（用户名，唯一）")
    protected String updateBy;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(timezone = "GMT+8" , pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;

    public String getId() {
        return id;
    }

    public T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public String getCreateBy() {
        return createBy;
    }

    public T setCreateBy(String createBy) {
        this.createBy = createBy;
        return (T) this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public T setCreateTime(Date createTime) {
        this.createTime = createTime;
        return (T) this;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public T setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return (T) this;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public T setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return (T) this;
    }

}
