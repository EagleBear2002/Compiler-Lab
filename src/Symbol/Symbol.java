package Symbol;

import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import Type.*;

public interface Symbol {
	
	String getName();
	
	void addUsage(int lineNo, int columnNo);
	
	boolean findUsage(int lineNo, int columnNo);
	
	Type getType();
}
