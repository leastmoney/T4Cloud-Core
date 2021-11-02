package com.t4cloud.t.base.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.t4cloud.t.base.exception.T4CloudException;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * 图片处理 工具类
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/7/10 15:05
 */
public class ImgUtil extends cn.hutool.core.img.ImgUtil {

    /**
     * 将Base字符串转图片文件
     *
     * @param base64 图片的Base64编码
     *               <p>
     * @return java.io.File
     * --------------------
     * @author TeaR
     * @date 2021/1/14 16:54
     */
    public static File base64ToFile(String base64) {
        if (base64 == null || "".equals(base64)) {
            return null;
        }

        //处理头信息和多余符号
        base64 = base64.replace("data:image/png;base64,", EMPTY)
                .replace("data:image/jpg;base64,", EMPTY)
                .replace("data:image/jpeg;base64,", EMPTY)
                .replace("\r\n", EMPTY);

        byte[] buff = Base64.decode(base64);
        File file = null;
        FileOutputStream fout = null;
        try {
            file = File.createTempFile("tmp", null);
            fout = new FileOutputStream(file);
            fout.write(buff);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 将网络图片转换成Base64编码字符串
     *
     * @param imgUrl 网络图片Url
     *               <p>
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2021/1/14 16:54
     */
    public static String imgUrlToBase64(String imgUrl) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        byte[] buffer = null;
        try {
            // 创建URL
            URL url = new URL(imgUrl);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            inputStream = conn.getInputStream();
            outputStream = new ByteArrayOutputStream();
            // 将内容读取内存中
            buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            buffer = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    // 关闭inputStream流
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // 关闭outputStream流
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对字节数组Base64编码
        return new BASE64Encoder().encode(buffer);
    }

    /**
     * 确认base串的合法性
     *
     * @param pic 图片的base64串
     *            <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/3/5 4:39 下午
     */
    public static void checkBasePic(String pic) {

        if (!StrUtil.startWith(pic, "data:image/png;base64,")
                && !StrUtil.startWith(pic, "data:image/jpg;base64,")
                && !StrUtil.startWith(pic, "data:image/jpeg;base64,")) {

            throw new T4CloudException("base64图片格式异常，请确保为 data:image/png;base64 或 data:image/jpg;base64");
        }

    }

    /**
     * 获取BasePic的图片格式
     *
     * @param pic 图片的base64串
     *            <p>
     * @return java.lang.String
     * --------------------
     * @author TeaR
     * @date 2021/3/5 4:45 下午
     */
    public static String suffixBasePic(String pic) {

        try {

            //确认字符串合法性
            checkBasePic(pic);

            //获取后缀名
            String header = pic.split(";")[0];
            String suffix = header.split("/")[1];

            return StrUtil.isBlank(suffix) ? ".jpg" : "." + suffix;

        } catch (Exception e) {
            return ".jpg";
        }

    }

//    public static void main(String[] args) {
//        String base64 = ImgUtil.imgUrlToBase64("https://api.yemoo.t4cloud.com/T4Cloud-Support/file/view/0573424fb551e03285eb341b037547ce");
//        System.out.println(base64);
//    }

}
