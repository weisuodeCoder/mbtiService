package com.example.mbtiService.entity.pay;

import com.example.mbtiService.config.PayConfig;
import com.example.mbtiService.utils.EncryptUtil;
import com.example.mbtiService.utils.IdWorker;
import com.example.mbtiService.utils.MD5Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

/**
 * @description: API接口支付参数类
 * @author: zhiqiang
 * @date: 2024/2/16 6:39
 **/
@Data
public class Mapi {
    private final int pid; // 商户id
    private final String out_trade_no = Long.toString(IdWorker.nextId()); // 商户订单号
    private final String notify_url ; // 异步通知地址
    private final String name; // 商品名称
    private final String money; // 商品金额
    private final String clientip; // 用户IP地址
    private final String type; // 支付方式
    private final String device; // 设备类型
    private String sign; // 签名字符串
    private final String sign_type; // 签名类型

    public Mapi(PayConfig payConfig,String type, String name, String money, String clientip) {
        System.out.println(payConfig);
        this.pid = payConfig.getPid();
        this.notify_url = payConfig.getNotifyUrl();
        this.device = payConfig.getDevice();
        this.sign_type = payConfig.getSignType();
        this.type = type;
        this.name = name;
        this.money = money;
        this.clientip = clientip;
    }

    public String generateSign(PayConfig payConfig) throws IllegalAccessException {
        // 创建一个树形图来存储按字段名排序的参数
        Map<String, String> paramMap = new TreeMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(this);
            if (value != null && !field.getName().equals("sign") && !field.getName().equals("sign_type") && !field.getName().equals("payConfig")) {
                paramMap.put(field.getName(), value.toString());
            }
        }

        // 将参数连接成URL查询格式
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (query.length() > 0) {
                query.append("&");
            }
            query.append(entry.getKey()).append("=").append(entry.getValue());
        }
        // 添加密钥
        String signSource = query + payConfig.getKey();
        // 加密工具
//        EncryptUtil encryptUtil = EncryptUtil.getInstance();
        MD5Utils md5Utils = new MD5Utils();
        // 计算MD5哈希
//        return  encryptUtil.MD5(signSource);
        return md5Utils.string2MD5(signSource);
    }
}