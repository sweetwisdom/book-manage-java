# 产物目录 (artifacts/)

本目录存放项目全流程中产生的各类产物，是实现"可追溯、可审计"的核心目录。

## 目录结构

```
artifacts/
├── 00_raw/                    # 原始材料
│   ├── inputs/                # 输入数据/样例
│   └── references/            # 参考资料
├── 01_ai_outputs/             # AI 原始输出
│   ├── chat_transcripts/      # 对话记录
│   ├── codegen_drafts/        # 代码草稿
│   └── eval_notes/            # 评审记录
├── 02_human_edits/            # 人工修改版本
├── 03_diff_and_reviews/       # 差异与评审
└── 04_release_snapshots/      # 里程碑快照
```

## 各子目录说明

### 00_raw/ - 原始材料
存放项目的原始输入材料，**不建议随意修改**。

#### inputs/
- 输入数据样例（需脱敏）
- 测试数据集
- 用户提供的原始文件

#### references/
- 参考文档
- 技术规范
- 外部链接清单

### 01_ai_outputs/ - AI 原始输出
存放 AI 生成的原始内容，**必须原封不动保存**，用于审计和对比。

#### chat_transcripts/
- ChatGPT 对话导出
- Claude 对话记录
- 其他 AI 工具对话

命名规范：`[日期]_[模型]_[主题].md`

#### codegen_drafts/
- AI 生成的代码草稿
- 代码片段
- 生成的配置文件

#### eval_notes/
- AI 评审意见
- AI 评分结果
- AI 建议原文

### 02_human_edits/ - 人工修改版本
存放人类基于 AI 输出修改后的版本，用于对比分析。

命名规范：`[原文件名]_human_edit_v[版本号].[扩展名]`

### 03_diff_and_reviews/ - 差异与评审
存放 AI 输出与人工修改之间的差异记录和评审意见。

内容包括：
- diff 文件
- 评审记录
- 决策理由说明

### 04_release_snapshots/ - 里程碑快照
存放各里程碑的完整快照，用于复现和演示。

推荐结构：
```
04_release_snapshots/
├── M1_end_of_day1/
├── M2_end_of_day2/
├── M3_end_of_day3/
└── M4_final_demo/
```

每个快照应包含：
- 可运行说明
- Demo 脚本
- 关键截图（1-3 张）
- 对应 commit hash

## 证据链说明

本目录的设计目的是建立完整的证据链：

```
Prompt (prompts/)
    ↓
AI 原始输出 (01_ai_outputs/)
    ↓
人工修改 (02_human_edits/)
    ↓
差异记录 (03_diff_and_reviews/)
    ↓
最终交付 (04_release_snapshots/)
```

这套证据链可用于：
- PPT 演示
- 项目复盘
- 培训教学
- 审计追溯

## 注意事项

1. **01_ai_outputs/** 中的文件禁止修改
2. 敏感数据必须脱敏后再存放
3. 大文件考虑使用 Git LFS 或外部存储
4. 定期清理临时文件，保持目录整洁
