package seclass.gatech.edu.glm.models;

/**
 * Created by Lighterkey on 10/13/2016.
 */

public class Item {

    private long mItemId;
    private String mName;
    private String mType;

    public Item(){
    }


    public Item(long itemId, String name, String type) {
        mItemId = itemId;
        mName = name;
        mType = type;
    }

    /** When ID is not yet set */
    public Item(String m_name, String m_type) {
        mItemId = -1;
        mName = m_name;
        mType = m_type;
    }

    public long getItemId() {
        return mItemId;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }

    public void setId(int id) {
        mItemId = id;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setType(String type) {
        mType = type;
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }
        Item i = (Item) obj;
        return mName.equals(i.getName()) && mType.equals(i.getType());
    }

    @Override
    public int hashCode() {
        return 31*mName.hashCode() + mType.hashCode();
    }

}
