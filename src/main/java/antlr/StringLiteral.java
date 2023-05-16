package antlr;

public class StringLiteral extends Literal {
    public StringLiteral(String s){
        this.object = s;
    }

    public String toString(){
        return object.toString();
    }

    public String getValue(){
        return (String) super.getValue();
    }
}
