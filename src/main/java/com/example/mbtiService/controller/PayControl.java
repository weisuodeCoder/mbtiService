package com.example.mbtiService.controller;

import com.example.mbtiService.entity.Result;
import com.example.mbtiService.entity.ResultCode;
import com.example.mbtiService.entity.pay.MapiResponse;
import com.example.mbtiService.service.PayService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayControl {
//    @Autowired
    private final PayService payService;

     public PayControl(PayService payService) {
         this.payService = payService;
     }
    /**
     * @description: 获取支付地址
     * @author: zhiqiang
     * @date: 2024/2/16 5:55
     * @param: [map]
     * @return: com.example.mbtiService.entity.Result
     **/
    @RequestMapping("/getPayUrl")
    @ResponseBody
    @Scope("prototype")
    public Result getPayUrl(@RequestBody Map<String, Object> map, HttpServletRequest request) throws IllegalAccessException, UnsupportedEncodingException {
        final String ipAddress = request.getRemoteAddr();
        MapiResponse payUrl = payService.getPayUrl(map, ipAddress);
        final Result result = new Result(ResultCode.SUCCESS,payUrl);
        System.out.println("result===============");
        System.out.println(result);
        return result;
    }
}