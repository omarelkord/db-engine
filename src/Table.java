import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

public class Table implements Serializable {
    private String name;
    private Hashtable<Integer, String> htblPageIdPagesPaths;
    private Hashtable<Integer, Pair> htblPageIdMinMax;
    private Hashtable<Object, Integer> htblKeyPageId;
    private Hashtable<Integer, Integer> htblPageIdCurrPageSize;
    private String clusteringKey;
    private String ckType;
    private int numOfCols;
    public static final String TABLE_DIRECTORY = "D:\\db-engine\\Tables\\";
    private int maxIDsoFar;

    public Table(String strTableName, String strClusteringKeyColumn) {
        this.name = strTableName;
        this.clusteringKey = strClusteringKeyColumn;
        htblPageIdPagesPaths = new Hashtable<>();
        htblPageIdMinMax = new Hashtable<>();
        htblPageIdCurrPageSize = new Hashtable<>();
        htblKeyPageId = new Hashtable<>();
        maxIDsoFar = -1;
    }

    public int getNumOfCols() {
        return numOfCols;
    }
    public void setNumOfCols(int numOfCols) {
        this.numOfCols = numOfCols;
    }

    public String getCkType() {
        return ckType;
    }

    public void setCkType(String ckType) {
        this.ckType = ckType;
    }


    public Hashtable<Object, Integer> getHtblKeyPageId() {
        return htblKeyPageId;
    }

    public void setHtblKeyPageId(Hashtable<Object, Integer> htblKeyPageId) {
        this.htblKeyPageId = htblKeyPageId;
    }


    public void serialize() throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(TABLE_DIRECTORY + this.getName() + ".class"));
        outputStream.writeObject(this);
        outputStream.close();
    }


    public static Table deserialize(String tableName) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(TABLE_DIRECTORY + tableName + ".class"));
        Table table = (Table) inputStream.readObject();

        inputStream.close();
        return table;
    }


    public Page getLocatedPage(Comparable CKValue, boolean toInsert) throws IOException, ClassNotFoundException {
        Page locatedPage = null;
//        int currPageId = 0;
        Vector<Integer> sortedID = new Vector<>(this.getHtblPageIdMinMax().keySet());
        Collections.sort(sortedID);

        for (Integer id : sortedID) {
            Page currPage = Page.deserialize(id);
            Pair pair = this.getHtblPageIdMinMax().get(id);
            Object min = pair.getMin();
            Object max = pair.getMax();

            Page nextPage = null;
            Pair nextPair = null;
            Object nextMin = null;
            if(hasPage(id+1)){
                nextPage = Page.deserialize(getNextID(currPage));
                nextPair = this.getHtblPageIdMinMax().get(getNextID(currPage));
                nextMin = nextPair.getMin();
            }


            // NOT FULL:
            // ) less than min =>
            // ) else => less than max (range) => insert same page
            // ) else if greater than max
            // )    if there's room ==> insert and update max
            // )    if full ==> next iteration (if not last iteration)

            Boolean islastPage = getNextID(currPage) == -1;

            boolean insFlag = toInsert && (CKValue.compareTo(min) < 0 || (CKValue.compareTo(min) > 0 && CKValue.compareTo(max) < 0)
                    || ((CKValue.compareTo(max) > 0 && !currPage.isFull()) &&
                    ((hasPage(getNextID(currPage)) && CKValue.compareTo((Comparable) nextMin) < 0) || islastPage)));

            boolean updateFlag = !toInsert && (CKValue.compareTo(min) >= 0 && CKValue.compareTo(max) <= 0);

            if (insFlag || updateFlag) {
                locatedPage = currPage;
                break;
            }

            currPage.serialize();
        }

        return locatedPage;
    }
    public int getNextID(Page page){
        Vector<Integer> idsInTable = new Vector<>(this.htblPageIdPagesPaths.keySet());
        Collections.sort(idsInTable);

        int index = idsInTable.indexOf(page.getId());

        if(index == idsInTable.size() - 1)
            return -1;

        return idsInTable.get(1 + index);
    }

    public int getMaxIDsoFar() {
        return maxIDsoFar;
    }

    public void setMaxIDsoFar(int maxIDsoFar) {
        this.maxIDsoFar = maxIDsoFar;
    }

    public int binarySearchInTable(Comparable value) throws Exception{
        Vector<Integer> sortedID = new Vector<Integer>(this.getHtblPageIdMinMax().keySet());
        Collections.sort(sortedID);

        int left = 0;
        int right = sortedID.size()-1;

        while (left<=right) {
            int mid = (right + left)/2;
            Pair pair = this.getHtblPageIdMinMax().get(mid);
            Object min = pair.getMin();
            Object max = pair.getMax();

            if(value.compareTo(min)>=0 && value.compareTo(max)<=0)
                return mid;
            if(value.compareTo(min)<0)
                right= mid-1;
            else
                left = mid+1;
        }
        return -1;
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
}
