# 编译原理 Lab2 实验报告

## 实现功能

本次实验完成了以下功能：

1. 对 SysY 语言进行语法分析
2. 输出语法树和语法高亮
3. 报告语法错误并提供提示信息

## 实验困难

实验所遇到的最大困难是测试样例时报错：

```bash
eaglebear2002@EagleBear2002-HP:/mnt/c/Users/37756/Documents/NJU/2022Fall/Compilers/Lab$ make parser-test1
java -jar /usr/local/lib/antlr-*-complete.jar -listener -visitor -long-messages ./src/SysYParser.g4 ./src/SysYLexer.g4
mkdir -p classes
javac -g -classpath /usr/local/lib/antlr-4.9.1-complete.jar ./src/Main.java ./src/MyLexerErrorListener.java ./src/MyParserErrorListener.java ./src/S
ysYLexer.java ./src/SysYParser.java ./src/SysYParserBaseListener.java ./src/SysYParserBaseVisitor.java ./src/SysYParserListener.java ./src/SysYParse
rVisitor.java ./src/Visitor.java -d classes
java -classpath ./classes:/usr/local/lib/antlr-4.9.1-complete.jar Main tests/parser-test1.sysy
Error type B at Line 7: mismatched input '<EOF>' expecting {'const', 'int', 'void'}
```

笔者不理解该报错的原因，后来无意中注释掉词法分析过程后消除了该报错：

```java
public class Main {
	public static SysYLexer lexer(String sourcePath) throws IOException {
		CharStream input = CharStreams.fromFileName(sourcePath);
		SysYLexer sysYLexer = new SysYLexer(input);

//		MyLexerErrorListener myLexerErrorListener = new MyLexerErrorListener();
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
}
```

笔者和助教沟通后，找到了问题所在：`sysYLexer.getAllTokens()` 方法分析了所有词法单元后指针移动到了最后，再调用词法单元时只能从 `<EOF>` 处开始读取。