package antlr;

public class Literal extends SQLExpr {
    Object object;

    public Object getValue(){
        return object;
    }

    public String toString(){
        return object.toString();
    }
}
