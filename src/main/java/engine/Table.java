package engine;

import java.io.*;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

public class Table implements Serializable {
    private String name;
    private Hashtable<Integer, Pair> htblPageIdMinMax;
    private String clusteringKey;
    private String ckType;

    private int numOfCols;
    private Vector<String> columnNames;
    private Hashtable<String, Vector<String>> htblIndexNameColumn;
    private Hashtable<String, Object> htblColMin;
    private Hashtable<String, Object> htblColMax;


    //    public static final String TABLE_DIRECTORY = "D:\\db-engine\\src\\main\\resources\\data\\";
    public static final String TABLE_DIRECTORY = "./src/main/resources/data/";

    private int maxIDsoFar;

    public Table(String strTableName, String strClusteringKeyColumn ,  Hashtable<String, Object> htblColNameMin,
                 Hashtable<String,Object> htblColNameMax) {
        this.name = strTableName;
        this.clusteringKey = strClusteringKeyColumn;
        htblPageIdMinMax = new Hashtable<>();
        htblIndexNameColumn = new Hashtable<>();
        maxIDsoFar = -1;
        htblColMin = htblColNameMin;
        htblColMax = htblColNameMax;
    }

    public Hashtable<String, Vector<String>> getHtblIndexNameColumn() {
        return htblIndexNameColumn;
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

    public void serialize() throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(TABLE_DIRECTORY + this.getName() + ".ser"));
        outputStream.writeObject(this);
        outputStream.close();
    }


    public static Table deserialize(String tableName) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(TABLE_DIRECTORY + tableName + ".ser"));
        Table table = (Table) inputStream.readObject();

        inputStream.close();
        return table;
    }


    public Page getPageToInsert2(Comparable CKValue) throws IOException, ClassNotFoundException {
        Page locatedPage = null;

        Vector<Integer> sortedID = new Vector<>(this.getHtblPageIdMinMax().keySet());
        Collections.sort(sortedID);

        for (Integer id : sortedID) {
            Page currPage = Page.deserialize(this.getName(), id);
            Pair pair = this.getHtblPageIdMinMax().get(id);
            Object min = pair.getMin();
            Object max = pair.getMax();

            Page nextPage = null;
            Pair nextPair = null;
            Object nextMin = null;


            if (hasPage(getNextID(currPage))) {
                nextPage = Page.deserialize(this.getName(), getNextID(currPage));
                nextPair = this.getHtblPageIdMinMax().get(getNextID(currPage));
                nextMin = nextPair.getMin();
            }


            Boolean islastPage = getNextID(currPage) == -1;

            boolean insFlag = CKValue.compareTo(min) < 0 || (CKValue.compareTo(min) > 0 && CKValue.compareTo(max) < 0)
                    || ((CKValue.compareTo(max) > 0 && !currPage.isFull()) &&
                    ((hasPage(getNextID(currPage)) && CKValue.compareTo((Comparable) nextMin) < 0) || islastPage));

            if (insFlag) {
                locatedPage = currPage;
                break;
            }

            currPage.serialize();
        }

        return locatedPage;
    }

    public void setClusteringKey(String clusteringKey) {
        this.clusteringKey = clusteringKey;
    }

    public Integer getPageIDToInsert(Comparable value){
        Vector<Integer> sortedID = new Vector<>(this.htblPageIdMinMax.keySet());
        Collections.sort(sortedID);

        int left = 0;
        int right = sortedID.size() - 1;

        while (left <= right) {
            int mid = (right + left) / 2;
            Pair pair = this.getHtblPageIdMinMax().get(sortedID.get(mid));
            Object min = pair.getMin();
            Object max = pair.getMax();

            if (value.compareTo(min) > 0 && value.compareTo(max) < 0)
                return sortedID.get(mid);
            if (value.compareTo(min) < 0)
                right = mid - 1;
            else
                left = mid + 1;
        }
        return sortedID.get(Math.max(right, 0));
    }

    public Page getPageToInsert(Comparable ckValue) throws IOException, ClassNotFoundException {

        //no available pages
        if(this.getHtblPageIdMinMax().isEmpty())
            return null;

        Integer id = this.getPageIDToInsert(ckValue);
        return Page.deserialize(this.getName(), id);
    }


    public int getNextID(Page page) {
        Vector<Integer> idsInTable = new Vector<>(this.htblPageIdMinMax.keySet());
        Collections.sort(idsInTable);

        int index = idsInTable.indexOf(page.getId());

        if (index == idsInTable.size() - 1)
            return -1;

        return idsInTable.get(1 + index);
    }

    public int getMaxIDsoFar() {
        return maxIDsoFar;
    }

    public void setMaxIDsoFar(int maxIDsoFar) {
        this.maxIDsoFar = maxIDsoFar;
    }

    public int binarySearchInTable(Comparable value) throws Exception {
        Vector<Integer> sortedID = new Vector<Integer>(this.htblPageIdMinMax.keySet());
        Collections.sort(sortedID);

        int left = 0;
        int right = sortedID.size() - 1;

        while (left <= right) {
            int mid = (right + left) / 2;
            Pair pair = this.getHtblPageIdMinMax().get(sortedID.get(mid));
            Object min = pair.getMin();
            Object max = pair.getMax();

            if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0)
                return sortedID.get(mid);
            if (value.compareTo(min) < 0)
                right = mid - 1;
            else
                left = mid + 1;
        }
        return -1;
    }

    public Page getPageToModify(Comparable ckValue) throws Exception {
        int locatedPageID = this.binarySearchInTable(((Comparable) ckValue));

        if (locatedPageID == -1)
            return null;
        return Page.deserialize(this.getName(), locatedPageID);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Hashtable<Integer, Pair> getHtblPageIdMinMax() {
        return htblPageIdMinMax;
    }

    public Hashtable<String, Object> getHtblColMin() {
        return htblColMin;
    }
    public Hashtable<String, Object> getHtblColMax() {
        return htblColMax;
    }

    public void setHtblPageIdMinMax(Hashtable<Integer, Pair> htblPageIdMaxMin) {
        this.htblPageIdMinMax = htblPageIdMaxMin;
    }

    public String getClusteringKey() {
        return clusteringKey;
    }

    public void setCKName(String clusteringKey) {
        this.clusteringKey = clusteringKey;
    }

    public Vector<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(Vector<String> columnNames) {
        this.columnNames = columnNames;
    }


    public boolean hasPage(int id) {
        return this.getHtblPageIdMinMax().get(id) != null;
    }

    public void updatePageDelete(Page locatedPage) throws Exception {
        if (locatedPage.isEmpty()) {

            String pagePath = locatedPage.getPath();
            File file = new File(pagePath);
            file.delete();

            this.getHtblPageIdMinMax().remove(locatedPage.getId());
        } else {
            this.setMinMax(locatedPage);
            locatedPage.serialize();
        }
    }

    public void setMinMax(Page page) {
        String ck = this.getClusteringKey();
        Pair newPair = new Pair(page.getTuples().get(0).get(ck), page.getTuples().get(page.getTuples().size() - 1).get(ck));
        this.getHtblPageIdMinMax().put(page.getId(), newPair);
    }

    public boolean isEmpty(){
        return this.htblPageIdMinMax.isEmpty();
    }


}
