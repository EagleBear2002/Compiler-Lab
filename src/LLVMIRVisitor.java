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
		
		return null;
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
	
	private LLVMValueRef binaryOperation(String op, LLVMValueRef lhs, LLVMValueRef rhs) {
		long numValue1 = LLVMConstIntGetZExtValue(lhs);
		long numValue2 = LLVMConstIntGetZExtValue(rhs);
		
		switch (op) {
			case "+":
				return LLVMConstInt(i32Type, numValue1 + numValue2, 1);
			case "-":
				return LLVMConstInt(i32Type, numValue1 - numValue2, 1);
			case "*":
				return LLVMConstInt(i32Type, numValue1 * numValue2, 1);
			case "/":
				return LLVMConstInt(i32Type, numValue1 / numValue2, 1);
			case "%":
				return LLVMConstInt(i32Type, numValue1 % numValue2, 1);
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
