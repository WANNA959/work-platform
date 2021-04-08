package com.godx.cloud.controller;


import com.alibaba.fastjson.JSON;
import com.godx.cloud.constant.constant;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.DownloadInfo;
import com.godx.cloud.model.TableInfo;
import com.godx.cloud.model.User;
import com.godx.cloud.service.DocService;
import com.godx.cloud.service.DownloadInfoService;
import com.godx.cloud.utils.DbUtil;
import com.godx.cloud.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/doc")
public class DocController implements constant {

    @Resource
    private DocService docService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DownloadInfoService downloadInfoService;

    @GetMapping("/dbInfo")
    public CommonResult getDbDoc(@RequestParam Map<String,Object> request, @RequestHeader("Authorization")String token) throws IOException, SQLException {
        String username=(String)request.get("username");
        String password=(String)request.get("password");
        String host=(String)request.get("host");
        String port=(String)request.get("port");
        String database=(String)request.get("database");

        User user = UserUtil.getUserInfo(redisTemplate, token);

        Connection connection = DbUtil.mySQLOpen(username, password, host, port, database);
        List<TableInfo> allTables = DbUtil.getAllTables(connection,database);
        List<String> tmp=new ArrayList<>();
        for(TableInfo table:allTables){
            tmp.add(table.getName());
        }
        String fileUrl=docService.getDbInfoDoc(connection,database,user);
        if(!StringUtils.isBlank(fileUrl)){
            DownloadInfo info=new DownloadInfo();
            info.setUserId(user.getId());
            info.setOssDetails(fileUrl);
            info.setFileType("dbdoc");
            info.setDbName(database);
            String tableJson = JSON.toJSON(tmp).toString();
            info.setTables(tableJson);
            info.setHost(host);
            info.setUsername(username);
            //todo password加密
            info.setPassword(password);
            log.info("downloadInfo:"+info.toString());
            downloadInfoService.insertItem(info);
        }
        return new CommonResult(STATUS_SUC,MESSAGE_OK);
    }
}
