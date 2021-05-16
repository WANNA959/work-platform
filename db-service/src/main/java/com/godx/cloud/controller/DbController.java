package com.godx.cloud.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.csvreader.CsvReader;
import com.godx.cloud.constant.constant;
import com.godx.cloud.model.*;
import com.godx.cloud.service.DbInfoService;
import com.godx.cloud.service.DownloadInfoService;
import com.godx.cloud.utils.*;
import jdk.net.SocketFlow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/db")
public class DbController implements constant {

    @Resource
    private DbInfoService dbInfoService;

    @Resource
    private DownloadInfoService downloadInfoService;

    @Resource
    private RedisTemplate redisTemplate;

    @Value("${bucket.filePath.DbDataPath}")
    private String dbDataPath;

    @PostMapping("/dbinsert")
    public CommonResult dbInsert(@Param("id") Integer id,@Param("table") String table, @RequestHeader("Authorization")String token,@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new CommonResult(STATUS_BADREQUEST,"上传失败，请选择文件");
        }

        String filePath = "C:/Users/Albert Zhu/Desktop/data/db/insert/"+IdUtil.simpleUUID()+".csv";
        File dest = new File(filePath);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error(e.toString(), e);
            return new CommonResult(STATUS_BADREQUEST,"上传失败");
        }

        DbInfo dbInfo = dbInfoService.queryById(id);
        if(dbInfo==null){
            return new CommonResult(STATUS_BADREQUEST,"fail");
        }
        String tokenKey = RedisKeyUtil.getTokenKey(token.substring(7));
        User user =(User) redisTemplate.opsForValue().get(tokenKey);
        if(user==null){
            return new CommonResult(STATUS_BADREQUEST,"fail");
        }
        if(dbInfo.getUserId()!=user.getId()){
            return new CommonResult(STATUS_BADREQUEST,"fail");
        }
//        String filePath = "C:\\Users\\Albert Zhu\\Desktop\\data\\db\\cda9587a6fbd4e0ea7445a4a3d8aeda2\\test2.csv";
        List<String> listData = new ArrayList<>();
//        Connection connection = DbUtil.mySQLOpen("wanna959", "Zhujianxing959", "rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com", "3306", "work_platform");
        Connection connection = DbUtil.mySQLOpen(dbInfo.getUsername(), dbInfo.getPassword(), dbInfo.getHost(), dbInfo.getPort(), dbInfo.getDbName());
        String sqlStr="";
        try {
            CsvReader csvReader = new CsvReader(filePath);
            // 读表头
            boolean re = csvReader.readHeaders();
            if (!re){
                //表头无法识别错误
                return new CommonResult(STATUS_BADREQUEST,"文件表头无法识别");
            }
            String[] headers = csvReader.getHeaders();
            log.info(headers[0]);
            List<ColumnInfo> columns = DbUtil.getTableInfo(connection, dbInfo.getDbName(), table);

            for(int idx=0;idx<headers.length;idx++){
                String header=headers[idx];
                int flag=0;
                if(StringUtils.isBlank(header)){
                    if(idx==headers.length-1){
                        continue;
                    }
                    else {
                        //列名称无法识别错误
                        return new CommonResult(STATUS_BADREQUEST,"文件列明无法识别");
                    }
                }

                for(ColumnInfo info:columns){
                    // 兼容驼峰&下划线 转换为下划线命名格式
                    if(DbServiceUtil.hump2Underline(info.getName()).equals(DbServiceUtil.hump2Underline(header))){
                        flag=1;
                        sqlStr+=DbServiceUtil.hump2Underline(info.getName())+",";
                        break;
                    }
                }
                if(flag==0){
                    //列名称无法识别错误
                    return new CommonResult(STATUS_BADREQUEST,"文件列明无法识别");
                }
            }
            sqlStr=sqlStr.substring(0,sqlStr.length()-1);
            log.info(sqlStr);
            while (csvReader.readRecord()) {
                String rawRecord = csvReader.getRawRecord();
                String[] split = rawRecord.split(",",-1);
                String tmp="\""+String.join("\",\"", split)+"\"";
                log.info(rawRecord.toString());
                listData.add(tmp);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("文件未找到");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        String exeSql= String.format("insert into %s (%s) values ", "test",sqlStr);
        int len=listData.size();
        int num=0;
        for(String value:listData){
            num++;
            if(num!=len){
                exeSql+=" ("+value+"),";
            } else{
                exeSql+=" ("+value+");";
            }
        }
        log.info(exeSql);
        PreparedStatement statement= null;
        try {
            statement = connection.prepareStatement(exeSql);
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommonResult(STATUS_BADREQUEST,e.getMessage());
        }
        try {
            boolean execute = statement.execute(exeSql);
        } catch (SQLException e) {
            e.printStackTrace();
            return new CommonResult(STATUS_BADREQUEST,e.getMessage());
        }
        return new CommonResult(STATUS_SUC,"批量导入成功");
    }

    @GetMapping("/getinfo")
    public CommonResult getinfo(@RequestParam("id") Integer id, @RequestHeader("Authorization")String token, HttpServletResponse response) throws SQLException, IOException {
        DbInfo dbInfo = dbInfoService.queryById(id);
        if(dbInfo==null){
            return new CommonResult(STATUS_BADREQUEST,"fail");
        }
        String tokenKey = RedisKeyUtil.getTokenKey(token.substring(7));
        User user =(User) redisTemplate.opsForValue().get(tokenKey);
        if(user==null){
            return new CommonResult(STATUS_BADREQUEST,"fail");
        }
        if(dbInfo.getUserId()!=user.getId()){
            return new CommonResult(STATUS_BADREQUEST,"fail");
        }

        Connection conn = DbUtil.mySQLOpen(dbInfo.getUsername(), dbInfo.getPassword(), dbInfo.getHost(), dbInfo.getPort(), dbInfo.getDbName());
        List<String> tables = JSON.parseArray(dbInfo.getTables(), String.class);

        String filePath="C:/Users/Albert Zhu/Desktop/data/db/"+IdUtil.simpleUUID();
        File file=new File(filePath);
        if (!file.isDirectory()){
            file.mkdirs();
        }

        //生成文件
        for(String table:tables){
            DbServiceUtil.writeCsvDataFile(conn,dbInfo.getDbName(),table,filePath);
        }

        //打包 压缩包名字
        String localZipPath=filePath+"/tmp.zip";
        FileOutputStream fos = new FileOutputStream(new File(localZipPath));
        FileUtil.toZip(new File(filePath),fos,true);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(new Date());
        String fileUrl=dbDataPath+"/"+dateStr+"/"+user.getUsername()+"-data-"+ IdUtil.simpleUUID()+".zip";
        String url = OSSUtil.uploadWithLocalFile(localZipPath, fileUrl);

        //插入数据库download_info
        DownloadInfo info=new DownloadInfo();
        info.setDbName(dbInfo.getDbName());
        info.setUserId(dbInfo.getUserId());
        info.setOssDetails(url);
        info.setFileType("data");
        info.setPassword(dbInfo.getPassword());
        info.setHost(dbInfo.getHost());
        info.setUsername(dbInfo.getUsername());
        info.setTables(dbInfo.getTables());

        downloadInfoService.insert(info);

        return new CommonResult(STATUS_SUC,"ok");
    }

}
