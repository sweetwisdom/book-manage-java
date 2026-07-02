package com.example.bookmanage.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基础控制器（保留路径前缀 /api）
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api")
public class BasicController {
}
