package com.example.bookmanage.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bookmanage.model.entity.Book;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书Mapper
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
