package antlr;

public class DeleteCommand extends SQLExpr{
    StringLiteral tableName;
    Condition condition;

    public DeleteCommand(StringLiteral tableName, Condition condition) {
        this.tableName = tableName;
        this.condition = condition;
    }

    public StringLiteral getTableName() {
        return tableName;
    }

    public Condition getCondition() {
        return condition;
    }
    public String toString() {
        return "Table Name = " + tableName.toString() + " Condition: " + condition.toString();
    }
}
