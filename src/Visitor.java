import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;

public class Visitor extends SysYParserBaseVisitor<Void> {
	private static int depth = 0;
	private GlobalScope globalScope = null;
	private Scope currentScope = null;
	private int localScopeCounter = 0;
	private int renameLineNo;
	private int renameColumnNo;
	private String newName;
	private boolean isPrint;
	
	void setPrint(boolean isPrint) {
		this.isPrint = isPrint;
	}
	
	void setRenameTag(int renameLineNo, int renameColumnNo, String newName) {
		this.renameLineNo = renameLineNo;
		this.renameColumnNo = renameColumnNo;
		this.newName = newName;
	}
	
	private void printIdent(int depth) {
		for (int i = 0; i < depth; ++i)
			System.err.print("  ");
	}
	
	private String getHelight(String ruleName) {
		switch (ruleName) {
			case "CONST":
			case "INT":
			case "VOID":
			case "IF":
			case "ELSE":
			case "WHILE":
			case "BREAK":
			case "CONTINUE":
			case "RETURN": {
				return "orange";
			}
			
			case "PLUS":
			case "MINUS":
			case "MUL":
			case "DIV":
			case "MOD":
			case "ASSIGN":
			case "EQ":
			case "NEQ":
			case "LT":
			case "GT":
			case "LE":
			case "GE":
			case "NOT":
			case "AND":
			case "OR": {
				return "blue";
			}
			
			case "IDENT": {
				return "red";
			}
			
			case "INTEGR_CONST": {
				return "green";
			}
			
			default: {
				return "no color";
			}
		}
	}
	
	@Override
	public Void visitChildren(RuleNode node) {
		RuleContext ctx = node.getRuleContext();
		int ruleIndex = ctx.getRuleIndex();
		String ruleName = SysYParser.ruleNames[ruleIndex];
		String realName = ruleName.substring(0, 1).toUpperCase() + ruleName.substring(1);
		
		if (isPrint) {
			printIdent(depth);
			System.err.println(realName);
		}
		
		depth++;
		Void ret = super.visitChildren(node);
		depth--;
		
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
	public Void visitTerminal(TerminalNode node) {
		Token token = node.getSymbol();
		int ruleNum = token.getType() - 1;
		
		if (ruleNum >= 0) {
			String ruleName = SysYLexer.ruleNames[ruleNum];
			String tokenText = token.getText();
			String color = getHelight(ruleName);
			
			if (ruleName == "INTEGR_CONST") {
				// if (tokenText.startsWith("0x") || tokenText.startsWith("0X")) {
				// tokenText = String.valueOf(Integer.parseInt(tokenText.substring(2), 16));
				// } else if (tokenText.startsWith("0")) {
				// tokenText = String.valueOf(Integer.parseInt(tokenText, 8));
				// }
				tokenText = toDecimalInteger(tokenText);
			}
			
			if (isPrint && ruleName == "IDENT") {
				int lineNO = token.getLine();
				int columnNO = token.getCharPositionInLine();
				Symbol symbol = currentScope.resolve(tokenText);
				if (symbol != null) {
					symbol.addUsage(lineNO, columnNO);
					
					if (symbol.findUsage(renameLineNo, renameColumnNo)) {
//						System.out.println("tokenText = " + tokenText + ", findUsage(" + renameLineNo + ", " + renameColumnNo + ")");
						tokenText = newName;
					}
				}
			}
			
			if (isPrint && color != "no color") {
				printIdent(depth);
				System.err.println(tokenText + " " + ruleName + "[" + color + "]");
			}
		}
		
		Void ret = super.visitTerminal(node);
		return ret;
	}
	
	@Override
	public Void visit(ParseTree tree) {
		Void ret = super.visit(tree);
		return ret;
	}
	
	@Override
	public Void visitProgram(SysYParser.ProgramContext ctx) {
		globalScope = new GlobalScope(null);
		currentScope = globalScope;
		Void ret = super.visitProgram(ctx);
		currentScope = currentScope.getEnclosingScope();
		
		return ret;
	}
	
	@Override
	public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
		String retTypeName = ctx.funcType().getText();
		globalScope.resolve(retTypeName);
		Type retType = (Type) globalScope.resolve(retTypeName);
		
		String funcName = ctx.IDENT().getText();
		if (currentScope.resolve(funcName) != null) {
			int lineNo = getLineNo(ctx.IDENT());
			System.err.println("Error type 4 at Line " + lineNo + ": Redefined function: " + funcName + ".");
		}
		
		ArrayList<Type> paramsType = new ArrayList<>();
		if (ctx.funcFParams() != null) {
			for (SysYParser.FuncFParamContext funcFParamContext : ctx.funcFParams().funcFParam()) {
				String fParamTypeName = funcFParamContext.bType().getText();
				BasicTypeSymbol fParamType = (BasicTypeSymbol) currentScope.resolve(fParamTypeName);
				paramsType.add(fParamType);
			}
		}
		
		FunctionType functionType = new FunctionType(retType, paramsType);
		FunctionSymbol fun = new FunctionSymbol(funcName, currentScope, functionType);
		currentScope.define(fun);
		currentScope = fun;
		
		Void ret = super.visitFuncDef(ctx);
		
		currentScope = currentScope.getEnclosingScope();
		
		return ret;
	}
	
