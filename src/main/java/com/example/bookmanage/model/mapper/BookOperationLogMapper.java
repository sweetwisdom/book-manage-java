package com.example.bookmanage.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bookmanage.model.entity.BookOperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookOperationLogMapper extends BaseMapper<BookOperationLog> {
}
