package com.example.bookmanage.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileUploadVO {
    private  String url;
    private  String originalName;
    private  long size;
}
