package com.t4cloud.t.base.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.t4cloud.t.base.entity.SmsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 阿里短信工具类
 *
 * @author TeaR
 * @return --------------------
 * @date 2020/4/8 16:27
 */
@Slf4j
@Component
public class SmsUtil {

    /**
     * 产品名称:云通信短信API产品,开发者无需替换
     **/
    static final String product = "Dysmsapi";

    /**
     * 产品域名,开发者无需替换
     */
    static final String domain = "dysmsapi.aliyuncs.com";
    static String accessKeyId;
    static String accessKeySecret;
    @Autowired
    private Environment env;

    public static boolean sendSms(String phone, JSONObject paramJson, SmsTemplate template) throws ClientException {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(template.getSignName());
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(template.getCode());
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(paramJson.toJSONString());

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        if (StrUtil.isNotBlank(template.getOutId())) {
            request.setOutId(template.getOutId());
        }

        boolean result = false;

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        log.info("短信接口返回的数据----------------");
        log.info("{Code:" + sendSmsResponse.getCode() + ",Message:" + sendSmsResponse.getMessage() + ",RequestId:" + sendSmsResponse.getRequestId() + ",BizId:" + sendSmsResponse.getBizId() + "}");
        if ("OK".equals(sendSmsResponse.getCode())) {
            result = true;
        }
        return result;

    }

    @PostConstruct
    public void readConfig() {
        accessKeyId = env.getProperty("t4cloud.sms.access-key");
        accessKeySecret = env.getProperty("t4cloud.sms.access-secret");
        if (StrUtil.isBlank(accessKeyId) || StrUtil.isBlank(accessKeySecret)) {
            log.debug("无短信配置，短信工具类暂不可用");
        }
    }

//    public static void main(String[] args) throws ClientException, InterruptedException {
//        JSONObject obj = new JSONObject();
//        obj.put("admin", "TeaR");
//        obj.put("realname", "周T4Cloud");
//        SmsTemplate template = new SmsTemplate();
//        template.setCode("SMS_185846149");
//        template.setSignName("隆秦博思");
//        sendSms("17821601874",obj, template);
//    }
}
