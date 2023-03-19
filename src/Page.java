import java.io.*;
import java.util.*;
public class Page implements Serializable {
    private Vector<Hashtable<String,Object>> tuples;
    private static int maxIDSoFar = 0;
    private int id;
    private int currPageSize;
    private static int maxPageSize;

    public Page() throws IOException{
        tuples = new Vector<>();
        this.id = maxIDSoFar++;
        currPageSize = 0;
        maxPageSize = Integer.parseInt(readConfig("DBApp.config").getProperty("MaximumRowsCountinTablePage"));
    }

    public static int getMaxIDSoFar() {
        return maxIDSoFar;
    }

    public static void setMaxIDSoFar(int maxIDSoFar) {
        Page.maxIDSoFar = maxIDSoFar;
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

    public static Properties readConfig(String path) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(path);
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }

    public boolean isFull(){
        if(currPageSize == maxPageSize)
            return true;
        return false;
    }
    public static void main(String[] args) throws IOException ,ClassNotFoundException {
        Page page = new Page();
        Vector<Hashtable<String,Object>> tuples = new Vector<>();
//        tuples.add("Hello");
//        tuples.add("World");
        page.setTuples(tuples);

        // Serialize the Page object to a binary file
        page.serialize("C:\\Users\\Ahmed Labib\\OneDrive\\Desktop\\guc\\6th Semester\\Database\\project\\test.bin");

        // Deserialize the Page object from the binary file
        Page deserializedPage = Page.deserialize("C:\\Users\\Ahmed Labib\\OneDrive\\Desktop\\guc\\6th Semester\\Database\\project\\test.bin");
        System.out.println(deserializedPage.getTuples());
    }
}
