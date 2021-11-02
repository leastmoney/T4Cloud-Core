package com.t4cloud.t.base.utils;

import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.ZipUtil;
import com.t4cloud.t.base.constant.CacheConstant;
import com.t4cloud.t.base.exception.T4CloudException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Slf4j
@Component
public class ZipUtils {

    //通用后缀名
    final private static String SUFFIX = ".zip";

    //项目路径
    private static String tempDir;

    @Autowired
    private Environment env;

    /**
     * @param filename 压缩后的名称(不带.zip)
     * @param folder   需要打包的文件夹
     * @param path     保存的路径
     * @param del      是否删除源文件
     *                 <p>
     * @return --------------------
     * @description: 保存方法，保存到某个路径下
     * @author: Qian
     * @date: 2021/8/13 13:03
     */
    public static void save(String filename, String folder, String path, boolean del) {
        //开始时间
        long start = System.currentTimeMillis();
        //目标路径
        filename = filename + SUFFIX;
        String pathName = path + "\\" + filename;
        try {
            //处理压缩
            ZipUtil.zip(folder, pathName, true);
            //删除源文件
            if (del) {
                clean(folder);
            }
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        }
        //结束时间
        long end = System.currentTimeMillis();
        log.debug("压缩完成，耗时：" + (end - start) + " ms");
    }


    /**
     * @param name   文件名称(不带.zip)
     * @param folder 需要打包的文件夹
     * @param del    是否删除原zip文件
     *               <p>
     * @return --------------------
     * @description: 下载方法，将文件打包下载
     * @author: Qian
     * @date: 2021/8/13 13:05
     */
    public static void download(String name, String folder, boolean del) {
        HttpServletResponse response = SpringContextUtil.getHttpServletResponse();
        if (null == response) {
            throw new T4CloudException("没有HttpServletResponse对象,无法提供下载");
        }
        //目标路径
        name = name + SUFFIX;
        String pathName = tempDir + "\\" + name;
        //处理压缩
        File zip = ZipUtil.zip(folder, pathName, true);
        //文件流和字符流
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            String encodeFileName = URLUtil.encode(name, "UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + encodeFileName);
            response.addHeader("filename", encodeFileName);
            response.setContentLength((int) zip.length());
            response.setContentType("application/zip");// 定义输出类型
            byte[] buffer = new byte[4096];
            fis = new FileInputStream(pathName);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                    // 删除临时文件
                    zip.delete();
                    //删除源文件
                    if (del) {
                        clean(folder);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 清理根路径，但不删除根路径本身
     *
     * @param path 根路径
     *             <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/8/23 16:18
     */
    private static void clean(String path) {
        File root = new File(path);
        for (String s : root.list()) {
            FileUtil.deleteFiles(path + File.separator + s);
            FileUtil.deleteEmptyDir(path + File.separator + s);
        }
    }

    @PostConstruct
    public void readConfig() {
        tempDir = env.getProperty("t4cloud.temp-dir");
        if (tempDir == null) {
            tempDir = CacheConstant.SYS_TEMP_DIR;
        }
        tempDir += "/zip/";
    }
}
