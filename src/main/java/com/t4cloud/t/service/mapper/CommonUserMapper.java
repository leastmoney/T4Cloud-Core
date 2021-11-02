package com.t4cloud.t.service.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.t4cloud.t.service.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户表 Mapper 接口
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-12
 */
public interface CommonUserMapper extends BaseMapper<SysUser> {

    @Select("select role_code from sys_role where id in (select role_id from sys_user_role where user_id = #{userId})")
    List<String> getRoleByUserId(@Param("userId") String userId);

}
