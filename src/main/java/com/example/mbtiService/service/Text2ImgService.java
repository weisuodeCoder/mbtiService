package com.example.mbtiService.service;

import com.alibaba.fastjson.JSONObject;
import com.example.mbtiService.config.StableDiffusionConfig;
import com.example.mbtiService.entity.OverrideSettings;
import com.example.mbtiService.entity.StableDiffusionTextToImg;
import com.example.mbtiService.entity.StableDiffusionTextToImgResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class Text2ImgService {
    @Autowired
    StableDiffusionConfig stableDiffusionConfig;

    /**
     * @description: 构造sd的返回值
     * @author: zhiqiang
     * @date: 2024/2/9 22:11
     * @param: [prompt]
     * @return: com.example.mbtiService.entity.StableDiffusionTextToImgResponse
     **/
    public List<String> generateImages(String prompt)  throws IOException {
        StableDiffusionTextToImg body = getArtisticWordStableDiffusionTextToImg(prompt);
        StableDiffusionTextToImgResponse stableDiffusionTextToImgResponse = callSdApi(body);
        final List<String> images = stableDiffusionTextToImgResponse.getImages();
        return images;
    }

    /**
     * @description: 构造sd请求体
     * @author: zhiqiang
     * @date: 2024/2/9 21:51
     * @param: []
     * @return: com.example.mbtiService.entity.StableDiffusionTextToImg
     **/
    private StableDiffusionTextToImg getArtisticWordStableDiffusionTextToImg(String prompt) throws IOException {

        String vae = "vaeFtMse840000EmaPruned_vae.safetensors";
        StableDiffusionTextToImg body = StableDiffusionTextToImg.builder()
                .sampler_name("")
                .prompt("<lora:MBTI-charaters:0.8>,"+prompt)
                .negative_prompt("EasyNegative, paintings, sketches, lowres, normal quality, skin spots, acnes, skin blemishes, age spot, glans,extra fingers,fewer fingers,strange fingers,bad hand,backlight, watermark, logo, bad anatomy,lace,rabbit,back,")
                .sampler_index("DPM++ 2M Karras")
                .seed(-1)
                .width(512)
                .height(512)
                .restore_faces(false)
                .tiling(false)
                .clip_skip(2)
                .batch_size(4)
                .steps(28)
                .override_settings(OverrideSettings.builder()
                        .sd_model_checkpoint("darkSushiMixMix_225D.safetensors")
                        .sd_vae(vae)
                        .build())
                .cfg_scale(7.0).build();
        return body;
    }

    /**
     * @description: 调用sd接口
     * @author: zhiqiang
     * @date: 2024/2/9 21:58
     * @param: [body]
     * @return: java.util.List<java.lang.String>
     **/
    private StableDiffusionTextToImgResponse callSdApi(StableDiffusionTextToImg body) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<StableDiffusionTextToImg> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> entity = restTemplate.postForEntity(stableDiffusionConfig.getApi() + "/sdapi/v1/txt2img", requestEntity, JSONObject.class);
        final StableDiffusionTextToImgResponse stableDiffusionTextToImgResponse = handleResponse(entity);
        return stableDiffusionTextToImgResponse;
    }

    private StableDiffusionTextToImgResponse handleResponse(ResponseEntity<JSONObject> response) {
        if (Objects.isNull(response) || !response.getStatusCode().is2xxSuccessful()) {
            System.out.println("call stable diffusion api status code: {}"+JSONObject.toJSONString(response));
        }

        final JSONObject body = response.getBody();
        if (Objects.isNull(body)) {
            System.out.println("send request failed. response body is empty");
        }
        return body.toJavaObject(StableDiffusionTextToImgResponse.class);
    }
}