package com.example.mbtiService.entity.pay;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MapiResponse implements Serializable {
    private int code; // 返回状态码:1为成功，其它值为失败
    private String msg; // 返回信息
    private String trade_no; // 返回信息
    private String payurl; // 支付跳转url
    private String qrcode; // 二维码链接
    private String urlscheme; // 小程序跳转url
}