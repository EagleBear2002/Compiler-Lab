import SymbolTable.*;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

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

	@Override
	public Void visitTerminal(TerminalNode node) {
		Token token = node.getSymbol();
		int ruleNum = token.getType() - 1;

		if (ruleNum >= 0) {
			String ruleName = SysYLexer.ruleNames[ruleNum];
			String tokenText = token.getText();
			String color = getHelight(ruleName);

			if (ruleName == "INTEGR_CONST") {
				if (tokenText.startsWith("0x") || tokenText.startsWith("0X")) {
					tokenText = String.valueOf(Integer.parseInt(tokenText.substring(2), 16));
				} else if (tokenText.startsWith("0")) {
					tokenText = String.valueOf(Integer.parseInt(tokenText, 8));
				}
			}

			if (isPrint && ruleName == "IDENT") {
				int lineNO = token.getLine();
				int columnNO = token.getCharPositionInLine();
				Symbol symbol = currentScope.resolve(tokenText);
				if (symbol != null) {
					symbol.addUsage(lineNO, columnNO);

					if (symbol.findUsage(renameLineNo, renameColumnNo)) {
						System.out.println("tokenText = " + tokenText + ", findUsage(" + renameLineNo + ", "
								+ renameColumnNo + ")");
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
		String typeName = ctx.funcType().getText();
		globalScope.resolve(typeName);

		String funcName = ctx.IDENT().getText();
		if (currentScope.resolve(funcName) != null) {
			int lineNo = ctx.IDENT().getSymbol().getLine();
			System.err.println("Error type 4 at Line " + lineNo + ": Undefined variable: " + funcName + ".");
		}
		
		FunctionSymbol fun = new FunctionSymbol(funcName, currentScope);
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
		Type type = (Type) globalScope.resolve(typeName);

		for (SysYParser.VarDefContext varDefContext : ctx.varDef()) {
			String varName = varDefContext.IDENT().getText();
			if (currentScope.resolve(varName) != null) {
				int lineNo = varDefContext.IDENT().getSymbol().getLine();
				System.err.println("Error type 3 at Line " + lineNo + ": Undefined variable: " + varName + ".");
			}
			VariableSymbol varSymbol = new VariableSymbol(varName, type);
			currentScope.define(varSymbol);
		}

		Void ret = super.visitVarDecl(ctx);
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

	@Override
	public Void visitLVal(SysYParser.LValContext ctx) {
		String varName = ctx.IDENT().getText();
		if (currentScope.resolve(varName) == null) {
			int lineNo = ctx.IDENT().getSymbol().getLine();
			System.err.println("Error type 1 at Line " + lineNo + ": Undefined variable: " + varName + ".");
		}
		Void ret = super.visitLVal(ctx);
		return ret;
	}

	@Override
	public Void visitStmt(SysYParser.StmtContext ctx) {
		Void ret = super.visitStmt(ctx);
		return ret;
	}

	@Override
	public Void visitExp(SysYParser.ExpContext ctx) {
		if (ctx.IDENT() != null) {
			String funcName = ctx.IDENT().getText();
			if (currentScope.resolve(funcName) == null) {
				int lineNo = ctx.IDENT().getSymbol().getLine();
				System.err.println("Error type 2 at Line " + lineNo + ": Undefined variable: " + funcName + ".");
			}
		}
		Void ret = super.visitExp(ctx);
		return ret;
	}
}
