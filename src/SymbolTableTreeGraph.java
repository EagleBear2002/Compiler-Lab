import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.util.Set;
import java.util.stream.Collectors;

public class SymbolTableTreeGraph {
	private final Set<String> nodes = new OrderedHashSet<>();
	private final MultiMap<String, String> edges = new MultiMap<>();
	
	public static String toDot(Scope scope) {
		String symbols = scope.getSymbols().values()
				.stream()
				.map(Symbol::getName)
				.collect(Collectors.joining("</TD><TD>", "<TR><TD>", "</TD></TR>"));
		
		return scope.getName() +
				" [label = <<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\">" +
				"<TR><TD COLSPAN = \"" + scope.getSymbols().size() + "\">" + scope.getName() + "</TD></TR>" +
				symbols +
				"</TABLE>>]";
	}
	
	public void addNode(String node) {
		nodes.add(node);
	}
	
	public void addEdge(String source, String target) {
		edges.map(source, target);
	}
	
	public String toDot() {
		StringBuilder buf = new StringBuilder();
		
		buf.append("digraph G {\n")
				.append("  rankdir = BT\n")
				.append("  ranksep = 0.25\n")
				.append("  edge [arrowsize = 0.5]\n")
				.append("  node [shape = none]\n\n");
		
		buf.append(String.join(";\n", nodes)).append(";\n\n");
		
		buf.append(edges.getPairs().stream()
				.map(edge -> String.format("%s -> %s", edge.a, edge.b))
				.collect(Collectors.joining(";\n", "", ";\n"))).append("}");
		
		return buf.toString();
	}
}