// Generated from ./src/SysYParser.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SysYParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SysYParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SysYParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(SysYParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#compUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompUnit(SysYParser.CompUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(SysYParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#constDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDecl(SysYParser.ConstDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#bType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBType(SysYParser.BTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#constDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstDef(SysYParser.ConstDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#constInitVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstInitVal(SysYParser.ConstInitValContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#varDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDecl(SysYParser.VarDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#varDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDef(SysYParser.VarDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#initVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitVal(SysYParser.InitValContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#funcDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncDef(SysYParser.FuncDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#funcType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncType(SysYParser.FuncTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#funcFParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncFParams(SysYParser.FuncFParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#funcFParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncFParam(SysYParser.FuncFParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(SysYParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#blockItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockItem(SysYParser.BlockItemContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignment}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(SysYParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code possibleExp}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPossibleExp(SysYParser.PossibleExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code blockStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStmt(SysYParser.BlockStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(SysYParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(SysYParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(SysYParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(SysYParser.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link SysYParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(SysYParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code lValExp}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLValExp(SysYParser.LValExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unaryExp}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExp(SysYParser.UnaryExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenExp}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExp(SysYParser.ParenExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code addExp}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExp(SysYParser.AddExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mulExp}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulExp(SysYParser.MulExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code funcCallExp}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCallExp(SysYParser.FuncCallExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numExp}
	 * labeled alternative in {@link SysYParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumExp(SysYParser.NumExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code compareExp}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompareExp(SysYParser.CompareExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relationExp}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationExp(SysYParser.RelationExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code condExp}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondExp(SysYParser.CondExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code andExp}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExp(SysYParser.AndExpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code orExp}
	 * labeled alternative in {@link SysYParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrExp(SysYParser.OrExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#lVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLVal(SysYParser.LValContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(SysYParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#unaryOp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryOp(SysYParser.UnaryOpContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#funcRParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncRParams(SysYParser.FuncRParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(SysYParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link SysYParser#constExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstExp(SysYParser.ConstExpContext ctx);
}