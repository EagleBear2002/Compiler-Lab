package Scope;

import java.util.Map;

import Symbol.*;

public interface Scope {
	String getName();
	
	void setName(String name);
	
	Scope getEnclosingScope();
	
	Map<String, Symbol> getSymbols();
	
	void define(Symbol symbol);
	
	Symbol resolve(String name);
	
	boolean definedSymbol(String name);
}