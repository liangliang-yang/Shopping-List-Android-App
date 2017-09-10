package seclass.gatech.edu.glm.models;

/**
 * Created by Lighterkey on 10/13/2016.
 */

public class GroceryList {
    private long mId;
    private String mName;

    public GroceryList(){
    }

    public GroceryList(String name){
        this.mName = name;
    }

    public GroceryList(long id, String name){
        mId = id;
        mName = name;
    }

    public long getListId(){
        return mId;
    }

    public String getName(){
        return mName;
    }

    public  void setId(int id) {mId = id;}

    public  void setName(String name) {mName = name;}

}
