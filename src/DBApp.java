import java.io.*;
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
                            Hashtable<String,String> htblColNameMax ) throws DBAppException, FileNotFoundException {

        for(Table t : tables){
            if(t.getName().equals(strTableName))
                throw new DBAppException("Invalid table name, already exists");
        }

        for (Map.Entry<String, String> entry : htblColNameType.entrySet()) {
            String value = entry.getValue();
            if(!dataTypes.contains(value))
                throw new DBAppException("Invalid data type");
        }

        if(htblColNameType.get(strClusteringKeyColumn) == null)
            throw new DBAppException("Invalid Primary Key");

        Table table = new Table(strTableName,strClusteringKeyColumn, htblColNameType, htblColNameMin, htblColNameMax);
        tables.add(table);

        writeInCSV(strTableName,strClusteringKeyColumn,htblColNameType,htblColNameMin,htblColNameMax); //a method that will write in the csv
    }

    public void writeInCSV(String strTableName,
                                  String strClusteringKeyColumn,
                                  Hashtable<String,String> htblColNameType,
                                  Hashtable<String,String> htblColNameMin,
                                  Hashtable<String,String> htblColNameMax ) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream("D:\\db-engine\\metadata.csv", true));
        String row = "";
        for(String key: htblColNameType.keySet()){
            row = "\r\n";
            pw.append(row);
            row = strTableName+","+key+","+htblColNameType.get(key)+","+htblColNameType.get(key)+","+strClusteringKeyColumn+","+"false,"+htblColNameMin.get(key)+","+htblColNameMax.get(key);
            pw.append(row);
        }
        pw.close();
    }


    public static void main(String[] args) throws DBAppException, FileNotFoundException {
        String strTableName = "Student";
        DBApp dbApp = new DBApp( );
        Hashtable htblColNameType = new Hashtable( );
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");
        Hashtable htblColNameMin = new Hashtable();
        htblColNameMin.put("id","1000");
        htblColNameMin.put("name","ZZZZZ");
        htblColNameMin.put("gpa","1000.0");
        Hashtable htblColNameMax = new Hashtable();
        htblColNameMax.put("id","1000");
        htblColNameMax.put("name","ZZZZZ");
        htblColNameMax.put("gpa","1000.0");
        dbApp.createTable( strTableName, "id", htblColNameType, htblColNameMin,htblColNameMax );
        String name = "Student2";
        Hashtable types = new Hashtable( );
        types.put("id", "java.lang.Integer");
        types.put("name", "java.lang.String");
        types.put("gpa", "java.lang.Double");
        Hashtable Min = new Hashtable();
        Min.put("id","1000");
        Min.put("name","ZZZZZ");
        Min.put("gpa","1000.0");
        Hashtable Max = new Hashtable();
        Max.put("id","1000");
        Max.put("name","ZZZZZ");
        Max.put("gpa","1000.0");
        dbApp.createTable( name, "id", types, Min,Max);

    }

}
