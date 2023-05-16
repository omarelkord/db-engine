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

                if(x instanceof DBAppNull)
                    x = null;
                if(y instanceof DBAppNull)
                    y = null;
                if(z instanceof DBAppNull)
                    z = null;
                octree.insertInTree(new Point(x, y, z, id));
            }
            currPage.serialize();
        }
    }
     public void insert(Hashtable<String, Object> tuple, int ref) {

        Object x = tuple.get(columns[0]);
        Object y = tuple.get(columns[1]);
        Object z = tuple.get(columns[2]);

        if(x instanceof DBAppNull)
            x = null;
        if(y instanceof DBAppNull)
            y = null;
        if(z instanceof DBAppNull)
            z = null;

        Point tuplePoint = new Point(x,y,z, ref);
        System.out.println("Entered insert in class Index");
        octree.insertInTree(tuplePoint);
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

    public Vector<Integer> searchSelect(Vector<SQLTerm> SQLTerms) throws Exception{

        Vector<Object> objValues = new Vector<>();

        Object x = null;
        Object y = null;
        Object z = null;

        Table table = Table.deserialize(tableName);

        Object maxX = table.getHtblColMax().get(columns[0]);
        Object minX = table.getHtblColMin().get(columns[0]);
        Object maxY = table.getHtblColMax().get(columns[1]);
        Object minY = table.getHtblColMin().get(columns[1]);
        Object maxZ = table.getHtblColMax().get(columns[2]);
        Object minZ = table.getHtblColMin().get(columns[2]);

        boolean [] include = new boolean[6];

        for (SQLTerm term : SQLTerms) {
            if (term.get_strColumnName().equals(columns[0])){
                x = term.get_objValue();
                switch(term.get_strOperator()){
                    case ">": minX = x;
                            include[0] = true;
                            include[1] = false;
                            break;
                    case "<": maxX = x;
                              include[0] = false;
                              include[1] = true;
                              break;
                    case ">=" : minX = x;
                                include[0] = true;
                                include[1] = true;
                                break;
                    case "<=:": maxX = x;
                                include[0] = true;
                                include[1] = true;
                                break;

                    case "=": minX = x ;
                              maxX = x;
                              include[0] = true;
                              include[1] = true;
                              break;
                }
            }

            if (term.get_strColumnName().equals(columns[1])) {
                y = term.get_objValue();
                switch (term.get_strOperator()) {
                    case ">":
                        minY = y;
                        include[2] = true;
                        include[3] = false;
                        break;
                    case "<":
                        maxY = y;
                        include[2] = false;
                        include[3] = true;
                        break;
                    case ">=":
                        minY = y;
                        include[2] = true;
                        include[3] = true;
                        break;
                    case "<=:":
                        maxY = y;
                        include[2] = true;
                        include[3] = true;
                        break;

                    case "=":
                        minY = y;
                        maxY = y;
                        include[2] = true;
                        include[3] = true;
                        break;
                }
            }
            if (term.get_strColumnName().equals(columns[2])){
                 z = term.get_objValue();

                switch(term.get_strOperator()){
                    case ">": minZ = z;
                        include[4] = true;
                        include[5] = false;
                        break;
                    case "<": maxZ = z;
                        include[4] = false;
                        include[5] = true;
                        break;
                    case ">=" : minZ = z;
                        include[4] = true;
                        include[5] = true;
                        break;
                    case "<=:": maxZ = z;
                        include[4] = true;
                        include[5] = true;
                        break;

                    case "=": minZ = z;
                        maxZ = z;
                        include[4] = true;
                        include[5] = true;
                        break;
                }

            }

        }

        table.serialize();

//        Vector<Point> foundPoints = octree.searchPoint(x, y, z);
        Vector<Point> foundPoints = octree.rangeSelect(maxX, minX , maxY , minY , maxZ ,minZ,include);
        Vector<Integer> references = new Vector<>();
        System.out.println(foundPoints);
        for (Point point : foundPoints) {
            references.addAll(point.pageReference);
        }

        return references;
    }

//    public void getMinMax(String strOperator , Object value , Object maxValue , Object minValue) {
//
//        switch(strOperator){
//            case ">": ;
//            case "<": ;
//            case ">=":;
//            case "<=":;
//            case "=": ;
//        }
//    }

    public Vector<Integer> searchDelete(Hashtable<String,Object> criteria){

        Object x = null;
        Object y = null;
        Object z = null;

        for (String col : criteria.keySet()) {
            if (col.equals(columns[0]))
                x = criteria.get(col);
            if (col.equals(columns[1]))
                y = criteria.get(col);
            if (col.equals(columns[2]))
                z = criteria.get(col);
        }


        Vector<Point> foundPoints = octree.searchPoint(x, y, z);
        Vector<Integer> references = new Vector<>();

        for (Point point : foundPoints)
            references.addAll(point.pageReference);

        return references;
    }

    public void updateReference(Hashtable<String,Object> tuple, int old,int newId){
        Object x = tuple.get(columns[0]);
        Object y = tuple.get(columns[1]);
        Object z = tuple.get(columns[2]);
        if(x instanceof DBAppNull)
            x = null;
        if(y instanceof DBAppNull)
            y = null;
        if(z instanceof DBAppNull)
            z = null;
        Vector<Point> list = octree.searchPoint(x,y,z);
        System.out.println(list);
        for(Point point : list) {
            for (int i = 0; i < point.pageReference.size(); i++)
                if (old == point.pageReference.get(i)) {
                    point.pageReference.set(i, newId);
                    return;
                }
        }
    }
    public void updatePoint(Hashtable<String,Object> updates,Hashtable<String,Object> tuple,int id){
        Object oldX = tuple.get(columns[0]);
        Object oldY = tuple.get(columns[1]);
        Object oldZ = tuple.get(columns[2]);
        if(oldX instanceof DBAppNull)
            oldX = null;
        if(oldY instanceof DBAppNull)
            oldY = null;
        if(oldZ instanceof DBAppNull)
            oldZ = null;
        Object newX = (updates.get(columns[0])==null)?(oldX):(updates.get(columns[0]));
        Object newY = (updates.get(columns[1])==null)?(oldY):(updates.get(columns[1]));
        Object newZ = (updates.get(columns[2])==null)?(oldZ):(updates.get(columns[2]));
        Point oldPoint = new Point(oldX, oldY,oldZ,id);
        Point newPoint = new Point(newX, newY,newZ,id);
        octree.updateTree(oldPoint,newPoint);
    }

    public void deletePoints(Hashtable<Integer,Vector<Hashtable<String,Object>>> info){

        for( Integer id : info.keySet()){
            Vector<Hashtable<String,Object>> tuples = info.get(id);

            for(Hashtable<String, Object> tuple : tuples) {

               Object x = tuple.get(columns[0]);
               Object y = tuple.get(columns[1]);
               Object z = tuple.get(columns[2]);

               if(x instanceof DBAppNull)
                   x = null;
               if (y instanceof DBAppNull)
                   y = null;
               if(z instanceof DBAppNull)
                   z = null;

               octree.deleteInTree(x,y,z, id);

            }
        }

    }
    public void deletePoints(Hashtable<String,Object> tuple,int id){
        Object x = tuple.get(columns[0]);
        Object y = tuple.get(columns[1]);
        Object z = tuple.get(columns[2]);

        if(x instanceof DBAppNull)
            x = null;
        if(y instanceof DBAppNull)
            y = null;
        if(z instanceof DBAppNull)
            z = null;

        octree.deleteInTree(x,y,z, id);
    }
}

