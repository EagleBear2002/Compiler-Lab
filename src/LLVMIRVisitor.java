import Scope.Scope;
import Symbol.Symbol;
import Type.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import Symbol.*;
import Scope.*;
import Type.*;

import static org.bytedeco.llvm.global.LLVM.*;

import java.util.ArrayList;

public class LLVMIRVisitor extends SysYParserBaseVisitor<LLVMValueRef> {
	// 创建 module
	private LLVMModuleRef module = LLVMModuleCreateWithName("moudle");
	
	// 初始化 IRBuilder，后续将使用这个 builder 去生成 LLVM IR
	private LLVMBuilderRef builder = LLVMCreateBuilder();
	
	// 考虑到我们的语言中仅存在 int 一个基本类型，可以通过下面的语句为 LLVM 的 int 型重命名方便以后使用
	private LLVMTypeRef i32Type = LLVMInt32Type();
	private GlobalScope globalScope = null;
	private Scope currentScope = null;
	private int localScopeCounter = 0;
	private boolean errorFound = false;
	
	public LLVMIRVisitor() {
		// 初始化 LLVM
		LLVMInitializeCore(LLVMGetGlobalPassRegistry());
		LLVMLinkInMCJIT();
		LLVMInitializeNativeAsmPrinter();
		LLVMInitializeNativeAsmParser();
		LLVMInitializeNativeTarget();
	}
	
	public LLVMModuleRef getModule() {
		return module;
	}
	
	@Override
	public LLVMValueRef visitChildren(RuleNode node) {
		RuleContext ctx = node.getRuleContext();
		int ruleIndex = ctx.getRuleIndex();
		String ruleName = SysYParser.ruleNames[ruleIndex];
		String realName = ruleName.substring(0, 1).toUpperCase() + ruleName.substring(1);
		
		LLVMValueRef ret = super.visitChildren(node);
		return ret;
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
		Token token = node.getSymbol();
		int ruleNum = token.getType() - 1;
		
		if (ruleNum < 0) {
			return super.visitTerminal(node);
		}
		
		String ruleName = SysYLexer.ruleNames[ruleNum];
		String tokenText = token.getText();
		Symbol symbol = currentScope.resolve(tokenText);
		
		if (ruleName.equals("INTEGR_CONST")) {
			tokenText = toDecimalInteger(tokenText);
		} else if (ruleName.equals("IDENT")) {
			int lineNO = token.getLine();
			int columnNO = token.getCharPositionInLine();
			if (symbol != null) {
				symbol.addUsage(lineNO, columnNO);
			}
		}
		
		return super.visitTerminal(node);
	}
	
	@Override
	public LLVMValueRef visit(ParseTree tree) {
		return super.visit(tree);
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
		globalScope.resolve(retTypeName);
		String funcName = ctx.IDENT().getText();
		
		Type retType = (Type) globalScope.resolve(retTypeName);
		ArrayList<Type> paramsType = new ArrayList<>();
		FunctionType functionType = new FunctionType(retType, paramsType);
		FunctionSymbol fun = new FunctionSymbol(funcName, currentScope, functionType);
		currentScope.define(fun);
		currentScope = fun;
		
		// 生成返回值类型
		LLVMTypeRef returnType = i32Type;
		
		// 生成函数参数类型
		PointerPointer<Pointer> argumentTypes = new PointerPointer<>(2).put(0, i32Type).put(1, i32Type);
		
		// 生成函数类型
		LLVMTypeRef ft = LLVMFunctionType(returnType, argumentTypes, /* argumentCount */ 2, /* isVariadic */ 0);
		
		// 生成函数，即向之前创建的 module 中添加函数
		LLVMValueRef function = LLVMAddFunction(module, funcName, ft);
		LLVMBasicBlockRef block1 = LLVMAppendBasicBlock(function, /*blockName:String*/"block1");
		LLVMPositionBuilderAtEnd(builder, block1); // 后续生成的指令将追加在 block1 的后面
		
		LLVMValueRef ret = super.visitFuncDef(ctx);
		currentScope = currentScope.getEnclosingScope();
		return ret;
	}
	
	@Override
	public LLVMValueRef visitBlock(SysYParser.BlockContext ctx) {
		LocalScope localScope = new LocalScope(currentScope);
		String localScopeName = localScope.getName() + localScopeCounter;
		localScope.setName(localScopeName);
		localScopeCounter++;
		currentScope = localScope;
		LLVMValueRef ret = super.visitBlock(ctx);
		currentScope = currentScope.getEnclosingScope();
		return ret;
	}
	
	int getLineNo(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	}
	
	@Override
	public LLVMValueRef visitVarDecl(SysYParser.VarDeclContext ctx) {
		String typeName = ctx.bType().getText();
		
		for (SysYParser.VarDefContext varDefContext : ctx.varDef()) {
			Type varType = (Type) globalScope.resolve(typeName);
			String varName = varDefContext.IDENT().getText();
			
			for (SysYParser.ConstExpContext constExpContext : varDefContext.constExp()) {
				int elementCount = Integer.parseInt(toDecimalInteger(constExpContext.getText()));
				varType = new ArrayType(elementCount, varType);
			}
			
			VariableSymbol varSymbol = new VariableSymbol(varName, varType);
			currentScope.define(varSymbol);
		}
		
		return super.visitVarDecl(ctx);
	}
	
	@Override
	public LLVMValueRef visitConstDecl(SysYParser.ConstDeclContext ctx) {
		String typeName = ctx.bType().getText();
		
		for (SysYParser.ConstDefContext varDefContext : ctx.constDef()) {
			Type constType = (Type) globalScope.resolve(typeName);
			String constName = varDefContext.IDENT().getText();
			
			for (SysYParser.ConstExpContext constExpContext : varDefContext.constExp()) {
				int elementCount = Integer.parseInt(toDecimalInteger(constExpContext.getText()));
				constType = new ArrayType(elementCount, constType);
			}
			
			VariableSymbol constSymbol = new VariableSymbol(constName, constType);
			currentScope.define(constSymbol);
		}
		
		return super.visitConstDecl(ctx);
	}
	
	@Override
	public LLVMValueRef visitFuncFParam(SysYParser.FuncFParamContext ctx) {
		String varTypeName = ctx.bType().getText();
		Type varType = (Type) globalScope.resolve(varTypeName);
		for (TerminalNode ignored : ctx.L_BRACKT()) {
			varType = new ArrayType(0, varType);
		}
		String varName = ctx.IDENT().getText();
		VariableSymbol varSymbol = new VariableSymbol(varName, varType);
		
		currentScope.define(varSymbol);
		((FunctionSymbol) currentScope).getType().getParamsType().add(varType);
		return super.visitFuncFParam(ctx);
	}
	
}
