import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.util.List;

public class Main {
	public static SysYLexer lexer(String sourcePath) throws IOException {
		CharStream input = CharStreams.fromFileName(sourcePath);
		SysYLexer sysYLexer = new SysYLexer(input);

		MyLexerErrorListener myLexerErrorListener = new MyLexerErrorListener();
		sysYLexer.removeErrorListeners();
		sysYLexer.addErrorListener(myLexerErrorListener);
		List<? extends Token> tokenList = sysYLexer.getAllTokens();

		if (myLexerErrorListener.listenError()) {
			return sysYLexer;
		}

		String[] ruleNames = sysYLexer.getRuleNames();
		for (Token token : tokenList) {
			String tokenText = token.getText();
			int ruleNum = token.getType();
			int lineNum = token.getLine();
			if (ruleNum == 34) {
				if (tokenText.startsWith("0x") || tokenText.startsWith("0X")) {
					tokenText = String.valueOf(Integer.parseInt(tokenText.substring(2), 16));
				} else if (tokenText.startsWith("0")) {
					tokenText = String.valueOf(Integer.parseInt(tokenText, 8));
				}
			}

//			System.err.printf("%s %s at Line %d.\n", ruleNames[ruleNum - 1], tokenText, lineNum);
		}

		return sysYLexer;
	}

	public static SysYParser parser(SysYLexer sysYLexer) {
		
//		CommonTokenStream commonTokenStream = new CommonTokenStream(sysYLexer);
//		SysYParser sysYParser = new SysYParser(commonTokenStream);
//		sysYParser.removeErrorListeners();
//		MyParserErrorListener parserErrorListener = new MyParserErrorListener();
//		sysYParser.addErrorListener(parserErrorListener);
//		ParseTree tree = sysYParser.program();
//		if(parserErrorListener.listenError()){
//			return;
//		}
//		Visitor visitor = new Visitor();
//		String[] s = sysYLexer.getRuleNames();
////        for(int i = 0; i < sysYLexer.getRuleNames().length; i++){
////            System.err.println(s[i]);
////        }
////		visitor.setTokenNames(s, sysYParser.getRuleNames());
////		visitor.visit(tree);
		
		CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
		SysYParser sysYParser = new SysYParser(tokens);
		MyParserErrorListener myParserErrorListener = new MyParserErrorListener();
		sysYParser.removeErrorListeners();
		sysYParser.addErrorListener(myParserErrorListener);

		ParseTree tree = sysYParser.program();
		System.err.println("Parser Checking");
		if (myParserErrorListener.listenError()) {
			return sysYParser;
		}
		System.err.println("Successful Checking");
		
		// Visitor extends SysYParserBaseVisitor<Void>
		Visitor visitor = new Visitor();
		visitor.visit(tree);
		
		return sysYParser;
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("input path is required");
			return;
		}

		SysYLexer sysYLexer = lexer(args[0]);
		SysYParser sysYParser = parser(sysYLexer);
	}
}
//import org.antlr.v4.runtime.*;
//import org.antlr.v4.runtime.tree.ParseTree;
//
//import java.io.*;
//
//public class Main {
//	public static void main(String[] args) throws IOException {
//		if (args.length < 1) {
//			System.err.println("input path is required");
//		}
//		String source = args[0];
//		CharStream input = CharStreams.fromFileName(source);
////        CharStream input = CharStreams.fromFileName("tests/test1.sysy");
//		SysYLexer sysYLexer = new SysYLexer(input);
//		sysYLexer.removeErrorListeners();
//		MyLexerErrorListener myErrorListener = new MyLexerErrorListener();
//		sysYLexer.addErrorListener(myErrorListener);
//		
//		CommonTokenStream commonTokenStream = new CommonTokenStream(sysYLexer);
//		SysYParser sysYParser = new SysYParser(commonTokenStream);
//		sysYParser.removeErrorListeners();
//		MyParserErrorListener parserErrorListener = new MyParserErrorListener();
//		sysYParser.addErrorListener(parserErrorListener);
//		ParseTree tree = sysYParser.program();
//		if(parserErrorListener.listenError()){
//			return;
//		}
//		Visitor visitor = new Visitor();
//		String[] s = sysYLexer.getRuleNames();
////        for(int i = 0; i < sysYLexer.getRuleNames().length; i++){
////            System.err.println(s[i]);
////        }
////		visitor.setTokenNames(s, sysYParser.getRuleNames());
////		visitor.visit(tree);
//
////
//
////        for(Token a : atn){
////            System.err.print(name[a.getType()-1]);
////            System.err.print(' ');
////            if(name[a.getType()-1].equals("INTEGR_CONST")) {
////                String s = String.valueOf(a.getText());
////                if(s.length() < 2){
////                    System.err.print(s);
////                }
////                else if(s.charAt(0) == '0' && s.charAt(1) == 'x'){
////                    int x=Integer.parseInt(s.substring(2),16);
////                    System.err.print(x);
////                }
////                else if(s.charAt(0) == '0'){
////                    int x=Integer.parseInt(s.substring(1), 8);
////                    System.err.print(x);
////                }
////                else {
////                    System.err.print(a.getText());
////                }
////            }
////            else {
////                System.err.print(a.getText());
////            }
////            System.err.print(" at Line ");
////            System.err.print(a.getLine());
////            System.err.println('.');
////
////        }
//	}
//}
