package antlr;

public class IntLiteral extends Literal{
    public IntLiteral(int n){
        this.object = n;
    }
    public String toString(){
        return object.toString();
    }

    public Integer getValue(){
        return (int) super.getValue();
    }

}
