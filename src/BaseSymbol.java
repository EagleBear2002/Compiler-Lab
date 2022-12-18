//import com.google.common.base.MoreObjects;

import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;

public class BaseSymbol implements Symbol {
	final String name;
	final Type type;
	public List<Pair<Integer, Integer>> usagePosition;
	
	public BaseSymbol(String name, Type type) {
		this.name = name;
		this.type = type;
		this.usagePosition = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public void addUsage(int lineNo, int columnNo) {
		// System.out.println("addUsage(" + lineNo + ", " + columnNo + ", " + getName() + ")");
		// System.out.println("size = " + usagePosition.size());
		usagePosition.add(new Pair(lineNo, columnNo));
	}
	
	public boolean findUsage(int lineNo, int columnNo) {
		boolean ret = usagePosition.contains(new Pair(lineNo, columnNo));
		return ret;
	}
	
//	public String toString() {
//		return MoreObjects.toStringHelper(this)
//				.add("name", name)
//				.add("type", type)
//				.toString();
//	}
}