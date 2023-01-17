package Scope;//import com.google.common.base.MoreObjects;

import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseScope implements Scope {
	private final Scope enclosingScope;
	private final Map<String, LLVMValueRef> valueRefs = new LinkedHashMap<>();
	private final Map<String, LLVMTypeRef> valueTypes = new LinkedHashMap<>();
	private String scopeName;
	
	public BaseScope(String name, Scope enclosingScope) {
		this.scopeName = name;
		this.enclosingScope = enclosingScope;
	}
	
	@Override
	public String getScopeName() {
		return this.scopeName;
	}
	
	@Override
	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}
	
	@Override
	public Scope getEnclosingScope() {
		return this.enclosingScope;
	}
	
	public Map<String, LLVMValueRef> getValueRef() {
		return this.valueRefs;
	}
	
	@Override
	public void define(String name, LLVMValueRef llvmValueRef, LLVMTypeRef llvmTypeRef) {
		valueRefs.put(name, llvmValueRef);
		valueTypes.put(name, llvmTypeRef);
//		System.out.println(this.name + "+(" + name + ", " + llvmValueRef + ")");
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
	
	@Override
	public LLVMTypeRef getType(String name) {
		LLVMTypeRef typeRef = valueTypes.get(name);
		if (typeRef != null) {
			return typeRef;
		}
		
		if (enclosingScope != null) {
			return enclosingScope.getType(name);
		}

		return null;
	}
}
