import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import Scope.*;

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
	
	private LLVMTypeRef getTypeRef(String typeName) {
		if (typeName.equals("int")) {
			return i32Type;
		} else {
			return voidType;
		}
	}
	
	@Override
	public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
		int paramsCount = 0;
		if (ctx.funcFParams() != null) {
			paramsCount = ctx.funcFParams().funcFParam().size();
		}
		
		PointerPointer<Pointer> paramsTypes = new PointerPointer<>(paramsCount);
		for (int i = 0; i < paramsCount; ++i) {
			SysYParser.FuncFParamContext funcFParamContext = ctx.funcFParams().funcFParam(i);
			String paramTypeName = funcFParamContext.bType().getText();
			LLVMTypeRef paramType = getTypeRef(paramTypeName);
			paramsTypes.put(i, paramType);
		}
		
		String retTypeName = ctx.funcType().getText();
		LLVMTypeRef retType = getTypeRef(retTypeName);
		LLVMTypeRef functionType = LLVMFunctionType(retType, paramsTypes, paramsCount, 0);
		
		String functionName = ctx.IDENT().getText();
		LLVMValueRef function = LLVMAddFunction(module, functionName, functionType);
		LLVMBasicBlockRef entry = LLVMAppendBasicBlock(function, functionName + "_entry");
		LLVMPositionBuilderAtEnd(builder, entry);
		
		for (int i = 0; i < paramsCount; ++i) {
			SysYParser.FuncFParamContext funcFParamContext = ctx.funcFParams().funcFParam(i);
			String paramTypeName = funcFParamContext.bType().getText();
			LLVMTypeRef paramType = getTypeRef(paramTypeName);
			String varName = ctx.funcFParams().funcFParam(i).IDENT().getText();
			LLVMValueRef varPointer = LLVMBuildAlloca(builder, paramType, "pointer_" + varName);
			currentScope.define(varName, varPointer);
			LLVMValueRef argValue = LLVMGetParam(function, i);
			LLVMBuildStore(builder, argValue, varPointer);
		}
		
		currentScope.define(functionName, function);
		currentScope = new LocalScope(currentScope);
		super.visitFuncDef(ctx);
		currentScope = currentScope.getEnclosingScope();
		
		return function;
	}
	
	@Override
	public LLVMValueRef visitBlock(SysYParser.BlockContext ctx) {
		LocalScope localScope = new LocalScope(currentScope);
		String localScopeName = localScope.getName() + (localScopeCounter++);
		localScope.setName(localScopeName);
		currentScope = localScope;
		LLVMValueRef ret = super.visitBlock(ctx);
		currentScope = currentScope.getEnclosingScope();
		return ret;
	}
	
	@Override
	public LLVMValueRef visitVarDecl(SysYParser.VarDeclContext ctx) {
		String typeName = ctx.bType().getText();
		
		for (SysYParser.VarDefContext varDefContext : ctx.varDef()) {
			LLVMTypeRef varType = getTypeRef(typeName);
			String varName = varDefContext.IDENT().getText();
			int elementCount = 0;
			
			for (SysYParser.ConstExpContext constExpContext : varDefContext.constExp()) {
				elementCount = Integer.parseInt(toDecimalInteger(constExpContext.getText()));
				varType = LLVMVectorType(varType, elementCount);
				System.err.println("elementCount = " + elementCount);
			}
			
			LLVMValueRef varPointer = LLVMBuildAlloca(builder, varType, "pointer_" + varName);
			
			if (varDefContext.ASSIGN() != null) {
				SysYParser.ExpContext expContext = varDefContext.initVal().exp();
				if (expContext != null) {
					LLVMValueRef initVal = visit(varDefContext.initVal().exp());
					LLVMBuildStore(builder, initVal, varPointer);
				} else {
					int initValCount = varDefContext.initVal().initVal().size();
					for (int i = 0; i < elementCount; ++i) {
						LLVMValueRef initVal;
						if (i < initValCount) {
							initVal = this.visit(varDefContext.initVal().initVal(i).exp());
						} else {
							initVal = LLVMConstInt(i32Type, 0, 1);
						}
						PointerPointer valuePointer = new PointerPointer(new LLVMValueRef[]{initVal});
						LLVMValueRef element = LLVMBuildGEP(builder, varPointer, valuePointer, i, "GEP");
						LLVMBuildStore(builder, initVal, element);
					}
				}
			}
			
			currentScope.define(varName, varPointer);
		}
		
		return super.visitVarDecl(ctx);
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
				return LLVMBuildNeg(builder, expValue, "neg_");
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
			return LLVMBuildAdd(builder, valueRef1, valueRef2, "add_");
		} else {
			return LLVMBuildSub(builder, valueRef1, valueRef2, "sub_");
		}
	}
	
	@Override
	public LLVMValueRef visitMulExp(SysYParser.MulExpContext ctx) {
		LLVMValueRef valueRef1 = visit(ctx.exp(0));
		LLVMValueRef valueRef2 = visit(ctx.exp(1));
		if (ctx.MUL() != null) {
			return LLVMBuildMul(builder, valueRef1, valueRef2, "mul_");
		} else if (ctx.DIV() != null) {
			return LLVMBuildSDiv(builder, valueRef1, valueRef2, "sdiv_");
		} else {
			return LLVMBuildSRem(builder, valueRef1, valueRef2, "srem_");
		}
	}
	
	@Override
	public LLVMValueRef visitLValExp(SysYParser.LValExpContext ctx) {
		return this.visitLVal(ctx.lVal());
	}
	
	@Override
	public LLVMValueRef visitLVal(SysYParser.LValContext ctx) {
		String lValName = ctx.getText();
		LLVMValueRef valuePointer = currentScope.resolve(lValName);
		LLVMValueRef value = LLVMBuildLoad(builder, valuePointer, lValName);
		return value;
	}
	
	@Override
	public LLVMValueRef visitNumExp(SysYParser.NumExpContext ctx) {
		return this.visit(ctx.number());
	}
	
	@Override
	public LLVMValueRef visitNumber(SysYParser.NumberContext ctx) {
		return this.visit(ctx.INTEGR_CONST());
	}
	
	@Override
	public LLVMValueRef visitFuncCallExp(SysYParser.FuncCallExpContext ctx) {
		String functionName = ctx.IDENT().getText();
		LLVMValueRef function = currentScope.resolve(functionName);
		PointerPointer<Pointer> args = null;
		int argsCount = 0;
		if (ctx.funcRParams() != null) {
			argsCount = ctx.funcRParams().param().size();
			args = new PointerPointer<>(argsCount);
			for (int i = 0; i < argsCount; ++i) {
				SysYParser.ParamContext paramContext = ctx.funcRParams().param(i);
				SysYParser.ExpContext expContext = paramContext.exp();
				args.put(i, this.visit(expContext));
			}
		}
		return LLVMBuildCall(builder, function, args, argsCount, "");
	}
	
	@Override
	public LLVMValueRef visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
		LLVMValueRef result = null; 
		if (ctx.exp() != null) {
			result = visit(ctx.exp());
		}
		return LLVMBuildRet(builder, result);
	}
}
