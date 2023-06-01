package antlr;

import java.util.Date;

public class DateLiteral extends Literal{
    public DateLiteral(Date date) {
        this.object = date;
    }

    public String toString(){
        return object.toString();
    }
}
