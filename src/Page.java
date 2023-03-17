import java.io.*;
import java.util.*;
public class Page implements Serializable {
    private Vector<Object> tuples;
    private static int maxIDSoFar = 0;
    private int id;

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

    public Page(){
        tuples = new Vector<>();
        this.id = maxIDSoFar++;
    }

    public void setTuples(Vector<Object> tuples) {
        this.tuples = tuples;
    }

    public Vector<Object> getTuples() {
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
    public static void main(String[] args) throws IOException ,ClassNotFoundException {
        Page page = new Page();
        Vector<Object> tuples = new Vector<>();
        tuples.add("Hello");
        tuples.add("World");
        page.setTuples(tuples);

        // Serialize the Page object to a binary file
        page.serialize("C:\\Users\\Ahmed Labib\\OneDrive\\Desktop\\guc\\6th Semester\\Database\\project\\test.bin");

        // Deserialize the Page object from the binary file
        Page deserializedPage = Page.deserialize("C:\\Users\\Ahmed Labib\\OneDrive\\Desktop\\guc\\6th Semester\\Database\\project\\test.bin");
        System.out.println(deserializedPage.getTuples());
    }
}
