package antlr;

import java.util.Hashtable;

public class createIndexCommand extends SQLExpr{
    StringLiteral tableName;

    Columns columns;

    public StringLiteral getTableName() {
        return tableName;
    }

    public Columns getColumns() {
        return columns;
    }

    public createIndexCommand(StringLiteral tableName, Columns columns) {
        this.tableName = tableName;
        this.columns = columns;
    }
}
