# 编译原理 Lab1 实验报告

## 实现功能

本次实验完成了以下功能：

1. 对 SysY 语言进行词法分析并报告错误的词法单元位置
2. 对不同进制的字面整型值进行转换

## 实验困难

实验所遇到的最大困难是使用 `make antlr` 时报错：

```bash
eaglebear2002@EagleBear2002-HP:/mnt/c/Users/37756/Documents/NJU/2022Fall/Compilers/Lab$ make antlr
java -jar /usr/local/lib/antlr-*-complete.jar -listener -visitor -long-messages  ./src/SysYLexer.g4
error(99): ./src/SysYLexer.g4::: grammar SysYLexer has no non-fragment rules
make: *** [Makefile:32: antlr] Error 1
```

笔者不理解该报错信息，反复检查词法规约，确认无误后在 Google 等搜索引擎需求帮助，未果。

笔者在检查项目目录时发现了问题所在：

笔者使用命令行编译项目在 `out` 目录中得到了 `SysYLexer.g4` 文件，并无意中注释掉该文件中所有词法规约内容。笔者之后未更改 `out/SysYLexer.g4` 内容，而是在修改 `src/SysYLexer.g4`。后面使用命令行编译时，系统总是默认先对 `out/SysYLexer.g4` 进行编译。

笔者删掉 `./out` 目录后解决了这一困难。