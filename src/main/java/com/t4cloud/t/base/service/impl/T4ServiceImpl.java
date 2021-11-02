package com.t4cloud.t.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.t4cloud.t.base.entity.T4Entity;
import com.t4cloud.t.base.service.T4Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 服务类基类 实现类
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 23:40
 */
@Validated
public class T4ServiceImpl<M extends BaseMapper<T>, T extends T4Entity> extends ServiceImpl<M, T> implements T4Service<T> {

    @Value("${t4cloud.excel.page-size:5000}")
    private int pageSize;

    @Override
    public boolean deleteLogic(@NotEmpty List<Object> ids) {
        return true;
    }

    @Override
    public boolean changeStatus(@NotEmpty List<Object> ids, Integer status) {
        return true;
    }

    /**
     * 大数据模式下的分页查询器
     *
     * @param queryWrapper 查询条件
     * @param i            当前页
     *                     <p>
     * @return java.util.List<java.lang.Object>
     * --------------------
     * @author Qiming
     * @date 2020/2/26 21:00
     */
    @Override
    public List<Object> selectListForExcelExport(Object queryWrapper, int i) {
        if (queryWrapper instanceof QueryWrapper) {
            Page<Object> page = this.page(new Page<>(i, pageSize), (QueryWrapper) queryWrapper);
            return page.getRecords();
        }
        return null;
    }
}
