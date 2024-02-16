package com.example.mbtiService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: 易支付配置参数
 * @author: zhiqiang
 * @date: 2024/2/16 6:40
 **/
@Data
@Component
@ConfigurationProperties(prefix = "pay")
public class PayConfig {
    private String baseUrl; // 接口地址
    private int pid; // 商户ID
    private String notifyUrl; // 异步通知地址
    private String signType; // 签名类型
    private String device; // 设备类型
    private String key; // 商户密钥
}