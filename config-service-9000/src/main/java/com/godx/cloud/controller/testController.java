package com.godx.cloud.controller;

import com.godx.cloud.constant.constant;
import com.godx.cloud.model.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RefreshScope
public class testController implements constant {

    @Value("${bucket.endpoint}")
    private String endpoint;

    @Value("${bucket.accessKeyId}")
    private String accessKeyId;

    @Value("${bucket.accessKeySecret}")
    private String accessKeySecret;

    @Value("${bucket.bucketName}")
    private String bucketName;

    @GetMapping("/getConfig")
    public CommonResult test1(){
        log.info(accessKeyId);
        log.info(accessKeySecret);
        return new CommonResult(STATUS_SUC,MESSAGE_OK,endpoint);
    }

}
