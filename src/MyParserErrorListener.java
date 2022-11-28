import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

public class MyParserErrorListener extends BaseErrorListener {
	private boolean isError = false;
	public boolean listenError() {
		return isError;
	}
	
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object o, int lineNum, int posInLine, String msg, RecognitionException e) {
		isError = true;
		System.err.printf("Error type B at Line %d: %s\n", lineNum, msg);
	}
	
	@Override
	public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
		System.err.printf("Error type B\n");
	}
	
	@Override
	public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {
		System.err.printf("Error type B\n");
	}
	
	@Override
	public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
		System.err.printf("Error type B\n");
	}
}
