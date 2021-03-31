package com.godx.cloud.utils;

import com.godx.cloud.model.ColumnInfo;
import com.godx.cloud.model.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.junit.Test;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.godx.cloud.util.DbUtil.mySQLOpen;
import static com.godx.cloud.util.DbUtil.sqlTypeToJava;

@Slf4j
public class VelocityUtil {

    @Test
    public void testModel() throws IOException, SQLException {
        loadProperties();
        String type="model";
        String name="Test";
        Connection conn = mySQLOpen("wanna959","Zhujianxing959","rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com","3306","work_platform");
        VelocityContext context = loadContext(conn);
        Template template = loadVmTemplate(type);
        String localFilePath="/Users/bytedance/IdeaProjects/code/test";
        generateFile(type,name,template,context,localFilePath);
    }

    @Test
    public void testDao() throws IOException, SQLException {
        loadProperties();
        String type="dao";
        String name="Test";
        Connection conn = mySQLOpen("wanna959","Zhujianxing959","rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com","3306","work_platform");
        VelocityContext context = loadContext(conn);
        Template template = loadVmTemplate(type);
        String localFilePath="/Users/bytedance/IdeaProjects/code/test";
        generateFile(type,name,template,context,localFilePath);
    }

    @Test
    public void testService() throws IOException, SQLException {
        loadProperties();
        String type="service";
        String name="Test";
        Connection conn = mySQLOpen("wanna959","Zhujianxing959","rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com","3306","work_platform");
        VelocityContext context = loadContext(conn);
        Template template = loadVmTemplate(type);
        String localFilePath="/Users/bytedance/IdeaProjects/code/test";
        generateFile(type,name,template,context,localFilePath);
    }

    @Test
    public void testController() throws IOException, SQLException {
        loadProperties();
        String type="controller";
        String name="Test";
        Connection conn = mySQLOpen("wanna959","Zhujianxing959","rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com","3306","work_platform");
        VelocityContext context = loadContext(conn);
        Template template = loadVmTemplate(type);
        String localFilePath="/Users/bytedance/IdeaProjects/code/test";
        generateFile(type,name,template,context,localFilePath);
    }

    @Test
    public void testMapper() throws IOException, SQLException {
        loadProperties();
        String type="mapper";
        String name="Test";
        Connection conn = mySQLOpen("wanna959","Zhujianxing959","rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com","3306","work_platform");
        VelocityContext context = loadContext(conn);
        Template template = loadVmTemplate(type);
        String localFilePath="/Users/bytedance/IdeaProjects/code/test";
        generateFile(type,name,template,context,localFilePath);
    }

    @Test
    public static void testServiceImpl() throws IOException, SQLException {
        loadProperties();
        String type="serviceImpl";
        String name="Test";
        Connection conn = mySQLOpen("wanna959","Zhujianxing959","rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com","3306","work_platform");
        VelocityContext context = loadContext(conn);
        Template template = loadVmTemplate(type);
        String localFilePath="/Users/bytedance/IdeaProjects/code/test";
        generateFile(type,name,template,context,localFilePath);
    }

    public static void loadProperties() throws IOException {
        //1 加载配置
        Properties prop = new Properties();
        prop.load(Test.class.getClassLoader().getResourceAsStream("velocity.properties"));
//        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//        prop.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
//        prop.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
//        prop.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        Velocity.init(prop);
    }

