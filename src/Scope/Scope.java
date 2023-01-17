package Scope;

import java.util.Map;

import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

public interface Scope {
	String getScopeName();
	
	void setScopeName(String scopeName);
	
	Scope getEnclosingScope();
	
	Map<String, LLVMValueRef> getValueRef();
	
	void define(String name, LLVMValueRef llvmValueRef, LLVMTypeRef llvmTypeRef);
	
	LLVMValueRef resolve(String name);
	
	LLVMTypeRef getType(String name);
}
