import java.io.*;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

public class Table implements Serializable {
    private String name;
    private Hashtable<Integer, String> htblPageIdPagesPaths;
    private Hashtable<Integer, Pair> htblPageIdMinMax;
    private Hashtable<Object, Integer> htblKeyPageId;

    //pageID -> currPageSize
    private Hashtable<Integer, Integer> htblPageIdCurrPageSize;
    private String clusteringKey;
    private String ckType;

    public int getNumOfCols() {
        return numOfCols;
    }

    public void setNumOfCols(int numOfCols) {
        this.numOfCols = numOfCols;
    }

    private int numOfCols;

    public Table(String strTableName, String strClusteringKeyColumn) {
        this.name = strTableName;
        this.clusteringKey = strClusteringKeyColumn;
        htblPageIdPagesPaths = new Hashtable<>();
        htblPageIdMinMax = new Hashtable<>();
        htblPageIdCurrPageSize = new Hashtable<>();
        htblKeyPageId = new Hashtable<>();
    }

    public String getCkType() {
        return ckType;
    }

    public void setCkType(String ckType) {
        this.ckType = ckType;
    }

    public static final String TABLE_DIRECTORY = "D:\\db-engine\\Tables\\";


    public Hashtable<Object, Integer> getHtblKeyPageId() {
        return htblKeyPageId;
    }

    public void setHtblKeyPageId(Hashtable<Object, Integer> htblKeyPageId) {
        this.htblKeyPageId = htblKeyPageId;
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

//    public String getClusteringKey() throws IOException, DBAppException {
//
//        BufferedReader br = new BufferedReader(new FileReader(DBApp.METADATA_PATH));
//
//        String line = br.readLine();
//        String[] content = line.split(",");
//
//        String clusteringKey="";
//
//        while (line != null) {
//
//            String tableName = content[0];
//            String colName = content[1];
//            String isClusteringKey = content[3];
//
//            if (!tableName.equals(this.getName())) {
//                line = br.readLine();
//                continue;
//            }
//
//            if (Boolean.parseBoolean(isClusteringKey))
//                clusteringKey = colName;
//
//            line = br.readLine();
//        }
//
//        br.close();
//
//        return clusteringKey;
//    }

    public Page getLocatedPage(Comparable CKValue, boolean toInsert) throws IOException, ClassNotFoundException {
        Page locatedPage = null;
//        int currPageId = 0;
        Vector<Integer> sortedID = new Vector<Integer>(this.getHtblPageIdMinMax().keySet());
        Collections.sort(sortedID);

        for (Integer id : sortedID) {
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

            boolean insFlag = toInsert && (CKValue.compareTo(min) < 0 || (CKValue.compareTo(min) > 0 && CKValue.compareTo(max) < 0)
                    || (CKValue.compareTo(max) > 0 && !currPage.isFull()));

            boolean updateFlag = !toInsert && (CKValue.compareTo(min) >= 0 && CKValue.compareTo(max) <= 0);

            if (insFlag || updateFlag) {
                locatedPage = currPage;
                break;
            }

            currPage.serialize();
        }

        return locatedPage;
    }


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

    public String getClusteringKey() {
        return clusteringKey;
    }

    public void setCKName(String clusteringKey) {
        this.clusteringKey = clusteringKey;
    }

    public String getPagePath(int id) {
        return this.getHtblPageIdPagesPaths().get(id);
    }

    public boolean hasPage(int id) {
        return this.getHtblPageIdPagesPaths().get(id) != null;
    }

    public static void main(String[] args) {


    }
}
