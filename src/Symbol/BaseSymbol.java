package Symbol;//import com.google.common.base.MoreObjects;

import org.antlr.v4.runtime.misc.Pair;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.ArrayList;
import java.util.List;
public class BaseSymbol implements Symbol {
	final String name;
	final LLVMTypeRef type;
	public List<Pair<Integer, Integer>> usagePosition;
	
	public BaseSymbol(String name, LLVMTypeRef type) {
		this.name = name;
		this.type = type;
		this.usagePosition = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public LLVMTypeRef getType() {
		return type;
	}
	
	public void addUsage(int lineNo, int columnNo) {
		usagePosition.add(new Pair(lineNo, columnNo));
	}
	
	public boolean findUsage(int lineNo, int columnNo) {
		boolean ret = usagePosition.contains(new Pair(lineNo, columnNo));
		return ret;
	}
}