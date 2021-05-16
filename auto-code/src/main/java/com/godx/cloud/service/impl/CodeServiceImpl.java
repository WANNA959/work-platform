package com.godx.cloud.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.godx.cloud.model.DownloadInfo;
import com.godx.cloud.model.User;
import com.godx.cloud.service.CodeService;
import com.godx.cloud.service.DownloadInfoService;
import com.godx.cloud.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CodeServiceImpl implements CodeService {

    @Resource
    private DownloadInfoService downloadInfoService;
    
    @Resource
    private RedisTemplate redisTemplate;

    @Value("${bucket.filePath.CodeMybatisHostPath}")
    private String CodeMybatisHostPath;

    @Override
    public String getMybatisCode(String username, String password,String host,String port, String database, List<String> tables,String token,List<String> types) throws IOException, SQLException {
        VelocityUtil.loadProperties();
        Connection connection = DbUtil.mySQLOpen(username, password, host, port, database);
        if(connection==null){
            return "数据库连接失败，请检查连接参数";
        }

//        List<String> types=new ArrayList<String>();
//        types.add("model");
//        types.add("dao");
//        types.add("service");
//        types.add("serviceImpl");
//        types.add("controller");
//        types.add("mapper");
        String localFilePath="C:/Users/Albert Zhu/Desktop/data/code/"+ IdUtil.simpleUUID();

        for(String table:tables){
            VelocityContext context = VelocityUtil.loadContext(connection,database,table);
            for(String type:types){
                Template template = VelocityUtil.loadVmTemplate(type);
                localFilePath = VelocityUtil.generateFile(type, table, template, context,localFilePath);
            }
        }
        //打包 压缩包名字
        String localZipPath=localFilePath+"/tmp.zip";
        FileOutputStream fos = new FileOutputStream(new File(localZipPath));
        VelocityUtil.toZip(new File(localFilePath),fos,true);

        // 上传到oss 并持久化记录 返回url
        User user = UserUtil.getUserInfo(redisTemplate, token);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(new Date());

        String fileUrl=CodeMybatisHostPath+"/"+dateStr+"/"+user.getUsername()+"-mybatis-code"+IdUtil.simpleUUID()+".zip";
        OSSUtil.uploadWithLocalFile(localZipPath, fileUrl);
        //删除本地文件
        File file = new File(localFilePath);
        FileUtil.deleteFileDirectory(file);

        if(!StringUtils.isBlank(fileUrl)){
            DownloadInfo info=new DownloadInfo();
            info.setUserId(user.getId());
            info.setOssDetails(fileUrl);
            info.setFileType("mybatis");
            info.setDbName(database);
            String tableJson = JSON.toJSON(tables).toString();
            info.setTables(tableJson);
            info.setHost(host);
            info.setUsername(username);
            //todo password加密
            info.setPassword(password);
            log.info("downloadInfo:"+info.toString());
            downloadInfoService.insertItem(info);
            return "";
        } else{
            return "上传OSS失败";
        }
    }
}
