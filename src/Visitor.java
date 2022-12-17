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
		
		printIdent(depth);
		System.err.println(realName);
		
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
			
			if (color != "no color") {
				printIdent(depth);
				System.err.println(tokenText + " " + ruleName + "[" + color + "]");
			}
		}
		
		return super.visitTerminal(node);
	}
	
	
	@Override
	public Void visit(ParseTree tree) {
		return super.visit(tree);
	}
	
	@Override
	public Void visitProgram(SysYParser.ProgramContext ctx) {
		globalScope = new GlobalScope(null);
		currentScope = globalScope;
		System.out.println("enterProgram");
		
		Void ret = super.visitProgram(ctx);
		
		System.out.println("exitProgram");
//		currentScope = currentScope.getEnclosingScope();
		
		return ret;
	}
	
	@Override
	public Void visitFuncDef(SysYParser.FuncDefContext ctx) {
		
		String typeName = ctx.funcType().getText();
		globalScope.resolve(typeName);
		
		String funcName = ctx.getText();
		FunctionSymbol fun = new FunctionSymbol(funcName, currentScope);
		
		currentScope.define(fun);
		currentScope = fun;
		
		System.out.println("enterFuncDef");
		System.out.println("typeName = " + typeName);
		System.out.println("funcName = " + funcName);
		
		Void ret = super.visitFuncDef(ctx);
		
		System.out.println("exitFuncDef");
		currentScope = currentScope.getEnclosingScope();
		
		return ret;
	}
	
	@Override
	public Void visitBlock(SysYParser.BlockContext ctx) {
		LocalScope localScope = new LocalScope(currentScope);
		String localScopeName = localScope.getName() + localScopeCounter;
		localScope.setName(localScopeName);
		localScopeCounter++;
		
		System.out.println("enterBlock");
		
		Void ret = super.visitBlock(ctx);
		
		System.out.println("exitBlock");
		currentScope = currentScope.getEnclosingScope();
		
		return ret;
	}
}
