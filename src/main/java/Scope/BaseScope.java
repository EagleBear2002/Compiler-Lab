package Scope;//import com.google.common.base.MoreObjects;

import java.util.LinkedHashMap;
import java.util.Map;
import Symbol.*;

public class BaseScope implements Scope {
	private final Scope enclosingScope;
	private final Map<String, Symbol> symbols = new LinkedHashMap<>();
	private String name;
	
	public BaseScope(String name, Scope enclosingScope) {
		this.name = name;
		this.enclosingScope = enclosingScope;
	}
	
	@Override
	public boolean definedSymbol(String name) {
		return symbols.containsKey(name);
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
	
	public Map<String, Symbol> getSymbols() {
		return this.symbols;
	}
	
	@Override
	public void define(Symbol symbol) {
		symbols.put(symbol.getName(), symbol);
//		System.out.println("+(" + symbol.getName() + ", " + symbol.getType() + ")");
	}
	
	@Override
	public Symbol resolve(String name) {
		Symbol symbol = symbols.get(name);
		if (symbol != null) {
			return symbol;
		}
		
		if (enclosingScope != null) {
			return enclosingScope.resolve(name);
		}
		
		return null;
	}
}
