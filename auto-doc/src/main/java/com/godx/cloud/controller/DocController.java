package com.godx.cloud.controller;


import com.godx.cloud.constant.constant;
import com.godx.cloud.model.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/doc")
public class DocController implements constant {

    @GetMapping("/dbInfo")
    public CommonResult getMybatisCode(@RequestParam("tables") List<String> tables, @RequestParam Map<String,Object> request, @RequestHeader("Authorization")String token) throws IOException, SQLException {
        String username=(String)request.get("username");
        String password=(String)request.get("password");
        String host=(String)request.get("host");
        String port=(String)request.get("port");
        String database=(String)request.get("database");

        return new CommonResult(STATUS_SUC,MESSAGE_OK);
    }
}
