import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Visitor extends SysYParserBaseVisitor<Void>{
	@Override
	public Void visitChildren(RuleNode node) {
		RuleContext ctx = node.getRuleContext();
		int id = ctx.getRuleIndex();
		String ruleName = SysYParser.ruleNames[id];
		System.err.printf("visitChildren(%s)\n", ruleName);
		return super.visitChildren(node);
	}
	
	@Override
	public Void visitTerminal(TerminalNode node) {
		return super.visitTerminal(node);
	}
}
