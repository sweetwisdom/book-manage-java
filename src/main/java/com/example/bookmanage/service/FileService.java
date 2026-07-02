package com.example.bookmanage.service;

import cn.hutool.http.server.HttpServerResponse;
import com.example.bookmanage.model.vo.FileUploadVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface FileService {

    FileUploadVO upload(MultipartFile file);
    void  download(String filePath, HttpServletResponse response);
 }