	@Override
	public Void visitBlock(SysYParser.BlockContext ctx) {
		LocalScope localScope = new LocalScope(currentScope);
		String localScopeName = localScope.getName() + localScopeCounter;
		localScope.setName(localScopeName);
		localScopeCounter++;
		currentScope = localScope;
		
		Void ret = super.visitBlock(ctx);
		currentScope = currentScope.getEnclosingScope();
		
		return ret;
	}
	
	@Override
	public Void visitVarDecl(SysYParser.VarDeclContext ctx) {
		String typeName = ctx.bType().getText();
		
		for (SysYParser.VarDefContext varDefContext : ctx.varDef()) {
			Type varType = (Type) globalScope.resolve(typeName);
			String varName = varDefContext.IDENT().getText();
			if (currentScope.resolve(varName) != null) {
				int lineNo = getLineNo(varDefContext.IDENT());
				System.err.println("Error type 3 at Line " + lineNo + ": Redefined variable: " + varName + ".");
			}
			
			for (SysYParser.ConstExpContext constExpContext : varDefContext.constExp()) {
				int elementCount = Integer.valueOf(toDecimalInteger(constExpContext.getText()));
				varType = new ArrayType(elementCount, varType);
			}
			
			if (varDefContext.ASSIGN() != null) {
				// TODO: Type 5
			}

//			System.out.println("varName = " + varName + ", varType = " + varType.toString());
			
			VariableSymbol varSymbol = new VariableSymbol(varName, varType);
			currentScope.define(varSymbol);
		}
		
		Void ret = super.visitVarDecl(ctx);
		return ret;
	}
	
	@Override
	public Void visitConstDecl(SysYParser.ConstDeclContext ctx) {
		String typeName = ctx.bType().getText();
		
		for (SysYParser.ConstDefContext varDefContext : ctx.constDef()) {
			Type constType = (Type) globalScope.resolve(typeName);
			String constName = varDefContext.IDENT().getText();
			if (currentScope.resolve(constName) != null) {
				int lineNo = getLineNo(varDefContext.IDENT());
				System.err.println("Error type 3 at Line " + lineNo + ": Redefined variable: " + constName + ".");
			}
			
			for (SysYParser.ConstExpContext constExpContext : varDefContext.constExp()) {
				int elementCount = Integer.valueOf(toDecimalInteger(constExpContext.getText()));
				constType = new ArrayType(elementCount, constType);
			}
			
			if (varDefContext.ASSIGN() == null) {
				System.err.println("const defination without initial value");
			}
			
			// TODO: Type 5
			// System.out.println("constName = " + constName + ", varType = " +
			// constName.toString());
			
			VariableSymbol constSymbol = new VariableSymbol(constName, constType);
			currentScope.define(constSymbol);
		}
		
		Void ret = super.visitConstDecl(ctx);
		return ret;
	}
	
	@Override
	public Void visitFuncFParam(SysYParser.FuncFParamContext ctx) {
		String typeName = ctx.bType().getText();
		Type type = (Type) globalScope.resolve(typeName);
		
		String varName = ctx.IDENT().getText();
		VariableSymbol varSymbol = new VariableSymbol(varName, type);
		
		currentScope.define(varSymbol);
		
		Void ret = super.visitFuncFParam(ctx);
		return ret;
	}
	
	private Type getLValType(SysYParser.LValContext ctx) {
		String varName = ctx.IDENT().getText();
//		System.out.println("varName = " + varName);
		if (currentScope.resolve(varName) == null) {
//			int lineNo = getLineNo(ctx.IDENT());
//			System.err.println("Error type 1 at Line " + lineNo + ": Undefined variable: " + varName + ".");
			return new BasicTypeSymbol("void");
		}
		Type varType = currentScope.resolve(varName).getType();
		for (SysYParser.ExpContext expContext : ctx.exp()) {
//			System.out.println("varType = " + varType);
			if (varType instanceof ArrayType) {
				varType = ((ArrayType) varType).elementType;
			} else {
				// TODO
			}
		}
		return varType;
	}
	
	private int getLineNo(TerminalNode node) {
		return node.getSymbol().getLine();
	}
	
	@Override
	public Void visitLVal(SysYParser.LValContext ctx) {
		// getLValType(ctx);
		String varName = ctx.IDENT().getText();
		if (currentScope.resolve(varName) == null) {
			int lineNo = getLineNo(ctx.IDENT());
			System.err.println("Error type 1 at Line " + lineNo + ": Undefined variable: " + varName + ".");
		}
		Void ret = super.visitLVal(ctx);
		return ret;
	}
	
