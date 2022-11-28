import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class Visitor extends SysYParserBaseVisitor<Void>{
	@Override
	public Void visitChildren(RuleNode node) {
		return super.visitChildren(node);
	}
	
	@Override
	public Void visitTerminal(TerminalNode node) {
		return super.visitTerminal(node);
	}
}
