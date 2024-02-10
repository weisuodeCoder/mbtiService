package com.example.mbtiService.controller;

import com.example.mbtiService.entity.Result;
import com.example.mbtiService.entity.ResultCode;
import com.example.mbtiService.service.Text2ImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/img")
public class Text2ImgController {
    @Autowired
    Text2ImgService text2ImgService;

    @RequestMapping("/text2Img")
    @ResponseBody
    @Scope("prototype")
    public Result text2Img(@RequestBody Map<String, Object> map) throws IOException {
        String prompt = (String) map.get("prompt");
        final List<String> images = text2ImgService.generateImages(prompt);
        final Result result = new Result(ResultCode.SUCCESS, images);
        return result;
    }
}