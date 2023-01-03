package Symbol;

import org.antlr.v4.runtime.misc.Pair;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.ArrayList;
import java.util.List;

public interface Symbol {
	
	String getName();
	
	void addUsage(int lineNo, int columnNo);
	
	boolean findUsage(int lineNo, int columnNo);
	
	LLVMTypeRef getType();
}
