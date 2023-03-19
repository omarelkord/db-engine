import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class DBApp {

    private Vector<String> tableNames;
    private Vector<String> dataTypes;
    private int maxNoRowsInPage;
    private int maxEntriesInNode;
    private static final String METADATA_PATH = "D:\\db-engine\\metadata.csv";

    public DBApp(){

    }


    public void init() throws IOException {

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

    public static String getClusteringKey(String strTableName, Hashtable<String, Object> htblColNameValue) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(METADATA_PATH));

        String line = br.readLine();
        String[] content = line.split(",");

        String clusteringKey = "";

        while (line != null) {

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

            if (Boolean.parseBoolean(isClusteringKey)) {
                clusteringKey = colName;
                break;
            }

            line = br.readLine();
        }

        br.close();
        return clusteringKey;
    }

    //UPDATE MIN & MAX VALUES AFTER INSERTION
    //CHECKED SO FAR:
    //              1) INSERTING FROM SCRATCH
    //              2) INSERTING IN PAGE WITH ROOM (NO-SHIFT)

    //NOT CHECKED:
    //              1)

    public void insertIntoTable(String strTableName,
                                Hashtable<String,Object> htblColNameValue) throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException, ParseException {
        Table table = Table.deserialize(strTableName);

        if(table == null)
            throw new DBAppException("Table not found");

        String clusteringKey = null;
        try {
            clusteringKey = getClusteringKey(strTableName, htblColNameValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Comparable clusteringKeyValue = (Comparable) htblColNameValue.get(clusteringKey);

        //INTEGRITY CONSTRAINTS
        if(clusteringKeyValue == null)
            throw new DBAppException("Cannot allow null values for Clustering Key");
        if(table.getHtblKeyPageId().get(clusteringKeyValue)!=null)
            throw new DBAppException("Cannot allow duplicate values for Clustering Key");

//        System.out.println(((String) htblColNameValue.get("name")) + table.getHtblPageIdPagesPaths().isEmpty());

        if(table.getHtblPageIdPagesPaths().isEmpty()){ //the first case is inserting in an empty table
            newPageInit(htblColNameValue, table);
            table.serialize(strTableName);
            return;
        }

        Page locatedPage = null;

        for(Integer id : table.getHtblPageIdMinMax().keySet()){
            Page currPage = Page.deserialize(table.getPagePath(id));
            Pair pair = table.getHtblPageIdMinMax().get(id);
            Object min = pair.getMin();
            Object max = pair.getMax();


            //NOT FULL:
            // ) less than min => insert
            // ) else => less than max (range) => insert same page
            // ) else if greater than max
            // )    if there's room ==> insert and update max
            // )    if full ==> next iteration (if not last iteration)


            if(clusteringKeyValue.compareTo(min) < 0
                || (clusteringKeyValue.compareTo(min) > 0 && clusteringKeyValue.compareTo(max) < 0)
                || (clusteringKeyValue.compareTo(max) > 0 && !currPage.isFull())) {

                locatedPage = currPage;
                break;
            }
        }

        if(locatedPage == null){
            newPageInit(htblColNameValue, table);
            table.serialize(strTableName);
            return;
        }

        Vector<Hashtable<String, Object>> tuples = new Vector<>();
        binaryInsert(htblColNameValue,locatedPage.getTuples(),clusteringKey);

        if(locatedPage.isFull()){
            int lastIndex = locatedPage.getTuples().size() - 1;
            Hashtable<String, Object> newTuple = locatedPage.getTuples().remove(lastIndex);

            shift(newTuple, locatedPage.getId() + 1, table);
//            locatedPage.serialize("path-"+locatedPage.getId());
        }

        locatedPage.serialize("page-"+locatedPage.getId());
        table.serialize(strTableName);
    }


    public void shift(Hashtable<String,Object> tuple, Integer id, Table table) throws IOException, ClassNotFoundException {
        if(!table.hasPage(id)) {
            newPageInit(tuple, table);
            return;
        }

        Page page = Page.deserialize(table.getPagePath(id));
        page.getTuples().add(0, tuple);
        page.serialize(table.getPagePath(id));

        Object CKValue = tuple.get(table.getCKName());
        table.getHtblKeyPageId().put(CKValue, id);

        if(page.isFull()) {
            int lastIndex = page.getTuples().size() - 1;
            Hashtable<String, Object> newTuple = page.getTuples().remove(lastIndex);

            shift(newTuple, id + 1, table);
        }
    }

    public void updateHtbls(Page newPage, Table table){

        Object CKValue = newPage.getTuples().get(0).get(table.getCKName());

        int id = newPage.getId();

        table.getHtblPageIdCurrPageSize().put(id, 1);
        table.getHtblPageIdMinMax().put(id, new Pair(CKValue, CKValue));
        table.getHtblPageIdPagesPaths().put(id, "page-" + id);


    }

    public void newPageInit(Hashtable<String,Object> tuple , Table table) throws IOException{
        Page newPage = new Page();
        newPage.getTuples().add(tuple);
        updateHtbls(newPage, table);

        newPage.serialize("page-" + newPage.getId());
    }

    public static void binaryInsert(Hashtable<String,Object> tuple, Vector<Hashtable<String, Object>> page, String ck){
        int left = 0;
        int right = page.size() - 1;

        while(left<=right){
            int mid = (left + right) / 2;

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
            if(key.equals(strClusteringKeyColumn))
                isClusteringKey = "True";
            else
                isClusteringKey = "False";
            row = strTableName + "," + key + "," + htblColNameType.get(key) + "," + isClusteringKey + ","
                    + "null," + "null," + htblColNameMin.get(key) + "," + htblColNameMax.get(key);
            pw.append(row);

            row = "\r\n";
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

    public static void main(String[] args) throws DBAppException, IOException, ParseException, ClassNotFoundException {

        Hashtable<String,Object> tuple = new Hashtable<>();
        tuple.put("age",1);
        tuple.put("name", "Sara");
        Hashtable<String,Object> tuple2 = new Hashtable<>();
        tuple2.put("age",2);
        tuple2.put("name", "Omar");
        Hashtable<String,Object> tuple3 = new Hashtable<>();
        tuple3.put("age",4);
        Hashtable<String,Object> tuple4 = new Hashtable<>();
        tuple4.put("age",6);
        Hashtable<String,Object> tuple5 = new Hashtable<>();
        tuple5.put("age",9);
        Hashtable<String,Object> tuple6 = new Hashtable<>();
        tuple6.put("age",5);

        DBApp dbApp = new DBApp();
        dbApp.init();

        Hashtable<String, String> htblColNameType = new Hashtable<>();
        htblColNameType.put("age", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");

        Hashtable<String, String> htblColNameMin = new Hashtable<>();
        htblColNameMin.put("age", "1");
        htblColNameMin.put("name", "ZZZZZZZZZZ");

        Hashtable<String, String> htblColNameMax = new Hashtable<>();
        htblColNameMax.put("age", "40");
        htblColNameMax.put("name", "ZZZZZZZZZ");

        dbApp.createTable("Students", "age",htblColNameType,htblColNameMin,htblColNameMax);

        dbApp.insertIntoTable("Students",tuple);
        dbApp.insertIntoTable("Students",tuple2);

        Table table = Table.deserialize("Students");

        for(int id : table.getHtblPageIdPagesPaths().keySet()){
            Page p = Page.deserialize(table.getPagePath(id));
            printVector(p.getTuples());
            p.serialize(table.getPagePath(id));
        }


//        System.out.println(page);


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

    private static void printBorder(int maxKeyLength, int maxValueLength) {
        int totalLength = maxKeyLength + maxValueLength + 3;
        System.out.print("+");
        for (int i = 0; i < totalLength; i++) {
            System.out.print("-");
        }
        System.out.println("+");
    }
    public static void printHashtable(Hashtable<?, ?> hashtable) {
        // Find the length of the longest key and value in the hashtable
        int maxKeyLength = 0;
        int maxValueLength = 0;
        for (Map.Entry<?, ?> entry : hashtable.entrySet()) {
            maxKeyLength = Math.max(maxKeyLength, entry.getKey().toString().length());
            maxValueLength = Math.max(maxValueLength, entry.getValue().toString().length());
        }

        // Print the top border
        printBorder(maxKeyLength, maxValueLength);

        // Print the keys in the top row
        System.out.print("|");
        for (Map.Entry<?, ?> entry : hashtable.entrySet()) {
            String key = entry.getKey().toString();
            System.out.printf(" %-" + maxKeyLength + "s |", key);
        }
        System.out.println();

        // Print the middle border
        printBorder(maxKeyLength, maxValueLength);

        // Print the values in the bottom row
        System.out.print("|");
        for (Map.Entry<?, ?> entry : hashtable.entrySet()) {
            String value = entry.getValue().toString();
            System.out.printf(" %-" + maxValueLength + "s |", value);
        }
        System.out.println();

        // Print the bottom border
        printBorder(maxKeyLength, maxValueLength);
    }

    public static void printVector(Vector<Hashtable<String, Object>> vector){
        for(Hashtable<String, Object> h : vector)
            printHashtable(h);
        System.out.println();
    }




}
