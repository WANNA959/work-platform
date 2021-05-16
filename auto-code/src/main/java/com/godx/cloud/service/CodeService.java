package com.godx.cloud.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface CodeService {

    String getMybatisCode(String username, String password,String host,String port, String database, List<String> tables,String token,List<String> type) throws IOException, SQLException;

}
