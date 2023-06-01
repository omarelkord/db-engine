package engine;

import antlr.*;
import gen.gLexer;
import gen.gParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

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

    //    public static final String METADATA_PATH = "D:\\db-engine\\src\\main\\resources\\metadata.csv";
    public static final String METADATA_PATH = "./src/main/resources/metadata.csv";
    public static final String TEMP_PATH = "./src/main/resources/temp.csv";
    //    public static final String CONFIG_PATH = "D:\\db-engine\\src\\main\\resources\\engine.DBApp.config";
    public static final String CONFIG_PATH = "./src/main/resources/DBApp.config";

    public DBApp() {

    }

    public void init() {
        dataTypes = new Vector<>();
        Collections.addAll(dataTypes, "java.lang.Integer", "java.lang.Double", "java.lang.String", "java.util.Date");

        try {
            tableNames = getTableNames();
        } catch (Exception e) {

        }

        try {
            Properties properties = readConfig(CONFIG_PATH);
            maxNoRowsInPage = Integer.parseInt(properties.getProperty("MaximumRowsCountinTablePage"));
            maxEntriesInNode = Integer.parseInt(properties.getProperty("MaximumEntriesinOctreeNode"));
        } catch (Exception e) {

        }
    }


    public void createTable(String strTableName,
                            String strClusteringKeyColumn,
                            Hashtable<String, String> htblColNameType,
                            Hashtable<String, String> htblColNameMin,
                            Hashtable<String, String> htblColNameMax) throws DBAppException {

        if (tableNames.contains(strTableName))
            throw new DBAppException("Table already exists");

        if (htblColNameType.size() != htblColNameMin.size() || htblColNameType.size() != htblColNameMax.size())
            throw new DBAppException("Should enter consistent columns' information");

        for (Map.Entry<String, String> entry : htblColNameType.entrySet()) {
            String value = entry.getValue();
            String colName = entry.getKey();

            if (!dataTypes.contains(value))
                throw new DBAppException("Invalid data type");

            if (htblColNameMin.get(colName) == null || htblColNameMax.get(colName) == null)
                throw new DBAppException("Should specify both min and max values for the column " + colName);
        }

        if (htblColNameType.get(strClusteringKeyColumn) == null)
            throw new DBAppException("Invalid Primary Key");


        //post-verification
        Hashtable<String, Object> htblColMin = new Hashtable<>();
        Hashtable<String, Object> htblColMax = new Hashtable<>();
        try {
            for (String colName : htblColNameMin.keySet()) {
                Object parsed = parse(htblColNameMin.get(colName), htblColNameType.get(colName));
                htblColMin.put(colName, parsed);
            }

            for (String colName : htblColNameMax.keySet()) {
                Object parsed = parse(htblColNameMax.get(colName), htblColNameType.get(colName));
                htblColMax.put(colName, parsed);
            }

            Table table = new Table(strTableName, strClusteringKeyColumn, htblColMin, htblColMax);
            table.setCkType(htblColNameType.get(strClusteringKeyColumn));
            table.setNumOfCols(htblColNameType.size());
            table.setColumnNames(new Vector<>(htblColNameType.keySet()));

            tableNames.add(strTableName);

            table.serialize();

            writeInCSV(strTableName, strClusteringKeyColumn, htblColNameType, htblColNameMin, htblColNameMax); //a method that will write in the csv
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBAppException(e.getMessage());

        }

    }

    public static Object inflate(Object x) {
        if (x instanceof Integer)
            return (int) x + 1;
        if (x instanceof Double)
            return (double) x + 1;
        if (x instanceof String)
            return ((String) x + "a");
        if (x instanceof Date)
            return new Date(((Date) x).getTime() + 86400000);

        return null;
    }


    public static String getAscii(String s) {
        s = s.toLowerCase();
        String result = "";
        int ascii1 = 0;
        for (int i = 0; i < s.length(); i++) {
            ascii1 = (int) s.charAt(i) + 1;
//                ascii1 = (char)ascii1;
            result += ascii1;
        }
        return result;
    }

    public void createIndex(String strTableName,
                            String[] strarrColName) throws Exception {
        verifyIndexCreation(strTableName, strarrColName);

        System.out.println("out of verify");


        Vector<String> colNames = modifyCsvForIndex(strTableName, strarrColName);
        String[] colNamesArray = colNames.toArray(new String[0]);

        Table table = Table.deserialize(strTableName);


        Object minX = table.getHtblColMin().get(colNamesArray[0]);
        Object maxX = table.getHtblColMax().get(colNamesArray[0]);

        System.out.print("MIN X = ");
        System.out.println(minX);


        Object minY = table.getHtblColMin().get(colNamesArray[1]);
        Object maxY = table.getHtblColMax().get(colNamesArray[1]);


        Object minZ = table.getHtblColMin().get(colNamesArray[2]);
        Object maxZ = table.getHtblColMax().get(colNamesArray[2]);

        Cube boundary = new Cube(inflate(maxX), minX, minY, inflate(maxY), inflate(maxZ), minZ);
        int capacity = Integer.parseInt(readConfig(DBApp.CONFIG_PATH).getProperty("MaximumEntriesinOctreeNode"));

        OctTree octree = new OctTree(capacity, boundary);


        String name = strarrColName[0] + strarrColName[1] + strarrColName[2] + "Index";
        for (int i = 0; i < colNamesArray.length; i++)
            System.out.println(colNamesArray[i]);
        Index index = new Index(strTableName, name, colNamesArray, octree);

//        Vector<String> indexcolNames = new Vector<>();
//        Collections.addAll(indexcolNames, strarrColName);

//      table.getIndexes().add(index);
        table.getHtblIndexNameColumn().put(name, colNames);
        index.populate();
        index.serialize();

        table.serialize();
    }

    public void verifyIndexCreation(String strTableName, String[] strarrColName) throws Exception {
        //checking inputs to the createIndex method are fine
        if (!tableNames.contains(strTableName))
            throw new DBAppException("Table not found");

        Table table = Table.deserialize(strTableName);

        if (strarrColName.length != 3)
            throw new DBAppException("Index should be created on three columns");

        for (String column : strarrColName)
            if (!table.getColumnNames().contains(column))
                throw new DBAppException(column + " field does not exist in the table");
        //inputs to the createIndex method are fine
        //checking to see if column already has an index or not

        BufferedReader br = new BufferedReader(new FileReader(METADATA_PATH));

        String line = br.readLine();
        String[] content = line.split(",");

        while (line != null) {

            content = line.split(",");
            String tableName = content[0];
            String colName = content[1];
            String indexName = content[4];


            if (!tableName.equals(table.getName())) {
                line = br.readLine();
                continue;
            }

            for (int i = 0; i < strarrColName.length; i++) {
                if (colName.equals(strarrColName[i]) && !indexName.equals("null")) {
                    throw new DBAppException("Table already has an index on this column");
                }
            }

            line = br.readLine();
        }
        br.close();
        table.serialize();
    }

    public Vector<String> modifyCsvForIndex(String strTableName,
                                            String[] strarrColName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(METADATA_PATH));

        // Create a temporary file for writing the modified content

        File tempFile = new File(TEMP_PATH);
        PrintWriter writer = new PrintWriter(tempFile);

        // Read each line of the file and modify the desired line
        String line = reader.readLine();
        String[] content;

        Vector<String> strVecColName = new Vector<String>();
        Collections.addAll(strVecColName, strarrColName);
        Vector<String> columnName = new Vector<>();
