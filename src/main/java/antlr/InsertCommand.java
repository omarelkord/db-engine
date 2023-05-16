package antlr;

import java.util.Hashtable;
import java.util.Vector;

public class InsertCommand extends SQLExpr {

    StringLiteral tableName;
    Columns columns;
    ValueList valueList;

//    String tableName;
//    Vector<>
//

    public StringLiteral getTableName() {
        return tableName;
    }

    public Columns getColumns() {
        return columns;
    }

    public ValueList getValueList() {
        return valueList;
    }

    public InsertCommand(StringLiteral tableName, Columns columns, ValueList valueList) {
        this.tableName = tableName;
        this.columns = columns;
        this.valueList = valueList;
    }

    public String toString(){
        return "Table Name: " + tableName.getValue() + " Columns:" + columns.getColumnNames() + " Values: " + valueList.getValues();
    }

}
