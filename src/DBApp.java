import java.util.*;

public class DBApp {
    private Vector<Table> tables;
    private Vector<String> dataTypes;
    public DBApp(){
        tables = new Vector<Table>();
        dataTypes = new Vector<>();
        Collections.addAll(dataTypes, "java.lang.Integer", "java.lang.Double", "java.lang.String", "java.util.Date");
    }

    public Vector<Table> getTables() {
        return tables;
    }

    public void setTables(Vector<Table> tables) {
        this.tables = tables;
    }

    public void createTable(String strTableName,
                            String strClusteringKeyColumn,
                            Hashtable<String,String> htblColNameType,
                            Hashtable<String,String> htblColNameMin,
                            Hashtable<String,String> htblColNameMax ) throws DBAppException{

        Table table1 = new Table(strTableName,strClusteringKeyColumn, htblColNameType, htblColNameMin, htblColNameMax);
        tables.add(table1);
        for (Map.Entry<String, String> entry : htblColNameType.entrySet()) {
            String value = entry.getValue();
            if(!dataTypes.contains(value))
                throw new DBAppException("Invalid data type");
        }
        if(htblColNameType.get(strClusteringKeyColumn) == null)
            throw new DBAppException("Invalid Primary Key");

    }

    public static void main(String[] args){

    }

}
