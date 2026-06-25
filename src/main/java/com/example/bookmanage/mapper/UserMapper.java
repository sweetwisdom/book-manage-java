package com.example.bookmanage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface UserMapper  extends BaseMapper<User> {

}
