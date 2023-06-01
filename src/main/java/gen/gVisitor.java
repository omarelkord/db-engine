// Generated from D:/db-engine/src/main/java/gen\g.g4 by ANTLR 4.12.0
package gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link gParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface gVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link gParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(gParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#createTable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreateTable(gParser.CreateTableContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#createIndex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreateIndex(gParser.CreateIndexContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#insert}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInsert(gParser.InsertContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#update}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUpdate(gParser.UpdateContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#delete}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDelete(gParser.DeleteContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(gParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#selection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelection(gParser.SelectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#columns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumns(gParser.ColumnsContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#values}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValues(gParser.ValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(gParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#object}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObject(gParser.ObjectContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#date}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDate(gParser.DateContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#year}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitYear(gParser.YearContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#month}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMonth(gParser.MonthContext ctx);
	/**
	 * Visit a parse tree produced by {@link gParser#day}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDay(gParser.DayContext ctx);
}