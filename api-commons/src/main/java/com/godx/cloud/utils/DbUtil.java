package com.godx.cloud.utils;

import com.godx.cloud.model.ColumnInfo;
import com.godx.cloud.model.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DbUtil {

    static String driver = "com.mysql.jdbc.Driver";    // URL指向要访问的数据库名******,8.0jar包新增时区。
//    static String url = "jdbc:mysql://rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com:3306/work_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false";    // MySQL配置时的用户名
//    static String user = "wanna959";    // Java连接MySQL配置时的密码******
//    static String password = "Zhujianxing959";
    public static String[] colnames; // 列名数组

    public static String[] colTypes; // 列名类型数组

    public static String[] upperColnames; //驼峰命名的字段

    public static List<StringBuffer> listArr = new ArrayList<StringBuffer>(); //存储最终的数据

    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection mySQLOpen(String user,String password,String host,String port,String database) {
        String url="jdbc:mysql://"+host+":"+port+"/"+database+"?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true";    // MySQL配置时的用户名
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("succeed to connection mysql!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public static void mySQLClose(ResultSet rs, Statement st, Connection con) {
        try {
            try {
                if (rs != null) {
                    rs.close();
                }
            } finally {
                try {
                    if (st != null) {
                        st.close();
                    }
                } finally {
                    if (con != null)
                        con.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得当前数据库生成数据库下所有的表名
     *
     */
    public static List<TableInfo> getAllTables(Connection conn,String database) {
        List<TableInfo> list = null;
        try {
            String sql= String.format("select table_name,table_comment from information_schema.tables where table_schema='%s'", database);
            PreparedStatement pst=conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery(sql);
            if (rs != null) {
                list = new ArrayList<TableInfo>();
            }
            while (rs.next()) {                //System.out.println(rs.getString("TABLE_NAME"));
                TableInfo info=new TableInfo();
                info.setName(rs.getString("table_name"));
                info.setComment(rs.getString("table_comment"));
                list.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获得当前数据库 某表下所有的表名
     *
     */
    public static List<ColumnInfo> getTableInfo(Connection conn,String database,String table) {
        DatabaseMetaData dbmd = null;
        List<ColumnInfo> columnList=null;
        try {
            dbmd = (DatabaseMetaData) conn.getMetaData();            //conn.getCatalog():获得当前目录
            String sql= String.format("select * from information_schema.columns where table_schema='%s' and table_name='%s'", database,table);
            PreparedStatement pst=conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery(sql);
            if (rs != null) {
                columnList = new ArrayList<>();
            } else{
                return null;
            }
            while (rs.next()) {                //System.out.println(rs.getString("TABLE_NAME"));
                ColumnInfo tmp=new ColumnInfo();
                tmp.setName(rs.getString("COLUMN_NAME"));
                tmp.setType(rs.getString("COLUMN_TYPE"));
                tmp.setComment(rs.getString("COLUMN_COMMENT"));
                tmp.setKey(rs.getString("COLUMN_KEY"));
                Map<String,Object> map=new HashMap<>();
                map.put("default",rs.getString("COLUMN_DEFAULT"));
                map.put("extra",rs.getString("extra"));
                tmp.setExt(map);
                tmp.setNull(rs.getString("IS_NULLABLE").equals("NO")?false:true);
                columnList.add(tmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return columnList;
    }

//    @Test
//    public void test1(){
//        List<String> list = getTableFromNowConnectDB();
//        for (String item:list){
//            log.info(item);
//        }
//
//    }

    /**
     * 生成所有的字段
     *
     * @param sb，把stringbuffer 对象传进来继续append
     * @return
     */
    public static void processAllField(StringBuffer sb) {
        for (int i = 0; i < colnames.length; i++) {
            sb.append("\tpublic " + colTypes[i] + " " + colnames[i] + ";\r\n");
        }
    }

    /**
     * 生成所有的set(),get()
     *
     * @param sb,把stringbuffer 对象传进来继续append
     */
    public static void processAllMethod(StringBuffer sb) {
        for (int i = 0; i < colnames.length; i++) {
            sb.append("\tpublic void set" + upperColnames[i] + "(" + colTypes[i] + " " + colnames[i] + "){\r\n");
            sb.append("\t\tthis." + colnames[i] + " = " + colnames[i] + ";\r\n");
            sb.append("\t}\r\n");

            sb.append("\tpublic " + colTypes[i] + " get" + upperColnames[i] + "(){\r\n");
            sb.append("\t\treturn this." + colnames[i] + ";\r\n");
            sb.append("\t}\r\n");
        }
    }

    /**
     * @param sb,把stringbuffer 对象传进来继续append
     * @author one
     * 该方法用于生成构造函数
     *      * @see 默认无惨和全参
     * <p>
     * public UserInfo(){}
     * public UserInfo(int uid, String uname, String usex) {
     * super();
     * this.uid = uid;
     * this.uname = uname;
     * this.usex = usex;
     * }
     */
    public static void processConstructor(StringBuffer sb, String tableName) {
        sb.append("\tpublic " + tableName + "(){}\r\n");
        sb.append("\tpublic " + tableName + "(");
        String link = "";
        for (int i = 0; i < colnames.length; i++) {
            sb.append(link + colTypes[i] + " " + colnames[i]);
            link = ",";
        }
        sb.append("){\r\n");
        for (int i = 0; i < colnames.length; i++) {
            sb.append("\t\tthis." + colnames[i] + "=" + colnames[i] + ";\r\n");
        }
        sb.append("\t}\r\n");
    }

    /**
     * 该方法用于类型转换
     *
     * @param dbType:传入的数据类型
     * @author one
     */
    public static String sqlTypeToJava(String dbType) {
        dbType = dbType.toUpperCase();
        switch (dbType) {
            case "VARCHAR":
            case "VARCHAR2":
            case "CHAR":
                return "String";
            case "NUMBER":
            case "DECIMAL":
                return "double";
            case "INT":
            case "SMALLINT":
            case "INTEGER":
                return "int";
            case "BIGINT":
                return "int";
            case "DATETIME":
            case "TIMESTAMP":
            case "DATE":
                return "java.util.Date";
            default:
                return "Object";
        }
    }

    /**
     * 创建java 文件 将生成的属性 get/set 方法 保存到 文件中 markerBean
     *
     * @param className 类名称
     * @param content   类内容 包括属性 getset 方法
     * @author one
     * @packageName fanshe
     */
    public static void createFloder(String className, String content, String packageName) {
        String folder = System.getProperty("user.dir") + "/src/" + packageName + "/";

        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = folder + className + ".java";
        try {
            File newdao = new File(fileName);
            FileWriter fw = new FileWriter(newdao);
            fw.write("package\t" + packageName.replace("/", ".") + ";\r\n");
            fw.write(content);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 该方法生成Entity
     *
     *      * @param tablename:表名集合
     * @param packname:要生成的路径
     * @author one
     */
//    public static List<StringBuffer> createEntity(List<String> tableNames, String packname) throws Exception {
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSetMetaData rsmd = null;        //每个表的表名
//        String eveTableName = "";
//        conn = mySQLOpen();
//        //System.out.println("当前数据库下的表的总数："+tableNames.size());
//        for (int i = 0; i < tableNames.size(); i++) {            //用StringBuffer的形式先输出测试一下
//            StringBuffer sb = new StringBuffer("");            //表名首字母转大写
//            eveTableName = tableNames.get(i).substring(0, 1).toUpperCase() + tableNames.get(i).substring(1, tableNames.get(i).length());
//            sb.append("public class " + eveTableName + " {\n\n");            //查询语句
//            String sql = "select * from " + eveTableName;
//
//            pstmt = conn.prepareStatement(sql);            //获得表的元数据
//            rsmd = pstmt.getMetaData();             // 每个表共有多少列
//            int size = rsmd.getColumnCount();            //把字段放在集合里面
//            colnames = new String[size];
//            colTypes = new String[size];
//            upperColnames = new String[size];
//            for (int j = 0; j < size; j++) {
//                String columnName = rsmd.getColumnName(j + 1);//每列的字段名
//                colnames[j] = columnName;
//                String colunmType = sqlTypeToJava(rsmd.getColumnTypeName(j + 1));//每列的类型
//                colTypes[j] = colunmType;                 //接下来做驼峰命名的字段
//                String upperColumnNam = rsmd.getColumnName(j + 1).substring(0, 1).toUpperCase() +
//                        rsmd.getColumnName(j + 1).substring(1, rsmd.getColumnName(j + 1).length());
//                upperColnames[j] = upperColumnNam;
//            }
//            processAllField(sb); //生成字段
//            processAllMethod(sb); //生成set,get方法
//            processConstructor(sb, eveTableName); //生成构造函数
//            //添加最后一个括号
//            sb.append("}");            //创建文件夹，sb.toString(),把数据扔进去            createFloder(eveTableName,sb.toString(),packname);
//            listArr.add(sb);
//        }        //关闭连接
//        mySQLClose(null, pstmt, conn);
//        return listArr;
//    }
//
//    /**
//     * 该方法生成Dao
//     *
//     *      * @param tablename:表名集合
//     * @param packname:要生成的路径
//     * @author one
//     */
//    public static List<StringBuffer> createDao(List<String> tableNames, String packname) throws Exception {
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSetMetaData rsmd = null;        //每个表的表名
//        String eveTableName = "";
//        conn = mySQLOpen();
//        //System.out.println("当前数据库下的表的总数："+tableNames.size());
//        for (int i = 0; i < tableNames.size(); i++) {            //用StringBuffer的形式先输出测试一下
//            StringBuffer sb = new StringBuffer("");            //表名首字母转大写
//            eveTableName = tableNames.get(i).substring(0, 1).toUpperCase() + tableNames.get(i).substring(1, tableNames.get(i).length());
//            sb.append("public class " + eveTableName + " {\n\n");            //查询语句
//            String sql = "select * from " + eveTableName;
//
//            pstmt = conn.prepareStatement(sql);            //获得表的元数据
//            rsmd = pstmt.getMetaData();             // 每个表共有多少列
//            int size = rsmd.getColumnCount();            //把字段放在集合里面
//            colnames = new String[size];
//            colTypes = new String[size];
//            upperColnames = new String[size];
//            for (int j = 0; j < size; j++) {
//                String columnName = rsmd.getColumnName(j + 1);//每列的字段名
//                colnames[j] = columnName;
//                String colunmType = sqlTypeToJava(rsmd.getColumnTypeName(j + 1));//每列的类型
//                colTypes[j] = colunmType;                 //接下来做驼峰命名的字段
//                String upperColumnNam = rsmd.getColumnName(j + 1).substring(0, 1).toUpperCase() +
//                        rsmd.getColumnName(j + 1).substring(1, rsmd.getColumnName(j + 1).length());
//                upperColnames[j] = upperColumnNam;
//            }
//            processAllField(sb); //生成字段
//            processAllMethod(sb); //生成set,get方法
//            processConstructor(sb, eveTableName); //生成构造函数
//            //添加最后一个括号
//            sb.append("}");            //创建文件夹，sb.toString(),把数据扔进去            createFloder(eveTableName,sb.toString(),packname);
//            listArr.add(sb);
//        }        //关闭连接
//        mySQLClose(null, pstmt, conn);
//        return listArr;
//    }
//
//
//
//    /**
//     * 展示
//     */
//    public void goCreate() {
//        try {
//            List<StringBuffer> result = createEntity(getTableFromNowConnectDB(), "vo2");
//            for (int i = 0; i < result.size(); i++) {
//                System.out.println("============第" + (i + 1) + "个表的实体类生成============");
//                System.out.println(result.get(i));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
