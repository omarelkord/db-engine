import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

public class Index implements Serializable {
    private String tableName;
    private String name;
    public OctTree octree;
    private String[] columns;


    public Index(String tableName,String name,String[] columns, OctTree octree){
        this.tableName=tableName;
        this.name=name;
        this.columns = columns;
        this.octree = octree;
    }


    public void populate() throws Exception{

        Table table = Table.deserialize(tableName);
        Vector<Integer> ids = new Vector<Integer>(table.getHtblPageIdMinMax().keySet());
        for (Integer id : ids) {
            Page currPage = Page.deserialize(table.getName(), id);

            for (Hashtable<String, Object> tuple : currPage.getTuples()){
                Object x = tuple.get(columns[0]);
                Object y = tuple.get(columns[1]);
                Object z = tuple.get(columns[2]);
                octree.insertInTree(new Point(x,y,z,id));
            }
            currPage.serialize();


        }
        table.serialize();
    }


}

