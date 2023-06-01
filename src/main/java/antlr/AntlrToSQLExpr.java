package antlr;

import gen.gParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class AntlrToSQLExpr extends gen.gBaseVisitor<SQLExpr> {

    @Override
    public SQLExpr visitStatement(gParser.StatementContext ctx) {

        StringLiteral colName = new StringLiteral(ctx.getChild(0).getText());
        StringLiteral operator = new StringLiteral(ctx.getChild(1).getText());
        Literal value = null;
        try {
            System.out.println("parsed = " + ctx.getChild(2).getText());
            value = parseLiteral(ctx.getChild(2).getText());
        } catch (Exception e) {

        }
        return new Statement(colName, operator, value);
    }

    @Override
    public SQLExpr visitCondition(gParser.ConditionContext ctx) {

        Vector<Statement> statements = new Vector<>();
        Vector<StringLiteral> logOperator = new Vector<>();

        for (int i = 0; i < ctx.getChildCount(); i = i + 2) {

            System.out.println(ctx.getChild(i).getClass());

            Statement currStatement = (Statement) visit(ctx.getChild(i));
            statements.add(currStatement);

            if (i + 1 < ctx.getChildCount()) {
                StringLiteral logicalOperator = new StringLiteral(ctx.getChild(i + 1).getText());
                logOperator.add(logicalOperator);
            }
        }

        return new Condition(statements, logOperator);
    }

    @Override
    public SQLExpr visitSelection(gParser.SelectionContext ctx) {
        StringLiteral tableName = new StringLiteral(ctx.getChild(3).getText());
        Condition condition = (Condition) visit(ctx.getChild(5));

        System.out.println(condition);

        return new SelectCommand(tableName, condition);
    }

    @Override
    public SQLExpr visitDelete(gParser.DeleteContext ctx) {
        StringLiteral tableName = new StringLiteral(ctx.getChild(1).getText());
        Condition condition = (Condition) visit(ctx.getChild(3));
        return new DeleteCommand(tableName, condition);
    }

    @Override
    public SQLExpr visitInsert(gParser.InsertContext ctx) {
        StringLiteral tableName = new StringLiteral(ctx.getChild(1).getText());
        Columns columns = (Columns) visit(ctx.getChild(3));
        ValueList valueList = (ValueList) visit(ctx.getChild(7));

        InsertCommand insertCommand = new InsertCommand(tableName, columns, valueList);

        return insertCommand;
    }

    @Override
    public SQLExpr visitUpdate(gParser.UpdateContext ctx) {
        StringLiteral tableName = new StringLiteral(ctx.getChild(1).getText());
        Condition setColumns = (Condition) visit(ctx.getChild(3));
        Condition updateCondition = (Condition) visit(ctx.getChild(5));

        return new UpdateCommand(tableName, setColumns, updateCondition);

    }

//    @Override
//    public SQLExpr visitObject(gParser.ObjectContext ctx) {
//        return super.visitObject(ctx);
//    }

    @Override
    public SQLExpr visitValues(gParser.ValuesContext ctx) {

        Vector<Literal> values = new Vector<>();
        ValueList finalVals = new ValueList(values);

        try {
            Literal literal = parseLiteral(ctx.getChild(0).getText());
            values.add(literal);
        } catch (Exception e) {

        }

        if (ctx.getChildCount() == 1) {
            return finalVals;
        }

        ValueList secondChildVals = (ValueList) visit(ctx.getChild(2));
        values.addAll(secondChildVals.getValues());


        return finalVals;
    }

    @Override
    public SQLExpr visitCreateIndex(gParser.CreateIndexContext ctx) {

        StringLiteral tableName = new StringLiteral(ctx.getChild(3).getText());
        Columns columns = (Columns) visit(ctx.getChild(5));

        return new createIndexCommand(tableName, columns);
    }


    @Override
    public SQLExpr visitColumns(gParser.ColumnsContext ctx) {
        Vector<StringLiteral> colNames = new Vector<>();
        Columns finalCols = new Columns(colNames);

        StringLiteral firstCol = new StringLiteral(ctx.getChild(0).getText());
        colNames.add(firstCol);

        if (ctx.getChildCount() == 1) {
            return finalCols;
        }

        Columns secondChildCols = (Columns) visit(ctx.getChild(2));
        colNames.addAll(secondChildCols.columnNames);


        return finalCols;
    }


    public static Literal parseLiteral(String value) throws Exception {

        Literal literal = new Literal();

        try {
            literal = new IntLiteral(Integer.parseInt(value));
        } catch (Exception e) {
            try {
                literal = new DoubleLiteral(Double.parseDouble(value));

            } catch (Exception f) {
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                    literal = new DateLiteral(date);
                } catch (Exception g) {
                    literal = new StringLiteral(value);
                }
            }
        }

        return literal;
    }


}
