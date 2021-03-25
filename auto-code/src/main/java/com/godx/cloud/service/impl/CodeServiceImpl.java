package com.godx.cloud.service.impl;

import cn.hutool.core.util.IdUtil;
import com.godx.cloud.service.CodeService;
import com.godx.cloud.util.DbUtil;
import com.godx.cloud.util.OSSUtil;
import com.godx.cloud.util.VelocityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CodeServiceImpl implements CodeService {
    @Override
    public void getMybatisCode(String username, String password,String host,String port, String database, List<String> tables) throws IOException, SQLException {
        VelocityUtil.loadProperties();
        Connection connection = DbUtil.mySQLOpen(username, password, host, port, database);
        VelocityContext context = VelocityUtil.loadContext(connection);

        List<String> types=new ArrayList<String>();
        types.add("model");
        types.add("dao");
        types.add("service");
        types.add("serviceImpl");
        types.add("controller");
        types.add("mapper");
        String name="Test";
        String localFilePath="/Users/bytedance/IdeaProjects/code/"+ IdUtil.simpleUUID();
        for(String table:tables){
            for(String type:types){
                Template template = VelocityUtil.loadVmTemplate(type);
                localFilePath = VelocityUtil.generateFile(type, table, template, context,localFilePath);
            }
        }
        //打包
        //todo 压缩包名字
        String localZipPath=localFilePath+"/username-mybatis-code"+".zip";
        FileOutputStream fos = new FileOutputStream(new File(localZipPath));
        VelocityUtil.toZip(new File(localFilePath),fos,true);

        // todo 上传到oss 并持久化记录 返回url（内网/外网
        OSSUtil.uploadWithLocalFile(localZipPath);


    }
}
