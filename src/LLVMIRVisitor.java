import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class LLVMIRVisitor extends SysYParserBaseVisitor<LLVMValueRef> {
	private final LLVMModuleRef module = LLVMModuleCreateWithName("moudle");
	private final LLVMBuilderRef builder = LLVMCreateBuilder();
	private final LLVMTypeRef i32Type = LLVMInt32Type();
	
	public LLVMIRVisitor() {
		LLVMInitializeCore(LLVMGetGlobalPassRegistry());
		LLVMLinkInMCJIT();
		LLVMInitializeNativeAsmPrinter();
		LLVMInitializeNativeAsmParser();
		LLVMInitializeNativeTarget();
	}
	
	public LLVMModuleRef getModule() {
		return module;
	}
	
	private String toDecimalInteger(String tokenText) {
		if (tokenText.startsWith("0x") || tokenText.startsWith("0X")) {
			tokenText = String.valueOf(Integer.parseInt(tokenText.substring(2), 16));
		} else if (tokenText.startsWith("0")) {
			tokenText = String.valueOf(Integer.parseInt(tokenText, 8));
		}
		return tokenText;
	}
	
	@Override
	public LLVMValueRef visitTerminal(TerminalNode node) {
		Token symbol = node.getSymbol();
		int symbolType = symbol.getType();
		
		if (symbolType == SysYParser.INTEGR_CONST) {
			int number = Integer.parseInt(toDecimalInteger(node.getText()));
			return LLVMConstInt(i32Type, number, 1);
		}
		
		return super.visitTerminal(node);
	}
	
	@Override
	public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
		LLVMTypeRef functionType = LLVMFunctionType(i32Type, LLVMVoidType(), 0, 0);
		String functionName = ctx.IDENT().getText();
		LLVMValueRef function = LLVMAddFunction(module, functionName, functionType);
		LLVMBasicBlockRef mainEntry = LLVMAppendBasicBlock(function, "mainEntry");
		LLVMPositionBuilderAtEnd(builder, mainEntry);
		super.visitFuncDef(ctx);
		
		return function;
	}
	
	@Override
	public LLVMValueRef visitUnaryExp(SysYParser.UnaryExpContext ctx) {
		String operator = ctx.unaryOp().getText();
		LLVMValueRef expValue = visit(ctx.exp());
		switch (operator) {
			case "+": {
				return expValue;
			}
			case "-": {
				return LLVMBuildNeg(builder, expValue, "tmp_");
			}
			case "!": {
				long numValue = LLVMConstIntGetZExtValue(expValue);
				if (numValue == 0) {
					return LLVMConstInt(i32Type, 1, 1);
				} else {
					return LLVMConstInt(i32Type, 0, 1);
				}
			}
			default: {
			}
		}
		
		return super.visitUnaryExp(ctx);
	}
	
	@Override
	public LLVMValueRef visitParenExp(SysYParser.ParenExpContext ctx) {
		return this.visit(ctx.exp());
	}
	
	
	@Override
	public LLVMValueRef visitAddExp(SysYParser.AddExpContext ctx) {
		if (ctx.PLUS() != null) {
			return binaryOperation("+", visit(ctx.exp(0)), visit(ctx.exp(1)));
		} else {
			return binaryOperation("-", visit(ctx.exp(0)), visit(ctx.exp(1)));
		}
	}
	
	@Override
	public LLVMValueRef visitMulExp(SysYParser.MulExpContext ctx) {
		if (ctx.MUL() != null) {
			return binaryOperation("*", visit(ctx.exp(0)), visit(ctx.exp(1)));
		} else if (ctx.DIV() != null) {
			return binaryOperation("/", visit(ctx.exp(0)), visit(ctx.exp(1)));
		} else {
			return binaryOperation("%", visit(ctx.exp(0)), visit(ctx.exp(1)));
		}
	}
	
	private LLVMValueRef binaryOperation(String operator, LLVMValueRef valueRef1, LLVMValueRef valueRef2) {
		long numValue1 = LLVMConstIntGetZExtValue(valueRef1);
		long numValue2 = LLVMConstIntGetZExtValue(valueRef2);
		switch (operator) {
			case "+":
//				return LLVMBuildAdd(builder, valueRef1, valueRef2, "tmp_");
				return LLVMConstInt(i32Type, numValue1 + numValue2, 1);
			case "-":
//				return LLVMBuildSub(builder, valueRef1, valueRef2, "tmp_");
				return LLVMConstInt(i32Type, numValue1 - numValue2, 1);
			case "*":
//				return LLVMBuildMul(builder, valueRef1, valueRef2, "tmp_");
				return LLVMConstInt(i32Type, numValue1 * numValue2, 1);
			case "/":
				return LLVMBuildSDiv(builder, valueRef1, valueRef2, "tmp_");
			case "%":
				return LLVMBuildSRem(builder, valueRef1, valueRef2, "tmp_");
			default:
				return null;
		}
	}
	
	@Override
	public LLVMValueRef visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
		LLVMValueRef result = visit(ctx.exp());
		return LLVMBuildRet(builder, result);
	}
}
