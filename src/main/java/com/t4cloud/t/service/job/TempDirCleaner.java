package com.t4cloud.t.service.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.t4cloud.t.base.constant.CacheConstant;
import com.t4cloud.t.base.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * 定时删除tmp-dir文件中的关的资源，根目录不会被删除，过期文件保留7天。空文件夹会请掉
 * 定时任务启动时间为每天的凌晨1点启动
 *
 * @author 风平浪静的明天
 * @return void
 * @date 2021/5/12 14:06
 * @descriptions
 **/
@Slf4j
@Component
public class TempDirCleaner {

    private static String deleteTempDir;
    private static Integer deleteTempClean;
    @Autowired
    private Environment env;

    @PostConstruct
    public void readConfig() {
        deleteTempDir = env.getProperty("t4cloud.temp-dir");
        if (deleteTempDir == null) {
            deleteTempDir = CacheConstant.SYS_TEMP_DIR;
        }
        deleteTempClean = env.getProperty("t4cloud.temp-clean", Integer.class);
        if (deleteTempClean == null) {
            deleteTempClean = CacheConstant.SYS_TEMP_CLEAN;
        }
    }

    @Scheduled(cron = "0 0 1 1/1 * ? ")
//    @Scheduled(cron="0/10 * * * * ? ")
    private void TempDirCleaner() {

        //无需清理缓存目录
        if (StrUtil.isBlank(deleteTempDir) || deleteTempClean == null || deleteTempClean <= 0) {
            return;
        }

        //任务开始
        log.info(String.format("缓存目录清理，开始：%s", DateUtil.now()));

        try {
            FileUtil.deleteFiles(deleteTempDir, deleteTempClean);
        } catch (Exception e) {
            log.error("缓存目录清理异常:", e);
        }

        //任务结束
        log.info(String.format("缓存目录清理，结束：%s", DateUtil.now()));
    }
}
