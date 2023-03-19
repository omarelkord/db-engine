import java.io.*;
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

    public void serialize(String tableName) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(TABLE_DIRECTORY + tableName + ".bin"));
        outputStream.writeObject(this);
        outputStream.close();
    }


    public static Table deserialize(String tableName) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(TABLE_DIRECTORY + tableName + ".bin"));
        Table table = (Table) inputStream.readObject();

        inputStream.close();
        return table;
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
