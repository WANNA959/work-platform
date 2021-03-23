package com.godx.cloud.dao;

import com.godx.cloud.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {

    User getUserById(int id);

    User getUserByUsername(String name);

    User getUserByEmail(String email);

    int insertUser(User user);

    int updateStatusById(@Param("id") int id,@Param("status") int status);

    int updateCodeById(@Param("id") int id,@Param("code") String code);

    int updatePassById(@Param("id") int id,@Param("pass") String pass);
}
