// Generated from D:/db-engine/src/main/java\g.g4 by ANTLR 4.12.0
package gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link gParser}.
 */
public interface gListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link gParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(gParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(gParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#operation}.
	 * @param ctx the parse tree
	 */
	void enterOperation(gParser.OperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#operation}.
	 * @param ctx the parse tree
	 */
	void exitOperation(gParser.OperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#createTable}.
	 * @param ctx the parse tree
	 */
	void enterCreateTable(gParser.CreateTableContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#createTable}.
	 * @param ctx the parse tree
	 */
	void exitCreateTable(gParser.CreateTableContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#createIndex}.
	 * @param ctx the parse tree
	 */
	void enterCreateIndex(gParser.CreateIndexContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#createIndex}.
	 * @param ctx the parse tree
	 */
	void exitCreateIndex(gParser.CreateIndexContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#insert}.
	 * @param ctx the parse tree
	 */
	void enterInsert(gParser.InsertContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#insert}.
	 * @param ctx the parse tree
	 */
	void exitInsert(gParser.InsertContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#update}.
	 * @param ctx the parse tree
	 */
	void enterUpdate(gParser.UpdateContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#update}.
	 * @param ctx the parse tree
	 */
	void exitUpdate(gParser.UpdateContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#delete}.
	 * @param ctx the parse tree
	 */
	void enterDelete(gParser.DeleteContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#delete}.
	 * @param ctx the parse tree
	 */
	void exitDelete(gParser.DeleteContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(gParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(gParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#selection}.
	 * @param ctx the parse tree
	 */
	void enterSelection(gParser.SelectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#selection}.
	 * @param ctx the parse tree
	 */
	void exitSelection(gParser.SelectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#updateColumns}.
	 * @param ctx the parse tree
	 */
	void enterUpdateColumns(gParser.UpdateColumnsContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#updateColumns}.
	 * @param ctx the parse tree
	 */
	void exitUpdateColumns(gParser.UpdateColumnsContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#columns}.
	 * @param ctx the parse tree
	 */
	void enterColumns(gParser.ColumnsContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#columns}.
	 * @param ctx the parse tree
	 */
	void exitColumns(gParser.ColumnsContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#values}.
	 * @param ctx the parse tree
	 */
	void enterValues(gParser.ValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#values}.
	 * @param ctx the parse tree
	 */
	void exitValues(gParser.ValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(gParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(gParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#object}.
	 * @param ctx the parse tree
	 */
	void enterObject(gParser.ObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#object}.
	 * @param ctx the parse tree
	 */
	void exitObject(gParser.ObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#date}.
	 * @param ctx the parse tree
	 */
	void enterDate(gParser.DateContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#date}.
	 * @param ctx the parse tree
	 */
	void exitDate(gParser.DateContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#year}.
	 * @param ctx the parse tree
	 */
	void enterYear(gParser.YearContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#year}.
	 * @param ctx the parse tree
	 */
	void exitYear(gParser.YearContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#month}.
	 * @param ctx the parse tree
	 */
	void enterMonth(gParser.MonthContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#month}.
	 * @param ctx the parse tree
	 */
	void exitMonth(gParser.MonthContext ctx);
	/**
	 * Enter a parse tree produced by {@link gParser#day}.
	 * @param ctx the parse tree
	 */
	void enterDay(gParser.DayContext ctx);
	/**
	 * Exit a parse tree produced by {@link gParser#day}.
	 * @param ctx the parse tree
	 */
	void exitDay(gParser.DayContext ctx);
}