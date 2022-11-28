import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import java.io.*;
import java.util.List;

public class Main {	
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("input path is required");
			return;
		}
		
// Lexer
		String sourcePath = args[0];
		CharStream input = CharStreams.fromFileName(sourcePath);
		
		SysYLexer sysYLexer = new SysYLexer(input);
		
		MyLexerErrorListener myLexerErrorListener = new MyLexerErrorListener();
		sysYLexer.removeErrorListeners();
		sysYLexer.addErrorListener(myLexerErrorListener);
		
		if (myLexerErrorListener.listenError()) {
			return;
		}
		
		List<? extends Token> tokenList = sysYLexer.getAllTokens();
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
//			System.err.printf("%s %s at Line %d.\n", ruleNames[ruleNum - 1], tokenText, lineNum);
//		}
		
// Parser
		CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
		SysYParser sysYParser = new SysYParser(tokens);
		MyParserErrorListener myParserErrorListener = new MyParserErrorListener();
		sysYParser.removeErrorListeners();
		sysYParser.addErrorListener(myParserErrorListener);
		
		if (myParserErrorListener.listenError()) {
			return;
		}
		
	}
}