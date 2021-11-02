package com.t4cloud.t.base.util;

import com.t4cloud.t.T4CloudCoreApplication;
import com.t4cloud.t.base.redis.topic.entity.RedisMsg;
import com.t4cloud.t.base.utils.RedisTopicUtil;
import com.t4cloud.t.service.entity.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 通用Service测试
 *
 * <p>
 *
 * @author 风平浪静的明天
 * @return --------------------
 * @date 2021/6/16 10:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {T4CloudCoreApplication.class})
public class RedisTopicUtilTest extends Thread{

    /**
     * 集成redis实现消息发布订阅模式-双通道
     *
     */
    @Test
    public void sendMessageTest() {
        RedisMsg msg = new RedisMsg();
        SysUser user = new SysUser();
        user.setTenantId("213123123");
        user.setId("121111");
        user.setAddress("中国");
        user.setEmail("acknowledge");
        msg.setData(user);
        msg.setChannel("chat1");
        msg.setMsg("成功");
        if(msg ==null){
            return;
        }
        RedisMsg msg2 = new RedisMsg();
        SysUser user2 = new SysUser();
        user2.setTenantId("111");
        user2.setId("2222");
        user2.setAddress("中国222");
        user2.setEmail("acknowledge222");
        msg2.setData(user2);
        msg2.setChannel("chat2");
        msg2.setMsg("成功2");
        if (msg2 == null) {
            return;
        }

        RedisTopicUtil.sendMessage("chat", msg);
        RedisTopicUtil.sendMessage("test", msg2);

        System.out.println("发布成功！");
    }

}
