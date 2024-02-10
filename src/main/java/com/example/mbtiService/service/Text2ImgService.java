package com.example.mbtiService.service;

import com.alibaba.fastjson.JSONObject;
import com.example.mbtiService.config.StableDiffusionConfig;
import com.example.mbtiService.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.assertj.core.util.Lists;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
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
        List<String> images = stableDiffusionTextToImgResponse.getImages();
        return images;
    }

    /**
     * @description: 构造sd请求体
     * @author: zhiqiang
     * @date: 2024/2/9 21:51
     * @param: String prompt
     * @return: com.example.mbtiService.entity.StableDiffusionTextToImg
     **/
    private StableDiffusionTextToImg getArtisticWordStableDiffusionTextToImg(String prompt) throws IOException {
        final StableDiffusionTextToImg body = StableDiffusionTextToImg.builder()
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
                        .sd_vae("vaeFtMse840000EmaPruned_vae.safetensors")
                        .build())
                .alwayson_scripts(getAlwaysonScripts())
                .cfg_scale(7.0).build();
        return body;
    }

    /**
     * @description: 获取拓展预设【人脸修复】
     * @author: zhiqiang
     * @date: 2024/2/10 19:00
     * @return: com.example.mbtiService.entity.AlwaysonScripts
     **/
    private AlwaysonScripts getAlwaysonScripts() {
        Args args1 = Args.builder().ad_model("person_yolov8n-seg.pt").build();
        Args args2 = Args.builder().ad_model("face_yolov8n_v2.pt").ad_prompt("detail face").build();
        Args args3 = Args.builder().ad_model("hand_yolov8n.pt").build();
        List<Args> args = Lists.newArrayList(args1, args2, args3);
        ADetailer aDetailer = ADetailer.builder().args(args).build();
        AlwaysonScripts alwaysonScripts = AlwaysonScripts.builder().aDetailer(aDetailer).build();
        return alwaysonScripts;
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

    /**
     * @description: 获取调用stable diffusion的返回值
     * @author: zhiqiang
     * @date: 2024/2/10 19:07
     * @param: [response]
     * @return: com.example.mbtiService.entity.StableDiffusionTextToImgResponse
     **/
    private StableDiffusionTextToImgResponse handleResponse(ResponseEntity<JSONObject> response) {
        if (Objects.isNull(response) || !response.getStatusCode().is2xxSuccessful()) {
            log.warn("调用stable diffusion api状态码为: {}",JSONObject.toJSONString(response));
        }

        final JSONObject body = response.getBody();
        if (Objects.isNull(body)) {
            log.error("发送请求失败。响应正文为空");
        }
        return body.toJavaObject(StableDiffusionTextToImgResponse.class);
    }
}