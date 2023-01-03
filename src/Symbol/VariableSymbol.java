package Symbol;

import Symbol.BaseSymbol;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

public class VariableSymbol extends BaseSymbol {
	public VariableSymbol(String name, LLVMTypeRef type) {
		super(name, type);
	}
}
