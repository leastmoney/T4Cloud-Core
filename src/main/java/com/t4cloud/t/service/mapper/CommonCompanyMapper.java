package com.t4cloud.t.service.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.t4cloud.t.service.entity.SysCompany;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公司表 Mapper 接口
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @since 2020-04-15
 */
public interface CommonCompanyMapper extends BaseMapper<SysCompany> {

    /**
     * 根据用户查询用户权限
     */
    List<SysCompany> queryByUserId(@Param("userId") String userId);

}
