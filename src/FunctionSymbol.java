import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;

public class FunctionSymbol extends BaseScope implements Symbol {
	public FunctionType type;
	public List<Pair<Integer, Integer>> usagePosition;
	
	@Override
	public FunctionType getType() {
		return type;
	}
	
	public FunctionSymbol(String name, Scope enclosingScope, FunctionType type) {
		super(name, enclosingScope);
		this.usagePosition = new ArrayList<>();
		this.type = type;
	}
	public void addUsage(int lineNo, int columnNo) {
		usagePosition.add(new Pair<>(lineNo, columnNo));
	}
	
	public boolean findUsage(int lineNo, int columnNo) {
		return usagePosition.contains(new Pair<>(lineNo, columnNo));
	}
}