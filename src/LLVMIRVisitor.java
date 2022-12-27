import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import Symbol.*;
import Scope.*;
import Type.*;

import static org.bytedeco.llvm.global.LLVM.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class LLVMIRVisitor extends SysYParserBaseVisitor<LLVMValueRef> {
	// 创建 module
	private final LLVMModuleRef module = LLVMModuleCreateWithName("moudle");
	
	// 初始化 IRBuilder，后续将使用这个 builder 去生成 LLVM IR
	private final LLVMBuilderRef builder = LLVMCreateBuilder();
	
	// 考虑到我们的语言中仅存在 int 一个基本类型，可以通过下面的语句为 LLVM 的 int 型重命名方便以后使用
	private final LLVMTypeRef i32Type = LLVMInt32Type();
	private final Stack<LLVMValueRef> operandStack = new Stack<>();
	
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
		
		return super.visitChildren(node);
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
		
		return super.visitTerminal(node);
	}
	
	@Override
	public LLVMValueRef visit(ParseTree tree) {
		return super.visit(tree);
	}
	
	@Override
	public LLVMValueRef visitProgram(SysYParser.ProgramContext ctx) {
		LLVMValueRef ret = super.visitProgram(ctx);
		return ret;
	}
	
	@Override
	public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
		LLVMTypeRef returnType = i32Type;
		
		LLVMTypeRef functionType = LLVMFunctionType(returnType, LLVMVoidType(), 0, 0);
		
		LLVMValueRef main = LLVMAddFunction(module, "main", functionType);
		
		operandStack.push(main);
		
		visitBlock(ctx.block());
		
		operandStack.pop();
		
		return null;
	}
	
//	@Override
//	public LLVMValueRef visitUnaryExp(SysYParser.UnaryExpContext ctx) {
//		String op = ctx.unaryOp().getText();
//		
//		LLVMValueRef expValue = visit(ctx.exp());
//		
//		switch (op) {
//			case "+" -> {
//				return expValue;
//			}
//			case "-" -> {
//				return LLVMBuildNeg(builder, expValue, "tmp_");
//			}
//			case "!" -> {
//				long value = LLVMConstIntGetZExtValue(expValue);
//				if (value == 0) {
//					return LLVMConstInt(i32Type, 1, 1);
//				} else {
//					return LLVMConstInt(i32Type, 0, 1);
//				}
//			}
//			default -> {
//			}
//		}
//		
//		return null;
//	}
	
	@Override
	public LLVMValueRef visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
		LLVMValueRef result = visit(ctx.exp());
		LLVMBasicBlockRef mainEntry = LLVMAppendBasicBlock(operandStack.peek(), "mainEntry");
		LLVMPositionBuilderAtEnd(builder, mainEntry);
		
		LLVMBuildRet(builder, result);
		
		return null;
	}
}
