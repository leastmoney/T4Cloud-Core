package com.t4cloud.t.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.t4cloud.t.T4CloudCoreApplication;
import com.t4cloud.t.base.entity.SystemLogRequest;
import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

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
public class commonSetviceTest {

    @Autowired
    private com.t4cloud.t.service.service.IT4CommonService commonService;

    /**
     * countSystemLog 测试方法
     *
     * <p>
     *
     * @return --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void countSystemLogTest() {
        Date now = DateUtil.date();
        DateTime startTime = DateUtil.offsetDay(DateUtil.beginOfDay(now), 0);
        String startDate = DateFormatUtils.format(startTime, "yyyy-MM-dd 00:00:00");
        String endDate = DateFormatUtils.format(now, "yyyy-MM-dd 23:59:59");
        StringBuffer sb = new StringBuffer();
        sb.append("'28cf78459b4cfc1050e8ab13f5b81c00'");
        SystemLogRequest systemLogRequests = commonService.countSystemLog(startDate,endDate,sb.toString());


        Assert.assertNotNull("countSystemLog失败", systemLogRequests);
    }
}
