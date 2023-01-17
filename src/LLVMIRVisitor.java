import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;
import Scope.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import static org.bytedeco.llvm.global.LLVM.*;

public class LLVMIRVisitor extends SysYParserBaseVisitor<LLVMValueRef> {
	private GlobalScope globalScope = null;
	private Scope currentScope = null;
	private int localScopeCounter = 0;
	private final Map<String, String> retTypeMap = new LinkedHashMap<>();
	private final LLVMModuleRef module = LLVMModuleCreateWithName("moudle");
	private final LLVMBuilderRef builder = LLVMCreateBuilder();
	private final LLVMTypeRef i32Type = LLVMInt32Type();
	private final LLVMTypeRef voidType = LLVMVoidType();
	private final LLVMTypeRef intPointerType = LLVMPointerType(i32Type, 0);
	private final LLVMValueRef zero = LLVMConstInt(i32Type, 0, 0);
	private boolean isReturned = false;
	private LLVMValueRef currentFunction = null;
	private final Stack<LLVMBasicBlockRef> whileConditionStack = new Stack<>();
	private final Stack<LLVMBasicBlockRef> afterWhileStack = new Stack<>();
	
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
			return LLVMConstInt(i32Type, number, 0);
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
			if (funcFParamContext.L_BRACKT().size() > 0) {
				paramType = LLVMPointerType(paramType, 0);
			}
			paramsTypes.put(i, paramType);
		}
		
		String retTypeName = ctx.funcType().getText();
		LLVMTypeRef retType = getTypeRef(retTypeName);
		LLVMTypeRef functionType = LLVMFunctionType(retType, paramsTypes, paramsCount, 0);
		
		String functionName = ctx.IDENT().getText();
		retTypeMap.put(functionName, retTypeName);
		currentFunction = LLVMAddFunction(module, functionName, functionType);
		LLVMBasicBlockRef entry = LLVMAppendBasicBlock(currentFunction, functionName + "Entry");
		LLVMPositionBuilderAtEnd(builder, entry);
		
		currentScope.define(functionName, currentFunction, functionType);
		currentScope = new LocalScope(currentScope);
		
		for (int i = 0; i < paramsCount; ++i) {
			SysYParser.FuncFParamContext funcFParamContext = ctx.funcFParams().funcFParam(i);
			String paramTypeName = funcFParamContext.bType().getText();
			LLVMTypeRef paramType = getTypeRef(paramTypeName);
			if (funcFParamContext.L_BRACKT().size() > 0) {
				paramType = LLVMPointerType(paramType, 0);
			}
			String paramName = ctx.funcFParams().funcFParam(i).IDENT().getText();
			LLVMValueRef varPointer = LLVMBuildAlloca(builder, paramType, paramName);
			currentScope.define(paramName, varPointer, paramType);
			LLVMValueRef argValue = LLVMGetParam(currentFunction, i);
			LLVMBuildStore(builder, argValue, varPointer);
		}
		
		isReturned = false;
		super.visitFuncDef(ctx);
		currentScope = currentScope.getEnclosingScope();
		
		if (!isReturned) {
			LLVMBuildRet(builder, null);
		}
		isReturned = false;
		return currentFunction;
	}
	
	@Override
	public LLVMValueRef visitBlock(SysYParser.BlockContext ctx) {
		LocalScope localScope = new LocalScope(currentScope);
		String localScopeName = localScope.getScopeName() + (localScopeCounter++);
		localScope.setScopeName(localScopeName);
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
				varType = LLVMArrayType(varType, elementCount);
			}
			
			LLVMValueRef varPointer = null;
			if (currentScope == globalScope) {
				varPointer = LLVMAddGlobal(module, varType, varName);
				if (elementCount == 0) {
					LLVMSetInitializer(varPointer, zero);
				} else {
					PointerPointer<Pointer> pointerPointer = new PointerPointer<>(elementCount);
					for (int i = 0; i < elementCount; ++i) {
						pointerPointer.put(i, zero);
					}
					LLVMValueRef initArray = LLVMConstArray(varType, pointerPointer, elementCount);
					LLVMSetInitializer(varPointer, initArray);
				}
			} else {
				varPointer = LLVMBuildAlloca(builder, varType, varName);
			}
			
			if (varDefContext.ASSIGN() != null) {
				SysYParser.ExpContext expContext = varDefContext.initVal().exp();
				if (expContext != null) {
					LLVMValueRef initVal = visit(expContext);
					if (currentScope == globalScope) {
						LLVMSetInitializer(varPointer, initVal);
					} else {
						LLVMBuildStore(builder, initVal, varPointer);
					}
				} else {
					int initValCount = varDefContext.initVal().initVal().size();
					if (currentScope == globalScope) {
						PointerPointer<Pointer> pointerPointer = new PointerPointer<>(elementCount);
						for (int i = 0; i < elementCount; ++i) {
							if (i < initValCount) {
								pointerPointer.put(i, this.visit(varDefContext.initVal().initVal(i).exp()));
							} else {
								pointerPointer.put(i, zero);
							}
						}
						LLVMValueRef initArray = LLVMConstArray(varType, pointerPointer, elementCount);
						LLVMSetInitializer(varPointer, initArray);
					} else {
						LLVMValueRef[] initArray = new LLVMValueRef[elementCount];
						for (int i = 0; i < elementCount; ++i) {
							if (i < initValCount) {
								initArray[i] = this.visit(varDefContext.initVal().initVal(i).exp());
							} else {
								initArray[i] = LLVMConstInt(i32Type, 0, 0);
							}
						}
						buildGEP(elementCount, varPointer, initArray);
					}
				}
			}
			
			currentScope.define(varName, varPointer, varType);
		}
		
		return null;
	}
	
	private void buildGEP(int elementCount, LLVMValueRef varPointer, LLVMValueRef[] initArray) {
		LLVMValueRef[] arrayPointer = new LLVMValueRef[2];
		arrayPointer[0] = zero;
		for (int i = 0; i < elementCount; i++) {
			arrayPointer[1] = LLVMConstInt(i32Type, i, 0);
			PointerPointer<LLVMValueRef> indexPointer = new PointerPointer<>(arrayPointer);
			LLVMValueRef elementPtr = LLVMBuildGEP(builder, varPointer, indexPointer, 2, "GEP_" + i);
			LLVMBuildStore(builder, initArray[i], elementPtr);
		}
	}
	
	@Override
	public LLVMValueRef visitConstDecl(SysYParser.ConstDeclContext ctx) {
		String typeName = ctx.bType().getText();
		
		for (SysYParser.ConstDefContext constDefContext : ctx.constDef()) {
			LLVMTypeRef varType = getTypeRef(typeName);
			String varName = constDefContext.IDENT().getText();
			int elementCount = 0;
			
			for (SysYParser.ConstExpContext constExpContext : constDefContext.constExp()) {
				elementCount = Integer.parseInt(toDecimalInteger(constExpContext.getText()));
				varType = LLVMArrayType(varType, elementCount);
			}
			
			LLVMValueRef varPointer = null;
			if (currentScope == globalScope) {
				varPointer = LLVMAddGlobal(module, varType, "global_" + varName);
				LLVMSetInitializer(varPointer, zero);
			} else {
				varPointer = LLVMBuildAlloca(builder, varType, "pointer_" + varName);
			}
			
			SysYParser.ConstExpContext constExpContext = constDefContext.constInitVal().constExp();
			if (constExpContext != null) {
				LLVMValueRef initVal = visit(constExpContext);
				if (currentScope == globalScope) {
					LLVMSetInitializer(varPointer, initVal);
				} else {
					LLVMBuildStore(builder, initVal, varPointer);
				}
			} else {
				int initValCount = constDefContext.constInitVal().constInitVal().size();
				if (currentScope == globalScope) {
					PointerPointer<LLVMValueRef> pointerPointer = new PointerPointer<>(elementCount);
					for (int i = 0; i < elementCount; ++i) {
						if (i < initValCount) {
							pointerPointer.put(i, this.visit(constDefContext.constInitVal().constInitVal(i).constExp()));
						} else {
							pointerPointer.put(i, zero);
						}
					}
					LLVMValueRef initArray = LLVMConstArray(varType, pointerPointer, elementCount);
					LLVMSetInitializer(varPointer, initArray);
				} else {
					LLVMValueRef[] initArray = new LLVMValueRef[elementCount];
					for (int i = 0; i < elementCount; ++i) {
						if (i < initValCount) {
							initArray[i] = this.visit(constDefContext.constInitVal().constInitVal(i).constExp());
						} else {
							initArray[i] = LLVMConstInt(i32Type, 0, 0);
						}
					}
					buildGEP(elementCount, varPointer, initArray);
				}
			}
			
			currentScope.define(varName, varPointer, varType);
		}
		
		return null;
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
				return LLVMBuildNot(builder, expValue, "not_");
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
	
	private boolean arrayAddr = false;
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
				return LLVMBuildGEP(builder, pointer, indexPointer, 1, "res");
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
			return LLVMBuildGEP(builder, varPointer, indexPointer, 2, "res");
		}
	}
	
	@Override
	public LLVMValueRef visitAssignment(SysYParser.AssignmentContext ctx) {
		LLVMValueRef exp = this.visit(ctx.exp());
		LLVMValueRef lValPointer = this.visitLVal(ctx.lVal());
		return LLVMBuildStore(builder, exp, lValPointer);
	}
	
	@Override
	public LLVMValueRef visitCondExp(SysYParser.CondExpContext ctx) {
		return this.visit(ctx.exp());
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
				LLVMValueRef arg = this.visit(expContext);
				args.put(i, arg);
			}
		}
		if (retTypeMap.get(functionName).equals("void")) {
			functionName = "";
		}
		return LLVMBuildCall(builder, function, args, argsCount, functionName);
	}
	
	@Override
	public LLVMValueRef visitReturnStmt(SysYParser.ReturnStmtContext ctx) {
		LLVMValueRef result = null;
		if (ctx.exp() != null) {
			result = visit(ctx.exp());
		}
		isReturned = true;
		return LLVMBuildRet(builder, result);
	}
	
	@Override
	public LLVMValueRef visitIfStmt(SysYParser.IfStmtContext ctx) {
		LLVMValueRef condVal = this.visit(ctx.cond());
		LLVMValueRef cmpResult = LLVMBuildICmp(builder, LLVMIntNE, zero, condVal, "tmp_");
		LLVMBasicBlockRef trueBlock = LLVMAppendBasicBlock(currentFunction, "true");
		LLVMBasicBlockRef falseBlock = LLVMAppendBasicBlock(currentFunction, "false");
		LLVMBasicBlockRef afterIf = LLVMAppendBasicBlock(currentFunction, "entry");
		
		LLVMBuildCondBr(builder, cmpResult, trueBlock, falseBlock);
		
		LLVMPositionBuilderAtEnd(builder, trueBlock);
		this.visit(ctx.stmt(0));
		LLVMBuildBr(builder, afterIf);
		
		LLVMPositionBuilderAtEnd(builder, falseBlock);
		if (ctx.ELSE() != null) {
			this.visit(ctx.stmt(1));
		}
		LLVMBuildBr(builder, afterIf);
		
		LLVMPositionBuilderAtEnd(builder, afterIf);
		return null;
	}
	
	@Override
	public LLVMValueRef visitCompareExp(SysYParser.CompareExpContext ctx) {
		LLVMValueRef lVal = this.visit(ctx.cond(0));
		LLVMValueRef rVal = this.visit(ctx.cond(1));
		LLVMValueRef cmpResult = null;
		if (ctx.LT() != null) {
			cmpResult = LLVMBuildICmp(builder, LLVMIntSLT, lVal, rVal, "tmp_");
		} else if (ctx.GT() != null) {
			cmpResult = LLVMBuildICmp(builder, LLVMIntSGT, lVal, rVal, "tmp_");
		} else if (ctx.LE() != null) {
			cmpResult = LLVMBuildICmp(builder, LLVMIntSLE, lVal, rVal, "tmp_");
		} else {
			cmpResult = LLVMBuildICmp(builder, LLVMIntSGE, lVal, rVal, "tmp_");
		}
		return LLVMBuildZExt(builder, cmpResult, i32Type, "tmp_");
	}
	
	@Override
	public LLVMValueRef visitRelationExp(SysYParser.RelationExpContext ctx) {
		LLVMValueRef lVal = this.visit(ctx.cond(0));
		LLVMValueRef rVal = this.visit(ctx.cond(1));
		LLVMValueRef cmpResult = null;
		if (ctx.NEQ() != null) {
			cmpResult = LLVMBuildICmp(builder, LLVMIntNE, lVal, rVal, "tmp_");
		} else {
			cmpResult = LLVMBuildICmp(builder, LLVMIntEQ, lVal, rVal, "tmp_");
		}
		return LLVMBuildZExt(builder, cmpResult, i32Type, "tmp_");
	}
	
	@Override
	public LLVMValueRef visitAndExp(SysYParser.AndExpContext ctx) {
		LLVMValueRef lVal = this.visit(ctx.cond(0));
		LLVMValueRef rVal = this.visit(ctx.cond(1));
		LLVMValueRef cmpResult = LLVMBuildAnd(builder, lVal, rVal, "tmp_");
		return LLVMBuildZExt(builder, cmpResult, i32Type, "tmp_");
	}
	
	@Override
	public LLVMValueRef visitOrExp(SysYParser.OrExpContext ctx) {
		LLVMValueRef lVal = this.visit(ctx.cond(0));
		LLVMValueRef rVal = this.visit(ctx.cond(1));
		LLVMValueRef cmpResult = LLVMBuildOr(builder, lVal, rVal, "tmp_");
		return LLVMBuildZExt(builder, cmpResult, i32Type, "tmp_");
	}
	
	@Override
	public LLVMValueRef visitWhileStmt(SysYParser.WhileStmtContext ctx) {
		LLVMBasicBlockRef whileCondition = LLVMAppendBasicBlock(currentFunction, "whileCondition");
		LLVMBasicBlockRef whileBody = LLVMAppendBasicBlock(currentFunction, "whileBody");
		LLVMBasicBlockRef afterWhile = LLVMAppendBasicBlock(currentFunction, "entry");
		
		LLVMBuildBr(builder, whileCondition);
		
		LLVMPositionBuilderAtEnd(builder, whileCondition);
		LLVMValueRef condVal = this.visit(ctx.cond());
		LLVMValueRef cmpResult = LLVMBuildICmp(builder, LLVMIntNE, zero, condVal, "tmp_");
		LLVMBuildCondBr(builder, cmpResult, whileBody, afterWhile);
		
		LLVMPositionBuilderAtEnd(builder, whileBody);
		whileConditionStack.push(whileCondition);
		afterWhileStack.push(afterWhile);
		this.visit(ctx.stmt());
		LLVMBuildBr(builder, whileCondition);
		whileConditionStack.pop();
		afterWhileStack.pop();
		LLVMBuildBr(builder, afterWhile);
		
		LLVMPositionBuilderAtEnd(builder, afterWhile);
		return null;
	}
	
	@Override
	public LLVMValueRef visitBreakStmt(SysYParser.BreakStmtContext ctx) {
		return LLVMBuildBr(builder, afterWhileStack.peek());
	}
	
	@Override
	public LLVMValueRef visitContinueStmt(SysYParser.ContinueStmtContext ctx) {
		return LLVMBuildBr(builder, whileConditionStack.peek());
	}
}
