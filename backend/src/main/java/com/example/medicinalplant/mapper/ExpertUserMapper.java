package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.ExpertUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ExpertUserMapper {
    @Select("SELECT * FROM expert_user WHERE username = #{username}")
    ExpertUser findByUsername(String username);

    @Insert("INSERT INTO expert_user(username, password, name, title, avatar_url) " +
            "VALUES(#{username}, #{password}, #{name}, #{title}, #{avatarUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ExpertUser expertUser);

    @Select("SELECT * FROM expert_user WHERE id = #{id}")
    ExpertUser findById(Integer id);

    @Update("UPDATE expert_user SET name = #{name}, title = #{title}, organization = #{organization}, " +
            "phone = #{phone}, email = #{email}, avatar_url = #{avatarUrl}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int updateProfile(ExpertUser expertUser);

    @Update("UPDATE expert_user SET password = #{password}, updated_at = NOW() WHERE id = #{id}")
    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    @Select("SELECT * FROM expert_user ORDER BY created_at DESC")
    java.util.List<ExpertUser> findAll();

    @Update("UPDATE expert_user SET role = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    @Delete("DELETE FROM expert_user WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM expert_user")
    int countTotal();

    @Select("SELECT DATE(created_at) as date, COUNT(*) as count FROM expert_user WHERE created_at >= DATE_SUB(NOW(), INTERVAL #{days} DAY) GROUP BY DATE(created_at) ORDER BY date")
    java.util.List<java.util.Map<String, Object>> countByDate(@Param("days") int days);
}