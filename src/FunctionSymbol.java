import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;

public class FunctionSymbol extends BaseScope implements Symbol {
	public FunctionType type;
	public List<Pair<Integer, Integer>> usagePosition;
	public FunctionSymbol(String name, Scope enclosingScope, FunctionType type) {
		super(name, enclosingScope);
		this.usagePosition = new ArrayList<>();
		this.type = type;
	}
	public void addUsage(int lineNo, int columnNo) {
//		System.out.println("addUsage(" + lineNo + ", " + columnNo + ", " + getName() + ")");
//		System.out.println("size = " + usagePosition.size());
		usagePosition.add(new Pair(lineNo, columnNo));
	}
	
	public boolean findUsage(int lineNo, int columnNo) {
		boolean ret = usagePosition.contains(new Pair(lineNo, columnNo));
		return ret;
	}
}