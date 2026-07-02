# Prompt 目录 (prompts/)

本目录存放项目中使用的所有 Prompt 模板，按照用途进行分类管理。

## 目录结构

```
prompts/
├── 00_conventions.md      # Prompt 书写规范
├── 01_requirements/       # 需求类 Prompt
├── 02_design/             # 设计类 Prompt
├── 03_implementation/     # 代码生成 Prompt
├── 04_debug/              # 调试类 Prompt
├── 05_docs/               # 文档类 Prompt
└── 99_archive/            # 归档的 Prompt
```

## 各子目录说明

### 00_conventions.md - Prompt 书写规范
定义 Prompt 的书写规范，包括：
- 文件命名规范
- 模板结构
- 变量命名规范
- 输出格式约束

### 01_requirements/ - 需求类 Prompt
用于需求分析和处理的 Prompt：
- 需求拆解
- 需求澄清
- 验收标准生成
- 用户故事编写

### 02_design/ - 设计类 Prompt
用于架构和设计的 Prompt：
- 系统架构设计
- 数据流设计
- API 接口设计
- 数据库 Schema 设计

### 03_implementation/ - 代码生成 Prompt
用于代码实现的 Prompt：
- 模块代码生成
- 函数实现
- 单元测试生成
- 代码重构

### 04_debug/ - 调试类 Prompt
用于问题排查的 Prompt：
- 日志分析
- 根因定位
- 修复方案生成
- 性能优化建议

### 05_docs/ - 文档类 Prompt
用于文档生成的 Prompt：
- README 生成
- API 文档生成
- 教程编写
- FAQ 生成

### 99_archive/ - 归档的 Prompt
存放已过期但仍有参考价值的 Prompt：
- 标注过期原因
- 保留历史版本
- 供后续参考

## 使用指南

1. **查找 Prompt**：根据任务类型进入对应目录
2. **使用 Prompt**：复制 Prompt 内容，替换变量后使用
3. **新增 Prompt**：遵循 `00_conventions.md` 中的规范
4. **更新 Prompt**：更新版本号，记录变更历史
5. **归档 Prompt**：过期的 Prompt 移至 `99_archive/`

## 质量要求

- 每个 Prompt 必须经过实际测试
- 必须包含使用示例
- 必须定义输出格式约束
- 定期审查和更新
