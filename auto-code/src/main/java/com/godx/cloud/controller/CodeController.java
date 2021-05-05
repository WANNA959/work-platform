package com.godx.cloud.controller;

import com.alibaba.fastjson.JSON;
import com.godx.cloud.constant.constant;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/code")
@Slf4j
public class CodeController implements constant {

    @Resource
    private CodeService codeService;

    @GetMapping("/mybatiscode")
    public CommonResult getMybatisCode(@RequestParam("tables") String tables, @RequestParam Map<String,Object> request, @RequestHeader("Authorization")String token) throws IOException, SQLException {
        String username=(String)request.get("username");
        String password=(String)request.get("password");
        String host=(String)request.get("host");
        String port=(String)request.get("port");
        String database=(String)request.get("database");

        List<String> tableList = (List<String>) JSON.parse(tables);
        codeService.getMybatisCode(username,password,host,port,database,tableList,token);

        return new CommonResult(STATUS_SUC,MESSAGE_OK);
    }
}
