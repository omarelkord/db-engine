import java.io.*;
import java.util.*;

public class DBApp {
    private Vector<Table> tables;
    private Vector<String> tableFilepaths;
    private Vector<String> dataTypes;
    private static String d_file_path = "D:\\";
    private int maxNoRowsInPage;
    private int maxEntriesInNode;

    public DBApp(){
       try{
           init();
       }catch(Exception e){

       }
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

    public void insertIntoTable(String strTableName,
                                Hashtable<String,Object> htblColNameValue) throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException, ParseException {


        boolean flag = false;
        Table table= null;
        for(int i=0;i<tables.size();i++){
            if(tables.get(i).getName().equals(strTableName)){
                flag=true;
                table = tables.get(i);
            }
        }
        if(!flag)
            throw new DBAppException("Table not found");

        BufferedReader br = new BufferedReader(new FileReader(d_file_path));

        String line = br.readLine();
        String[] content = line.split(",");

        String clusteringKey = "";

        while(line!=null) {

            String tableName = content[0];
            String colName = content[1];
            String colType = content[2];
            String isClusteringKey = content[3];
            String min = content[6];
            String max = content[7];
            Object value = htblColNameValue.get(colName);

            if (!tableName.equals(strTableName)) {
                line = br.readLine();
                continue;
            }

            if (value != null) {
                if (!sameType(value, colType))
                    throw new DBAppException("Incompatible data types");
                if (compare(value, max) > 0)
                    throw new DBAppException("Value is greater than the allowed maximum value");
                if (compare(value, min) < 0)
                    throw new DBAppException("Value is less than the allowed minimum value");
            }
            //setting primary key
            if (Boolean.parseBoolean(isClusteringKey)) {
                clusteringKey = colName;
            }

            line = br.readLine();
        }

        if(table.getPagesPaths().isEmpty()){
            Page page = new Page();
            String filepath = "page-" + page.getId();
            page.serialize(filepath);
            table.getPagesPaths().add(filepath);
        }
    }

    public boolean sameType(Object data, String dataType) throws ClassNotFoundException{
        return data.getClass().equals(Class.forName(dataType));
    }

    public static int compare(Object object, String value) throws ParseException {

        Comparable parsed;

        if(object instanceof Integer){
            parsed = Integer.parseInt(value);
        }else if(object instanceof Double){
            parsed = Double.parseDouble(value);
        }else if(object instanceof Date){
            parsed = new SimpleDateFormat("yyyy-MM-dd").parse(value);
        }else{
            parsed = value;
        }

        return ((Comparable) object).compareTo(parsed);
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

    public static Properties readConfig(String path) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(path);
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

    public static void main(String[] args) throws DBAppException, IOException, ParseException {
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
        Object i = "";
        Object date = new Date(2022 - 1900, 3, 16);
        String s = "2022-03-16";
        System.out.println(compare(date,s));

    }

}
