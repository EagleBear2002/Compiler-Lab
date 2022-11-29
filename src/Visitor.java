import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Visitor extends SysYParserBaseVisitor<Void> {
	private static int depth = 0;
	@Override
	public Void visitChildren(RuleNode node) {
//		RuleContext ctx = node.getRuleContext();
//		int ruleIndex = ctx.getRuleIndex();
//		String ruleName = SysYParser.ruleNames[ruleIndex];
//		String realName = ruleName.substring(0, 1).toUpperCase() + ruleName.substring(1);
//
//		for (int i = 0; i < depth; ++i)
//			System.err.print("  ");
//		System.err.println(realName);
//
//		depth++;
//		Void ret = super.visitChildren(node);
//		depth--;
		
//		return ret;
		
		Void result = this.defaultResult();
		int n = node.getChildCount();
		
		for(int i = 0; i < n && this.shouldVisitNextChild(node, result); ++i) {
			ParseTree c = node.getChild(i);
			Void childResult = c.accept(this);
			result = this.aggregateResult(result, childResult);
		}
		System.err.println("123");
		
		return result;
	}
	
	@Override
	public Void visitTerminal(TerminalNode node) {
		Token token = node.getSymbol();
		int tokenIndex = token.getTokenIndex();
		System.err.println("terminal text = " + token.getText());
		String ruleName = SysYLexer.ruleNames[tokenIndex];
		System.err.println("index = " + tokenIndex + ", ruleName: " + ruleName);
		
		return super.visitTerminal(node);
	}
}
