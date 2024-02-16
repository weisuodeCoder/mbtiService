package com.example.mbtiService.service;

import com.alibaba.fastjson.JSONObject;
import com.example.mbtiService.config.PayConfig;
import com.example.mbtiService.entity.StableDiffusionTextToImg;
import com.example.mbtiService.entity.StableDiffusionTextToImgResponse;
import com.example.mbtiService.entity.pay.Mapi;
import com.example.mbtiService.entity.pay.MapiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class PayService {
//    @Autowired
    private final PayConfig payConfig;

    public PayService(PayConfig payConfig) {
        this.payConfig = payConfig;
    }

    public MapiResponse getPayUrl(Map<String, Object> map,String ipAddress) throws IllegalAccessException, UnsupportedEncodingException {
        String type = (String) map.get("type");
        String name = (String) map.get("name");
        String money = (String) map.get("money");
        Mapi mapi = new Mapi(payConfig,type, name, money,ipAddress);
        // 添加计算的MD5值
        mapi.setSign(mapi.generateSign(payConfig));
        return callPayApi(mapi);
    }

    /**
     * @description: 调用mapi接口
     * @author: zhiqiang
     * @date: 2024/2/16 7:29
     * @param: [body]
     * @return: com.example.mbtiService.entity.pay.MapiResponse
     **/
    private MapiResponse callPayApi(Mapi body) throws IllegalAccessException, UnsupportedEncodingException {
        StringBuffer paramsUrl = new StringBuffer(payConfig.getBaseUrl() + "/mapi.php");
        paramsUrl.append("?");
        Field[] fields = body.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String value = field.get(body).toString();
            if(value != null) {
                paramsUrl.append(field.getName() + "=" + URLEncoder.encode(value,"UTF-8") + "&");
            }
        }
        paramsUrl.deleteCharAt(paramsUrl.length() - 1);
        System.out.println(paramsUrl);
        URI uri = URI.create(paramsUrl.toString());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Mapi> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<JSONObject> entity = restTemplate.postForEntity(uri, requestEntity, JSONObject.class);
        final MapiResponse mapiResponse = handleResponse(entity);
        return mapiResponse;
    }

    /**
     * @description:获取调用Mapi结果的返回值
     * @author: zhiqiang
     * @date: 2024/2/16 7:29
     * @param: [response]
     * @return: com.example.mbtiService.entity.pay.MapiResponse
     **/
    private MapiResponse handleResponse(ResponseEntity<JSONObject> response) {
        if (Objects.isNull(response) || !response.getStatusCode().is2xxSuccessful()) {
            log.warn("调用stable diffusion api状态码为: {}",JSONObject.toJSONString(response));
        }

        final JSONObject body = response.getBody();
        if (Objects.isNull(body)) {
            log.error("发送请求失败。响应正文为空");
        }
        return body.toJavaObject(MapiResponse.class);
    }
}