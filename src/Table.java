import java.io.*;
import java.util.Vector;

public class Table implements Serializable {
    private Vector<String> pagesPaths;
    private Vector<Object> pageMaxKey;
    private Vector<Object> pageMinKey;
    private String name;
    private Vector<Integer> pageSize;
    private String pk;


    public Table(String name , String pk){
        this.name= name;
        this.pk= pk;
        this.pageMaxKey = new Vector<Object>();
        this.pageMinKey = new Vector<Object>();
        this.pageSize = new Vector<Integer>();
        this.pagesPaths = new Vector<String>();
    }

    public void serialize(String filename) throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
        outputStream.writeObject(this);
        outputStream.close();
    }

    // Method to deserialize the Page object
    public static Page deserialize(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename));
        Page page = (Page) inputStream.readObject();
        inputStream.close();
        return page;
    }

    public Vector<String> getPagesPaths() {
        return pagesPaths;
    }

    public void setPagesPaths(Vector<String> pagesPaths) {
        this.pagesPaths = pagesPaths;
    }

    public Vector<Object> getMaxKey() {
        return pageMaxKey;
    }

    public void setMaxKey(Vector<Object> maxKey) {
        this.pageMaxKey = maxKey;
    }

    public Vector<Object> getMinKey() {
        return pageMinKey;
    }

    public void setMinKey(Vector<Object> minKey) {
        this.pageMinKey = minKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector<Integer> getPageSize() {
        return pageSize;
    }

    public void setPageSize(Vector<Integer> pageSize) {
        this.pageSize = pageSize;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }




    public static void main(String[] args){

    }
}