    public static VelocityContext loadContext(Connection conn) throws SQLException {
        //2 创建上下文对象 将数据对象添加到此上下文中
        VelocityContext context = new VelocityContext();
        //添加java类，才能调用java方法
        context.put("name", NameUtil.getInstance());
        context.put("tool", GlobalTool.getInstance());
        context.put("time", TimeUtils.getInstance());

        List<String> tempList = new ArrayList<String>();
        context.put("importList", tempList);
        context.put("author", "hello");

        //数据库
//        Connection conn = null;
//        PreparedStatement pstmt = null;
//        ResultSetMetaData rsmd = null;        //每个表的表名
//        conn = mySQLOpen("wanna959","Zhujianxing959","rm-bp167k6429i8drmq71o.mysql.rds.aliyuncs.com","3306","work_platform");
//        String sql = "select * from User";
//        pstmt = conn.prepareStatement(sql);            //获得表的元数据
//        rsmd = pstmt.getMetaData();             // 每个表共有多少列
//        int size = rsmd.getColumnCount();            //把字段放在集合里面
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet columnsRs = metaData.getColumns(null,null,"test",null);

        TableInfo tableInfo = new TableInfo();
        //tableName
        tableInfo.setName("Test");
        //packageName
        tableInfo.setSavePackageName("com.godx.cloud");
        //dbName
        tableInfo.setDBName("work_platform");
        //rawName
        tableInfo.setRawName("test");
        //columns
        List<ColumnInfo> list = new ArrayList<>();
        while (columnsRs.next()) {
            ColumnInfo col = new ColumnInfo();
//            String columnName = rsmd.getColumnName(j + 1);//每列的字段名
            String columnName = columnsRs.getString("COLUMN_NAME");
            col.setRawName(columnName);
            col.setName(NameUtil.getInstance().getJavaName(columnName));
//            String colunmType=rsmd.getColumnTypeName(j + 1);
            String colunmType=columnsRs.getString("TYPE_NAME");
            col.setType(sqlTypeToJava(colunmType));
            col.setComment(columnsRs.getString("REMARKS"));
            Map<String, Object> extMap = new HashMap<>();
            extMap.put("jdbcType", colunmType);
            col.setExt(extMap);
            log.info(col.toString());
            list.add(col);
        }
        tableInfo.setFullColumn(list);
        //pk
        ResultSet pkColumn = metaData.getPrimaryKeys(null, null, "test");
        Set<String> pkSet=new HashSet<>();
        List<ColumnInfo> pkList = new ArrayList<>();
        if (!pkColumn.isAfterLast()) {
            pkColumn.next();
            pkSet.add(pkColumn.getString("COLUMN_NAME"));
        }
        columnsRs.beforeFirst();
        while (columnsRs.next()) {
            String columnName=columnsRs.getString("COLUMN_NAME");
            String columnType=columnsRs.getString("TYPE_NAME");
            if(pkSet.contains(columnName)){
                ColumnInfo col = new ColumnInfo();
                col.setRawName(columnName);
                col.setName(NameUtil.getInstance().getJavaName(columnName));
                String colunmType = sqlTypeToJava(columnType);//每列的类型
                col.setType(colunmType);
                col.setShortType(NameUtil.getInstance().getClsNameByFullName(colunmType));
                pkList.add(col);
            }
        }
        log.info("pk:"+pkList.toString());
        tableInfo.setPkColumn(pkList);
        //other columns
        List<ColumnInfo> otherList = new ArrayList<>();
        for(ColumnInfo col:list){
            int flag=0;
            for(ColumnInfo col2:pkList){
                if(col.getName().equals(col2.getName())){
                    flag=1;
                    break;
                }
            }
            if(flag==0){
                otherList.add(col);
            }
        }
        log.info("otherList:"+otherList.toString());
        log.info("tableInfo:"+tableInfo.toString());
        tableInfo.setOtherColumn(otherList);
        context.put("tableInfo", tableInfo);

        return context;
    }

    public static Template loadVmTemplate(String fileType) throws IOException, SQLException {

        //4选择模板
//        Velocity.getTemplate("./Default/init.vm");
//        Velocity.getTemplate("./Default/autoImport.vm");
        Template template;
        if(fileType.equals("mapper")){
            template = Velocity.getTemplate("./template/"+fileType+".xml.vm");
        } else{
            template = Velocity.getTemplate("./template/"+fileType+".java.vm");
        }
        return template;
    }

    public static String type2fileName(String type,String name){
        switch (type){
            case "model":
                return name+".java";
            case "dao":
                return name+"Dao.java";
            case "service":
                return name+"Service.java";
            case "controller":
                return name+"Controller.java";
            case "mapper":
                return name+"Mapper.xml";
            case "serviceImpl":
                return name+"ServiceImpl.java";
            default:
                return null;
        }
    }

    public static String type2ParentDirectory(String type){
        switch (type){
            case "serviceImpl":
                return "/service/impl/";
            default:
                return "/"+type+"/";
        }
    }

    public static String generateFile(String type,String name,Template template,VelocityContext context,String localPath) throws IOException {
        //4创建文件
        log.info(type2fileName(type,name));
        String parentDirectory = type2ParentDirectory(type);
        File saveFile = new File(localPath+"/"+parentDirectory+type2fileName(type,name));

        //获得它的父类文件，如果不存在，就创建
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        //创建文件输出流
        FileOutputStream outStream = new FileOutputStream(saveFile);
        //因为模板整合的时候，需要提供一个Writer，所以创建一个Writer
        OutputStreamWriter writer = new OutputStreamWriter(outStream);
        //创建一个缓冲流
        BufferedWriter bufferWriter = new BufferedWriter(writer);

        //5 合并模板和数据并输出
        //引入StringWriter格式化代码
        StringWriter stringWriter = new StringWriter();
//        template.merge(context, bufferWriter);
        template.merge(context, stringWriter);
//        log.info(formJava(stringWriter.toString()));

//        bufferWriter.write(formJava(stringWriter.toString()));
        bufferWriter.write(stringWriter.toString());
        log.info(bufferWriter.toString());
        //强制刷新
        bufferWriter.flush();
        outStream.close();
        bufferWriter.close();
        return localPath;
    }

    /**
     * 压缩成ZIP 方法1
     * @param sourceFile 压缩文件夹路径
     * @param out  压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *             false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(File sourceFile, OutputStream out, boolean KeepDirStructure)
            throws RuntimeException{
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 递归压缩方法
     * @param sourceFile 源文件
     * @param zos    zip输出流
     * @param name    压缩后的名称
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *             false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[2*1024];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(),KeepDirStructure);
                    }
                }
            }
        }
    }
}
