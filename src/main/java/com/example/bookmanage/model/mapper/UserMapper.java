package com.example.bookmanage.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bookmanage.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper - 操作数据库
 */
@Mapper
public interface UserMapper  extends BaseMapper<User> {

}
