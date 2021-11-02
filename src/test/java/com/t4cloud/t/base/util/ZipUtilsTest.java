package com.t4cloud.t.base.util;

import com.t4cloud.t.T4CloudCoreApplication;
import com.t4cloud.t.base.utils.ZipUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @description: 文件zip压缩单元测试
 *
 * @return
 * --------------------
 * @author: Qian
 * @date: 2021/8/18 15:47
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {T4CloudCoreApplication.class})
public class ZipUtilsTest {

    /**
     * @description: 压缩方法，是否删除源文件
     *
     * @return
     * --------------------
     * @author: Qian
     * @date: 2021/8/18 15:48
     */
    @Test
    public void saveTest(){
     ZipUtils.save("03","C:\\mytest","C:\\mytest2",true);
    }
}
