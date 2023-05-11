import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

public class Index implements Serializable {
    private String tableName;
    private String name;
    public OctTree octree;
    private String[] columns;
    public static final String TABLE_DIRECTORY = "./src/main/resources/data/";


    public Index(String tableName, String name, String[] columns, OctTree octree) {
        this.tableName = tableName;
        this.name = name;
        this.columns = columns;
        this.octree = octree;
    }

    public void insert(Hashtable<String, Object> tuple, int ref) {
        Point tuplePoint = new Point(tuple.get(columns[0]), tuple.get(columns[1]), tuple.get(columns[2]), ref);
        System.out.println("Entered insert in class Index");
        octree.insertInTree(tuplePoint);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OctTree getOctree() {
        return octree;
    }

    public void setOctree(OctTree octree) {
        this.octree = octree;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public void populate() throws Exception {

        Table table = Table.deserialize(tableName);
        Vector<Integer> ids = new Vector<>(table.getHtblPageIdMinMax().keySet());
        for (Integer id : ids) {
            Page currPage = Page.deserialize(table.getName(), id);

            for (Hashtable<String, Object> tuple : currPage.getTuples()) {
                Object x = tuple.get(columns[0]);
                Object y = tuple.get(columns[1]);
                Object z = tuple.get(columns[2]);
                octree.insertInTree(new Point(x, y, z, id));
            }
            currPage.serialize();
        }
    }

    public void serialize() throws IOException {
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(TABLE_DIRECTORY + tableName + name + ".ser"));
        outputStream.writeObject(this);
        outputStream.close();
    }


    public static Index deserialize(String tableName, String indexName) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(TABLE_DIRECTORY + tableName + indexName + ".ser"));
        Index index = (Index) inputStream.readObject();

        inputStream.close();
        return index;
    }

    public Vector<Integer> search(Vector<SQLTerm> SQLTerms) {

        System.out.println("I am in index class");

        Vector<Object> objValues = new Vector<>();

        Object x = null;
        Object y = null;
        Object z = null;

        for (SQLTerm term : SQLTerms) {
            if (term.get_strColumnName().equals(columns[0]))
                x = term.get_objValue();
            if (term.get_strColumnName().equals(columns[1]))
                y = term.get_objValue();
            if (term.get_strColumnName().equals(columns[2]))
                z = term.get_objValue();
        }


        Vector<Point> foundPoints = octree.searchPoint(x, y, z);
        Vector<Integer> references = new Vector<>();

        for (Point point : foundPoints) {
            references.addAll(point.pageReference);
        }

        return references;
    }

    public void updateReference(Hashtable<String,Object> tuple, int old,int newId){
        Object x = tuple.get(columns[0]);
        Object y = tuple.get(columns[1]);
        Object z = tuple.get(columns[2]);
        Vector<Point> list = octree.searchPoint(x,y,z);
        Point point = list.get(0);
        for(int i=0;i<point.pageReference.size();i++)
            if(old==point.pageReference.get(i)){
                point.pageReference.set(i,newId);
                return;
            }
    }
}

