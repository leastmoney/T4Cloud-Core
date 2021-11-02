package com.t4cloud.t.base.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.t4cloud.t.base.entity.LoginUser;
import com.t4cloud.t.base.utils.UserUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MYbatis-Plus拦截器
 * <p>
 * 用以自动填充某些属性
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/2/23 23:43
 */
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        //插入增加时间
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        //插入增加的用户（如果有的话）
        LoginUser user = UserUtil.getCurrentUser();
        if (user != null) {
            this.strictInsertFill(metaObject, "createBy", String.class, user.getId());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //插入更新时间
        // XXX 这个方法只有在val为空的时候生效，如果有值就不会覆盖，导致UPDATE字段只有第一次会写入，这显然不是我们希望的结果，改成直接setValue强制更新，目前不知道会带来什么负面影响 -by TeaR  -2020/2/24-0:36
//        this.strictUpdateFill(metaObject, "updateTime", Date.class, DateUtil.date());
        metaObject.setValue("updateTime", new Date());
        //插入更新的用户（如果有的话）
        LoginUser user = UserUtil.getCurrentUser();
        if (user != null) {
//            this.strictUpdateFill(metaObject, "updateBy", String.class, user.getUsername());
            metaObject.setValue("updateBy", user.getId());
        }


    }

}