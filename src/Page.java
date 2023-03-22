import java.io.*;
import java.util.*;
public class Page implements Serializable {
    private Vector<Hashtable<String,Object>> tuples;
    private int id;
    private int maxPageSize;

    private static String PAGE_DIRECTORY = "D:\\db-engine\\Pages\\";

    public Page(int id) throws IOException{
        tuples = new Vector<>();
        this.id = id;

        maxPageSize = Integer.parseInt(readConfig("DBApp.config").getProperty("MaximumRowsCountinTablePage"));
    }
    public int getMaxPageSize() {
        return maxPageSize;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTuples(Vector<Hashtable<String,Object>> tuples) {
        this.tuples = tuples;
    }

    public Vector<Hashtable<String,Object>> getTuples() {
        return tuples;
    }

    public void serialize() throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(PAGE_DIRECTORY + "page-" + this.getId() + ".class"));
        outputStream.writeObject(this);
        outputStream.close();
    }

    // Method to deserialize the Page object
    public static Page deserialize(Integer id) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PAGE_DIRECTORY + "page-" + id + ".class"));
        Page page = (Page) inputStream.readObject();
        inputStream.close();
        return page;
    }

    public static Properties readConfig(String path) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(path);
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

    public boolean isFull(){
        if(this.tuples.size() == maxPageSize)
            return true;
        return false;
    }

    public boolean isOverFlow(){
        System.out.println("CURR PAGE SIZE = " + this.tuples.size());
        System.out.println("MAX PAGE SIZE = " + maxPageSize);

        return (this.tuples.size() > maxPageSize);
    }
    
}
