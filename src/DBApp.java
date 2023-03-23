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
    public static final String METADATA_PATH = "D:\\db-engine\\src\\main\\resources\\metadata.csv";
    public static final String CONFIG_PATH = "D:\\db-engine\\src\\main\\resources\\DBApp.config";

    public DBApp() {

    }

    public void init() {
        dataTypes = new Vector<>();
        Collections.addAll(dataTypes, "java.lang.Integer", "java.lang.Double", "java.lang.String", "java.util.Date");

        try {
            tableNames = getTableNames();
        }catch (Exception e){

        }

        try {
            Properties properties = readConfig(CONFIG_PATH);
            maxNoRowsInPage = Integer.parseInt(properties.getProperty("MaximumRowsCountinTablePage"));
            maxEntriesInNode = Integer.parseInt(properties.getProperty("MaximumEntriesinOctreeNode"));
        }catch (Exception e){

        }
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
        table.setColumnNames(new Vector<>(htblColNameType.keySet()));

        tableNames.add(strTableName);
        table.serialize();

        writeInCSV(strTableName, strClusteringKeyColumn, htblColNameType, htblColNameMin, htblColNameMax); //a method that will write in the csv
    }

    public void writeInCSV(String strTableName,
                           String strClusteringKeyColumn,
                           Hashtable<String, String> htblColNameType,
                           Hashtable<String, String> htblColNameMin,
                           Hashtable<String, String> htblColNameMax) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(new FileOutputStream(METADATA_PATH, true));
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

        Page locatedPage = table.getPageToInsert(ckValue);

        //HANDLES BOTH CASES: A) THERE ARE ZERO PAGES   B) THERE IS NO VIABLE PAGE TO INSERT IN
        if (locatedPage == null)
            newPageInit(htblColNameValue, table);
        else
            insertAndShift(htblColNameValue, locatedPage.getId(), table);

        table.serialize();
    }

    public void verifyInsert(String strTableName, Hashtable<String, Object> htblColNameValue) throws Exception {

        if (!tableNames.contains(strTableName))
            throw new DBAppException("Table not found");

        Table table = Table.deserialize(strTableName);

        if(htblColNameValue.size() != table.getColumnNames().size())
            throw new DBAppException("Invalid number of columns entered");

        for(String column : htblColNameValue.keySet())
            if (!table.getColumnNames().contains(column))
                throw new DBAppException(column + " field does not exist in the table");


        String ckName = table.getClusteringKey();
        Comparable ckValue = (Comparable) htblColNameValue.get(ckName);

        //INTEGRITY CONSTRAINTS
        if (ckValue == null) {
            throw new DBAppException("Cannot allow null values for Clustering Key");
        }


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
                    throw new DBAppException( value+ " is less than the allowed minimum value");}
            }

            line = br.readLine();
        }

        //VERIFY DUPE CK
        int pid = table.binarySearchInTable(ckValue);
        if(pid != -1){
            Page p = Page.deserialize(strTableName, pid);
            int tupleIdx = p.binarySearchInPage(table.getClusteringKey(), ckValue);

            if(tupleIdx != -1)
                throw new DBAppException("Cannot allow duplicate values for Clustering Key");
        }

        br.close();
        table.serialize();
    }


    public void insertAndShift(Hashtable<String, Object> tuple, int id, Table table) throws IOException, ClassNotFoundException, DBAppException {

        if (!table.hasPage(id)) {
            newPageInit(tuple, table);
            return;
        }

        Page page = Page.deserialize(table.getName(), id);
        String ckName = table.getClusteringKey();
        Object ckValue = tuple.get(ckName);

        binaryInsert(tuple, page, ckName);


        if (page.isOverFlow()) {
            Hashtable<String, Object> lastTuple = page.getTuples().remove(page.getTuples().size() - 1);
            insertAndShift(lastTuple, table.getNextID(page), table);
        }

        //hashtable updates
        table.setMinMax(page);
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

        verifyDelete(strTableName, htblColNameValue);
        Table table = Table.deserialize(strTableName);

        boolean hasCK = htblColNameValue.get(table.getClusteringKey()) != null;

        if (hasCK) {
            //binary search to locate the page
            Comparable ckValue = (Comparable) htblColNameValue.get(table.getClusteringKey());

            Page locatedPage = table.getPageToModify(ckValue);

            int tupleIndex = locatedPage.binarySearchInPage(table.getClusteringKey(), ((Comparable) ckValue));
            if (tupleIndex == -1)
                throw new DBAppException("Tuple does not exist");

            Hashtable<String, Object> tuple = locatedPage.getTuples().get(tupleIndex);

            if (!isMatch(htblColNameValue, tuple))
                return;
            locatedPage.getTuples().remove(tuple);
            table.updatePageDelete(locatedPage);

        } else {
            Vector<Integer> ids = new Vector<Integer>(table.getHtblPageIdMinMax().keySet());
            for (Integer id : ids) {
                Page currPage = Page.deserialize(table.getName(), id);
                Vector<Hashtable<String, Object>> tmp = new Vector<>();

                for (Hashtable<String, Object> tuple : currPage.getTuples())
                    if (isMatch(htblColNameValue, tuple))
                        tmp.add(tuple);


                currPage.getTuples().removeAll(tmp);
                table.updatePageDelete(currPage);
            }
        }

        table.serialize();
    }

    public boolean isMatch(Hashtable<String, Object> htblColNameValue, Hashtable<String, Object> tuple){
        for(String colName : htblColNameValue.keySet()){
            if(!htblColNameValue.get(colName).equals(tuple.get(colName))) // not all conditions satisfied
                return false;
        }
        return true;
    }

    public void verifyDelete(String strTableName, Hashtable<String, Object> htblColNameValue) throws Exception {

        if (!tableNames.contains(strTableName))
            throw new DBAppException("Table not found");     //OR CATCH FILENOTFOUNDEXC THEN THROW DBAPPEXC

        Table table = Table.deserialize(strTableName);

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

            }

            line = br.readLine();
        }

        br.close();

        for(String column : htblColNameValue.keySet())
            if (!table.getColumnNames().contains(column))
                throw new DBAppException(column + " field does not exist in the table");

        table.serialize();
    }

    //test later **
    public void updateTable(String strTableName,
                            String strClusteringKeyValue,
                            Hashtable<String, Object> htblColNameValue) throws Exception {

        verifyUpdate(strTableName, strClusteringKeyValue, htblColNameValue);
        Table table = Table.deserialize(strTableName);
        Comparable ckValue = (Comparable) parse(strClusteringKeyValue, table.getCkType());

        Page locatedPage = table.getPageToModify(ckValue);

        int tupleIndex = locatedPage.binarySearchInPage(table.getClusteringKey(), ((Comparable) ckValue));

        if(tupleIndex == -1)
            throw new DBAppException("Tuple does not exist");

        Hashtable<String, Object> tupleToUpdate = locatedPage.getTuples().get(tupleIndex);
        tupleToUpdate.putAll(htblColNameValue);

        table.setMinMax(locatedPage);
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

        for(String column : htblColNameValue.keySet())
            if (!table.getColumnNames().contains(column))
                throw new DBAppException(column + " field does not exist in the table");

        BufferedReader br = new BufferedReader(new FileReader(METADATA_PATH));

        String line = br.readLine();
        String[] content = line.split(",");

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
    }

    public Object parse(String value, String type) throws ClassNotFoundException, ParseException {
        return switch (type) {
            case "java.lang.Integer" -> Integer.parseInt(value);
            case "java.lang.Double" -> Double.parseDouble(value);
            case "java.util.Date" -> new SimpleDateFormat("yyyy-MM-dd").parse(value);
            default -> value;
        };
    }

    public void shift(Hashtable<String, Object> tuple, Integer id, Table table) throws IOException, ClassNotFoundException {
        if (!table.hasPage(id)) {
            newPageInit(tuple, table);
            return;
        }

        Page page = Page.deserialize(table.getName(), id);
        page.getTuples().add(0, tuple);

        Object CKValue = tuple.get(table.getClusteringKey());

        if (page.isOverFlow()) {
            int lastIndex = page.getTuples().size() - 1;
            Hashtable<String, Object> newTuple = page.getTuples().remove(lastIndex);
            shift(newTuple, id + 1, table);
        }

        table.setMinMax(page);
        page.serialize();
    }

    public void newPageInit(Hashtable<String, Object> tuple, Table table) throws IOException {
        table.setMaxIDsoFar(table.getMaxIDsoFar()+1);
        Page newPage = new Page(table.getName(), table.getMaxIDsoFar());

        newPage.getTuples().add(tuple);
        int id = newPage.getId();

        Object ckValue = tuple.get(table.getClusteringKey());

        table.getHtblPageIdMinMax().put(id, new Pair(ckValue, ckValue));

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
            System.out.println("can't find metadata");
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

    public static void main2(String[] args) throws Exception {

        Hashtable<String, Object> tuple0 = new Hashtable<>();
        tuple0.put("age", 0);
        tuple0.put("name", "Soubra");
        tuple0.put("gpa", 1.6);

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
        tuple9.put("gpa", 3.4);

        Hashtable<String, Object> tuple10 = new Hashtable<>();
        tuple10.put("age", 10);
        tuple10.put("name", "ashry");
        tuple10.put("gpa", 0.9);

        Hashtable<String, Object> tuple11 = new Hashtable<>();
        tuple11.put("age", 11);
        tuple11.put("name", "sara");
        tuple11.put("gpa", 0.9);


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
        DBApp dbApp = new DBApp();
        dbApp.init();

//         dbApp.createTable("Students", "age", htblColNameType, htblColNameMin, htblColNameMax);
//             dbApp.insertIntoTable("Students", tuple0);
             dbApp.insertIntoTable("Students", tuple2);
//        dbApp.insertIntoTable("Students", tuple6);
//        dbApp.insertIntoTable("Students", tuple7);
//        dbApp.insertIntoTable("Students", tuple8);
//        dbApp.insertIntoTable("Students", tuple1);
//        dbApp.insertIntoTable("Students", tuple3);
  //      dbApp.insertIntoTable("Students", tuple5);
//        dbApp.insertIntoTable("Students", tuple4);
//        dbApp.insertIntoTable("Students", tuple9);
//         dbApp.insertIntoTable("Students", tuple10);

//        dbApp.insertIntoTable("Students", tuple11);


//        Hashtable<String, Object> updateHtbl = new Hashtable<>();
//        updateHtbl.put("gpa", 3.0);
//        updateHtbl.put("name", "bonii");
//       dbApp.updateTable("Students", "7", updateHtbl);

         Hashtable<String,Object> deletingCriteria0 = new Hashtable<>();
         Hashtable<String,Object> deletingCriteria1 = new Hashtable<>();
         Hashtable<String,Object> deletingCriteria2 = new Hashtable<>();
         deletingCriteria0.put( "age", 2);
//         deletingCriteria1.put("gpa", 2.3);
//         deletingCriteria2.put( "name", "nada");
//       deletingCriteria.put("name","Lobna");

//        dbApp.deleteFromTable("Students", deletingCriteria0);
//        dbApp.deleteFromTable("Students", deletingCriteria1);
//        dbApp.deleteFromTable("Students", deletingCriteria2);

        Table table = Table.deserialize("Students");

        for (int id : table.getHtblPageIdMinMax().keySet()) {
            Page p = Page.deserialize(table.getName(), id);
            System.out.println("PAGE " + id);
            System.out.println(p.getTuples());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            p.serialize();
        }
    }


    //testing
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DBApp dbApp = new DBApp();
        dbApp.init();

        Table table = Table.deserialize("pcs");

        for (int id : table.getHtblPageIdMinMax().keySet()) {
            Page p = Page.deserialize(table.getName(), id);
            System.out.println("PAGE " + id);
            System.out.println(p.getTuples());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            p.serialize();
        }
    }

}
