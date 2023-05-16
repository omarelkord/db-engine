package antlr;

public class Value {
    Literal literal;

    public Value(Literal literal){
        this.literal = literal;
    }

    public Object getValue(){
        return this.literal.getValue();
    }


}