	@Override
	public Void visitStmt(SysYParser.StmtContext ctx) {
		if (ctx.ASSIGN() != null) {
			Type lValType = getLValType(ctx.lVal());
			Type rValType = getExpType(ctx.exp());
			if (!lValType.toString().equals(rValType.toString())) {
				int lineNo = getLineNo(ctx.ASSIGN());
				System.err.println("Error type 5 at Line " + lineNo + ": type.Type mismatched for assignment.");
			}
		}
		if (ctx.RETURN() != null) {
			Type retType = new BasicTypeSymbol("void");
			if (ctx.exp() != null) {
				retType = getExpType(ctx.exp());
			}
			
			Scope tmpScope = currentScope;
			while (!(tmpScope instanceof FunctionSymbol)) {
				tmpScope = tmpScope.getEnclosingScope();
			}
			Type expectedType = ((FunctionSymbol) tmpScope).getType().getRetType();
			if (!retType.toString().equals(expectedType.toString())) {
				int lineNo = getLineNo(ctx.RETURN());
				System.err.println("Error type 7 at Line " + lineNo + ": type.Type mismatched for return.");
			}
		}
		Void ret = super.visitStmt(ctx);
		return ret;
	}
	
	private Type getExpType(SysYParser.ExpContext ctx) {
		if (ctx.IDENT() != null) { // IDENT L_PAREN funcRParams? R_PAREN
			String funcName = ctx.IDENT().getText();
			if (currentScope.resolve(funcName) == null) {
//				int lineNo = getLineNo(ctx.IDENT());
//				System.err.println("Error type 2 at Line " + lineNo + ": Undefined function: " + funcName + ".");
			} else {
				Symbol symbol = currentScope.resolve(funcName);
				Type type = symbol.getType();
				return type;
			}
		} else if (ctx.L_PAREN() != null || ctx.unaryOp() != null) { // L_PAREN exp R_PAREN | unaryOp exp
			return getExpType(ctx.exp(0));
		} else if (ctx.lVal() != null) { // lVal
			return getLValType(ctx.lVal());
		} else if (ctx.number() != null) { // number
			return new BasicTypeSymbol("int");
		} else if (ctx.MUL() != null || ctx.DIV() != null || ctx.MOD() != null || ctx.PLUS() != null
				|| ctx.MINUS() != null) {
			Type op1Type = getExpType(ctx.exp(0));
			Type op2Type = getExpType(ctx.exp(1));
			if (op1Type.toString().equals("int") && op2Type.toString().equals("int")) {
				return op1Type;
			} else {
//				TerminalNode operator;
//				if (ctx.MUL() != null) {
//					operator = ctx.MUL();
//				} else if (ctx.DIV() != null) {
//					operator = ctx.DIV();
//				} else if (ctx.MOD() != null) {
//					operator = ctx.MOD();
//				} else if (ctx.PLUS() != null) {
//					operator = ctx.PLUS();
//				} else {
//					operator = ctx.MINUS();
//				}
//				int lineNo = getLineNo(operator);
//				System.err.println("Error type 6 at Line " + lineNo + ": type.Type mismatched for operands.");
			}
		}
		return new BasicTypeSymbol("void");
	}
	
	@Override
	public Void visitExp(SysYParser.ExpContext ctx) {
		if (ctx.IDENT() != null) { // IDENT L_PAREN funcRParams? R_PAREN
			String funcName = ctx.IDENT().getText();
			Symbol symbol = currentScope.resolve(funcName);
			if (symbol == null) {
				int lineNo = getLineNo(ctx.IDENT());
				System.err.println("Error type 2 at Line " + lineNo + ": Undefined function: " + funcName + ".");
			} else if (!(symbol.getType() instanceof FunctionType)) {
				int lineNo = getLineNo(ctx.IDENT());
				System.err.println("Error type 10 at Line " + lineNo + ": Not a function: test1.");
			} else {
				FunctionType functionType = (FunctionType) currentScope.resolve(funcName).getType();
				ArrayList<Type> paramsType = functionType.getParamsType();
				ArrayList<Type> argsType = new ArrayList<>();
				for (SysYParser.ParamContext paramContext : ctx.funcRParams().param()) {
					argsType.add(getExpType(paramContext.exp()));
				}
				if (!paramsType.equals(argsType)) {
					int lineNo = getLineNo(ctx.IDENT());
					System.err.println("Error type 8 at Line " + lineNo + ": Function is not applicable for arguments.");
				}
			}
		} else if (ctx.MUL() != null || ctx.DIV() != null || ctx.MOD() != null || ctx.PLUS() != null
				|| ctx.MINUS() != null) {
			Type op1Type = getExpType(ctx.exp(0));
			Type op2Type = getExpType(ctx.exp(1));
			if (op1Type.toString().equals("int") && op2Type.toString().equals("int")) {
			} else {
				TerminalNode operator;
				if (ctx.MUL() != null) {
					operator = ctx.MUL();
				} else if (ctx.DIV() != null) {
					operator = ctx.DIV();
				} else if (ctx.MOD() != null) {
					operator = ctx.MOD();
				} else if (ctx.PLUS() != null) {
					operator = ctx.PLUS();
				} else {
					operator = ctx.MINUS();
				}
				int lineNo = getLineNo(operator);
				System.err.println("Error type 6 at Line " + lineNo + ": type.Type mismatched for operands.");
			}
		}
		Void ret = super.visitExp(ctx);
		return ret;
	}
	
}
