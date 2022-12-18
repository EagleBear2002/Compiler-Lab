// /*
// import cymbol.CymbolBaseListener;
// import cymbol.CymbolParser;

// public class SymbolTableListener extends CymbolBaseListener {
// 	private final SymbolTableTreeGraph graph = new SymbolTableTreeGraph();

// 	private GlobalScope globalScope = null;
// 	private Scope currentScope = null;
// 	private int localScopeCounter = 0;

// 	// Adding Code Below

// 	/**
// 	 * (1) When/How to start/enter a new scope?
// 	 */
// 	@Override
// 	public void enterProg(CymbolParser.ProgContext ctx) {
// 		globalScope = new GlobalScope(null);
// 		currentScope = globalScope;
// 	}

// 	@Override
// 	public void enterFunctionDecl(CymbolParser.FunctionDeclContext ctx) {
// 		String typeName = ctx.type().getText();
// 		globalScope.resolve(typeName);

// 		String funName = ctx.ID().getText();
// 		FunctionSymbol fun = new FunctionSymbol(funName, currentScope);
// 		graph.addEdge(funName, currentScope.getName());

// 		currentScope.define(fun);
// 		currentScope = fun;
// 	}

// 	@Override
// 	public void enterBlock(CymbolParser.BlockContext ctx) {
// 		LocalScope localScope = new LocalScope(currentScope);
// 		String localScopeName = localScope.getName() + localScopeCounter;
// 		localScope.setName(localScopeName);
// 		localScopeCounter++;

// 		graph.addEdge(localScopeName, currentScope.getName());

// 		currentScope = localScope;
// 	}

// 	/**
// 	 * (2) When/How to exit the current scope?
// 	 */
// 	@Override
// 	public void exitProg(CymbolParser.ProgContext ctx) {
// 		graph.addNode(SymbolTableTreeGraph.toDot(currentScope));
// 		currentScope = currentScope.getEnclosingScope();
// 	}

// 	@Override
// 	public void exitFunctionDecl(CymbolParser.FunctionDeclContext ctx) {
// 		graph.addNode(SymbolTableTreeGraph.toDot(currentScope));
// 		currentScope = currentScope.getEnclosingScope();
// 	}

// 	@Override
// 	public void exitBlock(CymbolParser.BlockContext ctx) {
// 		graph.addNode(SymbolTableTreeGraph.toDot(currentScope));
// 		currentScope = currentScope.getEnclosingScope();
// 	}

// 	/**
// 	 * (3) When to define symbols?
// 	 */
// 	@Override
// 	public void exitVarDecl(CymbolParser.VarDeclContext ctx) {
// 		String typeName = ctx.type().getText();
// 		Type type = (Type) globalScope.resolve(typeName);

// 		String varName = ctx.ID().getText();
// 		VariableSymbol varSymbol = new VariableSymbol(varName, type);

// 		currentScope.define(varSymbol);
// 	}

// 	@Override
// 	public void exitFormalParameter(CymbolParser.FormalParameterContext ctx) {
// 		String typeName = ctx.type().getText();
// 		Type type = (Type) globalScope.resolve(typeName);

// 		String varName = ctx.ID().getText();
// 		VariableSymbol varSymbol = new VariableSymbol(varName, type);

// 		currentScope.define(varSymbol);
// 	}

// 	/**
// 	 * (4) When to resolve symbols?
// 	 */
// 	@Override
// 	public void exitId(CymbolParser.IdContext ctx) {
// 		String varName = ctx.ID().getText();
// 		currentScope.resolve(varName);
// 	}

// 	/**
// 	 * (5) When to add nodes?
// 	 */

// 	/**
// 	 * (6) When to add edges?
// 	 */

// 	public SymbolTableTreeGraph getGraph() {
// 		return graph;
// 	}
// }
// */