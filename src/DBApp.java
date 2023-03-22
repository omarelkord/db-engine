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
        tableNames = getTableNames();
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
        table.setCkType(htblColNameType.get(strClusteringKeyColumn));
        table.setNumOfCols(htblColNameType.size());

        tableNames.add(strTableName);
        table.serialize();

        writeInCSV(strTableName, strClusteringKeyColumn, htblColNameType, htblColNameMin, htblColNameMax); //a method that will write in the csv
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

    public void insertIntoTable(String strTableName,
                                Hashtable<String, Object> htblColNameValue) throws Exception {

        verifyInsert(strTableName, htblColNameValue);
        Table table = Table.deserialize(strTableName);
        Comparable ckValue = (Comparable) htblColNameValue.get(table.getClusteringKey());

        Page locatedPage = table.getLocatedPage(ckValue, true);

        if(locatedPage!=null)
            System.out.println("NOT NULL ID = " + locatedPage.getId());

        //HANDLES BOTH CASES: A) THERE ARE ZERO PAGES   B) THERE IS NO VIABLE PAGE TO INSERT IN
        if (locatedPage == null)
            newPageInit(htblColNameValue, table);
        else
            insertAndShift(htblColNameValue, locatedPage.getId(), table);

        table.serialize();
    }

    public void verifyInsert(String strTableName, Hashtable<String, Object> htblColNameValue) throws Exception {

        if (!tableNames.contains(strTableName))
            throw new DBAppException("Table not found");     //OR CATCH FILENOTFOUNDEXC THEN THROW DBAPPEXC

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

            content = line.split(",");
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
                if (!sameType(value, colType)) {
                    System.out.println(colName);
                    throw new DBAppException("Incompatible data types");
                }
                if (compare(value, max) > 0)
                    throw new DBAppException( value + " is greater than the allowed maximum value");
                if (compare(value, min) < 0){

                    System.out.println(min+" "+value +" "+ compare(value,min));
                    throw new DBAppException( value+ " is less than the allowed minimum value");}
            }

            line = br.readLine();
        }

        br.close();
        table.serialize();
    }


    public void insertAndShift(Hashtable<String, Object> tuple, int id, Table table) throws IOException, ClassNotFoundException, DBAppException {
        System.out.println("Searching for id  = " + id);

        if (!table.hasPage(id)) {
            System.out.println(table.getHtblPageIdPagesPaths());
            newPageInit(tuple, table);
            return;
        }

        Page page = Page.deserialize(id);
        String ckName = table.getClusteringKey();
        Object ckValue = tuple.get(ckName);

        binaryInsert(tuple, page, ckName);
        System.out.println("PAGE SIZE AFTER INSERTION = " + page.getId());


        if (page.isOverFlow()) {
            System.out.println("ENTERED OVERFLOW FOR ID = " + page.getId());
            Hashtable<String, Object> lastTuple = page.getTuples().remove(page.getTuples().size() - 1);
            insertAndShift(lastTuple, table.getNextID(page), table);
        }

        //hashtable updates
        table.getHtblKeyPageId().put(ckValue, id);
        setMinMax(table, page);
        page.serialize();
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

    public void deleteFromTable(String strTableName,
                                Hashtable<String, Object> htblColNameValue)
            throws Exception {

        verifyDelete(strTableName,htblColNameValue);

        Table table = Table.deserialize(strTableName);

        boolean hasCK = htblColNameValue.get(table.getClusteringKey()) != null;
        if(hasCK){
            //binary search to locate the page
           Object ckValue = htblColNameValue.get(table.getClusteringKey()) ;
           int locatedPageindex =  table.binarySearchInTable(((Comparable) ckValue));
           if(locatedPageindex == -1)
               return;
           Page locatedPage = Page.deserialize(locatedPageindex);

           int tupleIndex = binarySearchInPage(locatedPage,table.getClusteringKey(), ((Comparable) ckValue));
           if(tupleIndex == -1)
               return;
           Hashtable<String, Object> tuple = locatedPage.getTuples().get(tupleIndex);

           for(String colName : htblColNameValue.keySet()){
               if(!htblColNameValue.get(colName).equals(tuple.get(colName))) // not all conditions satisfied
                   return;
           }

           locatedPage.getTuples().remove(tuple);
           locatedPage.serialize();
        }
        else{
            //linear
        }
        // 1) LOCATE PAGE (WITHIN EXPLICIT RANGE : min <= ckValue <= max)
        // 2) BINARY SEARCH FOR ELEMENT
        // 3) REMOVE ELEMENT
        // 4) SHIFT UP ALL PAGES STARTING FROM LAST TILL BASE CASE
        // 5) CORNER CASE: IF PAGE BECOMES EMPTY => DELETE PAGE => DELETE IN HTBLS
        // 6) UPDATE ALL PG HTBLS

        table.serialize();

    }

    public void verifyDelete(String strTableName, Hashtable<String, Object> htblColNameValue) throws Exception {

        if (!tableNames.contains(strTableName))
            throw new DBAppException("Table not found");     //OR CATCH FILENOTFOUNDEXC THEN THROW DBAPPEXC

        Table table = Table.deserialize(strTableName);

        BufferedReader br = new BufferedReader(new FileReader(METADATA_PATH));

        String line = br.readLine();
        String[] content = line.split(",");
        Vector<String> colNames = new Vector<>();

        while (line != null) {
            content = line.split(",");
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

            colNames.add(colName);

            if (value != null) {
                if (!sameType(value, colType)) {
                    System.out.println(colName);
                    throw new DBAppException("Incompatible data types");
                }

            }

            line = br.readLine();
        }

        br.close();

        System.out.println(colNames);

        for(String columns : htblColNameValue.keySet())
            if (!colNames.contains(columns))
                throw new DBAppException(columns + " field does not exist in the table");

        table.serialize();
    }

    public void updateTable(String strTableName,
                            String strClusteringKeyValue,
                            Hashtable<String, Object> htblColNameValue) throws Exception {

        verifyUpdate(strTableName, strClusteringKeyValue, htblColNameValue);
        Table table = Table.deserialize(strTableName);
        Comparable ckValue = (Comparable) parse(strClusteringKeyValue, table.getCkType());

        Page locatedPage = table.getLocatedPage(ckValue, false);

        if (locatedPage == null)
            throw new DBAppException("This tuple does not exist");

        int tupleIndex = binarySearchInPage(locatedPage, table.getClusteringKey(), ckValue);
        Hashtable<String, Object> tupleToUpdate = locatedPage.getTuples().get(tupleIndex);

        tupleToUpdate.putAll(htblColNameValue);
        setMinMax(table, locatedPage);
        locatedPage.serialize();
        table.serialize();
    }



    public void verifyUpdate(String strTableName,
                             String strClusteringKeyValue,
                             Hashtable<String, Object> htblColNameValue) throws Exception {

        if (!tableNames.contains(strTableName))
            throw new DBAppException("Table not found");

        Table table = Table.deserialize(strTableName);

        if (htblColNameValue.get(table.getClusteringKey()) != null)
            throw new DBAppException("Cannot update the clustering key");
        if(table.getNumOfCols() - 1 < htblColNameValue.size())
            throw new DBAppException("Number of inserted columns to update is incorrect");

        BufferedReader br = new BufferedReader(new FileReader(METADATA_PATH));

        String line = br.readLine();
        String[] content = line.split(",");
        Vector<String> colNames = new Vector<>();

        while (line != null) {
            content = line.split(",");
            String tableName = content[0];
            String colName = content[1];
            String colType = content[2];
            boolean isClusteringKey = Boolean.parseBoolean(content[3]);
            String min = content[6];
            String max = content[7];
            Object value = htblColNameValue.get(colName);

            if (!tableName.equals(table.getName())) {
                line = br.readLine();
                continue;
            }

            colNames.add(colName);

            if (value != null) {
                if (!sameType(value, colType))
                    throw new DBAppException("Incompatible data types");
                if (compare(value, max) > 0)
                    throw new DBAppException("Value is greater than the allowed maximum value");
                if (compare(value, min) < 0)
                    throw new DBAppException("Value is less than the allowed minimum value");
            }

            if (isClusteringKey) {
                try{
                    parse(strClusteringKeyValue, colType);
                }catch (Exception e){
                    throw new DBAppException("Invalid data type for clustering key");
                }
            }

            line = br.readLine();
        }
        table.serialize();
        br.close();

        for(String columns : htblColNameValue.keySet())
            if (!colNames.contains(columns))
                throw new DBAppException(columns + " does not exist in the table");
    }

    public int binarySearchInPage(Page page, String ckName, Comparable ckValue) {
        Vector<Hashtable<String, Object>> tuples = page.getTuples();

        int left = 0;
        int right = tuples.size() - 1;

        while (left <= right) {
            int mid = (left + right) / 2;

            if (((Comparable) tuples.get(mid).get(ckName)).compareTo(ckValue) == 0)
                return mid;

            if (((Comparable) tuples.get(mid).get(ckName)).compareTo(ckValue) > 0)
                right = mid - 1;
            else
                left = mid + 1;
        }

        return -1;
    }

    public Object parse(String value, String type) throws ClassNotFoundException, ParseException {
        return switch (type) {
            case "java.lang.Integer" -> Integer.parseInt(value);
            case "java.lang.Double" -> Double.parseDouble(value);
            case "java.util.Date" -> new SimpleDateFormat("yyyy-MM-dd").parse(value);
            default -> value;
        };
    }


    public void setMinMax(Table table, Page page) {
        String ck = table.getClusteringKey();
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

        Object CKValue = tuple.get(table.getClusteringKey());
        table.getHtblKeyPageId().put(CKValue, id);

        if (page.isOverFlow()) {
            int lastIndex = page.getTuples().size() - 1;
            Hashtable<String, Object> newTuple = page.getTuples().remove(lastIndex);
            shift(newTuple, id + 1, table);
        }

        setMinMax(table, page);
        page.serialize();
    }


    public void newPageInit(Hashtable<String, Object> tuple, Table table) throws IOException {
        table.setMaxIDsoFar(table.getMaxIDsoFar()+1);
        Page newPage = new Page(table.getMaxIDsoFar());

        newPage.getTuples().add(tuple);
        int id = newPage.getId();

        Object ckValue = tuple.get(table.getClusteringKey());

        table.getHtblPageIdCurrPageSize().put(id, 1);
        table.getHtblPageIdMinMax().put(id, new Pair(ckValue, ckValue));
        table.getHtblPageIdPagesPaths().put(id, "page-" + id);
        table.getHtblKeyPageId().put(ckValue, id);

        newPage.serialize();
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
            //System.out.println(object+ " hey");
            parsed = value;
        }
        //System.out.println(object+" "+parsed+" ");
        return ((Comparable) object).compareTo(parsed);
    }

    public static Properties readConfig(String path) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(path);
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

    public Vector<String> getTableNames() throws IOException {

        Vector<String> tableNames = new Vector<>();

        String line = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(METADATA_PATH));
            line = br.readLine();
        } catch (Exception ignored) {
            return new Vector<>();
        }



        while (line != null) {
            String[] content = line.split(",");
            String tableName = content[0];
            String colName = content[1];
            String colType = content[2];
            String min = content[6];
            String max = content[7];

            if (!tableNames.contains(tableName))
                tableNames.add(tableName);

            line = br.readLine();
        }

        br.close();
        return tableNames;
    }

    public static void main(String[] args) throws Exception {

        // Hashtable<String,Object> tuple = new Hashtable<>();
        //tuple.put("age",0);
        //tuple.put("name", "Sara");
        Hashtable<String, Object> tuple1 = new Hashtable<>();
        tuple1.put("age", 1);
        tuple1.put("name", "Kord");
        tuple1.put("gpa", 1.6);

        Hashtable<String, Object> tuple2 = new Hashtable<>();
        tuple2.put("age", 2);
        tuple2.put("name", "Omar");
        tuple2.put("gpa", 4.0);

        Hashtable<String, Object> tuple3 = new Hashtable<>();
        tuple3.put("age", 3);
        tuple3.put("name", "Ahmed");
        tuple3.put("gpa", 0.9);

        Hashtable<String, Object> tuple4 = new Hashtable<>();
        tuple4.put("age", 4);
        tuple4.put("name", "Malak");
        tuple4.put("gpa", 2.3);

        Hashtable<String, Object> tuple5 = new Hashtable<>();
        tuple5.put("age", 5);
        tuple5.put("name", "Menna");
        tuple5.put("gpa", 0.8);

        Hashtable<String, Object> tuple6 = new Hashtable<>();
        tuple6.put("age", 6);
        tuple6.put("name", "Lobna");
        tuple6.put("gpa", 1.4);

        Hashtable<String, Object> tuple7 = new Hashtable<>();
        tuple7.put("age", 7);
        tuple7.put("name", "boni");
        tuple7.put("gpa", 3.2);

        Hashtable<String, Object> tuple8 = new Hashtable<>();
        tuple8.put("age", 8);
        tuple8.put("name", "nada");
        tuple8.put("gpa", 2.5);

        Hashtable<String, Object> tuple9 = new Hashtable<>();
        tuple9.put("age", 9);
        tuple9.put("name", "noura");
        tuple5.put("gpa", 3.4);

        Hashtable<String, Object> tuple10 = new Hashtable<>();
        tuple10.put("age", 10);
        tuple10.put("name", "ashry");
        tuple10.put("gpa", 0.9);

        Hashtable<String, Object> tuple11 = new Hashtable<>();
        tuple11.put("age", 11);
        tuple11.put("name", "sara");
        tuple11.put("gpa", 0.9);

        DBApp dbApp = new DBApp();
        dbApp.init();

        Hashtable<String, String> htblColNameType = new Hashtable<>();
        htblColNameType.put("age", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");

        Hashtable<String, String> htblColNameMin = new Hashtable<>();
        htblColNameMin.put("age", "1");
        htblColNameMin.put("name", "A");
        htblColNameMin.put("gpa", "0.7");

        Hashtable<String, String> htblColNameMax = new Hashtable<>();
        htblColNameMax.put("age", "40");
        htblColNameMax.put("name", "zzzzzzz");
        htblColNameMax.put("gpa", "4.0");


//       dbApp.createTable("Students", "age", htblColNameType, htblColNameMin, htblColNameMax);

//        dbApp.insertIntoTable("Students", tuple2);
        dbApp.insertIntoTable("Students", tuple6);
//        dbApp.insertIntoTable("Students", tuple7);
//        dbApp.insertIntoTable("Students", tuple8);
//        dbApp.insertIntoTable("Students", tuple1);
//        dbApp.insertIntoTable("Students", tuple3);
//        dbApp.insertIntoTable("Students", tuple5);
//        dbApp.insertIntoTable("Students", tuple4);
//        dbApp.insertIntoTable("Students", tuple9);
//        dbApp.insertIntoTable("Students", tuple10);

//        dbApp.insertIntoTable("Students", tuple11);


//        Hashtable<String, Object> updateHtbl = new Hashtable<>();
//        updateHtbl.put("gpa", 0.7);
//        updateHtbl.put("name", "Lolosh");
//       Hashtable<String,Object>  deletingCriteria = new Hashtable<>();
//        deletingCriteria.put("age",3);

//        //dbApp.updateTable("Students", "6", updateHtbl);
//       dbApp.deleteFromTable("Students",deletingCriteria);

        Table table = Table.deserialize("Students");

        for (int id : table.getHtblPageIdPagesPaths().keySet()) {
            Page p = Page.deserialize(id);
            System.out.println("PAGE " + id);
            System.out.println(p.getTuples());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            p.serialize();
        }
    }

    public static void main2(String[] args) throws IOException, DBAppException {
        DBApp dbApp = new DBApp();
        dbApp.init();

        Hashtable<String, String> htblColNameType = new Hashtable<>();
        htblColNameType.put("age", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("gpa", "java.lang.Double");

        Hashtable<String, String> htblColNameMin = new Hashtable<>();
        htblColNameMin.put("age", "1");
        htblColNameMin.put("name", "ZZZZZZZZZZ");
        htblColNameMin.put("gpa", "0.7");

        Hashtable<String, String> htblColNameMax = new Hashtable<>();
        htblColNameMax.put("age", "40");
        htblColNameMax.put("name", "ZZZZZZZZZ");
        htblColNameMax.put("gpa", "4.0");

        dbApp.createTable("Student", "age", htblColNameType, htblColNameMin, htblColNameMax);
        dbApp.createTable("Instructor", "age", htblColNameType, htblColNameMin, htblColNameMax);
        dbApp.createTable("Staff", "age", htblColNameType, htblColNameMin, htblColNameMax);

        dbApp.getTableNames();
    }

}
