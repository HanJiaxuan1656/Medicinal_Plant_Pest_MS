package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.Admin;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminMapper {
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin findByUsername(String username);

    @Insert("INSERT INTO admin(username, password) VALUES(#{username}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);

    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin findById(Integer id);

    @Update("UPDATE admin SET email = #{email}, phone = #{phone}, avatar_url = #{avatarUrl} WHERE id = #{id}")
    int updateProfile(Admin admin);

    @Update("UPDATE admin SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);
}