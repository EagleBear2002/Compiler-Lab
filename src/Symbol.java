import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;

public interface Symbol {
	
	public String getName();
	
	public void addUsage(int lineNo, int columnNo);
	
	public boolean findUsage(int lineNo, int columnNo);
//	public default void addUsage(int lineNo, int columnNo) {
//		System.out.println("addUsage(" + lineNo + ", " + columnNo + ", " + getName() + ")");
//		System.out.println("size = " + usagePosition.size());
//		usagePosition.add(new Pair(lineNo, columnNo));
//	}
//	
//	public default boolean findUsage(int lineNo, int columnNo) {
//		boolean ret = usagePosition.contains(new Pair(lineNo, columnNo));
//		return ret;
//	}
}
