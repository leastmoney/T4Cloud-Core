package com.t4cloud.t.service.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.t4cloud.t.service.entity.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单权限表 Mapper 接口
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-12
 */
public interface CommonPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据用户查询用户权限
     */
    public List<SysPermission> queryByUser(@Param("userId") String userId);

}
