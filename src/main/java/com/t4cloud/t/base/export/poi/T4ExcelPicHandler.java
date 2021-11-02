package com.t4cloud.t.base.export.poi;

import cn.afterturn.easypoi.handler.impl.ExcelDataHandlerDefaultImpl;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.t4cloud.t.base.constant.CacheConstant;
import com.t4cloud.t.base.exception.T4CloudException;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Random;

/**
 * xcel导出,图片自定义处理
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/4/2 14:13
 */
@Component
public class T4ExcelPicHandler<T> extends ExcelDataHandlerDefaultImpl<T> {

    private static String oss;
    private static String tempDir;
    @Autowired
    private Environment env;

    @PostConstruct
    public void readConfig() {
        oss = env.getProperty("t4cloud.excel.oss");
        if (oss == null) {
            oss = "https://api.cloud.t4cloud.com/T4Cloud-Support/file/view/";
        }

        tempDir = env.getProperty("t4cloud.temp-dir");
        if (tempDir == null) {
            tempDir = CacheConstant.SYS_TEMP_DIR;
        }

        tempDir += "/poi/";
    }

    @Override
    public Object exportHandler(T obj, String name, Object value) {
        //没有图片不处理
        if (value == null || StrUtil.isBlank(value.toString())) {
            return null;
        }

        String fullPath;

        //判断是否外链图片,非外链图片，需要添加路径
        if (!value.toString().startsWith("http")) {
            fullPath = oss + value;
        }else {
            fullPath = value.toString();
        }

        //下载到临时路径
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
            //可以在中括号内加上任何想要替换的字符
            String reg="[\n`~!@#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";
            String imageName = fullPath.replaceAll(reg,"") + "." + rd + "." + ext;
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
        } catch (Exception e) {
            e.printStackTrace();
            value = null;
        }

        return value;
    }

}
