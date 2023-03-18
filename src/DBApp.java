import java.io.*;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DBApp {

    private Vector<String> tableNames;
    private Vector<String> dataTypes;
    private static String d_file_path = "D:\\";
    private int maxNoRowsInPage;
    private int maxEntriesInNode;

    public DBApp(){

    }


    public void init() throws IOException {

//        for(String filepath : tableFilepaths){
//
//        }

        tableNames = new Vector<>();
        dataTypes = new Vector<>();
        Collections.addAll(dataTypes, "java.lang.Integer", "java.lang.Double", "java.lang.String", "java.util.Date");

        Properties properties = readConfig("DBApp.config");
        maxNoRowsInPage = Integer.parseInt(properties.getProperty("MaximumRowsCountinTablePage"));
        maxEntriesInNode = Integer.parseInt(properties.getProperty("MaximumEntriesinOctreeNode"));
    }

    public void createTable(String strTableName,
                            String strClusteringKeyColumn,
                            Hashtable<String,String> htblColNameType,
                            Hashtable<String,String> htblColNameMin,
                            Hashtable<String,String> htblColNameMax) throws DBAppException, FileNotFoundException, IOException {

        if(tableNames.contains(strTableName))
            throw new DBAppException("Table already exists");

        for (Map.Entry<String, String> entry : htblColNameType.entrySet()) {
            String value = entry.getValue();
            if(!dataTypes.contains(value))
                throw new DBAppException("Invalid data type");
        }

        if(htblColNameType.get(strClusteringKeyColumn) == null)
            throw new DBAppException("Invalid Primary Key");


        //post-verification

        Table table = new Table(strTableName, strClusteringKeyColumn);
        tableNames.add(strTableName);
        table.serialize(strTableName);

        writeInCSV(strTableName,strClusteringKeyColumn,htblColNameType,htblColNameMin,htblColNameMax); //a method that will write in the csv
    }

    public void insertIntoTable(String strTableName,
                                Hashtable<String,Object> htblColNameValue) throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException, ParseException {
        Table table = Table.deserialize(strTableName);

        if(table == null)
            throw new DBAppException("Table not found");

//        BufferedReader br = new BufferedReader(new FileReader(METADATA_PATH));
//
//        String line = br.readLine();
//        String[] content = line.split(",");

        String clusteringKey = null;
        try {
            clusteringKey = getClusteringKey(strTableName, htblColNameValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//
//        while(line!=null) {
//
//            String tableName = content[0];
//            String colName = content[1];
//            String colType = content[2];
//            String isClusteringKey = content[3];
//            String min = content[6];
//            String max = content[7];
//            Object value = htblColNameValue.get(colName);
//
//            if (!tableName.equals(strTableName)) {
//                line = br.readLine();
//                continue;
//            }
//
//            if (value != null) {
//                if (!sameType(value, colType))
//                    throw new DBAppException("Incompatible data types");
//                if (compare(value, max) > 0)
//                    throw new DBAppException("Value is greater than the allowed maximum value");
//                if (compare(value, min) < 0)
//                    throw new DBAppException("Value is less than the allowed minimum value");
//            }
//            //setting primary key
//            if (Boolean.parseBoolean(isClusteringKey)) {
//                clusteringKey = colName;
//            }
//
//            line = br.readLine();
//        }

        Comparable clusteringKeyValue = (Comparable) htblColNameValue.get(clusteringKey);

        //INTEGRITY CONSTRAINTS
        if(clusteringKeyValue == null)
            throw new DBAppException("Cannot allow null values for this field");
        if(table.getHtblKeyPageId().get(clusteringKeyValue)!=null)
            throw new DBAppException("Cannot allow duplicate values for this field");


        if(table.getHtblPageIdPagesPaths().isEmpty()){ //the first case is inserting in an empty table
            Page page = new Page();
            page.getTuples().add(htblColNameValue);
            String filepath = "page-" + page.getId();
            page.serialize(filepath);
            table.getPagesPaths().add(filepath);
        }
        else {

//            clusteringKeyValue

            for(Integer id : table.getHtblPageIdMinMax().keySet()){
                Pair pair = table.getHtblPageIdMinMax().get(id);
                Object min = pair.getMin();
                Object max = pair.getMax();



                if(clusteringKeyValue.compareTo(min) > 0 && clusteringKeyValue.compareTo(max) < 0){
                    String locatedPagePath = table.getHtblPageIdPagesPaths().get(id);
                    Page locatedPage = Page.deserialize(locatedPagePath);

                    //HELPER CALL();
                    //binary search for record
                    Vector<Hashtable<String, Object>> tuples = new Vector<>();
                    binaryInsert(htblColNameValue,locatedPage.getTuples(),clusteringKey);


                }
            }
        }
    }

    public void helper(Hashtable<String,Object> tuple, Integer id,Table table) throws IOException, ClassNotFoundException {
        if(!table.hasPage(id)) {
            // ADJUST HASHTABLES
            Page newPage = new Page();
            newPage.getTuples().add(0, tuple);
            updateHtbls(newPage, table);
            return;
        }

        Page page = Page.deserialize(table.getPagePath(id));
        page.getTuples().add(0, tuple);

        Object CKValue = tuple.get(table.getCKName());

        // >>>
        table.getHtblKeyPageId().put(CKValue, id);

        if(page.isFull()) {
            int lastIndex = page.getTuples().size() - 1;
            Hashtable<String, Object> newTuple = page.getTuples().remove(lastIndex);

            helper(newTuple, id + 1, table);
        }
    }

    public void updateHtbls(Page newPage, Table table){
        Object CKValue = newPage.getTuples().get(0).get(table.getCKName());
        int id = newPage.getId();
        table.getHtblPageIdCurrPageSize().put(id, 1);
        table.getHtblPageIdMinMax().put(id, new Pair(CKValue, CKValue));
        table.getHtblPageIdPagesPaths().put(id, table.getPagePath(id));

    }

    public static void binaryInsert(Hashtable<String,Object> tuple, Vector<Hashtable<String, Object>> page, String ck){
        int left = 0;
        int right = page.size() - 1;

        while(left<=right){
            int mid = (left + right) / 2;

            if(tuple.get(ck) == null)
                System.out.println(true);

            if( ((Comparable)page.get(mid).get(ck)).compareTo(tuple.get(ck))>0) {
                right = mid - 1;
            }
            else {
                left = mid + 1;
            }
        }
        page.add(left,tuple);

    }
    public static boolean sameType(Object data, String dataType) throws ClassNotFoundException{
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
        PrintWriter pw = new PrintWriter(new FileOutputStream("metadata.csv", true));
        String row = "";
        String isClusteringKey;
        for(String key: htblColNameType.keySet()){
            row = "\r\n";
            pw.append(row);
            if(key.equals(strClusteringKeyColumn))
                isClusteringKey = "True";
            else
                isClusteringKey = "False";
            row = strTableName + "," + key + "," + htblColNameType.get(key) + "," + isClusteringKey + ","
                    + "null," + "null," + htblColNameMin.get(key) + "," + htblColNameMax.get(key);
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
        DBApp dbApp = new DBApp();
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
