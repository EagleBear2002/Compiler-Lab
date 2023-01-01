//import Scope.*;
//import Symbol.*;
//import Type.*;
//import org.antlr.v4.runtime.ParserRuleContext;
//import org.antlr.v4.runtime.RuleContext;
//import org.antlr.v4.runtime.Token;
//import org.antlr.v4.runtime.tree.ParseTree;
//import org.antlr.v4.runtime.tree.RuleNode;
//import org.antlr.v4.runtime.tree.TerminalNode;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Visitor extends SysYParserBaseVisitor<Void> {
//	private static int depth = 0;
//	private final List<Object> msgToPrint = new ArrayList<>();
//	private GlobalScope globalScope = null;
//	private Scope currentScope = null;
//	private int localScopeCounter = 0;
//	private boolean errorFound = false;
//	
//	public List<Object> getMsgToPrint() {
//		return msgToPrint;
//	}
//	
//	public boolean getErrorFound() {
//		return errorFound;
//	}
//	
//	private void reportError(int typeNo, int lineNo, String msg) {
//		System.err.println("Error type " + typeNo + " at Line " + lineNo + ": " + msg + ".");
//		errorFound = true;
//	}
//	
//	private String ident2String(int depth) {
//		return "  ".repeat(Math.max(0, depth));
//	}
//	
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
//	
//	@Override
//	public Void visitChildren(RuleNode node) {
//		RuleContext ctx = node.getRuleContext();
//		int ruleIndex = ctx.getRuleIndex();
//		String ruleName = SysYParser.ruleNames[ruleIndex];
//		String realName = ruleName.substring(0, 1).toUpperCase() + ruleName.substring(1);
//		
//		msgToPrint.add(ident2String(depth) + realName + "\n");
//		
//		depth++;
//		Void ret = super.visitChildren(node);
//		depth--;
//		
//		return ret;
//	}
//	
//	private String toDecimalInteger(String tokenText) {
//		if (tokenText.startsWith("0x") || tokenText.startsWith("0X")) {
//			tokenText = String.valueOf(Integer.parseInt(tokenText.substring(2), 16));
//		} else if (tokenText.startsWith("0")) {
//			tokenText = String.valueOf(Integer.parseInt(tokenText, 8));
//		}
//		return tokenText;
//	}
//	
//	@Override
//	public Void visitTerminal(TerminalNode node) {
//		Token token = node.getSymbol();
//		int ruleNum = token.getType() - 1;
//		
//		if (ruleNum < 0) {
//			return super.visitTerminal(node);
//		}
//		
//		String ruleName = SysYLexer.ruleNames[ruleNum];
//		String tokenText = token.getText();
//		String color = getHelight(ruleName);
//		Symbol symbol = currentScope.resolve(tokenText);
//		
//		if (ruleName.equals("INTEGR_CONST")) {
//			tokenText = toDecimalInteger(tokenText);
//		} else if (ruleName.equals("IDENT")) {
//			int lineNO = token.getLine();
//			int columnNO = token.getCharPositionInLine();
//			if (symbol != null) {
//				symbol.addUsage(lineNO, columnNO);
//			}
//		}
//		
//		if (!color.equals("no color")) {
//			msgToPrint.add(ident2String(depth));
//			if (symbol == null) {
//				msgToPrint.add(tokenText);
//			} else {
//				msgToPrint.add(symbol);
//			}
//			msgToPrint.add(" " + ruleName + "[" + color + "]" + "\n");
//		}
//		
//		return super.visitTerminal(node);
//	}
//	
//	@Override
//	public Void visit(ParseTree tree) {
//		return super.visit(tree);
//	}
//	
//	@Override
//	public Void visitProgram(SysYParser.ProgramContext ctx) {
//		currentScope = globalScope = new GlobalScope(null);
//		Void ret = super.visitProgram(ctx);
//		currentScope = currentScope.getEnclosingScope();
//		return ret;
//	}
//	
//	@Override
//	public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
//		String retTypeName = ctx.funcType().getText();
//		globalScope.resolve(retTypeName);
//		String funcName = ctx.IDENT().getText();
//		if (currentScope.definedSymbol(funcName)) {
//			reportError(4, getLineNo(ctx), "Redefined function: " + funcName);
//			return null;
//		}
//		
//		Type retType = (Type) globalScope.resolve(retTypeName);
//		ArrayList<Type> paramsType = new ArrayList<>();
//		FunctionType functionType = new FunctionType(retType, paramsType);
//		FunctionSymbol fun = new FunctionSymbol(funcName, currentScope, functionType);
//		currentScope.define(fun);
//		currentScope = fun;
//		Void ret = super.visitFuncDef(ctx);
//		currentScope = currentScope.getEnclosingScope();
//		return ret;
//	}
//	
//	@Override
//	public Void visitBlock(SysYParser.BlockContext ctx) {
//		LocalScope localScope = new LocalScope(currentScope);
//		String localScopeName = localScope.getName() + localScopeCounter;
//		localScope.setName(localScopeName);
//		localScopeCounter++;
//		currentScope = localScope;
//		Void ret = super.visitBlock(ctx);
//		currentScope = currentScope.getEnclosingScope();
//		return ret;
//	}
//	
//	private int getLineNo(ParserRuleContext ctx) {
//		return ctx.getStart().getLine();
//	}
//	
//	@Override
//	public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
//		String typeName = ctx.bType().getText();
//		
//		for (SysYParser.VarDefContext varDefContext : ctx.varDef()) {
//			Type varType = (Type) globalScope.resolve(typeName);
//			String varName = varDefContext.IDENT().getText();
//			if (currentScope.definedSymbol(varName)) {
//				reportError(3, getLineNo(varDefContext), "Redefined variable: " + varName);
//				continue;
//			}
//			
//			for (SysYParser.ConstExpContext constExpContext : varDefContext.constExp()) {
//				int elementCount = Integer.parseInt(toDecimalInteger(constExpContext.getText()));
//				varType = new ArrayType(elementCount, varType);
//			}
//			
//			if (varDefContext.ASSIGN() != null) {
//				SysYParser.ExpContext expContext = varDefContext.initVal().exp();
//				if (expContext != null) {
//					Type initValType = getExpType(expContext);
//					if (!initValType.toString().equals("noType") && !varType.toString().equals(initValType.toString())) {
//						reportError(5, getLineNo(varDefContext), "Type mismatched for assignment");
//					}
//				}
//			}
//			
//			VariableSymbol varSymbol = new VariableSymbol(varName, varType);
//			currentScope.define(varSymbol);
//		}
//		
//		return super.visitVarDecl(ctx);
//	}
//	
//	@Override
//	public Void visitConstDecl(SysYParser.ConstDeclContext ctx) {
//		String typeName = ctx.bType().getText();
//		
//		for (SysYParser.ConstDefContext varDefContext : ctx.constDef()) {
//			Type constType = (Type) globalScope.resolve(typeName);
//			String constName = varDefContext.IDENT().getText();
//			if (currentScope.definedSymbol(constName)) {
//				reportError(3, getLineNo(varDefContext), "Redefined variable: " + constName);
//				continue;
//			}
//			
//			for (SysYParser.ConstExpContext constExpContext : varDefContext.constExp()) {
//				int elementCount = Integer.parseInt(toDecimalInteger(constExpContext.getText()));
//				constType = new ArrayType(elementCount, constType);
//			}
//			
//			SysYParser.ConstExpContext expContext = varDefContext.constInitVal().constExp();
//			if (expContext != null) {
//				Type initValType = getExpType(expContext.exp());
//				if (!initValType.toString().equals("noType") && !constType.toString().equals(initValType.toString())) {
//					reportError(5, getLineNo(varDefContext), "Type mismatched for assignment");
//				}
//			}
//			
//			VariableSymbol constSymbol = new VariableSymbol(constName, constType);
//			currentScope.define(constSymbol);
//		}
//		
//		return super.visitConstDecl(ctx);
//	}
//	
//	@Override
//	public Void visitFuncFParam(SysYParser.FuncFParamContext ctx) {
//		String varTypeName = ctx.bType().getText();
//		Type varType = (Type) globalScope.resolve(varTypeName);
//		for (TerminalNode ignored : ctx.L_BRACKT()) {
//			varType = new ArrayType(0, varType);
//		}
//		String varName = ctx.IDENT().getText();
//		VariableSymbol varSymbol = new VariableSymbol(varName, varType);
//		
//		if (currentScope.definedSymbol(varName)) {
//			reportError(3, getLineNo(ctx), "Redefined variable: " + varName);
//		} else {
//			currentScope.define(varSymbol);
//			((FunctionSymbol) currentScope).getType().getParamsType().add(varType);
//		}
//		return super.visitFuncFParam(ctx);
//	}
//	
//	private Type getLValType(SysYParser.LValContext ctx) {
//		String varName = ctx.IDENT().getText();
//		Symbol symbol = currentScope.resolve(varName);
//		if (symbol == null) {
//			return new BasicTypeSymbol("noType");
//		}
//		Type varType = symbol.getType();
//		for (SysYParser.ExpContext ignored : ctx.exp()) {
//			if (varType instanceof ArrayType) {
//				varType = ((ArrayType) varType).elementType;
//			} else {
//				return new BasicTypeSymbol("noType");
//			}
//		}
//		return varType;
//	}
//	
//	
//	@Override
//	public Void visitLVal(SysYParser.LValContext ctx) {
//		String varName = ctx.IDENT().getText();
//		Symbol symbol = currentScope.resolve(varName);
//		if (symbol == null) {
//			reportError(1, getLineNo(ctx), "Undefined variable: " + varName);
//			return null;
//		}
//		
//		Type varType = symbol.getType();
//		int arrayDimension = ctx.exp().size();
//		for (int i = 0; i < arrayDimension; ++i) {
//			if (varType instanceof ArrayType) {
//				varType = ((ArrayType) varType).elementType;
//				SysYParser.ExpContext expContext = ctx.exp(i);
//				varName += "[" + expContext.getText() + "]";
//			} else {
//				TerminalNode node = ctx.L_BRACKT(i);
//				reportError(9, getLineNo(ctx), "Not an array: " + varName);
//				break;
//			}
//		}
//		
//		return super.visitLVal(ctx);
//	}
//	
//	@Override
//	public Void visitStmt(SysYParser.StmtContext ctx) {
//		if (ctx.ASSIGN() != null) {
//			Type lValType = getLValType(ctx.lVal());
//			Type rValType = getExpType(ctx.exp());
//			if (lValType instanceof FunctionType) {
//				reportError(11, getLineNo(ctx), "The left-hand side of an assignment must be a variable");
//			} else if (!lValType.toString().equals("noType") && !rValType.toString().equals("noType") && !lValType.toString().equals(rValType.toString())) {
//				reportError(5, getLineNo(ctx), "Type mismatched for assignment");
//			}
//		} else if (ctx.RETURN() != null) {
//			Type retType = new BasicTypeSymbol("void");
//			if (ctx.exp() != null) {
//				retType = getExpType(ctx.exp());
//			}
//			
//			Scope tmpScope = currentScope;
//			while (!(tmpScope instanceof FunctionSymbol)) {
//				tmpScope = tmpScope.getEnclosingScope();
//			}
//			
//			Type expectedType = ((FunctionSymbol) tmpScope).getType().getRetType();
//			if (!retType.toString().equals("noType") && !expectedType.toString().equals("noType") && !retType.toString().equals(expectedType.toString())) {
//				reportError(7, getLineNo(ctx), "Type mismatched for return");
//			}
//		}
//		return super.visitStmt(ctx);
//	}
//	
//	private Type getExpType(SysYParser.ExpContext ctx) {
//		if (ctx.IDENT() != null) { // IDENT L_PAREN funcRParams? R_PAREN
//			String funcName = ctx.IDENT().getText();
//			Symbol symbol = currentScope.resolve(funcName);
//			if (symbol != null && symbol.getType() instanceof FunctionType) {
//				FunctionType functionType = (FunctionType) currentScope.resolve(funcName).getType();
//				ArrayList<Type> paramsType = functionType.getParamsType(), argsType = new ArrayList<>();
//				if (ctx.funcRParams() != null) {
//					for (SysYParser.ParamContext paramContext : ctx.funcRParams().param()) {
//						argsType.add(getExpType(paramContext.exp()));
//					}
//				}
//				if (paramsType.equals(argsType)) {
//					return functionType.getRetType();
//				}
//			}
//		} else if (ctx.L_PAREN() != null) { // L_PAREN exp R_PAREN
//			return getExpType(ctx.exp(0));
//		} else if (ctx.unaryOp() != null) { // unaryOp exp
//			return getExpType(ctx.exp(0));
//		} else if (ctx.lVal() != null) { // lVal
//			return getLValType(ctx.lVal());
//		} else if (ctx.number() != null) { // number
//			return new BasicTypeSymbol("int");
//		} else if (ctx.MUL() != null || ctx.DIV() != null || ctx.MOD() != null || ctx.PLUS() != null || ctx.MINUS() != null) {
//			Type op1Type = getExpType(ctx.exp(0));
//			Type op2Type = getExpType(ctx.exp(1));
//			if (op1Type.toString().equals("int") && op2Type.toString().equals("int")) {
//				return op1Type;
//			}
//		}
//		return new BasicTypeSymbol("noType");
//	}
//	
//	private boolean checkArgsTyps(ArrayList<Type> paramsType, ArrayList<Type> argsType) {
//		int len1 = paramsType.size();
//		int len2 = argsType.size();
//		
//		for (Type type : paramsType) {
//			if (type.toString().equals("noType")) {
//				return true;
//			}
//		}
//		
//		for (Type type : argsType) {
//			if (type.toString().equals("noType")) {
//				return true;
//			}
//		}
//		
//		if (len1 != len2) {
//			return false;
//		}
//		
//		for (int i = 0; i < len1; ++i) {
//			Type paramType = paramsType.get(i);
//			Type argType = argsType.get(i);
//			if (!paramType.toString().equals(argType.toString())) {
//				return false;
//			}
//		}
//		
//		return true;
//	}
//	
//	@Override
//	public Void visitExp(SysYParser.ExpContext ctx) {
//		if (ctx.IDENT() != null) { // IDENT L_PAREN funcRParams? R_PAREN
//			String funcName = ctx.IDENT().getText();
//			Symbol symbol = currentScope.resolve(funcName);
//			if (symbol == null) {
//				reportError(2, getLineNo(ctx), "Undefined function: " + funcName);
//			} else if (!(symbol.getType() instanceof FunctionType)) {
//				reportError(10, getLineNo(ctx), "Not a function: " + funcName);
//			} else {
//				FunctionType functionType = (FunctionType) symbol.getType();
//				ArrayList<Type> paramsType = functionType.getParamsType();
//				ArrayList<Type> argsType = new ArrayList<>();
//				if (ctx.funcRParams() != null) {
//					for (SysYParser.ParamContext paramContext : ctx.funcRParams().param()) {
//						argsType.add(getExpType(paramContext.exp()));
//					}
//				}
//				if (!checkArgsTyps(paramsType, argsType)) {
//					reportError(8, getLineNo(ctx), "Function is not applicable for arguments");
//				}
//			}
//		} else if (ctx.unaryOp() != null) { // unaryOp exp
//			Type expType = getExpType(ctx.exp(0));
//			if (!expType.toString().equals("int")) {
//				reportError(6, getLineNo(ctx), "Type mismatched for operands");
//			}
//		} else if (ctx.MUL() != null || ctx.DIV() != null || ctx.MOD() != null || ctx.PLUS() != null || ctx.MINUS() != null) {
//			Type op1Type = getExpType(ctx.exp(0)), op2Type = getExpType(ctx.exp(1));
//			if (op1Type.toString().equals("noType") || op2Type.toString().equals("noType")) {
//			} else if (op1Type.toString().equals("int") && op2Type.toString().equals("int")) {
//			} else {
//				reportError(6, getLineNo(ctx), "Type mismatched for operands");
//			}
//		}
//		return super.visitExp(ctx);
//	}
//	
//	private Type getCondType(SysYParser.CondContext ctx) {
//		if (ctx.exp() != null) {
//			return getExpType(ctx.exp());
//		}
//		
//		Type cond1 = getCondType(ctx.cond(0));
//		Type cond2 = getCondType(ctx.cond(1));
//		if (cond1.toString().equals("int") && cond2.toString().equals("int")) {
//			return cond1;
//		}
//		return new BasicTypeSymbol("noType");
//	}
//	
//	@Override
//	public Void visitCond(SysYParser.CondContext ctx) {
//		if (ctx.exp() == null && !getCondType(ctx).toString().equals("int")) {
//			reportError(6, getLineNo(ctx), "Type mismatched for operands");
//		}
//		return super.visitCond(ctx);
//	}
//}
