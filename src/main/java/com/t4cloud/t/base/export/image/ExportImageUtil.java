package com.t4cloud.t.base.export.image;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.t4cloud.t.base.constant.ResultConstant;
import com.t4cloud.t.base.exception.T4CloudException;
import com.t4cloud.t.base.export.image.entity.ColStyle;
import com.t4cloud.t.base.export.image.entity.ImageConfig;
import com.t4cloud.t.base.export.image.entity.LineStyle;
import com.t4cloud.t.base.utils.SpringContextUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExportImageUtil {
    // ----------------------------------------------- 导出 -----------------------------------------------


    /**
     * 使用默认配置 - 无副标题
     *
     * @param title:    标题
     * @param clazz:    实体类
     * @param dataList: 实体对象集合
     * @return void
     * ------------------
     * @Author mawang
     * @Date 2021/6/2 13:45
     **/
    public static void export(String title, String selectedColKeys, Class clazz, List dataList) {
        export(title, null, selectedColKeys, clazz, dataList, new ImageConfig());
    }

    /**
     * 使用默认配置
     *
     * @param title:    标题
     * @param subTitle: 副标题
     * @param clazz:    实体类
     * @param dataList: 实体对象集合
     * @return void
     * ------------------
     * @Author mawang
     * @Date 2021/6/2 13:45
     **/
    public static void export(String title, String subTitle, String selectedColKeys, Class clazz, List dataList) {
        export(title, subTitle, selectedColKeys, clazz, dataList, new ImageConfig());
    }

    /**
     * 自定义导出图片配置
     *
     * @param title:       标题
     * @param subTitle:    副标题
     * @param clazz:       实体类
     * @param dataList:    实体对象集合
     * @param imageConfig: 自定义配置
     * @return void
     * ------------------
     * @Author mawang
     * @Date 2021/6/2 13:43
     **/
    public static void export(String title, String subTitle, String selectedColKeys, Class clazz, List dataList, ImageConfig imageConfig) {
        //处理所需列属性
        List<ColStyle> propertyList = handleCol(clazz, selectedColKeys);
        //图片绘制 列:propertyList 行:dataList
        BufferedImage image = createImage(title, subTitle, propertyList, dataList, imageConfig);
        //下发图片流
        downLoadImg(title, image);
    }

    // ----------------------------------------------- 私有方法 -----------------------------------------------


    /**
     * 创建一张图片
     *
     * @param title:    标题名称
     * @param subTitle: 副标题名称
     * @param colList:  数据集合
     * @param config:   图片配置
     * @return void
     * ------------------
     * @Author mawang
     * @Date 2021/5/19 18:10
     **/
    private static BufferedImage createImage(String title, String subTitle, List<ColStyle> colList, List rowList, ImageConfig config) {

        //属性个数
        int cellCount = 0;
        for (ColStyle col : colList) {
            cellCount += col.getWidth();
        }
        //行高
        int titleHeight = config.getTitleStyle().getHeight();
        int subTitleHeight = StrUtil.isBlank(subTitle) ? 0 : config.getSubTitleStyle().getHeight();
        int contentLineHeight = config.getContentStyle().getHeight();
        //宽度
        int width = config.getWidth() != 0 ? config.getWidth() : cellCount * 8;
        //高度
        int height = config.getHeight() != 0 ? config.getHeight() : titleHeight + subTitleHeight + contentLineHeight * (rowList.size() + 1);
        //得到图片流
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //得到画笔
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        //初始化（生成大小图片+标题）
        initImg(title, titleHeight, subTitle, width, height, g2, config);
        //画表格,填充属性名称
        drawTableHeand(colList, width, subTitle, g2, config);
        //填充表格值
        fullTableValue(colList, rowList, width, subTitle, g2, config);
        //处理边框
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawRect(1, 1, width - 2, height - 2);

        try {
            // 释放对象
            g2.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bi;
    }


    /**
     * 填充表格行属性内容+渲染行
     *
     * @param propertyList: 列属性集合
     * @param dataList:     行属性结合
     * @param width:        行宽度
     * @param subTitle:     副标题（有无副标题计算起始高度）
     * @param g2:           画笔
     * @param config:       配置信息
     * @return void
     * ------------------
     * @Author mawang
     * @Date 2021/6/1 16:10
     **/

    private static void fullTableValue(List<ColStyle> propertyList, List dataList, int width, String subTitle, Graphics2D g2, ImageConfig config) {
        int startY = StrUtil.isEmpty(subTitle) ? config.getTitleStyle().getHeight() : config.getTitleStyle().getHeight() + config.getSubTitleStyle().getHeight();
        Font contentFont = new Font(config.getContentStyle().getFont(), config.getContentStyle().getFontWeight(), config.getContentStyle().getFontSize());
        g2.setFont(contentFont);
        //开始遍历数据
        for (int i = 0; i < dataList.size(); i++) {
            //COL横线
            doubleLineStyle(config, startY, width, g2, i);
            g2.drawLine(0, startY + config.getContentStyle().getHeight() * (i + 1), width, startY + config.getContentStyle().getHeight() * (i + 1));
            Object obj = dataList.get(i);
            int startX = 0;
            for (ColStyle col : propertyList) {
                //COL竖线
                g2.drawLine(startX * 8, startY + config.getContentStyle().getHeight() * (i + 1), startX * 8, startY + config.getContentStyle().getHeight() * (i + 2));
                String pName = col.getPropertyName();
                double cellWidth = col.getWidth();
                Object prop = ReflectUtil.getFieldValue(obj, pName);
                if (prop != null) {
                    try {

                        //处理字典
                        if (StrUtil.isNotBlank(col.getDict())) {
                            IT4ImgDictHandler dictHandler = SpringContextUtil.getBean(IT4ImgDictHandler.class);
                            prop = dictHandler.toName(col.getDict(), prop);
                        }
                        //处理日期
                        if (col.getFormat() != null) {
                            prop = col.getFormat().format(prop);

                        }
                        //处理图片
                        if (col.getType() == 2) {
                            IT4ImgPicHandler picHandler = SpringContextUtil.getBean(IT4ImgPicHandler.class);
                            prop = picHandler.exportHandler(prop);//有异常就返回图片地址
                            handleImg(config, i, startX, startY, cellWidth, prop, g2);
                            startX += cellWidth;
                            continue;
                        }
                    } catch (Exception e) {
                        //有异常就按原value输出
                        e.printStackTrace();
                    }

                }
                String value = prop == null ? "" : prop.toString();
                float x = getAlign(config, startX, cellWidth, value, contentFont);
                float y = getCenterY(config, startY, i + 1);
                g2.setColor(config.getContentStyle().getFontColor());
                g2.drawString(value, x, y);
                g2.setColor(Color.BLACK);//初始化画笔颜色
                startX += cellWidth;
            }
        }
    }

    /**
     * 计算Y坐标中心位置
     *
     * @param config:      配置类
     * @param startY:      起始Y坐标
     * @param contentLine: 表格内容开始行  表头为0
     * @return int
     * ------------------
     * @Author mawang
     * @Date 2021/6/2 12:54
     **/
    private static float getCenterY(ImageConfig config, int startY, int contentLine) {
        float y = startY + config.getContentStyle().getHeight() * contentLine + config.getContentStyle().getHeight() / 2 + config.getContentStyle().getFontSize() / 2;
        return y;
    }

    /**
     * 计算row中元素X坐标位置
     *
     * @param startX    x坐标起始位置
     * @param cellWidth 单元个宽度
     * @param value     属性值
     * @return
     */
    private static float getAlign(ImageConfig config, int startX, double cellWidth, String value, Font font) {
        //占位长度分中英文
        String reg = "[\u4e00-\u9fa5]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(value);
        String newStr = matcher.replaceAll("");
        String align = config.getContentStyle().getAlign();
        //分是否加粗
        double zhong = 8;
        double english = 4;
        if (LineStyle.FONT_BOLD == font.getStyle()) {
            zhong = 10;
            english = 5.5;
        }
        float x = 0;
        switch (align) {
            case "center"://中间坐标-汉字-英文占位
                x = (float) ((startX * 8 + (cellWidth * 4)) - (value.length() - newStr.length()) * zhong - newStr.length() * english);
                //防止超出边框
                if (startX == 0 && ((value.length() - newStr.length()) * zhong + (newStr.length() * english)) > cellWidth * 4) {
                    x = startX * 8 + 2;
                }
                break;
            case "right"://末位坐标-汉字占位-英文占位
                x = (float) ((startX + cellWidth) * 8 - (value.length() - newStr.length()) * zhong * 2 - newStr.length() * english * 2);
                break;
            default:
                x = startX * 8 + 2;
                break;
        }
        return x;
    }

    /**
     * 处理图片属性
     *
     * @param g2:          画笔
     * @param config:      配置信息
     * @param contentLine: 所在行
     * @param startX:      起始X坐标
     * @param cellWidth:   单位宽度
     * @param imgValue:    当前属性--对象  对应的值
     * @return int
     * ------------------
     * @Author mawang
     * @Date 2021/6/1 16:13
     **/

    private static void handleImg(ImageConfig config, int contentLine, int startX, int startY, double cellWidth, Object imgValue, Graphics2D g2) {
        try {
            BufferedImage content = ImageIO.read(new File(imgValue.toString()));
            int height1 = content.getHeight();
            int width1 = content.getWidth();
            double v1 = (double) width1 / (cellWidth * 8);
            double v2 = (double) height1 / config.getContentStyle().getHeight();
            double v = 0;
            double adjustX = 1;//防止压到线
            double adajustY = 1;
            if (v1 > v2) {
                v = v1;
                adajustY = (config.getContentStyle().getHeight() - (height1 / v1)) / 2;
            } else {
                v = v2;
                adjustX = ((cellWidth * 8) - (width1 / v2)) / 2;
            }
            g2.drawImage(content.getScaledInstance((int) (width1 / v), (int) (height1 / v), Image.SCALE_DEFAULT), (int) (startX * 8 + adjustX), (int) (((contentLine + 1) * config.getContentStyle().getHeight() + startY) + adajustY), null);
        } catch (IOException e) {
            throw new T4CloudException("图片读取错误");
        }
    }

    /**
     * 初始化图片，填充标题信息
     * ------------------
     *
     * @Author mawang
     * @Date 2021/5/18 14:07
     **/
    private static void initImg(String title, int titleHeight, String subTitle, int width, int height, Graphics2D g2, ImageConfig config) {
        // 设置背景颜色
        g2.setColor(Color.WHITE);
        // 填充整张图片(其实就是设置背景颜色)
        g2.fillRect(0, 0, width, height);
        // 设置字体颜色
        g2.setColor(Color.black);
        // 从上到下第二个横线(标题下面横线)
        g2.drawLine(0, titleHeight, width, titleHeight);
        // 边框不需要加粗
        g2.setStroke(new BasicStroke(0.0f));
        //生成标题
        Font titleFont = new Font(config.getTitleStyle().getFont(), config.getTitleStyle().getFontWeight(), config.getTitleStyle().getFontSize());
        g2.setFont(titleFont);
        // 抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //计算X点坐标
        int titleWidthX = titleAlign(title, width, config.getTitleStyle().getAlign(), titleFont, config.getTitleStyle().getFontSize(), g2);
        if (config.getTitleStyle().getColor() != null) {
            // 设置背景颜色
            g2.setColor(config.getTitleStyle().getColor());
            g2.fillRect(0, 0, width, config.getTitleStyle().getHeight());
        }
        g2.setColor(config.getTitleStyle().getFontColor());
        g2.drawString(title, titleWidthX, config.getTitleStyle().getHeight() / 2 + config.getTitleStyle().getFontSize() / 2);
        //生成副标题
        if (StrUtil.isBlank(subTitle)) {
            // 恢复画笔颜色
            g2.setColor(Color.black);
            return;
        }
        int startY = StrUtil.isEmpty(subTitle) ? config.getTitleStyle().getHeight() : config.getTitleStyle().getHeight() + config.getSubTitleStyle().getHeight();
        if (config.getSubTitleStyle().getColor() != null) {
            // 设置背景颜色
            g2.setColor(config.getSubTitleStyle().getColor());
            g2.fillRect(0, startY + 1, width, config.getSubTitleStyle().getHeight() - 1);
        }
        //生成副标题
        Font subTitleFont = new Font(config.getSubTitleStyle().getFont(), config.getSubTitleStyle().getFontWeight(), config.getSubTitleStyle().getFontSize());
        int subTitleX = titleAlign(subTitle, width, config.getSubTitleStyle().getAlign(), subTitleFont, config.getSubTitleStyle().getFontSize(), g2);
        g2.setColor(config.getSubTitleStyle().getFontColor());
        g2.drawString(subTitle, subTitleX, config.getTitleStyle().getHeight() + config.getSubTitleStyle().getHeight() - config.getSubTitleStyle().getFontSize());
        // 恢复画笔颜色
        g2.setColor(Color.black);
        g2.drawLine(0, startY, width, startY);
    }

    /**
     * 标题X坐标计算
     *
     * @param title    标题（主副）
     * @param width    行宽度
     * @param align    主/副标题 ALING属性值
     * @param font     字体样式
     * @param fontSize 字体大小
     * @param g2       画笔
     * @return
     */
    private static int titleAlign(String title, int width, String align, Font font, int fontSize, Graphics2D g2) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics(font);
        int titleWidth = fm.stringWidth(title);
        int titleX = 0;
        if (LineStyle.ALIGN_CENTER.equalsIgnoreCase(align)) {
            titleX = (width - titleWidth) / 2 - 35;
        }
        if (LineStyle.ALIGN_RIGHT.equalsIgnoreCase(align)) {
            //这里未计算中英文
            titleX = width - title.length() * fontSize;
        }
        return titleX;
    }

    /**
     * 双行模式
     *
     * @param config: 图片配置类
     * @param startY: 渲染背景起始高度
     * @param width:  渲染背景所需宽度
     * @param g2:     画布
     * @param line:   所在行
     * @return void
     * ------------------
     * @Author mawang
     * @Date 2021/6/1 15:50
     **/
    private static void doubleLineStyle(ImageConfig config, int startY, int width, Graphics2D g2, int line) {
        if (config.getDoubleContentStyle() == null) {
            return;
        }
        //横线(此处是算上头行的，因此要-1）
        if ((line - 1) % 2 == 0) {
            // 设置背景颜色
            g2.setColor(config.getDoubleContentStyle().getColor());
            g2.fillRect(0, startY + config.getContentStyle().getHeight() * (line + 1) + 1, width, config.getContentStyle().getHeight());
            g2.setColor(Color.black);
        }
    }


    /**
     * 绘制表格head表头
     *
     * @param rowList:  行属性值集合
     * @param imgWidth: 图片宽度
     * @param subTitle: 副标题
     * @param g2:       画笔
     * @param config:   图片配置
     * @return void
     * ------------------
     * @Author mawang
     * @Date 2021/6/2 16:04
     **/
    private static void drawTableHeand(List<ColStyle> rowList, int imgWidth, String subTitle, Graphics2D g2, ImageConfig config) {
        Font font = new Font(config.getContentStyle().getFont(), 1, config.getContentStyle().getFontSize() + 5);
        g2.setFont(font);
        int startY = StrUtil.isEmpty(subTitle) ? config.getTitleStyle().getHeight() : config.getTitleStyle().getHeight() + config.getSubTitleStyle().getHeight();
        double width = 0;
        int startX = 0;
        for (ColStyle row : rowList) {
            width = row.getWidth();
            //计算居中坐标
            float x = getAlign(config, startX, width, row.getName(), font);
            float y = getCenterY(config, startY, 0);
            g2.drawLine(startX * 8, startY, startX * 8, startY + config.getContentStyle().getHeight());
            g2.setColor(config.getContentStyle().getFontColor());//初始化画笔颜色
            g2.drawString(row.getName(), x, y);
            g2.setColor(Color.BLACK);
            startX += width;
        }
        g2.drawLine(0, startY + config.getContentStyle().getHeight(), imgWidth, startY + config.getContentStyle().getHeight());
    }


    private static void handleDateFormat(ColStyle colStyle, Excel excel, Field field) {


        if (!field.getType().getName().equals("java.util.Date")) {
            return;
        }
        String format = excel == null ? "" : excel.format();
        //准备具体时间格式
        SimpleDateFormat simpleDateFormat = null;
        try {
            if (StrUtil.isNotBlank(format)) {
                simpleDateFormat = new SimpleDateFormat(format);
            } else if (field.getAnnotation(JsonFormat.class) != null && StrUtil.isNotBlank(field.getAnnotation(JsonFormat.class).pattern())) {
                simpleDateFormat = new SimpleDateFormat(field.getAnnotation(JsonFormat.class).pattern());
                //补充时区
                if (StrUtil.isNotBlank(field.getAnnotation(JsonFormat.class).timezone())) {
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone(field.getAnnotation(JsonFormat.class).timezone()));
                }
            } else if (field.getAnnotation(DateTimeFormat.class) != null && StrUtil.isNotBlank(field.getAnnotation(DateTimeFormat.class).pattern())) {
                simpleDateFormat = new SimpleDateFormat(field.getAnnotation(DateTimeFormat.class).pattern());
            } else {
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            }
            colStyle.setFormat(simpleDateFormat);
        } catch (Exception e) {
            //时间戳优化转换异常
        }
    }


    /**
     * 返回图片流
     *
     * @param fileName 图片名
     * @param img      图片对象
     */
    private static void downLoadImg(String fileName, BufferedImage img) {
        try {
            HttpServletResponse response = SpringContextUtil.getHttpServletResponse();
            String encodeFileName = URLUtil.encode(fileName + ".png", "UTF-8");
            response.setContentType("image/png;application/force-download;charset=UTF-8;");
            response.addHeader(ResultConstant.ACCESS_CONTROL_EXPOSE_HEADERS, ResultConstant.FILENAME);
            response.addHeader(ResultConstant.FILENAME, encodeFileName);
            response.addHeader("Content-Disposition", "attachment;filename=" + encodeFileName);
            ImageIO.write(img, "PNG", response.getOutputStream());
        } catch (IOException var3) {
            throw new T4CloudException("导出图片失败");
        }
    }

    /**
     * 处理列属性
     *
     * @param clazz: 传入的类class
     * @return java.util.List<ColStyle>
     * ------------------
     * @Author mawang
     * @Date 2021/6/1 16:08
     **/
    private static List<ColStyle> handleCol(Class clazz, String selectedColKeys) {

        List<ColStyle> list = new ArrayList<>();
        //遍历实体类属性
        for (Field field : ReflectUtil.getFields(clazz)) {
            Excel excel = field.getAnnotation(Excel.class);
            ColStyle colStyle = new ColStyle();
            if (selectedColKeys != null) {
                //选中属性的”_dict“要去除
                selectedColKeys = selectedColKeys.replace("_dict", "");
                List<String> selected = Arrays.asList(selectedColKeys.split(","));
                if (!selected.contains(field.getName())) {
                    continue;
                }
            } else {//没有选中的时候还是要只有excel的
                if (excel == null) {
                    continue;
                }
            }
            //无Excel注解，使用默认
            if (excel == null) {
                //属性名称用ApiModelProperty注解
                ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                String value = annotation == null ? field.getName() : annotation.value();
                colStyle.setName(value)
                        .setOrder(list.size())
                        .setType(1)
                        .setWidth(30)//防止长度不够，还是默认长一点
                        .setPropertyName(field.getName());
            } else {
                //装载需要的属性对象
                colStyle.setName(excel.name())
                        .setOrder(Integer.parseInt(excel.orderNum()))
                        .setType(excel.type())
                        .setWidth((int) excel.width())
                        .setDict(excel.dict())
                        .setPropertyName(field.getName());

            }

            //日期类型设置format
            handleDateFormat(colStyle, excel, field);
            list.add(colStyle);
        }
        //排序
        list = list.stream().sorted(Comparator.comparing(ColStyle::getOrder)).collect(Collectors.toList());
        return list;
    }

}
