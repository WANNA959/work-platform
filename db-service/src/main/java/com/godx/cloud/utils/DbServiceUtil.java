package com.godx.cloud.utils;

import com.csvreader.CsvWriter;
import com.godx.cloud.model.ColumnInfo;
import com.godx.cloud.utils.DbUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DbServiceUtil {

    public static void writeCsvDataFile(Connection conn,String database, String table,String filePath) throws SQLException, IOException {
//        String sql= String.format("select * from %s ",tables.get(0));
        String sql= String.format("select * from %s ",table);
        PreparedStatement pst=conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery(sql);

        CsvWriter csvWriter=new CsvWriter(filePath+"/"+table+"_data.csv",',', Charset.forName("utf-8"));
        List<ColumnInfo> columns = DbUtil.getTableInfo(conn, database, table);
        String[] headers = new String[columns.size()+1];
        int idx=0;
        for(ColumnInfo columnInfo:columns){
            headers[idx++]=columnInfo.getName();
        }
        csvWriter.writeRecord(headers);

        while (rs.next()) {
            int idx2=0;
            String[] content = new String[idx];
            for(idx2=0;idx2<idx;idx2++){
                content[idx2]=rs.getString(headers[idx2]);
            }
            csvWriter.writeRecord(content);
        }
        csvWriter.close();
    }

    /**
     * 转驼峰命名正则匹配规则
     */
    private static final Pattern TO_HUMP_PATTERN = Pattern.compile("[-_]([a-z0-9])");
    private static final Pattern TO_LINE_PATTERN = Pattern.compile("[A-Z]+");

    /**
     * 驼峰转下划线，全小写
     *
     * @param str 驼峰字符串
     * @return 下划线字符串
     */
    public static String hump2Underline(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        Matcher matcher = TO_LINE_PATTERN.matcher(str);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            if (matcher.start() > 0) {
                matcher.appendReplacement(buffer, "_" + matcher.group(0).toLowerCase());
            } else {
                matcher.appendReplacement(buffer, matcher.group(0).toLowerCase());
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
