package com.example.bookmanage.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.file")
public class FileProperties {
    private String uploadDir = "./uplaods";
    private long maxSize = 10 * 1024 * 1024; // 10mb
    private List<String> allowedExtensions = Arrays.asList(
            "jpg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx", "txt", "zip"
    );

}
