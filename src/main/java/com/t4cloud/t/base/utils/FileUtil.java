package com.t4cloud.t.base.utils;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

@Slf4j
public class FileUtil extends cn.hutool.core.io.FileUtil {

    /**
     * 删除超时文件
     *
     * @param path 指定根目录
     * @param days 超时时间（天）
     *             <p>
     * @return void
     * --------------------
     * @author Qian
     * @date 2021/5/14 2:40 下午
     */
    public static void deleteFiles(String path, int days) {
        // 计算过期时间
        Date date = DateUtil.offsetDay(DateUtil.date(), -days);
        // 设置文件过滤条件
        IOFileFilter timeFileFilter = FileFilterUtils.ageFileFilter(date, true);
        // 删除符合条件的文件
        File root = new File(path);
        Iterator itFile = FileUtils.iterateFiles(root, timeFileFilter, TrueFileFilter.INSTANCE);
        while (itFile.hasNext()) {
            File file = (File) itFile.next();
            log.debug("File deleting:" + file.getPath());
            file.delete();
        }
        // 清理空的文件夹
        String[] folderList = root.list();
        for (String s : folderList) {
            deleteEmptyDir(path + File.separator + s);
        }
    }


    /**
     * 删除文件
     *
     * @param path 文件
     */
    public static void deleteFiles(String path) {
        //读取file对象
        File parent = new File(path);
        //验证文件对象
        if (!parent.exists()) {
            return;
        }
        //如果是文件则直接删除
        if (parent.isFile()) {
            parent.delete();
            return;
        }
        //如果是文件夹，则遍历删除文件夹下的项目
        String[] childList = parent.list();
        for (String childFileName : childList) {
            //获取子目录，递归删除
            String childFilePath = parent.getPath() + File.separator + childFileName;
            deleteFiles(childFilePath);
        }
    }

    /**
     * 清理路径下的空文件夹，如果本身为空也会被删掉
     *
     * @param path 当前路径
     *             <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/8/23 15:58
     */
    public static void deleteEmptyDir(String path) {
        //读取对象
        File file = new File(path);
        //不存在或不是文件夹就不处理了
        if (!file.exists() || !file.isDirectory()) {
            return;
        }
        //检查下一层目录
        String[] fileList = file.list();
        for (String child : fileList) {
            deleteEmptyDir(path + File.separator + child);
        }
        //如果是空文件夹就可以删了
        if (isEmpty(file)) {
            log.debug("Dir deleting:" + path);
            file.delete();
            return;
        }
    }

//    public static void main(String[] args) {
//        FileUtil.deleteFiles("C:\\TeaR-APP", 0);
//    }

}
