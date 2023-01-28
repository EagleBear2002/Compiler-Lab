# 《编译原理》课程实验

## 项目功能

该项目完成了从 SysY 语言的子集到 LLVMIR 的编译过程。

SysY 语言的介绍参考 [SysY语言定义](docus/SysY语言定义.pdf)。

## 实验报告

8 个实验的实验报告在 `/docus` 目录中，包含每个实验的功能、设计和问题解决。

## 运行说明

### 操作系统

使用 Windows + WSL2（Ubuntu 20.04）完成项目开发。

在 Windows 系统中，使用 IDEA 也可运行。

使用 JDK 11 完成实验。

### 配置 Antlr4

若使用 IDE，仅需安装 Antlr 的插件即可，以 IDEA 为例，在左上角，文件->设置->插件，搜索 ANTLR4 即可找到插件，下载后重启即可，之后可通过该插件处理 `.g4` 文件。

若文本编辑器则需自行安装 Antlr

前往 [Antlr 官网](https://www.antlr.org/download.html)，下载 Complete ANTLR 4.9.X.jar，对于 Linux 也可以选择从命令行，使用：

```shell
curl -O https://www.antlr.org/download/antlr-4.9.1-complete.jar
```

### 配置 LLVM

Linux 下安装：

```bash
sudo apt-get install llvm
sudo apt-get install clang
```

安装后可通过如下命令测试，出现版本信息即为安装成功：

```bash
clang -v
lli --version
```

### 问题解决

配置过程中遇到的问题解决方案参见：

1. [IDEA导入ANTLR包的方式](IDEA导入ANTLR包的方式.pdf)
2. [lab4及后续实验环境搭建](lab4及后续实验环境搭建.pdf)
3. [LLVM](LLVM.pdf)