package Scope;

import Symbol.BasicTypeSymbol;

public class GlobalScope extends BaseScope {
	public GlobalScope(Scope enclosingScope) {
		super("Scope.Scope.GlobalScope", enclosingScope);
		define(new BasicTypeSymbol("int"));
		define(new BasicTypeSymbol("void"));
	}
}