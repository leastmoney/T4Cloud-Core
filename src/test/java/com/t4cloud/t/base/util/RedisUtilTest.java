package com.t4cloud.t.base.util;

import com.t4cloud.t.T4CloudCoreApplication;
import com.t4cloud.t.base.utils.RedisUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * RedisUtil测试工具类
 *
 * <p>
 * @return
 * --------------------
 * @author 风平浪静的明天
 * @date 2021/6/16 10:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {T4CloudCoreApplication.class})
public class RedisUtilTest {

    /**
     * expire测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void expireTest(){
        //设置key-value
        RedisUtil.set("1","2");
        //设置失效时间
        boolean expire = RedisUtil.expire("1", 60);
        //验证
        Assert.assertTrue("设置失效时间失败",expire);
        long expire1 = RedisUtil.getExpire("1");
        Assert.assertTrue("设置失效时间异常",(expire1 < 60));
    }


    /**
     * getExpire测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void getExpireTest(){
        //设置key-value
        RedisUtil.set("1","2");
        //设置失效时间
        RedisUtil.expire("1", 120);
        //获取失效时间
        long expire = RedisUtil.getExpire("1");
        //验证
        if(expire < 0 || expire > 120){
            Boolean flag = false;
            Assert.assertTrue("获取失效时间失败",flag);
        }
    }


    /**
     * hasKey测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void hasKeyTest(){
        //设置key-value
        RedisUtil.set("1","2");
        //获取是否存在
        boolean b = RedisUtil.hasKey("1");
        //验证
        Assert.assertTrue("获取key值失败",b);
        //删除
        RedisUtil.del("1");
    }

    /**
     * del测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void delTest(){
        //设置key-value
        RedisUtil.set("1","2");
        RedisUtil.set("2","3");
        String [] strs= new String[]{"1","2"};
        //删除缓存
        RedisUtil.del(strs);
        //验证
        Object o = RedisUtil.get("1");
        Assert.assertNull("删除缓存失败",o);
    }


    /**
     * 递增测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void incrTest(){
        //设置key-value
        RedisUtil.set("1",1);
        //递增
        long incr = RedisUtil.incr("1", 1);
        //验证
        Assert.assertEquals("递增方法失败",incr,2);
        //删除
        RedisUtil.del("1");
    }


    /**
     * 普通缓存获取测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void getTest(){
        //设置key-value
        RedisUtil.set("1","2");
        //获取
        Object o = RedisUtil.get("1");
        //验证
        Assert.assertEquals("普通缓存获取失败",o.toString(),"2");
        //删除
        RedisUtil.del("1");
    }


    /**
     * 普通缓存放入测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void setTest(){
        //设置key-value
        boolean flag = RedisUtil.set("1", "2");
        //验证
        Assert.assertTrue("普通缓存放入失败",flag);
        //验证
        Object o = RedisUtil.get("1");
        Assert.assertEquals("普通缓存放入异常",o.toString(),"2");
        //删除
        RedisUtil.del("1");
    }

    /**
     * 普通缓存放入并设置时间测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void setTest2(){
        //设置key-value
        boolean flag = RedisUtil.set("1", "2",60);
        //验证
        Assert.assertTrue("放入并设置时间失败",flag);
        //获取失效时间
        long expire = RedisUtil.getExpire("1");
        //验证
        if(expire < 0 || expire > 60){
            flag = false;
            Assert.assertTrue("放入并设置时间异常",flag);
        }
    }

    /**
     * 匹配符合条件的KEY测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void keysTest(){
        //设置key-value
        RedisUtil.set("1", "2");
        RedisUtil.set("11", "2");
        RedisUtil.set("111", "2");
        //获取key匹配的键值对
        Set<String> keys = RedisUtil.keys("1");
        for (String key : keys) {
            //验证
            Assert.assertEquals("匹配符合条件的KEY失败",key,"1");
        }
        //删除
        RedisUtil.del("1");
        RedisUtil.del("11");
        RedisUtil.del("111");
    }

    /**
     * HashGet测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void hgetTest(){
        //设置key-item-value
        RedisUtil.hset("1", "1","2");
        //获取
        Object hget = RedisUtil.hget("1","1");
        //验证
        Assert.assertEquals("HashGet方法测试失败",hget.toString(),"2");
        //删除
        RedisUtil.del("1");
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void hsetTest(){
        //设置key-value
        boolean hset = RedisUtil.hset("1", "1", "2");
        System.out.println("返回结果:" + hset);
        //验证
        Assert.assertTrue("hset方法测试失败",hset);
        //删除
        RedisUtil.del("1");
    }

    /**
     * 获取hashKey对应的所有键值测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void hmgetTest(){
        //设置key-value
        RedisUtil.hset("1", "1", "1");
        RedisUtil.hset("1", "2", "2");
        RedisUtil.hset("1", "3", "3");
        //获取
        Map<Object, Object> map = RedisUtil.hmget("1");
        //验证
        Assert.assertNotNull("获取hashKey对应的所有键值方法测试失败",map);
        //删除
        RedisUtil.del("1");
    }

    /**
     * HashSet测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void hmsetTest(){
        Map<String, Object> map = new HashMap<>();
        map.put("1","1");
        map.put("2","2");
        map.put("3","3");
        //设置键值对
        boolean hmset = RedisUtil.hmset("1", map);
        //验证
        Assert.assertTrue("HashSet方法测试失败",hmset);
        Map<Object, Object> hmget = RedisUtil.hmget("1");
        Assert.assertNotNull("HashSet方法测试异常",hmget);
        //删除
        RedisUtil.del("1");
    }

    /**
     * HashSet 并设置时间测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void hmsetTest2(){
        Map<String, Object> map = new HashMap<>();
        map.put("1","1");
        map.put("2","2");
        map.put("3","3");
        //设置key，map和失效时间
        boolean hmset = RedisUtil.hmset("1", map,60);
        //验证
        Assert.assertTrue("HashSet 并设置时间方法测试失败",hmset);
        long expire = RedisUtil.getExpire("1");
        if(expire < 0 || expire > 60){
            Boolean flag = false;
            Assert.assertTrue("HashSet 并设置时间方法测试异常",flag);
        }
    }


    /**
     * 删除hash表中的值测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void hdelTest(){
        //设置key-item-value
        RedisUtil.hset("1", "1", "1");
        RedisUtil.hset("1", "2", "2");
        RedisUtil.hset("1", "3", "3");
        String [] strs= new String[]{"1","2"};
        //删除
        RedisUtil.hdel("1",strs);
        //验证
        Map<Object, Object> hmget = RedisUtil.hmget("1");
        for(Object key:hmget.keySet()){
            String value = hmget.get(key).toString();
            Assert.assertEquals("删除hash表中的值方法测试失败",key.toString(),"3");
            Assert.assertEquals("删除hash表中的值方法测试失败",value,"3");
        }
        //删除
        RedisUtil.del("1");
    }

    /**
     * 判断hash表中是否有该项的值测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void hHasKeyTest(){
        //设置key-item-value
        RedisUtil.hset("1", "1", "1");
        RedisUtil.hset("1", "2", "2");
        RedisUtil.hset("1", "3", "3");
        //获取
        boolean b = RedisUtil.hHasKey("1", "2");
        //验证
        Assert.assertTrue("判断hash表中是否有该项的值失败",b);
        //删除
        RedisUtil.del("1");
    }

    /**
     * 根据key获取Set中的所有值测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void sGetTest(){
        String [] strs= new String[]{"1","2"};
        //设置key-set
        RedisUtil.sSet("1", strs);
        //获取
        Set<Object> objects = RedisUtil.sGet("1");
        //验证
        Assert.assertNotNull("根据key获取Set中的所有值方法测试失败",objects);
        for (Object object : objects) {
            Boolean flag = false;
            if(object.toString().equals("1") || object.toString().equals("2")){
                flag = true;
                Assert.assertTrue("根据key获取Set中的所有值方法测试异常",flag);
            }
        }
        //删除
        RedisUtil.del("1");
    }


    /**
     * 根据value从一个set中查询,是否存在测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void sHasKeyTest(){
        String [] strs= new String[]{"1","2"};
        //设置key-set
        RedisUtil.sSet("1", strs);
        //获取
        boolean b = RedisUtil.sHasKey("1", "2");
        //验证
        Assert.assertTrue(" 根据value从一个set中查询,是否存在方法测试失败",b);
        //删除
        RedisUtil.del("1");
    }

    /**
     * 将数据放入set缓存测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void sSetTest(){
        String [] strs= new String[]{"1","2"};
        //设置key-set
        long l = RedisUtil.sSet("1", strs);
        //验证,长度为2w
        Assert.assertEquals(" 将数据放入set缓存测试方法失败",l,2);
        //删除
        RedisUtil.del("1");
    }


    /**
     * 将set数据放入缓存测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void sSetAndTimeTest(){
        String [] strs= new String[]{"1","2"};
        //设置key-set和失效时间
        long l = RedisUtil.sSetAndTime("1", 60,strs);
        //验证,长度为2w
        Assert.assertEquals(" 将数据放入set缓存和失效时间测试方法失败",l,2);
        //获取失效时间
        long expire = RedisUtil.getExpire("1");
        //验证
        if(expire < 0 || expire > 60){
            Boolean flag = false;
            Assert.assertTrue(" 将数据放入set缓存和失效时间测试方法异常",flag);
        }
    }


    /**
     * 获取set缓存的长度测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void sGetSetSizeTest(){
        String [] strs= new String[]{"1","2","3"};
        //设置key-set
        RedisUtil.sSet("1", strs);
        //获取
        long l = RedisUtil.sGetSetSize("1");
        //验证,长度为3
        Assert.assertEquals("获取set缓存的长度测试方法失败",l,3);
    }


    /**
     * 移除值为value的测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void setRemoveTest(){
        String [] strs= new String[]{"1","2","3"};
        //设置key-set
        RedisUtil.sSet("1", strs);
        String [] strs1= new String[]{"1","2"};
        //移除
        long l = RedisUtil.setRemove("1",strs1);
        //验证,长度为2
        Assert.assertEquals("获取set缓存的长度测试方法失败",l,2);
        RedisUtil.del("1");
    }
}

