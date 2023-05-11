import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class ResultSet implements Iterator {

    private Vector<Hashtable<String, Object>> resultTuples;
    private int maxIdx;

    public ResultSet(){
        resultTuples = new Vector<>();
    }
    @Override
    public boolean hasNext() {
        return maxIdx < resultTuples.size();
    }

    @Override
    public Object next() {
        System.out.println("Entered next");
        if(hasNext())
            return resultTuples.get(maxIdx++);
        return null;
    }

    public Vector<Hashtable<String, Object>> getResultTuples() {
        return resultTuples;
    }

    public int getMaxIdx() {
        return maxIdx;
    }
}
