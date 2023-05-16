package antlr;

import java.util.Vector;

public class Columns extends SQLExpr{

    Vector<StringLiteral> columnNames;

    public Columns(Vector<StringLiteral> columnNames) {
        this.columnNames = columnNames;
    }

    public Vector<StringLiteral> getColumnNames() {
        return columnNames;
    }
}
