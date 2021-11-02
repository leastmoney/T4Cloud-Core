package com.t4cloud.t.base.entity;

import lombok.Data;

/**
 * SmsTemplate
 * <p>
 * 短信模板类
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2020/4/8 16:38
 */
@Data
public class SmsTemplate {

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信编码
     */
    private String code;

    /**
     * outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
     */
    private String outId;

}
