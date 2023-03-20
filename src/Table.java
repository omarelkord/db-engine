import java.io.*;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

public class Table implements Serializable {
    private String name;
    private Hashtable<Integer,String> htblPageIdPagesPaths;
    private Hashtable<Integer, Pair> htblPageIdMinMax;
    private Hashtable<Object,Integer> htblKeyPageId;

    //pageID -> currPageSize
    private Hashtable<Integer, Integer> htblPageIdCurrPageSize;
    private String CK;

    public static final String TABLE_DIRECTORY = "D:\\db-engine\\Tables\\";


    public Hashtable<Object, Integer> getHtblKeyPageId() {
        return htblKeyPageId;
    }

    public void setHtblKeyPageId(Hashtable<Object, Integer> htblKeyPageId) {
        this.htblKeyPageId = htblKeyPageId;
    }

    public Table(String strTableName, String strClusteringKeyColumn){
        this.name= strTableName;
        this.CK = strClusteringKeyColumn;
        htblPageIdPagesPaths = new Hashtable<>();
        htblPageIdMinMax = new Hashtable<>();
        htblPageIdCurrPageSize = new Hashtable<>();
        htblKeyPageId = new Hashtable<>();
    }

    public void serialize() throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(TABLE_DIRECTORY + this.getName()));
        outputStream.writeObject(this);
        outputStream.close();
    }


    public static Table deserialize(String tableName) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(TABLE_DIRECTORY + tableName));
        Table table = (Table) inputStream.readObject();

        inputStream.close();
        return table;
    }

    public String getClusteringKey() throws IOException, DBAppException {

        BufferedReader br = new BufferedReader(new FileReader(DBApp.METADATA_PATH));

        String line = br.readLine();
        String[] content = line.split(",");

        String clusteringKey="";

        while (line != null) {

            String tableName = content[0];
            String colName = content[1];
            String isClusteringKey = content[3];

            if (!tableName.equals(this.getName())) {
                line = br.readLine();
                continue;
            }

            if (Boolean.parseBoolean(isClusteringKey))
                clusteringKey = colName;

            line = br.readLine();
        }

        br.close();

        return clusteringKey;
    }

    public Page getLocatedPage(Comparable CKValue) throws IOException, ClassNotFoundException {
        Page locatedPage = null;
//        int currPageId = 0;
        Vector<Integer> sortedID = new Vector<Integer>(this.getHtblPageIdMinMax().keySet());
        Collections.sort(sortedID);

        for(Integer id : sortedID){
            Page currPage = Page.deserialize(id);
            Pair pair = this.getHtblPageIdMinMax().get(id);
            Object min = pair.getMin();
            Object max = pair.getMax();


            // NOT FULL:
            // ) less than min => insert
            // ) else => less than max (range) => insert same page
            // ) else if greater than max
            // )    if there's room ==> insert and update max
            // )    if full ==> next iteration (if not last iteration)


            if(CKValue.compareTo(min) < 0
                    || (CKValue.compareTo(min) > 0 && CKValue.compareTo(max) < 0)
                    || (CKValue.compareTo(max) > 0 && !currPage.isFull())) {
//                currPageId = id;
                locatedPage = currPage;
                break;
            }
            currPage.serialize();
        }

        return locatedPage;
    }

//    public void verify(Hashtable<String, Object> htblColNameValue) throws Exception {
//
//        if (this == null)
//            throw new DBAppException("Table not found");
//
//
//        String ckName = table.getClusteringKey();
//        Object ckValue = htblColNameValue.get(ckName);
//
//        //INTEGRITY CONSTRAINTS
//        if (ckValue == null)
//            throw new DBAppException("Cannot allow null values for Clustering Key");
//        if (table.getHtblKeyPageId().get(ckValue) != null)
//            throw new DBAppException("Cannot allow duplicate values for Clustering Key");
//
//
//        BufferedReader br = new BufferedReader(new FileReader(METADATA_PATH));
//
//        String line = br.readLine();
//        String[] content = line.split(",");
//
//        while (line != null) {
//
//            String tableName = content[0];
//            String colName = content[1];
//            String colType = content[2];
//            String min = content[6];
//            String max = content[7];
//            Object value = htblColNameValue.get(colName);
//
//            if (!tableName.equals(table.getName())) {
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
//
//            line = br.readLine();
//        }
//
//        br.close();
//    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hashtable<Integer, String> getHtblPageIdPagesPaths() {
        return htblPageIdPagesPaths;
    }

    public void setHtblPageIdPagesPaths(Hashtable<Integer, String> htblPageIdPagesPaths) {
        this.htblPageIdPagesPaths = htblPageIdPagesPaths;
    }

    public Hashtable<Integer, Pair> getHtblPageIdMinMax() {
        return htblPageIdMinMax;
    }

    public void setHtblPageIdMinMax(Hashtable<Integer, Pair> htblPageIdMaxMin) {
        this.htblPageIdMinMax = htblPageIdMaxMin;
    }

    public Hashtable<Integer, Integer> getHtblPageIdCurrPageSize() {
        return htblPageIdCurrPageSize;
    }

    public void setHtblPageIdCurrPageSize(Hashtable<Integer, Integer> htblPageIdCurrPageSize) {
        this.htblPageIdCurrPageSize = htblPageIdCurrPageSize;
    }

    public String getCKName() {
        return CK;
    }

    public void setCKName(String ck) {
        this.CK = ck;
    }

    public String getPagePath(int id){
        return this.getHtblPageIdPagesPaths().get(id);
    }

    public boolean hasPage(int id){
        return this.getHtblPageIdPagesPaths().get(id) != null;
    }

    public static void main(String[] args){



    }
}
