package antlr;

import java.util.Vector;

public class Condition extends SQLExpr {

    Vector<Statement> statements;
    Vector<StringLiteral> operators;

    public Condition(Vector<Statement> statements, Vector<StringLiteral> operators) {
        this.statements = statements;
        this.operators = operators;
    }

    public String toString() {
        return "Statements" + statements + " Operator" + operators;
    }

    public Vector<Statement> getStatements() {
        return statements;
    }

    public Vector<StringLiteral> getOperators() {
        return operators;
    }
}
