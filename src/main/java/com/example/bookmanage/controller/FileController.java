package com.example.bookmanage.controller;

import com.example.bookmanage.common.response.ApiResponse;
import com.example.bookmanage.model.vo.FileUploadVO;
import com.example.bookmanage.service.FileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件上传
 */
@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }
   @PostMapping("/upload")
    public ApiResponse<FileUploadVO> upload(@RequestParam("file") MultipartFile file) {
        FileUploadVO result = fileService.upload(file);
        return ApiResponse.success(result);
    }
    @GetMapping("/download")
    public void download(HttpServletResponse response, String filePath){
        fileService.download(filePath,response);
    }
}
