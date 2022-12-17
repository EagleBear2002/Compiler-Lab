import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;

public class Main {
	public static SysYLexer lexer(String sourcePath) throws IOException {
		CharStream input = CharStreams.fromFileName(sourcePath);
		SysYLexer sysYLexer = new SysYLexer(input);
		return sysYLexer;
	}
	
	public static SysYParser parser(SysYLexer sysYLexer) {
		CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
		SysYParser sysYParser = new SysYParser(tokens);
		return sysYParser;
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("input path is required");
			return;
		}
		
		String filePath = args[0];
		int lineNO = Integer.valueOf(args[1]);
		int column = Integer.valueOf(args[2]);
		String name = args[2];
		SysYLexer sysYLexer = lexer(filePath);
		SysYParser sysYParser = parser(sysYLexer);
		
		MyParserErrorListener myParserErrorListener = new MyParserErrorListener();
		sysYParser.removeErrorListeners();
		sysYParser.addErrorListener(myParserErrorListener);
		
		ParseTree tree = sysYParser.program();
		if (myParserErrorListener.listenError()) {
		}
		
		Visitor visitor = new Visitor();
		visitor.visit(tree);
		
	}
}