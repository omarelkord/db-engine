package antlr;

import gen.gParser;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.swing.plaf.nimbus.State;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class AntlrToSQLExpr extends gen.gBaseVisitor<SQLExpr>{

    @Override
    public SQLExpr visitCondition(gParser.ConditionContext ctx) {

        Vector<Statement> statements = new Vector<>();
        Vector<StringLiteral> logOperator = new Vector<>();

        for(int i = 0; i < ctx.getChildCount(); i= i+4){

            StringLiteral colNameChild = new StringLiteral(ctx.getChild(i).getText());
            StringLiteral operatorChild = new StringLiteral(ctx.getChild(i+1).getText());
            Literal valueChild = null;
            try{
                valueChild = parseLiteral(ctx.getChild(i+2).getText());
            }catch (Exception e){

            }

            if(i+3 < ctx.getChildCount()){
                StringLiteral logicalOperator = new StringLiteral(ctx.getChild(i+3).getText());
                logOperator.add(logicalOperator);
            }


            Statement statement = new Statement(colNameChild, operatorChild, valueChild);
            statements.add(statement);
        }

        return new Condition(statements, logOperator);
    }

    @Override
    public SQLExpr visitSelection(gParser.SelectionContext ctx) {
        StringLiteral tableName = new StringLiteral(ctx.getChild(3).getText());
        Condition condition = (Condition) visit(ctx.getChild(5));

        return new SelectCommand(tableName, condition);
    }

    @Override
    public SQLExpr visitDelete(gParser.DeleteContext ctx) {
        StringLiteral tableName = new StringLiteral(ctx.getChild(1).getText());
        Condition condition = (Condition) visit(ctx.getChild(3));
        return new DeleteCommand(tableName,condition);
    }

    @Override
    public SQLExpr visitInsert(gParser.InsertContext ctx) {
        StringLiteral tableName = new StringLiteral(ctx.getChild(1).getText());
        Columns columns = (Columns) visit(ctx.getChild(3));
        ValueList valueList = (ValueList) visit(ctx.getChild(7));

        InsertCommand insertCommand = new InsertCommand(tableName, columns, valueList);

        return insertCommand;
    }

//    @Override
//    public SQLExpr visitObject(gParser.ObjectContext ctx) {
//        return super.visitObject(ctx);
//    }

    @Override
    public SQLExpr visitValues(gParser.ValuesContext ctx) {

        Vector<Literal> values = new Vector<>();
        ValueList finalVals = new ValueList(values);

        try{
            Literal literal = parseLiteral(ctx.getChild(0).getText());
            values.add(literal);
        }catch(Exception e){

        }

        if(ctx.getChildCount() == 1){
            return finalVals;
        }

        ValueList secondChildVals = (ValueList) visit(ctx.getChild(2));
        values.addAll(secondChildVals.getValues());


        return finalVals;
    }

    @Override
    public SQLExpr visitColumns(gParser.ColumnsContext ctx) {
        Vector<StringLiteral> colNames = new Vector<>();
        Columns finalCols = new Columns(colNames);

        StringLiteral firstCol = new StringLiteral(ctx.getChild(0).getText());
        colNames.add(firstCol);

        if(ctx.getChildCount() == 1){
            return finalCols;
        }

        Columns secondChildCols = (Columns) visit(ctx.getChild(2));
        colNames.addAll(secondChildCols.columnNames);


        return finalCols;
    }


    public static Literal parseLiteral(String value) throws Exception{

        Literal literal = new Literal();

        try{
            literal = new IntLiteral(Integer.parseInt(value));
            System.out.println("INT LIT");
        }catch (Exception e){
            try {
                literal =  new DoubleLiteral(Double.parseDouble(value));
                System.out.println("DOUBLE LIT");

            }
            catch (Exception f){
                try{
                    literal = new DateLiteral(new SimpleDateFormat("yyyy-MM-dd").parse(value));
                    System.out.println("DATE LIT");

                }
                catch (Exception g){
                    literal = new StringLiteral(value);
                    System.out.println("STRING LIT");

                }
            }
        }

        return literal;
    }



    //    @Override
//    public SQLExpr visitStatement(gParser.StatementContext ctx) {
//        return super.visitStatement(ctx);
//    }
}
