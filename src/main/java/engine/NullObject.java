package engine;

import java.io.Serializable;

public class NullObject implements Serializable {
    private static NullObject instance = null;


    private NullObject(){

    }

    // Static method to get the singleton instance of the class
    public static NullObject getInstance() {
        if(instance == null)
            instance = new NullObject();

        return instance;

    }

    @Override
    public String toString() {
        return "null";
    }

}
