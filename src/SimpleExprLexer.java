// Generated from ./src/SysYLexer.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SimpleExprLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, ID=8, INT=9, WS=10, 
		SL_COMMENT=11, ML_COMMENT=12;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "ID", "INT", 
			"WS", "SL_COMMENT", "ML_COMMENT", "LETTER", "DIGIT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'='", "'if'", "'*'", "'/'", "'+'", "'-'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "ID", "INT", "WS", "SL_COMMENT", 
			"ML_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SimpleExprLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SysYLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\16j\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3"+
		"\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\5\t\61\n\t\3\t\3\t\3\t\7\t\66\n"+
		"\t\f\t\16\t9\13\t\3\n\3\n\3\n\7\n>\n\n\f\n\16\nA\13\n\5\nC\n\n\3\13\6"+
		"\13F\n\13\r\13\16\13G\3\13\3\13\3\f\3\f\3\f\3\f\7\fP\n\f\f\f\16\fS\13"+
		"\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\7\r]\n\r\f\r\16\r`\13\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\16\3\16\3\17\3\17\4Q^\2\20\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21"+
		"\n\23\13\25\f\27\r\31\16\33\2\35\2\3\2\6\3\2\63;\5\2\13\f\17\17\"\"\4"+
		"\2C\\c|\3\2\62;\2p\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\3\37\3\2\2\2\5!\3\2\2\2\7#\3\2\2\2\t&\3\2"+
		"\2\2\13(\3\2\2\2\r*\3\2\2\2\17,\3\2\2\2\21\60\3\2\2\2\23B\3\2\2\2\25E"+
		"\3\2\2\2\27K\3\2\2\2\31X\3\2\2\2\33f\3\2\2\2\35h\3\2\2\2\37 \7=\2\2 \4"+
		"\3\2\2\2!\"\7?\2\2\"\6\3\2\2\2#$\7k\2\2$%\7h\2\2%\b\3\2\2\2&\'\7,\2\2"+
		"\'\n\3\2\2\2()\7\61\2\2)\f\3\2\2\2*+\7-\2\2+\16\3\2\2\2,-\7/\2\2-\20\3"+
		"\2\2\2.\61\5\33\16\2/\61\7a\2\2\60.\3\2\2\2\60/\3\2\2\2\61\67\3\2\2\2"+
		"\62\66\5\33\16\2\63\66\5\35\17\2\64\66\7a\2\2\65\62\3\2\2\2\65\63\3\2"+
		"\2\2\65\64\3\2\2\2\669\3\2\2\2\67\65\3\2\2\2\678\3\2\2\28\22\3\2\2\29"+
		"\67\3\2\2\2:C\7\62\2\2;?\t\2\2\2<>\5\35\17\2=<\3\2\2\2>A\3\2\2\2?=\3\2"+
		"\2\2?@\3\2\2\2@C\3\2\2\2A?\3\2\2\2B:\3\2\2\2B;\3\2\2\2C\24\3\2\2\2DF\t"+
		"\3\2\2ED\3\2\2\2FG\3\2\2\2GE\3\2\2\2GH\3\2\2\2HI\3\2\2\2IJ\b\13\2\2J\26"+
		"\3\2\2\2KL\7\61\2\2LM\7\61\2\2MQ\3\2\2\2NP\13\2\2\2ON\3\2\2\2PS\3\2\2"+
		"\2QR\3\2\2\2QO\3\2\2\2RT\3\2\2\2SQ\3\2\2\2TU\7\f\2\2UV\3\2\2\2VW\b\f\2"+
		"\2W\30\3\2\2\2XY\7\61\2\2YZ\7,\2\2Z^\3\2\2\2[]\13\2\2\2\\[\3\2\2\2]`\3"+
		"\2\2\2^_\3\2\2\2^\\\3\2\2\2_a\3\2\2\2`^\3\2\2\2ab\7,\2\2bc\7\61\2\2cd"+
		"\3\2\2\2de\b\r\2\2e\32\3\2\2\2fg\t\4\2\2g\34\3\2\2\2hi\t\5\2\2i\36\3\2"+
		"\2\2\13\2\60\65\67?BGQ^\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}