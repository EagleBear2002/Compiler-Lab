# 编译原理 Lab6 实验报告

## 实现功能

本次实验完成了以下功能：

1. 全局变量声明和使用
1. 条件语句

## 实验设计

本次实验重新设计了 `LLVMVisitor` 类，其中全局变量相关内容主要是对 `visitVarDecl` 方法的修改。

## 实验困难

笔者实验过程中被三个困难的测试用例，报错信息如下：

```
hardtest1.sy：0（Aborted (core dumped) ）
hardtest2.sy：0（Aborted (core dumped) ）
hardtest3.sy：0（Aborted (core dumped) ）
```

和顾龙助教沟通后，构建了引发错误的测试用例：

```
int a[10] = {};

int f(int a) {
	return 0;
}

int main() {
	a[1] = 1;
	return 0;
}
```

通过输出日志发现，函数参数 `a` 被定义在了全局作用域内，这是 `visitFuncDef` 方法当中的疏忽所致，修复了该 bug 后完成了本次实验。
