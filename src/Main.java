import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.io.*;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("input path is required");
			return;
		}
		
		String sourcePath = args[0];
		CharStream input = CharStreams.fromFileName(sourcePath);
		SysYLexer sysYLexer = new SysYLexer(input);
		
		MyErrorListener myErrorListener = new MyErrorListener();
		sysYLexer.removeErrorListeners();
		sysYLexer.addErrorListener(myErrorListener);
		List<? extends Token> tokens = sysYLexer.getAllTokens();
		
		if (myErrorListener.listenError()) {
//			System.err.println("Error listened");
			return;
		}
		
		String[] ruleNames = sysYLexer.getRuleNames();
		for (Token token : tokens) {
//			System.err.println(token);
			String tokenText = token.getText();
			int ruleNum = token.getType();
			int lineNum = token.getLine();
			if (ruleNum == 34) { // HEXADECIMAL
				System.err.println("INTEGR_CONST");
				
				if (tokenText.startsWith("0x") || tokenText.startsWith("0X")) {
					tokenText = String.valueOf(Integer.parseInt(tokenText.substring(2), 16));
				} else if (tokenText.startsWith("0")) {
					tokenText = String.valueOf(Integer.parseInt(tokenText, 8));
				}
			}
			
			System.err.printf("%s %s at Line %d\n", ruleNames[ruleNum-1], tokenText, lineNum);
		}
	}
}