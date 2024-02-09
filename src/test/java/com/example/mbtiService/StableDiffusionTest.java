package com.example.mbtiService;

import com.alibaba.fastjson.JSONObject;
import com.example.mbtiService.entity.OverrideSettings;
import com.example.mbtiService.entity.StableDiffusionTextToImg;
import com.example.mbtiService.entity.StableDiffusionTextToImgResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;

@Slf4j
public class StableDiffusionTest {

    @Test
    public void testSdApi() throws IOException {
        log.info("开始....");
//        StableDiffusionTextToImg body = getArtisticWordStableDiffusionTextToImg();
//        final List<String> images = callSdApi(body);
//        for (String image : images) {
//            writeBase642ImageFile(image, String.format("./%s.png", UUID.randomUUID().toString().replaceAll("-", "")));
//        }
//        log.info("结束....");
    }

    public static void writeBase642ImageFile(String image, String fileName) {
        try (OutputStream outputStream = new FileOutputStream(fileName)) {
            byte[] imageBytes = Base64.getDecoder().decode(image);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("图片写入成功");
            log.info("图片写入成功！");
        } catch (IOException e) {
            System.out.println("图片写入失败");
            e.printStackTrace();
        }
    }

    private StableDiffusionTextToImg getArtisticWordStableDiffusionTextToImg() throws IOException {

        String vae = "vaeFtMse840000EmaPruned_vae.safetensors";
        StableDiffusionTextToImg body = StableDiffusionTextToImg.builder()
                .sampler_name("")
                .prompt("1gril, <lora:MBTI-charaters:0.8>,glasses")
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

    private List<String> callSdApi(StableDiffusionTextToImg body) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<StableDiffusionTextToImg> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> entity = restTemplate.postForEntity("http://127.0.0.1:7860/sdapi/v1/txt2img", requestEntity, JSONObject.class);
        final StableDiffusionTextToImgResponse stableDiffusionTextToImgResponse = handleResponse(entity);
        final List<String> images = stableDiffusionTextToImgResponse.getImages();

        if (CollectionUtils.isEmpty(images)) {
            log.info("empty images");
            return Lists.newArrayList();
        }

        return images;
    }


    private StableDiffusionTextToImgResponse handleResponse(ResponseEntity<JSONObject> response) {
        if (Objects.isNull(response) || !response.getStatusCode().is2xxSuccessful()) {
            log.warn("call stable diffusion api status code: {}", JSONObject.toJSONString(response));
        }

        final JSONObject body = response.getBody();
        if (Objects.isNull(body)) {
            log.error("send request failed. response body is empty");
        }
        return body.toJavaObject(StableDiffusionTextToImgResponse.class);
    }
}