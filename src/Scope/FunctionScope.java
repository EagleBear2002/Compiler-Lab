package Scope;

import org.antlr.v4.runtime.misc.Pair;

import Symbol.*;
import org.bytedeco.llvm.LLVM.LLVMTypeRef;

import java.util.ArrayList;
import java.util.List;

public class FunctionScope extends BaseScope implements Symbol {
	public LLVMTypeRef type;
	public List<Pair<Integer, Integer>> usagePosition;
	
	@Override
	public LLVMTypeRef getType() {
		return type;
	}
	
	public FunctionScope(String name, Scope enclosingScope, LLVMTypeRef type) {
		super(name, enclosingScope);
		this.usagePosition = new ArrayList<>();
		this.type = type;
	}
	
	public void addUsage(int lineNo, int columnNo) {
		usagePosition.add(new Pair<>(lineNo, columnNo));
	}
	
	public boolean findUsage(int lineNo, int columnNo) {
		return usagePosition.contains(new Pair<>(lineNo, columnNo));
	}
}