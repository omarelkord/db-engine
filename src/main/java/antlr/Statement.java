package antlr;

public class Statement extends SQLExpr{

    StringLiteral colName;
    StringLiteral operator;
    Literal value;

    public StringLiteral getColName() {
        return colName;
    }

    public StringLiteral getOperator() {
        return operator;
    }

    public Literal getValue() {
        return value;
    }

    public Statement(StringLiteral colName, StringLiteral operator, Literal value) {
        this.colName = colName;
        this.operator = operator;
        this.value = value;
    }

    public String toString(){
        return colName.toString() + " " + operator.toString() + " " + value.toString();
    }


}
