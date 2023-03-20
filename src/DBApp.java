import java.io.*;
import java.sql.SQLOutput;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class DBApp {

    private Vector<String> tableNames;
    private Vector<String> dataTypes;
    private int maxNoRowsInPage;
    private int maxEntriesInNode;
    public static final String METADATA_PATH = "D:\\db-engine\\metadata.csv";

    public DBApp() {

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
                            Hashtable<String, String> htblColNameType,
                            Hashtable<String, String> htblColNameMin,
                            Hashtable<String, String> htblColNameMax) throws DBAppException, FileNotFoundException, IOException {

        if (tableNames.contains(strTableName))
            throw new DBAppException("Table already exists");

        for (Map.Entry<String, String> entry : htblColNameType.entrySet()) {
            String value = entry.getValue();

            if (!dataTypes.contains(value))
                throw new DBAppException("Invalid data type");
        }

        if (htblColNameType.get(strClusteringKeyColumn) == null)
            throw new DBAppException("Invalid Primary Key");


        //post-verification

        Table table = new Table(strTableName, strClusteringKeyColumn);
        tableNames.add(strTableName);
        table.serialize();

        writeInCSV(strTableName, strClusteringKeyColumn, htblColNameType, htblColNameMin, htblColNameMax); //a method that will write in the csv
    }

    public void verify(String strTableName, Hashtable<String, Object> htblColNameValue) throws Exception {

        if (!tableNames.contains(strTableName))
            throw new DBAppException("Table not found");        //OR CATCH FILENOTFOUNDEXC THEN THROW DBAPPEXC

        Table table = Table.deserialize(strTableName);

        String ckName = table.getClusteringKey();
        Object ckValue = htblColNameValue.get(ckName);

        //INTEGRITY CONSTRAINTS
        if (ckValue == null)
            throw new DBAppException("Cannot allow null values for Clustering Key");
        if (table.getHtblKeyPageId().get(ckValue) != null)
            throw new DBAppException("Cannot allow duplicate values for Clustering Key");


        BufferedReader br = new BufferedReader(new FileReader(METADATA_PATH));

        String line = br.readLine();
        String[] content = line.split(",");

        while (line != null) {

            String tableName = content[0];
            String colName = content[1];
            String colType = content[2];
            String min = content[6];
            String max = content[7];
            Object value = htblColNameValue.get(colName);

            if (!tableName.equals(table.getName())) {
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

            line = br.readLine();
        }

        br.close();
    }

    //UPDATE MIN & MAX VALUES AFTER INSERTION
    //CHECKED SO FAR:
    //              1) INSERTING FROM SCRATCH
    //              2) INSERTING IN PAGE WITH ROOM (NO-SHIFT)

    //NOT CHECKED:
    //              1)

    public void insertIntoTable(String strTableName,
                                Hashtable<String, Object> htblColNameValue) throws Exception {

        verify(strTableName, htblColNameValue);
        Table table = Table.deserialize(strTableName);
//        verify(table, htblColNameValue);

        Comparable ckValue = (Comparable) htblColNameValue.get(table.getClusteringKey());
        Page locatedPage = table.getLocatedPage(ckValue);

        //HANDLES BOTH CASES: A) THERE ARE ZERO PAGES   B) THERE IS NO VIABLE PAGE TO INSERT IN
        if (locatedPage == null)
            newPageInit(htblColNameValue, table);
        else
            insertAndShift(htblColNameValue, locatedPage.getId(), table);

        table.serialize();
    }

    public void deleteFromTable(String strTableName,
                                Hashtable<String, Object> htblColNameValue)
            throws DBAppException {

    }

    public void setMinMax(Table table, Page page) {
        String ck = table.getCKName();
        Pair newPair = new Pair(page.getTuples().get(0).get(ck), page.getTuples().get(page.getTuples().size() - 1).get(ck));
        table.getHtblPageIdMinMax().put(page.getId(), newPair);
    }


    public void shift(Hashtable<String, Object> tuple, Integer id, Table table) throws IOException, ClassNotFoundException {
        if (!table.hasPage(id)) {
            newPageInit(tuple, table);
            return;
        }

        Page page = Page.deserialize(id);
        page.getTuples().add(0, tuple);

        Object CKValue = tuple.get(table.getCKName());
        table.getHtblKeyPageId().put(CKValue, id);

        if (page.isOverFlow()) {
            int lastIndex = page.getTuples().size() - 1;
            Hashtable<String, Object> newTuple = page.getTuples().remove(lastIndex);
            shift(newTuple, id + 1, table);
        }

        setMinMax(table, page);
        page.serialize();
    }

    public void insertAndShift(Hashtable<String, Object> tuple, Integer id, Table table) throws IOException, ClassNotFoundException, DBAppException {

        if (!table.hasPage(id)) {
            newPageInit(tuple, table);
            return;
        }

        Page page = Page.deserialize(id);
        String ckName = table.getClusteringKey();
        Object ckValue = tuple.get(ckName);

        binaryInsert(tuple, page, ckName);

        if (page.isOverFlow()) {
            Hashtable<String, Object> lastTuple = page.getTuples().remove(page.getTuples().size() - 1);
            insertAndShift(lastTuple, id + 1, table);
        }

        //hashtable updates
        table.getHtblKeyPageId().put(ckValue, id);
        setMinMax(table, page);
        page.serialize();
    }


    public void initPageHtbls(Page newPage, Table table) {

        Object CKValue = newPage.getTuples().get(0).get(table.getCKName());

        int id = newPage.getId();

        table.getHtblPageIdCurrPageSize().put(id, 1);
        table.getHtblPageIdMinMax().put(id, new Pair(CKValue, CKValue));
        table.getHtblPageIdPagesPaths().put(id, "page-" + id);
        table.getHtblKeyPageId().put(CKValue, id);
    }

    public void newPageInit(Hashtable<String, Object> tuple, Table table) throws IOException {
        Page newPage = new Page();
        newPage.getTuples().add(tuple);
        initPageHtbls(newPage, table);

        newPage.serialize();
    }


    public static void binaryInsert(Hashtable<String, Object> tuple, Page page, String ck) {
        Vector<Hashtable<String, Object>> tuples = page.getTuples();

        int left = 0;
        int right = tuples.size() - 1;

        while (left <= right) {
            int mid = (left + right) / 2;

            if (((Comparable) tuples.get(mid).get(ck)).compareTo(tuple.get(ck)) > 0)
                right = mid - 1;
            else
                left = mid + 1;
        }

        tuples.add(left, tuple);
    }

    public static boolean sameType(Object data, String dataType) throws ClassNotFoundException {
        return data.getClass().equals(Class.forName(dataType));
    }

    public static int compare(Object object, String value) throws ParseException {

        Comparable parsed;

        if (object instanceof Integer) {
            parsed = Integer.parseInt(value);
        } else if (object instanceof Double) {
            parsed = Double.parseDouble(value);
        } else if (object instanceof Date) {
            parsed = new SimpleDateFormat("yyyy-MM-dd").parse(value);
        } else {
            parsed = value;
        }

        return ((Comparable) object).compareTo(parsed);
    }


    public void writeInCSV(String strTableName,
                           String strClusteringKeyColumn,
                           Hashtable<String, String> htblColNameType,
                           Hashtable<String, String> htblColNameMin,
                           Hashtable<String, String> htblColNameMax) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new FileOutputStream("metadata.csv", true));
        String row = "";
        String isClusteringKey;
        for (String key : htblColNameType.keySet()) {
            if (key.equals(strClusteringKeyColumn))
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

    public static void main(String[] args) throws Exception {

        // Hashtable<String,Object> tuple = new Hashtable<>();
        //tuple.put("age",0);
        //tuple.put("name", "Sara");
        Hashtable<String, Object> tuple1 = new Hashtable<>();
        tuple1.put("age", 1);
        tuple1.put("name", "Kord");
        Hashtable<String, Object> tuple2 = new Hashtable<>();
        tuple2.put("age", 2);
        tuple2.put("name", "Omar");
        Hashtable<String, Object> tuple3 = new Hashtable<>();
        tuple3.put("age", 3);
        tuple3.put("name", "Ahmed");
        Hashtable<String, Object> tuple4 = new Hashtable<>();
        tuple4.put("age", 4);
        tuple4.put("name", "Malak");

        Hashtable<String, Object> tuple5 = new Hashtable<>();
        tuple5.put("age", 5);
        tuple5.put("name", "Menna");
        Hashtable<String, Object> tuple6 = new Hashtable<>();
        tuple6.put("age", 6);
        tuple6.put("name", "Lobna");
        Hashtable<String, Object> tuple7 = new Hashtable<>();
        tuple7.put("age", 7);
        tuple7.put("name", "boni");
        Hashtable<String, Object> tuple8 = new Hashtable<>();
        tuple8.put("age", 8);
        tuple8.put("name", "nada");
        Hashtable<String, Object> tuple9 = new Hashtable<>();
        tuple9.put("age", 9);
        tuple9.put("name", "noura");
        Hashtable<String, Object> tuple10 = new Hashtable<>();
        tuple10.put("age", 10);
        tuple10.put("name", "ashry");


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

        dbApp.createTable("Students", "age", htblColNameType, htblColNameMin, htblColNameMax);

        dbApp.insertIntoTable("Students", tuple2);
        dbApp.insertIntoTable("Students", tuple6);
        dbApp.insertIntoTable("Students", tuple5);
        dbApp.insertIntoTable("Students", tuple7);

//        dbApp.insertIntoTable("Students",tuple3);

        dbApp.insertIntoTable("Students", tuple4);
        dbApp.insertIntoTable("Students", tuple3);
        dbApp.insertIntoTable("Students", tuple10);
        dbApp.insertIntoTable("Students", tuple9);
        dbApp.insertIntoTable("Students", tuple8);
        dbApp.insertIntoTable("Students", tuple1);

//        dbApp.insertIntoTable("Students",tuple4);
//        dbApp.insertIntoTable("Students",tuple3);
//
//
//
//        dbApp.insertIntoTable("Students",tuple1);


        Table table = Table.deserialize("Students");

        for (int id : table.getHtblPageIdPagesPaths().keySet()) {
            Page p = Page.deserialize(id);
            System.out.println("PAGE " + id);
            System.out.println(p.getTuples());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            p.serialize();
        }
        //System.out.println(table.getHtblPageIdPagesPaths().size() );


        Table table1 = Table.deserialize("Professors");
        System.out.println(table1);
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

    public static void printVector(Vector<Hashtable<String, Object>> vector) {
        for (Hashtable<String, Object> h : vector)
            printHashtable(h);
        System.out.println();
    }


}
