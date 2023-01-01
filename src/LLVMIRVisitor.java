import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import Scope.*;
import Type.*;
import Symbol.*;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.llvm.global.LLVM.*;

public class LLVMIRVisitor extends SysYParserBaseVisitor<LLVMValueRef> {
	private static int depth = 0;
	private final List<Object> msgToPrint = new ArrayList<>();
	private GlobalScope globalScope = null;
	private Scope currentScope = null;
	private int localScopeCounter = 0;
	private boolean errorFound = false;
	
	private final LLVMModuleRef module = LLVMModuleCreateWithName("module");
	private final LLVMBuilderRef builder = LLVMCreateBuilder();
	private final LLVMTypeRef i32Type = LLVMInt32Type();
	private final LLVMTypeRef voidType = LLVMVoidType();

//	public List<Object> getMsgToPrint() {
//		return msgToPrint;
//	}

//	public boolean getErrorFound() {
//		return errorFound;
//	}

//	private int getLineNo(ParserRuleContext ctx) {
//		return ctx.getStart().getLine();
//	}

//	private void reportError(int typeNo, int lineNo, String msg) {
//		System.err.println("Error type " + typeNo + " at Line " + lineNo + ": " + msg + ".");
//		errorFound = true;
//	}

//	private String ident2String(int depth) {
//		return "  ".repeat(Math.max(0, depth));
//	}
	
	//	private String getHelight(String ruleName) {
//		switch (ruleName) {
//			case "CONST":
//			case "INT":
//			case "VOID":
//			case "IF":
//			case "ELSE":
//			case "WHILE":
//			case "BREAK":
//			case "CONTINUE":
//			case "RETURN": {
//				return "orange";
//			}
//			case "PLUS":
//			case "MINUS":
//			case "MUL":
//			case "DIV":
//			case "MOD":
//			case "ASSIGN":
//			case "EQ":
//			case "NEQ":
//			case "LT":
//			case "GT":
//			case "LE":
//			case "GE":
//			case "NOT":
//			case "AND":
//			case "OR": {
//				return "blue";
//			}
//			case "IDENT": {
//				return "red";
//			}
//			case "INTEGR_CONST": {
//				return "green";
//			}
//			default: {
//				return "no color";
//			}
//		}
//	}
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
			int number = (int) Long.parseLong(toDecimalInteger(node.getText()));
			return LLVMConstInt(i32Type, number, 1);
		}
		
		return super.visitTerminal(node);
	}
	
	@Override
	public LLVMValueRef visitProgram(SysYParser.ProgramContext ctx) {
		currentScope = globalScope = new GlobalScope(null);
		LLVMValueRef ret = super.visitProgram(ctx);
		currentScope = currentScope.getEnclosingScope();
		return ret;
	}
	
	@Override
	public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
		String retTypeName = ctx.funcType().getText();
		LLVMTypeRef retType;
		if (retTypeName.equals("int")) {
			retType = i32Type;
		} else {
			retType = voidType;
		}
		
		int paramsCount = 0;
		if (ctx.funcFParams() != null) {
			paramsCount = ctx.funcFParams().funcFParam().size();
		}
		
		PointerPointer<Pointer> paramsTypes = new PointerPointer<>(paramsCount);
		for (int i = 0; i < paramsCount; ++i) {
			SysYParser.FuncFParamContext funcFParamContext = ctx.funcFParams().funcFParam(i);
			String paramTypeName = funcFParamContext.bType().getText();
			if (paramTypeName.equals("int")) {
				paramsTypes.put(i, i32Type);
			} else {
				paramsTypes.put(i, voidType);
			}
		}
		LLVMTypeRef functionType = LLVMFunctionType(retType, paramsTypes, paramsCount, 0);
		
		String functionName = ctx.IDENT().getText();
		LLVMValueRef function = LLVMAddFunction(module, functionName, functionType);
		LLVMBasicBlockRef mainEntry = LLVMAppendBasicBlock(function, functionName + "Entry");
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
		LLVMValueRef valueRef1 = visit(ctx.exp(0));
		LLVMValueRef valueRef2 = visit(ctx.exp(1));
		if (ctx.PLUS() != null) {
			return LLVMBuildAdd(builder, valueRef1, valueRef2, "tmp_");
		} else {
			return LLVMBuildSub(builder, valueRef1, valueRef2, "tmp_");
		}
	}
	
	@Override
	public LLVMValueRef visitMulExp(SysYParser.MulExpContext ctx) {
		LLVMValueRef valueRef1 = visit(ctx.exp(0));
		LLVMValueRef valueRef2 = visit(ctx.exp(1));
		if (ctx.MUL() != null) {
			return LLVMBuildMul(builder, valueRef1, valueRef2, "tmp_");
		} else if (ctx.DIV() != null) {
			return LLVMBuildSDiv(builder, valueRef1, valueRef2, "tmp_");
		} else {
			return LLVMBuildSRem(builder, valueRef1, valueRef2, "tmp_");
		}
	}
	
	@Override
	public LLVMValueRef visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
		LLVMValueRef result = visit(ctx.exp());
		return LLVMBuildRet(builder, result);
	}
}
