package com.example.bookmanage.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.example.bookmanage.common.config.FileProperties;
import com.example.bookmanage.common.exception.BusinessException;
import com.example.bookmanage.common.exception.ErrorCode;
import com.example.bookmanage.model.vo.FileUploadVO;
import com.example.bookmanage.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final FileProperties fileProperties;

    public FileServiceImpl(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    @Override
    public FileUploadVO upload(MultipartFile file) {

        // 1. 校验文件非空
        validateFileNotEmpty(file);

        // 2. 校验文件大小
        validateFileSize(file);

        // 3. 校验扩展名

        String extension = getAndValidateExtension(file);

        // 4生成存储路径
        String relativePath = generateRelativePath(extension);

        // 5 写入磁盘
        saveToFileSystem(file, relativePath);
        log.info("文件上传ok,originalName={},url={},size={}", file.getOriginalFilename(), relativePath, file.getSize());

        return new FileUploadVO()
                .setUrl(relativePath)
                .setOriginalName(file.getOriginalFilename())
                .setSize(file.getSize());
    }

    @Override
    public void download(String filePath, HttpServletResponse response) {

        Path path = Paths.get(fileProperties.getUploadDir(), filePath);
        if (!Files.exists(path)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件不存在");

        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + path + "\"");
        try {
            Files.copy(path, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 校验非空文件
     *
     * @param file
     */

    private void validateFileNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件不能为空");
        }
    }

    /**
     * 校验文件大小
     *
     * @param file
     */

    private void validateFileSize(MultipartFile file) {

        if (file.getSize() > fileProperties.getMaxSize()) {
            long maxSize = fileProperties.getMaxSize() / (1024 * 1024);
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件大小超过限制，最大运行" + maxSize + "MB");
        }
    }

    /**
     * 获取并校验文件扩展名
     *
     * @param file
     * @return
     */
    private String getAndValidateExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "文件缺少扩展名");
        }
        // 截取最后一个点号之后的内容，转小写
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!fileProperties.getAllowedExtensions().contains(extension)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不支持的文件类型");
        }
        return extension;
    }

    /**
     * 相对存储路径
     *
     * @param extension 文件扩展名
     * @return 相对路径
     */
    private String generateRelativePath(String extension) {
        String dateDir = LocalDate.now().format(dateTimeFormatter);
        String uuid = IdUtil.simpleUUID();
        return dateDir + "/" + uuid + "." + extension;
    }

    private void saveToFileSystem(MultipartFile file, String relativePath) {
        String fullPath = fileProperties.getUploadDir() + "/" + relativePath;
        //     自动创建子目录
        FileUtil.mkParentDirs(fullPath);

        try {
            file.transferTo(new File(fullPath));
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件保存失败请重试");
        }
    }
}
