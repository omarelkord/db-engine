package antlr;

import java.util.Hashtable;

public class InsertCommand {
    String tableName;
    Hashtable<String, Object> htblColNameVal;

    public InsertCommand(String tableName, Hashtable<String, Object> htblColNameVal){
        this.tableName = tableName;
        this.htblColNameVal = htblColNameVal;
    }
}
