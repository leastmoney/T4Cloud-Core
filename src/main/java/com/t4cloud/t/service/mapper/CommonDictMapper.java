package com.t4cloud.t.service.mapper;


import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.t4cloud.t.service.entity.SysDict;
import com.t4cloud.t.service.entity.SysDictValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典 Mapper 接口
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-09
 */
@Mapper
public interface CommonDictMapper extends BaseMapper<SysDict> {

    List<SysDictValue> queryDict(@Param("code") String code);

    String queryDictText(@Param("code") String code, @Param("key") String key);

    String superQueryDictText(@Param("code") String code, @Param("key") String key, @Param("table") String table, @Param("prop") String prop);

    String queryDictKey(@Param("code") String code, @Param("text") String text);

}
