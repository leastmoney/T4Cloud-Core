package com.t4cloud.t.base.export.image;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.t4cloud.t.base.exception.T4CloudException;
import com.t4cloud.t.base.utils.SpringContextUtil;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.core.env.Environment;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Random;

public interface IT4ImgPicHandler {

    /**
     * 图片导出
     *
     * @param value <p>
     * @return java.lang.Object
     * --------------------
     * @author mw
     * @date 2021/6/23 3:40 下午
     */
    default Object exportHandler(Object value) {
        Environment env = SpringContextUtil.getBean(Environment.class);

        String oss = env.getProperty("t4cloud.excel.oss");
        if (oss == null) {
            oss = "https://api.cloud.t4cloud.com/T4Cloud-Support/file/view/";
        }

        String tempDir = env.getProperty("t4cloud.temp-dir");
        if (tempDir == null) {
            tempDir = "/tmp/t4cloud";
        }

        tempDir = tempDir + "/poi/";
        String fullPath = "";

        //没有图片不处理
        if (value == null || StrUtil.isBlank(value.toString())) {
            return null;
        }

        //处理是否未http开头
        if (!value.toString().startsWith("http")) {
            fullPath = oss + value;
        } else {
            fullPath = value.toString();
        }

        URL url = null;
        try {
            url = new URL(fullPath);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            String contentType = url.openConnection().getContentType();
            if (contentType.contains(";")) {
                contentType = contentType.split(";")[0];
            }

            MimeType mimeType = MimeTypes.getDefaultMimeTypes().forName(contentType);
            String ext = mimeType.getExtension();
            if (StrUtil.isBlank(ext)) {
                throw new T4CloudException("无法识别资源后缀名：" + url.openConnection().getContentType());
            }

            Random random = new Random();
            int rd = random.nextInt(9999);
            String reg = "[\n`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";
            String imageName = fullPath.replaceAll(reg, "") + "." + rd + "." + ext;
            String dir = tempDir + DateUtil.year(DateUtil.date()) + "/" + DateUtil.month(DateUtil.date()) + "/" + DateUtil.dayOfMonth(DateUtil.date());
            String path = dir + "/" + imageName;
            FileUtil.mkdir(dir);
            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            byte[] buffer = new byte[1024];

            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }

            dataInputStream.close();
            fileOutputStream.close();
            value = path;
        } catch (Exception var18) {
            var18.printStackTrace();
            value = null;
        }

        return value;

    }

}
