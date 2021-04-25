import cn.hutool.db.meta.Column;
import com.csvreader.CsvReader;
import com.godx.cloud.model.ColumnInfo;
import com.godx.cloud.utils.DbServiceUtil;
import com.godx.cloud.utils.DbUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class test {

    @Test
    public void test1() throws SQLException {
        String filePath = "C:\\Users\\Albert Zhu\\Desktop\\data\\db\\cda9587a6fbd4e0ea7445a4a3d8aeda2\\test2.csv";
        List<String> listData = new ArrayList<>();
        Connection connection = DbUtil.mySQLOpen("wanna959", "Zhujianxing959", "rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com", "3306", "work_platform");
        String sqlStr="";
        try {
            CsvReader csvReader = new CsvReader(filePath);
            // 读表头
            boolean re = csvReader.readHeaders();
            if (!re){
                //表头无法识别错误
                log.info("wrong1");
            }
            String[] headers = csvReader.getHeaders();
            log.info(headers[0]);
            List<ColumnInfo> columns = DbUtil.getTableInfo(connection, "work_platform", "test");

            for(int idx=0;idx<headers.length;idx++){
                String header=headers[idx];
                int flag=0;
                if(StringUtils.isBlank(header)){
                    if(idx==headers.length-1){
                        continue;
                    }
                    else {
                        //列名称无法识别错误
                        log.info("wrong1");
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
                    log.info("wrong2");
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
        PreparedStatement statement=connection.prepareStatement(exeSql);
        boolean execute = statement.execute(exeSql);

    }

    @Test
    public void test2(){
        String demo="createTime";
        String s = DbServiceUtil.hump2Underline(demo);
        log.info(s);

    }
}
