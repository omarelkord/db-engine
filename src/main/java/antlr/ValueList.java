package antlr;

import java.util.List;
import java.util.Vector;

public class ValueList extends SQLExpr {
    Vector<Literal> values;

    public ValueList(Vector<Literal> values) {
        this.values = values;
    }

    public Vector<Literal> getValues() {
        return values;
    }
}

