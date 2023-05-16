package antlr;

import java.util.Vector;

public class SelectCommand extends SQLExpr {

    StringLiteral tableName;

    Condition condition;

    public SelectCommand(StringLiteral tableName, Condition condition) {
        this.tableName = tableName;
        this.condition = condition;
    }

    public String toString() {
        return "Table Name = " + tableName.toString() + " Condition: " + condition.toString();
    }

    public Condition getCondition(){
        return condition;
    }

    public StringLiteral getTableName() {
        return tableName;
    }
}
