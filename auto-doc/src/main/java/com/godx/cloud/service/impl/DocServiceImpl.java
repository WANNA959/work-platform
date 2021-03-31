package com.godx.cloud.service.impl;

import com.godx.cloud.service.DocService;
import com.godx.cloud.utils.DbUtil;
import com.godx.cloud.utils.VelocityUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class DocServiceImpl implements DocService {

    public void generateDocDbInfo(String username,String password,String host,String port,String database) throws SQLException {
        Connection connection = DbUtil.mySQLOpen(username, password, host, port, database);
        VelocityUtil.loadContext(connection,database);
    }
}
