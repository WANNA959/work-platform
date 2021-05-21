package com.godx.cloud.controller;


import com.alibaba.fastjson.JSON;
import com.godx.cloud.constant.constant;
import com.godx.cloud.model.*;
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

    @Resource
    private DownloadInfoService dbInfoService;

    @GetMapping("/dbInfo")
    public CommonResult getDbDoc(Integer id, @RequestHeader("Authorization")String token) throws IOException, SQLException {
        User user = UserUtil.getUserInfo(redisTemplate, token);
        if(user!=null){
            log.info(user.toString());
        } else{
            log.info("no user in redis");
            return new CommonResult(STATUS_BADREQUEST,"fail to delete");
        }

        DbInfo dbInfo = dbInfoService.queryById(id);
        if (user.getId()!=dbInfo.getUserId()){
            return new CommonResult(STATUS_BADREQUEST,"鉴权不匹配");
        }
        String username=dbInfo.getUsername();
        String password= dbInfo.getPassword();
        String host= dbInfo.getHost();
        String port= dbInfo.getPort();
        String database= dbInfo.getDbName();


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
            info.setFileType("doc");
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
