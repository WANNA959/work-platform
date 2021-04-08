package com.godx.cloud.service;

import com.godx.cloud.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface DocService {

    public String getDbInfoDoc(Connection connection, String datadata, User user) throws IOException, SQLException;
}
