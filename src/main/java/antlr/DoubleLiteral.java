package antlr;

public class DoubleLiteral extends Literal{
    public DoubleLiteral(Double d){
        this.object = d;
    }

    public String toString(){
        return this.object.toString();
    }
}
