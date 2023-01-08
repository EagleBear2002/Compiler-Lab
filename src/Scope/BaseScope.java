package Scope;//import com.google.common.base.MoreObjects;

import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseScope implements Scope {
	private final Scope enclosingScope;
	private final Map<String, LLVMValueRef> valueRefs = new LinkedHashMap<>();
	private String name;
	
	public BaseScope(String name, Scope enclosingScope) {
		this.name = name;
		this.enclosingScope = enclosingScope;
	}
	
	@Override
	public boolean definedSymbol(String name) {
		return valueRefs.containsKey(name);
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Scope getEnclosingScope() {
		return this.enclosingScope;
	}
	
	public Map<String, LLVMValueRef> getValueRef() {
		return this.valueRefs;
	}
	
	@Override
	public void define(String name, LLVMValueRef llvmValueRef) {
		valueRefs.put(name, llvmValueRef);
		System.out.println(this.name + "+(" + name + ", " + llvmValueRef + ")");
	}
	
	@Override
	public LLVMValueRef resolve(String name) {
		LLVMValueRef symbol = valueRefs.get(name);
		if (symbol != null) {
			return symbol;
		}
		
		if (enclosingScope != null) {
			return enclosingScope.resolve(name);
		}
		
//		System.out.println("can not resolve: " + name);
		return null;
	}
}
