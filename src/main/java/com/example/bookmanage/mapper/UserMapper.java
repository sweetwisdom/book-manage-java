package com.example.bookmanage.mapper;

import com.example.bookmanage.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper - 操作数据库
 */
@Mapper
public interface UserMapper {

    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM user")
    List<User> findAll();

    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Long id);

    /**
     * 根据名称查询用户
     */
    @Select("SELECT * FROM user WHERE name = #{name}")
    User findByName(@Param("name") String name);

    /**
     * 插入用户
     */
    @Insert("INSERT INTO user (name, age) VALUES (#{name}, #{age})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}
