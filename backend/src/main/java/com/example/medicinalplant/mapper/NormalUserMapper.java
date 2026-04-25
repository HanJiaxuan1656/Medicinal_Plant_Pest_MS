package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.NormalUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface NormalUserMapper {
    @Select("SELECT * FROM normal_user WHERE username = #{username}")
    NormalUser findByUsername(String username);

    @Insert("INSERT INTO normal_user(username, password, nickname, avatar_url) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{avatarUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NormalUser normalUser);

    @Select("SELECT * FROM normal_user WHERE id = #{id}")
    NormalUser findById(Integer id);

    @Update("UPDATE normal_user SET nickname = #{nickname}, " +
            "phone = #{phone}, email = #{email}, avatar_url = #{avatarUrl} " +
            "WHERE id = #{id}")
    int update(NormalUser normalUser);

    @Update("UPDATE normal_user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    @Select("SELECT * FROM normal_user ORDER BY created_at DESC")
    java.util.List<NormalUser> findAll();

    @Update("UPDATE normal_user SET role = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    @Delete("DELETE FROM normal_user WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM normal_user")
    int countTotal();

    @Select("SELECT DATE(created_at) as date, COUNT(*) as count FROM normal_user WHERE created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY) GROUP BY DATE(created_at) ORDER BY date")
    java.util.List<java.util.Map<String, Object>> countByDate(@Param("days") int days);
}