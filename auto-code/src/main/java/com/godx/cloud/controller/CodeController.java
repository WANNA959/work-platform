package com.godx.cloud.controller;

import com.godx.cloud.constant.constant;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.service.CodeService;
import com.godx.cloud.util.OSSUtil;
import com.godx.cloud.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
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
    public CommonResult getMybatisCode(@RequestParam("tables") List<String> tables, @RequestParam Map<String,Object> request, @RequestHeader("Authorization")String token) throws IOException, SQLException {
        String username=(String)request.get("username");
        String password=(String)request.get("password");
        String host=(String)request.get("host");
        String port=(String)request.get("port");
        String database=(String)request.get("database");
        codeService.getMybatisCode(username,password,host,port,database,tables,token);

        return new CommonResult(STATUS_SUC,MESSAGE_OK);
    }
}
