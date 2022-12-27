import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class LLVMIRVisitor extends SysYParserBaseVisitor<LLVMValueRef> {
	// 创建 module
	private final LLVMModuleRef module = LLVMModuleCreateWithName("moudle");
	
	// 初始化 IRBuilder，后续将使用这个 builder 去生成 LLVM IR
	private final LLVMBuilderRef builder = LLVMCreateBuilder();
	
	// 考虑到我们的语言中仅存在 int 一个基本类型，可以通过下面的语句为 LLVM 的 int 型重命名方便以后使用
	private final LLVMTypeRef i32Type = LLVMInt32Type();
	
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
	
//	@Override
//	public LLVMValueRef visitChildren(RuleNode node) {
//		RuleContext ctx = node.getRuleContext();
//		int ruleIndex = ctx.getRuleIndex();
//		String ruleName = SysYParser.ruleNames[ruleIndex];
//		String realName = ruleName.substring(0, 1).toUpperCase() + ruleName.substring(1);
//		
//		return super.visitChildren(node);
//	}
	
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
		String funtionName = ctx.IDENT().getText();
		LLVMValueRef funtion = LLVMAddFunction(module, funtionName, functionType);
		
		LLVMBasicBlockRef mainEntry = LLVMAppendBasicBlock(funtion, "mainEntry");
		LLVMPositionBuilderAtEnd(builder, mainEntry);
		super.visitFuncDef(ctx);
		
		return funtion;
	}

	@Override
	public LLVMValueRef visitUnaryExp(SysYParser.UnaryExpContext ctx) {
		String op = ctx.unaryOp().getText();

		LLVMValueRef expValue = visit(ctx.exp());

		switch (op) {
			case "+" -> {
				return expValue;
			}
			case "-" -> {
				return LLVMBuildNeg(builder, expValue, "tmp_");
			}
			case "!" -> {
				long value = LLVMConstIntGetZExtValue(expValue);
				if (value == 0) {
					return LLVMConstInt(i32Type, 1, 1);
				} else {
					return LLVMConstInt(i32Type, 0, 1);
				}
			}
			default -> {
			}
		}

		return null;
	}
	
	@Override
	public LLVMValueRef visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
		LLVMValueRef result = visit(ctx.exp());
		return LLVMBuildRet(builder, result);
	}
}
