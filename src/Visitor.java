import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Visitor extends SysYParserBaseVisitor<Void> {
	private static int depth = 0;
	
	private void printIdent(int depth) {
		for (int i = 0; i < depth; ++i)
			System.err.print("  ");
	}
	
	private String getHelight(String ruleName) {
		switch (ruleName) {
			case "CONST":
		}
		return "color";
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
		
//		Void result = this.defaultResult();
//		int n = node.getChildCount();
//		
//		for(int i = 0; i < n && this.shouldVisitNextChild(node, result); ++i) {
//			ParseTree c = node.getChild(i);
//			Void childResult = c.accept(this);
//			result = this.aggregateResult(result, childResult);
//		}
//		
//		return result;
	}
	
	@Override
	public Void visitTerminal(TerminalNode node) {
		Token token = node.getSymbol();
		int tokenIndex = token.getTokenIndex();
		String ruleName = SysYLexer.ruleNames[tokenIndex];
		String tokenText = token.getText();
		
		printIdent(depth);
		System.err.println(tokenText + ruleName + "[" + getHelight(ruleName) +"]");
		
		return super.visitTerminal(node);
	}
}
