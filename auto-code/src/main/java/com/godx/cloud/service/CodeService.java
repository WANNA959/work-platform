package com.godx.cloud.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface CodeService {

    void getMybatisCode(String username, String password,String host,String port, String database, List<String> tables,String token) throws IOException, SQLException;

}
