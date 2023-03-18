import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public void init() throws IOException {

//        for(String filepath : tableFilepaths){
//
//        }

        tables = new Vector<Table>();
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

        Table table = new Table(strTableName,strClusteringKeyColumn);

        table.serialize(d_file_path + strTableName);
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

        Hashtable<String, Integer> myHashtable = new Hashtable<>();
        myHashtable.put("Alice Wonderland", 25);
        myHashtable.put("Bob Maximos", 30);
        myHashtable.put("Charlie Factory", 35);

        Vector<Hashtable<String, Integer>> vector = new Vector<>();
        vector.add(myHashtable);
        vector.add(myHashtable);
        vector.add(myHashtable);
        // print the hashtable in a tabular format with a border

        for(Hashtable<String, Integer> hashtable : vector){
            printHashtableWithBorder(hashtable);
            System.out.println();
        }


        ArrayList<Integer> list = new ArrayList<>();
        Collections.addAll(list,1,3,5,7,9,11,12);
//
        list.remove(2);
//        int left = 0;
//        int right = list.size() - 1;
//
//        int x = 13;
//
//        //1 3 5 9 11 12
//        while(left<=right){
//            int mid = (left + right) / 2;
////            System.out.println("left = " + left);
////            System.out.println("right = " + right);
//
//            if(x < list.get(mid)) {
//                right = mid - 1;
//            }
//            else {
//                left = mid + 1;
//            }
//
//        }
//
//        list.add(left, x);
//        System.out.println(list);

    }
    public static <K, V> void printHashtableWithBorder(Hashtable<K, V> hashtable) {
        // find the maximum length of the keys and values in the hashtable
        int maxKeyLength = 0;
        int maxValueLength = 0;
        for (K key : hashtable.keySet()) {
            int keyLength = key.toString().length();
            if (keyLength > maxKeyLength) {
                maxKeyLength = keyLength;
            }

            int valueLength = hashtable.get(key).toString().length();
            if (valueLength > maxValueLength) {
                maxValueLength = valueLength;
            }
        }

        // build the output string
        StringBuilder sb = new StringBuilder();
        String borderHorizontal = "+" + "-".repeat(maxKeyLength + 2) + "+" + "-".repeat(maxValueLength + 2) + "+\n";
        sb.append(borderHorizontal);
        sb.append(String.format("| %-" + maxKeyLength + "s | %-" + maxValueLength + "s |\n", "Col", "Val"));
        sb.append(borderHorizontal);
        for (K key : hashtable.keySet()) {
            V value = hashtable.get(key);
            sb.append(String.format("| %-" + maxKeyLength + "s | %-" + maxValueLength + "s |\n", key.toString(), value.toString()));
        }
        sb.append(borderHorizontal);

        // print the output string
        System.out.print(sb.toString());
    }





}
