import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

import java.io.*;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("input path is required");
		}
		String sourcePath = args[0];
		CharStream input = CharStreams.fromFileName(sourcePath);
//		SysYLexerLexer sysYLexer = new SysYLexerLexer(input);
		
		MyErrorListener myErrorListener = new MyErrorListener();
//		sysYLexer.removeErrorListeners();
//		sysYLexer.addErrorListener(myErrorListener);
//		List<? extends Token> tokens = sysYLexer.getAllTokens();
		
		if (!myErrorListener.listenError()) {
			return;
		}
		
//		for (Token token : tokens) {
//			
//		}
	}
}