//        Vector<Pair> minMax = new Vector<Pair>();

        while (line != null) {
            content = line.split(",");
            String table = content[0];
            String colName = content[1];
            if (!table.equals(strTableName))
                writer.println(line);

            else if (!strVecColName.contains(colName))
                writer.println(line);
            else {
                String colType = content[2];
                String isClusteringKey = content[3];
                String indexName = strarrColName[0] + strarrColName[1] + strarrColName[2] + "Index";
                String indexType = "Octree";
                String min = content[6];
                String max = content[7];
                String row = table + "," + colName + "," + colType + "," + isClusteringKey + "," + indexName + "," +
                        indexType + "," + min + "," + max;
                writer.println(row);

//               Pair pair = new Pair(parse(min, colType), parse(max, colType));
                columnName.add(colName);
//                minMax.add(pair);
            }
            line = reader.readLine();
        }
        reader.close();
        writer.close();
        File originalFile = new File(METADATA_PATH);
        originalFile.delete();
        tempFile.renameTo(originalFile);

        return columnName;

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

    public Hashtable<String, Object> changeStringInHashtable(Hashtable<String, Object> htblColNameValue) {
        for (String column : htblColNameValue.keySet()) {
            if (htblColNameValue.get(column) instanceof String) {
                String lower = (String) ((String) htblColNameValue.get(column)).toLowerCase();
                htblColNameValue.put(column, lower);
            }
        }
        return htblColNameValue;
    }

    public void insertIntoTable(String strTableName,
                                Hashtable<String, Object> htblColNameValue) throws DBAppException {

        try {
            verifyInsert(strTableName, htblColNameValue);
            htblColNameValue = changeStringInHashtable(htblColNameValue);
            Table table = Table.deserialize(strTableName);
            Comparable ckValue = (Comparable) htblColNameValue.get(table.getClusteringKey());

            Page locatedPage = table.getPageToInsert(ckValue);

            if (htblColNameValue.size() < table.getNumOfCols()) {
                for (String colName : table.getColumnNames())
                    if (htblColNameValue.get(colName) == null)
                        htblColNameValue.put(colName, DBAppNull.getInstance());
            }

            //INSERT IN INDEX SHOULD BE BEFORE SHIFT
            int ref = (locatedPage == null) ? (table.getMaxIDsoFar() + 1) : locatedPage.getId();

            if (!table.isEmpty() && ckValue.compareTo(table.getHtblPageIdMinMax().get(ref).getMax()) >= 0 && locatedPage.isFull())
                ref++;

            for (String idxName : table.getHtblIndexNameColumn().keySet()) {
                Index index = Index.deserialize(table.getName(), idxName);
                System.out.println("Inserted in " + index.getName() + " into page " + ref);
                index.insert(htblColNameValue, ref);
                index.serialize();
            }

            if (locatedPage == null)
                locatedPage = newPageInit(htblColNameValue, table);
            else
                insertAndShift(htblColNameValue, locatedPage.getId(), table);

            table.serialize();

        } catch (Exception e) {
            e.printStackTrace();
            throw new DBAppException(e.getMessage());
        }
    }


    public void verifyInsert(String strTableName, Hashtable<String, Object> htblColNameValue) throws Exception {


        if (!tableNames.contains(strTableName))
            throw new DBAppException("Table not found");

        Table table = Table.deserialize(strTableName);

//        if(htblColNameValue.size() != table.getColumnNames().size())
//            throw new engine.DBAppException("Invalid number of columns entered");

        if (htblColNameValue.size() > table.getNumOfCols())
            throw new DBAppException("Invalid number of columns entered");

        for (String column : htblColNameValue.keySet())
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
                    // System.out.println(colName);
                    throw new DBAppException("Incompatible data types");
                }
                if (compare(value, max) > 0)
                    throw new DBAppException(value + " is greater than the allowed maximum value");
                if (compare(value, min) < 0) {
                    throw new DBAppException(value + " is less than the allowed minimum value");
                }
            }

            line = br.readLine();

        }

        //VERIFY DUPE CK
        int pid = table.binarySearchInTable(ckValue);
        if (pid != -1) {
            Page p = Page.deserialize(strTableName, pid);
            int tupleIdx = p.binarySearchInPage(table.getClusteringKey(), ckValue);
            p.serialize();

            if (tupleIdx != -1)
                throw new DBAppException("Cannot allow duplicate values for Clustering Key");
        }

        br.close();
        table.serialize();
    }

    public void updateIndexReference(Hashtable<String, Object> tuple, int oldID, int newID, Table table) throws IOException, ClassNotFoundException {
        if (newID == -1)
            newID = table.getMaxIDsoFar() + 1;

        for (String idxName : table.getHtblIndexNameColumn().keySet()) {
            Index index = Index.deserialize(table.getName(), idxName);
            index.updateReference(tuple, oldID, newID);
            index.serialize();
        }
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
            int pageId = table.getNextID(page);
            table.setMinMax(page);
            page.serialize();
            updateIndexReference(lastTuple, id, pageId, table);
            insertAndShift(lastTuple, pageId, table);
        } else {
            table.setMinMax(page);
            page.serialize();
        }

        //hashtable updates


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
            throws DBAppException {
        try {
            verifyDelete(strTableName, htblColNameValue);
            htblColNameValue = changeStringInHashtable(htblColNameValue);
            Table table = Table.deserialize(strTableName);

            boolean hasCK = htblColNameValue.get(table.getClusteringKey()) != null;
            String indexFound = getIndex(table, htblColNameValue, hasCK);
            // index on 3 columns
            // binary search using ck
            // index partial no ck
            // seq scan
            if (hasCK && indexFound == null) {
                System.out.println("BINARY SEARCH");
                //binary search to locate the page
                Comparable ckValue = (Comparable) htblColNameValue.get(table.getClusteringKey());

                Page locatedPage = table.getPageToModify(ckValue);

                int tupleIndex = locatedPage.binarySearchInPage(table.getClusteringKey(), ((Comparable) ckValue));
                if (tupleIndex == -1)
                    return;

                Hashtable<String, Object> tuple = locatedPage.getTuples().get(tupleIndex);

                if (!isMatch(htblColNameValue, tuple))
                    return;


                //REMOVE IT FROM INDEX
                for (String IdxName : table.getHtblIndexNameColumn().keySet()) {
                    Index index = Index.deserialize(strTableName, IdxName);
                    index.deletePoints(tuple, locatedPage.getId());
                    index.serialize();
                }

                locatedPage.getTuples().remove(tuple);
                table.updatePageDelete(locatedPage);

            } else {

                Vector<Integer> ids = null;
                if (indexFound != null) {
                    System.out.println("SEARCHING WITH INDEX " + indexFound);
                    Index index = Index.deserialize(strTableName, indexFound);
                    ids = index.searchDelete(htblColNameValue);
                    index.serialize();

                } else {
                    System.out.println("LINEAR SCAN");
                    ids = new Vector<Integer>(table.getHtblPageIdMinMax().keySet());
                }


                Hashtable<Integer, Vector<Hashtable<String, Object>>> htblIdTuples = new Hashtable<>();
                for (Integer id : ids) {
                    //CASE DUPLICATES HAVE SAME REFERENCE
                    if (htblIdTuples.get(id) != null)
                        continue;
                    Page currPage = Page.deserialize(table.getName(), id);
                    Vector<Hashtable<String, Object>> tmp = new Vector<>();

                    for (Hashtable<String, Object> tuple : currPage.getTuples())
                        if (isMatch(htblColNameValue, tuple))
                            tmp.add(tuple);

                    htblIdTuples.put(id, tmp);

                    currPage.getTuples().removeAll(tmp);
                    table.updatePageDelete(currPage);
                }


                updateIndices(table, htblIdTuples);

            }

            table.serialize();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBAppException(e.getMessage());
        }
    }

    public String getIndex(Table table, Hashtable<String, Object> htblColNameValue, boolean hasCk) {

        Hashtable<String, Vector<String>> htblIdxNameCol = table.getHtblIndexNameColumn();

        //USING INDEX
        String indexFound = null;
        int countSoFar = 0;

        for (String idxName : htblIdxNameCol.keySet()) {
            int c = 0;
            Vector<String> cols = htblIdxNameCol.get(idxName);

            for (String column : htblColNameValue.keySet())
                if (cols.contains(column))
                    c++;

            if (c == 3) {
                indexFound = idxName;
                break;
            }
            //Partial query
            if (c > countSoFar && !hasCk) {
                indexFound = idxName;
                countSoFar = c;
            }
        }
        return indexFound;
    }

    public static void updateIndices(Table table, Hashtable<Integer, Vector<Hashtable<String, Object>>> htblIdTuples) throws Exception {
        for (String idxName : table.getHtblIndexNameColumn().keySet()) {
            Index index = Index.deserialize(table.getName(), idxName);
            index.deletePoints(htblIdTuples);
            index.serialize();
        }
    }

    public boolean isMatch(Hashtable<String, Object> htblColNameValue, Hashtable<String, Object> tuple) {
        for (String colName : htblColNameValue.keySet()) {
            if (!htblColNameValue.get(colName).equals(tuple.get(colName))) // not all conditions satisfied
                return false;
        }
        return true;
    }

    public void verifyDelete(String strTableName, Hashtable<String, Object> htblColNameValue) throws Exception {

        if (!tableNames.contains(strTableName))
            throw new DBAppException("engine.Table not found");

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
                    // System.out.println(colName);
                    throw new DBAppException("Incompatible data types");
                }

            }

            line = br.readLine();
        }

        br.close();

        for (String column : htblColNameValue.keySet())
            if (!table.getColumnNames().contains(column))
                throw new DBAppException(column + " field does not exist in the table");

        table.serialize();
    }

    //test later **
    public void updateTable(String strTableName,
                            String strClusteringKeyValue,
                            Hashtable<String, Object> htblColNameValue) throws DBAppException {
        try {
            verifyUpdate(strTableName, strClusteringKeyValue, htblColNameValue);

            htblColNameValue = changeStringInHashtable(htblColNameValue);
            Table table = Table.deserialize(strTableName);
            Comparable ckValue = (Comparable) parse(strClusteringKeyValue, table.getCkType());
            if (ckValue instanceof String)
                ckValue = ((String) ckValue).toLowerCase();

            Page locatedPage = table.getPageToModify(ckValue);
            int locatedPageID = table.binarySearchInTable(((Comparable) ckValue));
            int tupleIndex = locatedPage.binarySearchInPage(table.getClusteringKey(), ((Comparable) ckValue));

            if (tupleIndex == -1)
                throw new DBAppException("Tuple does not exist");

            Hashtable<String, Object> tupleToUpdate = locatedPage.getTuples().get(tupleIndex);

            for (String idxName : table.getHtblIndexNameColumn().keySet()) {
                Index index = Index.deserialize(table.getName(), idxName);
                index.updatePoint(htblColNameValue, tupleToUpdate, locatedPageID);
                index.serialize();
            }
            tupleToUpdate.putAll(htblColNameValue);
            table.setMinMax(locatedPage);
            locatedPage.serialize();
            table.serialize();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBAppException(e.getMessage());
        }
    }

    public void verifyUpdate(String strTableName,
                             String strClusteringKeyValue,
                             Hashtable<String, Object> htblColNameValue) throws Exception {

        if (!tableNames.contains(strTableName))
            throw new DBAppException("engine.Table not found");

        Table table = Table.deserialize(strTableName);

        if (htblColNameValue.get(table.getClusteringKey()) != null)
            throw new DBAppException("Cannot update the clustering key");

        for (String column : htblColNameValue.keySet())
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
                try {
                    parse(strClusteringKeyValue, colType);
                } catch (Exception e) {
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
        // page=null;
    }

    public Page newPageInit(Hashtable<String, Object> tuple, Table table) throws IOException {
        table.setMaxIDsoFar(table.getMaxIDsoFar() + 1);
        Page newPage = new Page(table.getName(), table.getMaxIDsoFar());

        newPage.getTuples().add(tuple);
        int id = newPage.getId();

        Object ckValue = tuple.get(table.getClusteringKey());

        table.getHtblPageIdMinMax().put(id, new Pair(ckValue, ckValue));

        newPage.serialize();
        return newPage;
    }

    public static boolean sameType(Object data, String dataType) throws ClassNotFoundException {
        if (data == null)
            return true;
        if (data instanceof DBAppNull)
            return true;
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
            System.out.println("Can't find metadata");
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

    public void verifySelect(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws Exception {
        if (strarrOperators.length != arrSQLTerms.length - 1)
            throw new DBAppException("Number of operators is incorrect");

        for (String s : strarrOperators) {
            if (s.equals("AND") || s.equals("OR") || s.equals("XOR"))
                continue;
            throw new DBAppException("Invalid operator");
        }

        for (SQLTerm term : arrSQLTerms) {
            term.checkOperator();
            verifySelectTypes(term);
        }
    }

    public void verifySelectTypes(SQLTerm term) throws IOException, DBAppException, ClassNotFoundException {
        if (!tableNames.contains(term.get_strTableName())) {
            throw new DBAppException("This table does not exist");
        }
        Table table = Table.deserialize(term.get_strTableName());
        if (!table.getColumnNames().contains(term.get_strColumnName())) {
            throw new DBAppException("Column" + term.get_strColumnName() + "does not exist");
        }
        table.serialize();

        BufferedReader br = new BufferedReader(new FileReader(METADATA_PATH));
        String line = br.readLine();

        while (line != null) {
            String[] content = line.split(",");
            String tableName = content[0];
            String colName = content[1];
            String colType = content[2];
            String min = content[6];
            String max = content[7];
            if (!colName.equals(term.get_strColumnName())) {
                line = br.readLine();
                continue;
            }

            if (!sameType(term.get_objValue(), colType))
                throw new DBAppException("Type mismatch");


            line = br.readLine();
        }
        br.close();
    }

    public static boolean operate(Object op1, Object op2, String operator) {
        boolean cond1 = op1 instanceof DBAppNull;
        boolean cond2 = op2 == null;
        if (!cond1 && !cond2) {
            Comparable op3 = (Comparable) op1;
            return switch (operator) {
                case "=" -> op3.equals(op2);
                case "!=" -> !op3.equals(op2);
                case ">" -> op3.compareTo(op2) > 0;
                case ">=" -> op3.compareTo(op2) >= 0;
                case "<" -> op3.compareTo(op2) < 0;
                case "<=" -> op3.compareTo(op2) <= 0;
                case "AND" -> (boolean) op3 && (boolean) op2;
                case "OR" -> (boolean) op3 || (boolean) op2;
                case "XOR" -> ((boolean) op3 && !((boolean) op2)) || (!((boolean) op3) && (boolean) op2);

                default -> false;
            };
        } else if ((cond1 && !cond2) || (!cond1 && cond2)) {
            if (operator.equals("!="))
                return true;
            return false;
        } else {
            if (operator.equals("="))
                return true;
            else
                return false;
        }


    }

    public static boolean satisfy(Hashtable<String, Object> tuples, SQLTerm term) {
        String colName = term.get_strColumnName();
        return operate(tuples.get(colName), term.get_objValue(), term.get_strOperator());
    }

    public static Vector<Boolean> getSatisfied(Hashtable<String, Object> tuples, SQLTerm[] terms) {
        Vector<Boolean> res = new Vector<>();
        for (SQLTerm term : terms)
            res.add(satisfy(tuples, term));
        return res;
    }

    public static boolean compute(Vector<Boolean> boolArr, String[] strarrOperators) {
        boolean res = boolArr.remove(0);

        for (int i = 0; i < boolArr.size(); i++) {
            boolean op1 = boolArr.get(i);
            String operator = strarrOperators[i];
            res = operate(res, op1, operator);
        }

        return res;
    }

    public static boolean isAllAnd(String[] strarrOperators) {
        for (String op : strarrOperators)
            if (!op.equals("AND"))
                return false;
        return true;
    }


    public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws Exception {

        verifySelect(arrSQLTerms, strarrOperators);

        String tableName = arrSQLTerms[0].get_strTableName();
        Table table = Table.deserialize(tableName);
        Hashtable<String, Vector<String>> htblIdxNameCol = table.getHtblIndexNameColumn();

        ResultSet rs = new ResultSet();

        String indexFound = "";
        Vector<SQLTerm> termsFound = null;

        if (isAllAnd(strarrOperators))
            for (String idxName : htblIdxNameCol.keySet()) {
                int c = 0;
                termsFound = new Vector<>();
                Vector<String> cols = htblIdxNameCol.get(idxName);

                for (SQLTerm sqlTerm : arrSQLTerms)
                    if (cols.contains(sqlTerm.get_strColumnName()) && !(sqlTerm.get_strOperator().equals("!="))) {
                        termsFound.add(sqlTerm);
                        c++;
                    }

                if (c == 3) {
                    indexFound = idxName;
                    break;
                }
            }


        if (!indexFound.equals("")) {
            //INDEX BASED
            System.out.println("in index");
            Index idx = Index.deserialize(tableName, indexFound);
            Vector<Integer> references = idx.searchSelect(termsFound);

            Vector<Integer> refSeen = new Vector<Integer>();


            for (int ref : references) {
                if (refSeen.contains(ref))
                    continue;
                refSeen.add(ref);
                Page p = Page.deserialize(tableName, ref);

                for (Hashtable<String, Object> tuple : p.getTuples()) {
                    Vector<Boolean> bools = getSatisfied(tuple, arrSQLTerms);
                    if (compute(bools, strarrOperators))
                        rs.getResultTuples().add(tuple);
                }
            }
            idx.serialize();
        } else {
            //LINEAR SCAN TABLE
            for (int id : table.getHtblPageIdMinMax().keySet()) {
                Page page = Page.deserialize(tableName, id);
                for (Hashtable<String, Object> tuple : page.getTuples()) {
                    Vector<Boolean> bools = getSatisfied(tuple, arrSQLTerms);
                    if (compute(bools, strarrOperators)) {
                        rs.getResultTuples().add(tuple);
                    }
                }
            }
        }

        table.serialize();

        return rs;
    }

    public static void main2(String[] args) {

        String fileName = "D:/db-engine/test.txt";

        gParser parser = getParser(fileName);
        ParseTree antlrAST = parser.prog();
        AntlrToProgram progVisitor = new AntlrToProgram();
        Program prog = progVisitor.visit(antlrAST);

    }

    private static gParser getParser(String fileName) {

        gParser parser = null;

        try {
            CharStream input = CharStreams.fromFileName(fileName);
            gLexer lexer = new gLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new gParser(tokens);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parser;

    }

    public Iterator execute(SQLExpr expr) throws Exception {

        if (expr instanceof SelectCommand) {
            SelectCommand selectCmd = (SelectCommand) expr;
            Condition condition = selectCmd.getCondition();
            SQLTerm[] sqlTerms = new SQLTerm[condition.getStatements().size()];
            String[] operators = new String[condition.getOperators().size()];

            for (int i = 0; i < condition.getStatements().size(); i++) {
                Statement s = condition.getStatements().get(i);
                SQLTerm sqlTerm = new SQLTerm(selectCmd.getTableName().getValue(), s.getColName().getValue(), s.getOperator().getValue(), s.getValue().getValue());
                sqlTerms[i] = sqlTerm;
            }

            for (int i = 0; i < condition.getOperators().size(); i++) {
                String s = condition.getOperators().get(i).getValue().toUpperCase();
                operators[i] = s;
            }

            return this.selectFromTable(sqlTerms, operators);
        } else if (expr instanceof InsertCommand) {
            InsertCommand insertCmd = (InsertCommand) expr;
            Hashtable<String, Object> tuple = new Hashtable<>();

            String tableName = insertCmd.getTableName().getValue();

            for (int i = 0; i < insertCmd.getValueList().getValues().size(); i++) {
                Object object = insertCmd.getValueList().getValues().get(i).getValue();
                String colName = insertCmd.getColumns().getColumnNames().get(i).getValue();

                tuple.put(colName, object);
                System.out.println(tuple);
            }

            this.insertIntoTable(tableName, tuple);
        } else if (expr instanceof DeleteCommand) {
            DeleteCommand delCmd = (DeleteCommand) expr;
            Hashtable<String, Object> delCriteria = new Hashtable<>();

            String tableName = delCmd.getTableName().getValue();
            Condition condition = delCmd.getCondition();

            for (Statement s : condition.getStatements()) {
                delCriteria.put(s.getColName().getValue(), s.getValue().getValue());
            }

            this.deleteFromTable(tableName, delCriteria);
        } else if (expr instanceof UpdateCommand) {
            UpdateCommand updateCmd = (UpdateCommand) expr;
            Hashtable<String, Object> htblColNameValue = new Hashtable<>();

            String tableName = updateCmd.getTableName().getValue();

            Condition setColumns = updateCmd.getSetColumns();

            for (Statement s : setColumns.getStatements()) {
                htblColNameValue.put(s.getColName().getValue(), s.getValue().getValue());
            }

            Condition updateCond = updateCmd.getUpdateConditions();
            String strCkVal = updateCond.getStatements().get(0).getValue().getValue().toString();


            this.updateTable(tableName, strCkVal, htblColNameValue);

        }

        return null;

    }

    public Iterator parseSQL(StringBuffer strbufSQL) throws DBAppException {

        String filePath = "./src/main/resources/query.txt";
        Iterator results = null;

        try {
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(strbufSQL.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        gParser parser = getParser(filePath);
        ParseTree antlrAST = parser.prog();
        AntlrToProgram progVisitor = new AntlrToProgram();
        Program prog = progVisitor.visit(antlrAST);

        try {
            results = this.execute(prog.getSqlExpr());
        } catch (Exception e) {
            e.printStackTrace();
            throw new DBAppException();

        }

        return results;
    }

    public static void main(String[] args) throws Exception {
        Hashtable<String, String> htblColNameType = new Hashtable<>();
//        htblColNameType.put("id", "java.lang.Integer");
        htblColNameType.put("name", "java.lang.String");
        htblColNameType.put("age", "java.lang.Integer");
        htblColNameType.put("gpa", "java.lang.Double");
//        htblColNameType.put("dob", "java.util.Date");

        Hashtable<String, String> htblColNameMin = new Hashtable<>();
//        htblColNameMin.put("id", "0");
        htblColNameMin.put("name", "A");
        htblColNameMin.put("age", "1");
        htblColNameMin.put("gpa", "0.7");
//        htblColNameMin.put("dob", "1940-01-01");


        Hashtable<String, String> htblColNameMax = new Hashtable<>();
//        htblColNameMax.put("id", "20");
        htblColNameMax.put("name", "zzzzzzz");
        htblColNameMax.put("age", "40");
        htblColNameMax.put("gpa", "4.0");
//        htblColNameMax.put("dob", "2023-01-01");

        Hashtable<String, Object> tuple0 = new Hashtable<>();
//        tuple0.put("id", 0);
        tuple0.put("name", "Malak");
        tuple0.put("age", 20);
        tuple0.put("gpa", 0.71);
//        tuple0.put("dob", new Date(1028812800000L));

        Hashtable<String, Object> tuple1 = new Hashtable<>();
//        tuple1.put("id", 1);
        tuple1.put("name", "Ahmed");
        tuple1.put("age", 23);
        tuple1.put("gpa", 1.3);
//        tuple1.put("dob", new Date(1039315200000L));

        Hashtable<String, Object> tuple2 = new Hashtable<>();
//        tuple2.put("id", 2);
        tuple2.put("name", "Omar");
        tuple2.put("age", 27);
        tuple2.put("gpa", 3.9);


        Hashtable<String, Object> tuple3 = new Hashtable<>();
//        tuple3.put("id", 3);
        tuple3.put("name", "Lobna");
        tuple3.put("age", 29);
        tuple3.put("gpa", 1.2);
//        tuple3.put("dob", new Date(759625200000L));

        Hashtable<String, Object> tuple4 = new Hashtable<>();
//        tuple4.put("id", 4);
        tuple4.put("name", "Samy");
        tuple4.put("age", 10);
        tuple4.put("gpa", 4.0);
//        tuple4.put("dob", new Date(28800000L));


        Hashtable<String, Object> tuple5 = new Hashtable<>();
        tuple5.put("id", 5);
        tuple5.put("name", "kord");
        tuple5.put("age", 15);
        tuple5.put("gpa", 2.5);
        tuple5.put("dob", new Date(1193616000000l));


        Hashtable<String, Object> tuple6 = new Hashtable<>();
        tuple6.put("id", 6);
        tuple6.put("name", "Menna");
        tuple6.put("gpa", 3.0);
        tuple6.put("dob", new Date(0L));


        Hashtable<String, Object> tuple7 = new Hashtable<>();
        tuple7.put("id", 7);
        tuple7.put("name", "Menna");
        tuple7.put("gpa", 3.0);
        tuple7.put("dob", new Date(0L));


        Hashtable<String, Object> tuple8 = new Hashtable<>();
        tuple8.put("age", 8);
        tuple8.put("name", "boni");
        tuple8.put("gpa", 3.0);
        tuple8.put("semester", 7);
        tuple8.put("address", "nozha");
        tuple8.put("lastName", "amer");

        Hashtable<String, Object> duplicate8 = new Hashtable<>();
        duplicate8.put("age", 20);
        duplicate8.put("name", "nada");
        duplicate8.put("gpa", 2.5);
        duplicate8.put("semester", 8);

        Hashtable<String, Object> tuple9 = new Hashtable<>();
        tuple9.put("age", 9);
//        tuple9.put("name", "Haboosh");
        tuple9.put("gpa", 3.4);
        tuple9.put("semester", 9);
        tuple9.put("address", "nozha");
        tuple9.put("lastName", "haboosh");

        Hashtable<String, Object> tuple10 = new Hashtable<>();
        tuple10.put("age", 10);
        tuple10.put("gpa", 0.9);
        tuple10.put("semester", 10);

        Hashtable<String, Object> tuple11 = new Hashtable<>();
        tuple11.put("age", 16);
        tuple11.put("name", "Mariam");
        tuple11.put("gpa", 0.95);
        tuple11.put("semester", 2);
        tuple11.put("address", "new cairo");
        tuple11.put("lastName", "mariam");

        Hashtable<String, Object> tuple12 = new Hashtable<>();
        tuple12.put("age", 14);
        tuple12.put("name", "Haboosh");
        tuple12.put("gpa", 2.5);


        DBApp dbApp = new DBApp();
        dbApp.init();


//        Hashtable<String, Object> updateHtbl = new Hashtable<>();
//        updateHtbl.put("age", 40);

//        dbApp.updateTable("Students", "3", updateHtbl);

//        Hashtable<String, Object> deletingCriteria0 = new Hashtable<>();


//        deletingCriteria0.put("age", DBAppNull.getInstance());
//        deletingCriteria0.put("age", 27);
//        deletingCriteria0.put("gpa",3.9 );

//       dbApp.deleteFromTable("Students", deletingCriteria0);
//        dbApp.createTable("students", "age", htblColNameType, htblColNameMin, htblColNameMax);
//        dbApp.insertIntoTable("students", tuple1);
//        dbApp.insertIntoTable("students", tuple2);
//        dbApp.insertIntoTable("students", tuple3);
//        dbApp.insertIntoTable("students", tuple4);
//        dbApp.insertIntoTable("students", tuple8);
//        dbApp.insertIntoTable("Students", tuple1);
//        dbApp.insertIntoTable("students", tuple3);
//        dbApp.insertIntoTable("students", tuple5);
//        dbApp.insertIntoTable("students", tuple4);
//        dbApp.insertIntoTable("Students", tuple9);
//        dbApp.insertIntoTable("Students", duplicate8);
//        dbApp.createTable("Students", "age", htblColNameType, htblColNameMin, htblColNameMax);
//        dbApp.insertIntoTable("Students", tuple0);
//        dbApp.insertIntoTable("Students", tuple2);
//        dbApp.insertIntoTable("Students", tuple6);
//        dbApp.insertIntoTable("Students", tuple7);
//        dbApp.insertIntoTable("Students", tuple8);
//        dbApp.insertIntoTable("Students", tuple1);
//        dbApp.insertIntoTable("Students", tuple3);
//        dbApp.insertIntoTable("Students", tuple5);
//        dbApp.insertIntoTable("Students", tuple4);
//        dbApp.insertIntoTable("Students", tuple9);
//        dbApp.insertIntoTable("Students", tuple10);
//
//        dbApp.insertIntoTable("Students", tuple11);
//        dbApp.insertIntoTable("Students", tuple12);

//
//        Hashtable<String, Object> updateHtbl = new Hashtable<>();
//        updateHtbl.put("name", "George");
//
//        Hashtable<String, Object> updateHtbl = new Hashtable<>();
//        updateHtbl.put("semester", 7);
//        dbApp.updateTable("Students", "9", updateHtbl);

//        Hashtable<String, Object> deletingCriteria0 = new Hashtable<>();
//        Hashtable<String, Object> deletingCriteria1 = new Hashtable<>();
//        Hashtable<String, Object> deletingCriteria2 = new Hashtable<>();
        //   deletingCriteria0.put("age", 2);
//       deletingCriteria1.put("name","lobna");
//       deletingCriteria1.put( "age", 10);
//       deletingCriteria1.put( "age",8);
//       deletingCriteria1.put("name","Kord");
//       deletingCriteria1.put( "gpa", 3.0);
//       deletingCriteria1.put( "semester",7);
//       dbApp.deleteFromTable("Students", deletingCriteria1);
        //case searching partial query and delete point that has null value in index but not deleted (10,null,10)
//       dbApp.deleteFromTable("Students", deletingCriteria1);
//       dbApp.deleteFromTable("Students", deletingCriteria2);

//
//        SQLTerm sqlTerm = new SQLTerm("Students", "age", "<", 20);
//        SQLTerm sqlTerm1 = new SQLTerm("Students", "name", ">=", "a");
//        SQLTerm sqlTerm2 = new SQLTerm("Students", "gpa", ">=", 2.0);
//        SQLTerm sqlTerm3 = new SQLTerm("Students", "id", "!=", 6);
//////
//        SQLTerm[] sqlTerms = {sqlTerm, sqlTerm1, sqlTerm2, sqlTerm3};
//        String[] strarrOperators = {"AND", "AND", "OR"};
//
//        SQLTerm sqlTerm = new SQLTerm("Students", "gpa", "<", 2.5);
//        SQLTerm sqlTerm1 = new SQLTerm("Students", "name", ">", "boni");
//        SQLTerm sqlTerm2 = new SQLTerm("Students", "semester", ">=", 6);
////
//        SQLTerm[] sqlTerms = {sqlTerm, sqlTerm1, sqlTerm2};
//        String[] strarrOperators = {"AND", "AND"};

//        Iterator rs = dbApp.selectFromTable(sqlTerms, strarrOperators);
//        System.out.println(rs);

        Table table = Table.deserialize("students");

//        dbApp.createIndex("Students", new String[]{"name", "age", "gpa"});


//        Index index3 = Index.deserialize(table.getName(), "nameagegpaIndex");
//        index3.octree.printTree();
//        System.out.println();

        Iterator resultSet = dbApp.parseSQL(new StringBuffer("update students set name = ola where age = 29"));
//        System.out.println(resultSet.next());
//        System.out.println(resultSet.next());
//        System.out.println(resultSet.next());

//        dbApp.createIndex("Students", new String[]{"semester", "name", "age"});


//        Index index3 = Index.deserialize(table.getName(), "semesternameageIndex");
//        index3.octree.printTree();
//        System.out.println();
//        System.out.println();
//        engine.Index index4 = engine.Index.deserialize(table.getName(), "gpaaddresslastNameIndex");
//        index4.octree.printTree();
//        System.out.println();
//      System.out.println(index3);
//      System.out.println(table.getHtblIndexName());

//        String str = "1995-10-04";
//        dbApp.parseLiteral(str);


        for (int id : table.getHtblPageIdMinMax().keySet()) {
            Page p = Page.deserialize(table.getName(), id);
            System.out.println("PAGE " + id);
            System.out.println(p.getTuples());
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            p.serialize();
        }


    }
}