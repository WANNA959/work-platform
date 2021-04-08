package com.godx.cloud.utils;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.RenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.policy.HackLoopTableRenderPolicy;
import com.godx.cloud.model.ColumnInfo;
import com.godx.cloud.model.DbStruct;
import com.godx.cloud.model.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PoiTlUtil {

    @Test
    public void test1() throws IOException, SQLException {
        String host="rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com";
        String dbName="work_platform";
        String port="3306";
        String user = "wanna959";
        String password = "Zhujianxing959";
        Connection connection = DbUtil.mySQLOpen(user, password, host, port, dbName);
        XWPFTemplate template = loadContext2(connection, "work_platform");
        // todo 更正文件路径和命名
        generateFile2(template,"/Users/bytedance/IdeaProjects/doc/"+"dbInfo"+".docx");
    }

    public static XWPFTemplate loadContext2(Connection conn, String database) throws SQLException, IOException {
        //2 创建上下文对象 将数据对象添加到此上下文中
        Map<String,Object> map=new HashMap<>();

        HackLoopTableRenderPolicy policy = new HackLoopTableRenderPolicy();

        Configure config = Configure.newBuilder()
                .bind("TableInfos",policy)
                .bind("DbStruct", policy)
                .bind("ColumnInfos", policy)
                .bind("fullColumn", policy)
                .build();
        // dbStruct
        DbStruct db=new DbStruct();
        db.setName(database);

        List<TableInfo> tableList = DbUtil.getAllTables(conn,database);
        log.info(tableList.toString());
        for (TableInfo table:tableList){
            List<ColumnInfo> columns = DbUtil.getTableInfo(conn,database, table.getName());
            table.setFullColumn(columns);
        }
        db.setTableInfos(tableList);
        map.put("DbStruct", db);
        map.put("dbname", db.getName());
        log.info("DbStruct:"+db.toString());

        XWPFTemplate template = loadTemplate2(map,config);
        return template;

    }

    public static XWPFTemplate loadTemplate2(Map<String,Object> data,Configure config) throws IOException, SQLException {

        //4选择模板
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource resource = patternResolver.getResource("template/template.docx");
        // 获取InputStream
        XWPFTemplate template = XWPFTemplate.compile(resource.getInputStream(),config)
                .render(data);
        return template;

    }

    public static String generateFile2(XWPFTemplate template,String localPath) throws IOException {
        //4创建文件
        FileOutputStream out;
        File file=new File(localPath);
        //获得它的父类文件，如果不存在，就创建
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        out = new FileOutputStream(file);
        template.write(out);
        out.flush();
        out.close();
        template.close();
        return localPath;
    }
}
