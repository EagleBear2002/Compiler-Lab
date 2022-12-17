import Lexer.SysYLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import Parser.MyParserErrorListener;
import Parser.SysYParser;
import Parser.Visitor;

import java.io.*;

public class Main {
	public static SysYLexer lexer(String sourcePath) throws IOException {
		CharStream input = CharStreams.fromFileName(sourcePath);
		SysYLexer sysYLexer = new SysYLexer(input);

//		lexer.MyLexerErrorListener myLexerErrorListener = new lexer.MyLexerErrorListener();
//		sysYLexer.removeErrorListeners();
//		sysYLexer.addErrorListener(myLexerErrorListener);
//		List<? extends Token> tokenList = sysYLexer.getAllTokens();
//
//		if (myLexerErrorListener.listenError()) {
//			return sysYLexer;
//		}
//
//		String[] ruleNames = sysYLexer.getRuleNames();
//		for (Token token : tokenList) {
//			String tokenText = token.getText();
//			int ruleNum = token.getType();
//			int lineNum = token.getLine();
//			if (ruleNum == 34) {
//				if (tokenText.startsWith("0x") || tokenText.startsWith("0X")) {
//					tokenText = String.valueOf(Integer.parseInt(tokenText.substring(2), 16));
//				} else if (tokenText.startsWith("0")) {
//					tokenText = String.valueOf(Integer.parseInt(tokenText, 8));
//				}
//			}
//
////			System.err.printf("%s %s at Line %d.\n", ruleNames[ruleNum - 1], tokenText, lineNum);
//		}

		return sysYLexer;
	}

	public static SysYParser parser(SysYLexer sysYLexer) {
		CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
		SysYParser sysYParser = new SysYParser(tokens);
		MyParserErrorListener myParserErrorListener = new MyParserErrorListener();
		sysYParser.removeErrorListeners();
		sysYParser.addErrorListener(myParserErrorListener);

		ParseTree tree = sysYParser.program();
		if (myParserErrorListener.listenError()) {
			return sysYParser;
		}

		Visitor visitor = new Visitor();
		visitor.visit(tree);
		
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
	}
}