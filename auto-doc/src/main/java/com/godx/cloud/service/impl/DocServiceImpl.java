package com.godx.cloud.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.oss.internal.OSSUtils;
import com.deepoove.poi.XWPFTemplate;
import com.godx.cloud.model.DownloadInfo;
import com.godx.cloud.model.User;
import com.godx.cloud.service.DocService;
import com.godx.cloud.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class DocServiceImpl implements DocService {

    @Value("${bucket.filePath.DocDbHostPath}")
    private String DocDbHostPath;


    public void generateDocDbInfo(String username,String password,String host,String port,String database) throws SQLException {
        Connection connection = DbUtil.mySQLOpen(username, password, host, port, database);
        VelocityUtil.loadContext(connection,database);
    }

    @Override
    public String getDbInfoDoc(Connection connection, String database,User user) throws IOException, SQLException {
        XWPFTemplate template = PoiTlUtil.loadContext2(connection, database);

//        String localFilePath="/Users/bytedance/IdeaProjects/doc/"+ IdUtil.simpleUUID();
        String localFilePath="C:/Users/Albert Zhu/Desktop/data/doc/"+ IdUtil.simpleUUID();
        PoiTlUtil.generateFile2(template,localFilePath+"/tmp.docx");
        //打包 压缩包名字
        String localZipPath=localFilePath+"/tmp.zip";
        FileOutputStream fos = new FileOutputStream(new File(localZipPath));
        VelocityUtil.toZip(new File(localFilePath),fos,true);

        // 上传到oss 并持久化记录 返回url
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(new Date());

        String fileUrl=DocDbHostPath+"/"+dateStr+"/"+user.getUsername()+"-doc-db-"+IdUtil.simpleUUID()+".zip";

        OSSUtil.uploadWithLocalFile(localZipPath, fileUrl);
        //删除本地文件
        File file = new File(localFilePath);
        FileUtil.deleteFileDirectory(file);
        return fileUrl;

    }
}
