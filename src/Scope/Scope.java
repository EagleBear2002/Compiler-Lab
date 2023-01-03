package Scope;

import java.util.Map;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

public interface Scope {
	String getName();
	
	void setName(String name);
	
	Scope getEnclosingScope();
	
	Map<String, LLVMValueRef> getValueRef();
	
	void define(String name, LLVMValueRef llvmValueRef);
	
	LLVMValueRef resolve(String name);
	
	boolean definedSymbol(String name);
}