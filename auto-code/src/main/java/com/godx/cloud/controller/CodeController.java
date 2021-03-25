package com.godx.cloud.controller;

import com.godx.cloud.model.CommonResult;
import com.godx.cloud.service.CodeService;
import com.godx.cloud.util.OSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/code")
@Slf4j
public class CodeController {

    @Resource
    private CodeService codeService;

    @Value("${bucket.endpoint}")
    private String endpoint;

    @Value("${server.port}")
    private String port;

    @Test
    @GetMapping("/test")
    public void test(){
        String filePath="/Users/bytedance/Downloads/resetpass.vue";
        String url = OSSUtil.uploadWithLocalFile(filePath);
        log.info(url);
    }

    @Test
    @GetMapping("/test2")
    public void test2(){
        OSSUtil.download("test/2021-03-25/42dca68d460d47c295e145049c7184ee-resetpass.vue","/Users/bytedance/Downloads/test.vue");
    }

    @GetMapping("/mybatiscode")
    public CommonResult getMybatisCode(@RequestParam("tables") List<String> tables,@RequestParam Map<String,Object> request) throws IOException, SQLException {
        String username=(String)request.get("username");
        String password=(String)request.get("password");
        String host=(String)request.get("host");
        String port=(String)request.get("port");
        String database=(String)request.get("database");
        codeService.getMybatisCode(username,password,host,port,database,tables);

        return new CommonResult();
    }
}
