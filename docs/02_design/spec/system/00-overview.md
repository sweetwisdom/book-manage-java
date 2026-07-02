# 系统架构概览

> 模板占位文档。从 next-mohub 复制完整 spec 或按项目填写。

## 六层架构

```
Data → Version → Session → Semantic → Evidence → Policy
```

## 各层职责（摘要）

| 层 | 职责 | Owner 写入 |
|----|------|------------|
| Data | CAS 对象（不可变字节） | Data |
| Version | Refs、commits、PR 状态 | Version |
| Session | 会话成员、草稿检查点 | Session |
| Semantic | IR、ARM、ChangeSet | Semantic |
| Evidence | 运行、检查、门禁记录 | Evidence |
| Policy | 能力、策略、信任根 | Policy |

## 下一步

1. 复制 `next-mohub/docs/02_design/spec/system/*.md` 作为起点，或
2. 按你的领域重写各层规范
