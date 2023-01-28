# 编译原理 Lab8 实验报告

## 实现功能

本次实验完成了以下功能：

1. 一维数组作为函数参数

## 实验设计

本次实验主要修改了 `visitLVal()` 和 `visitLValExp()` 等方法。

```java
@Override
public LLVMValueRef visitLValExp(SysYParser.LValExpContext ctx) {
    LLVMValueRef lValPointer = this.visitLVal(ctx.lVal());
    if (arrayAddr) {
        arrayAddr = false;
        return lValPointer;
    }
    return LLVMBuildLoad(builder, lValPointer, ctx.lVal().getText());
}

@Override
public LLVMValueRef visitLVal(SysYParser.LValContext ctx) {
    String lValName = ctx.IDENT().getText();
    LLVMValueRef varPointer = currentScope.resolve(lValName);
    LLVMTypeRef varType = currentScope.getType(lValName);
    if (varType.equals(i32Type)) {
        return varPointer;
    } else if (varType.equals(intPointerType)) {
        if (ctx.exp().size() > 0) {
            LLVMValueRef[] arrayPointer = new LLVMValueRef[1];
            arrayPointer[0] = this.visit(ctx.exp(0));
            PointerPointer<LLVMValueRef> indexPointer = new PointerPointer<>(arrayPointer);
            LLVMValueRef pointer = LLVMBuildLoad(builder, varPointer, lValName);
            return LLVMBuildGEP(builder, pointer, indexPointer, 1, "&" + lValName);
        } else {
            return varPointer;
        }
    } else {
        LLVMValueRef[] arrayPointer = new LLVMValueRef[2];
        arrayPointer[0] = zero;
        if (ctx.exp().size() > 0) {
            arrayPointer[1] = this.visit(ctx.exp(0));
        } else {
            arrayAddr = true;
            arrayPointer[1] = zero;
        }
        PointerPointer<LLVMValueRef> indexPointer = new PointerPointer<>(arrayPointer);
        return LLVMBuildGEP(builder, varPointer, indexPointer, 2, "&" + lValName);
    }
}
```

## 实验困难

笔者完成实验代码编写后，遇到了 OJ 的报错：

```
lli-13: lli: out.ir:44:1: error: expected instruction opcode } ^
```

笔者与同学交流发现，在返回值为 `int` 的函数末尾添加 `ret i32 0` 语句即可通过 OJ 的所有测试用例。笔者推测测试用例中存在没有正确返回返回值的函数。

