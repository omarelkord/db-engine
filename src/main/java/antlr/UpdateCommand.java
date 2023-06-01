package antlr;

import java.util.Hashtable;

public class UpdateCommand extends SQLExpr{
    StringLiteral tableName;
    Condition setColumns;
    Condition updateConditions;

    public UpdateCommand(StringLiteral tableName, Condition setColumns, Condition updateConditions) {
        this.tableName = tableName;
        this.setColumns = setColumns;
        this.updateConditions = updateConditions;
    }

    public StringLiteral getTableName() {
        return tableName;
    }

    public Condition getSetColumns() {
        return setColumns;
    }

    public Condition getUpdateConditions() {
        return updateConditions;
    }


}
