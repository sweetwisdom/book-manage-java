---
name: env-scanner
description: >
  Scan the full development environment and generate a structured Markdown report.
  Use this skill whenever the user wants to check what's installed on a machine,
  audit their dev toolchain, inventory a new workstation, or generate an environment
  snapshot document. Trigger on phrases like "what's installed", "check my environment",
  "system info", "what do I have", "环境信息", "系统盘点", "装了哪些工具",
  or any request to scan/audit/report on installed development tools, languages,
  package managers, version managers, editors, or system utilities.
---

# Environment Scanner

Generate a comprehensive, categorized Markdown report of the current machine's
development environment. The report covers system info, programming languages,
version managers, package managers, editors, and system-level tools.

## Workflow

### 1. Detect platform

Run `uname -s` (or check `$OSTYPE`) to determine the OS:
- **Windows**: look for `MINGW`, `MSYS`, `CYGWIN`, or `Windows_NT`
- **macOS**: `Darwin`
- **Linux**: `Linux`

Adapt all subsequent commands to the detected platform (see command tables below).

### 2. Scan in parallel batches

Run independent checks in parallel to save time. Group related tools into batches:

**Batch A — System info**
- OS name, version, kernel
- CPU model and cores
- Memory (total / used / swap)
- Disk usage (root partition)
- Hostname, uptime
- WSL distros (Windows only)

**Batch B — Version managers** (check these FIRST, as they override system defaults)
- **Node**: Volta (`volta --version`), nvm (`nvm --version`), fnm (`fnm --version`), mise/asdf (`mise --version`)
- **Python**: pyenv (`pyenv --version`), uv (`uv --version`), conda (`conda --version`), virtualenv
- **Java**: sdkman (`sdk version`), jabba (`jabba --version`)
- **Rust**: rustup (`rustup --version`)
- **Go**: goenv (`goenv --version`), g (g --version)
- **Ruby**: rbenv (`rbenv --version`), rvm (`rvm --version`)

For each detected version manager, also list installed versions (e.g., `volta list all`,
`pyenv versions`, `sdk list java`, `rustup show`).

**Batch C — Programming languages & runtimes**
- Python, Node.js, Java, Go, Rust, Ruby, PHP, Perl, .NET, Swift, Kotlin, Dart, Lua, Elixir

**Batch D — Package managers**
- **JS/TS**: npm, pnpm, yarn, bun
- **Python**: pip, pipx, poetry, pdm
- **System**: brew (macOS), apt/yum/pacman (Linux), winget/scoop/choco (Windows)
- **Cross-platform**: cargo, gem, composer, mix

**Batch E — Editors & IDEs**
- VS Code, Zed, Vim, Neovim, Emacs, JetBrains Toolbox, Sublime Text

**Batch F — DevOps & system tools**
- Git, Docker, Podman, kubectl, terraform, ansible
- cmake, make, gcc/g++, clang, zig
- ffmpeg, imagemagick, sqlite3, redis-cli, psql, mysql
- gh (GitHub CLI), glab (GitLab CLI)
- nginx, caddy, apache
- tmux, screen, jq, yq, ripgrep, fd, fzf, bat, lazygit

**Batch G — Shell & CLI environment**
- Shell (bash, zsh, fish, PowerShell)
- Terminal multiplexer (tmux, screen)
- Modern CLI replacements (bat, eza/exa, ripgrep, fd, delta, lazygit)

### 3. Command reference by platform

Use the appropriate command for each platform. All commands should be wrapped in
error handling — if a command is not found, record "not installed".

| Check | Windows (bash/MSYS) | macOS / Linux |
|-------|---------------------|---------------|
| OS info | `systeminfo` or `cmd.exe /c ver` | `uname -a` + `lsb_release -a` (Linux) / `sw_vers` (macOS) |
| CPU | `wmic cpu get Name` | `lscpu` / `sysctl -n machdep.cpu.brand_string` |
| Memory | `wmic OS get TotalVisibleMemorySize` | `free -h` / `sysctl -n hw.memsize` |
| Disk | `wmic logicaldisk get size,freespace` | `df -h /` |
| Version manager versions | `volta list all`, `pyenv versions`, `sdk list` | Same |
| Default shell | `$SHELL` | `$SHELL` |
| WSL | `wsl --list --verbose` | N/A |
| Brew | N/A | `brew --version` |

### 4. Generate the report

Output a single Markdown file named `dev-environment.md` (Chinese user convention:
`环境报告.md` is also fine if the conversation language is Chinese) in the current
working directory.

Use this structure:

```markdown
# 开发环境报告

> 生成日期：YYYY-MM-DD

## 系统信息

| 项目 | 详情 |
|------|------|
| 设备 | ... |
| 系统 | ... |
| ...  | ... |

## 版本管理器

| 工具 | 版本 | 管理的运行时 | 已安装版本 | 默认版本 |
|------|------|-------------|-----------|---------|
| Volta | 2.0.2 | Node.js, yarn | 14.18.3, 16.18.1, 22.12.0, 24.14.1 | 22.12.0 |
| ...  | ...  | ...         | ...       | ...     |

## 编程语言 & 运行时

| 语言 | 版本 | 备注 |
|------|------|------|
| ...  | ...  | ...  |

## 包管理器

### 语言级包管理器
| 工具 | 版本 | 备注 |
|------|------|------|
| ...  | ...  | ...  |

### 系统级包管理器
| 工具 | 版本 | 备注 |
|------|------|------|
| ...  | ...  | ...  |

## 编辑器 & IDE

| 工具 | 版本 |
|------|------|
| ...  | ...  |

## DevOps & 系统工具

| 工具 | 版本 | 状态 |
|------|------|------|
| ...  | ...  | 已安装 / 未安装 |

## Shell & CLI 环境

| 工具 | 版本 |
|------|------|
| ...  | ...  |
```

### 5. Reporting rules

- **Version managers first**: if a version manager is detected, list its managed
  runtimes and installed versions BEFORE listing the runtime itself. This prevents
  confusion (e.g., "Node.js 22.12.0" without mentioning Volta is misleading).
- **Explicit "not installed"**: for commonly expected tools that are missing, still
  list them with status "未安装" so the user has a complete picture. Only include
  tools from the "DevOps & system tools" and "Shell & CLI" categories — don't list
  every possible language as "not installed".
- **No recommendations**: this skill only scans and reports. Do not suggest
  installing missing tools or upgrading old versions unless the user explicitly asks.
- **Parallel execution**: maximize parallelism when running scan commands — group
  independent checks into parallel batches as described in step 2.
