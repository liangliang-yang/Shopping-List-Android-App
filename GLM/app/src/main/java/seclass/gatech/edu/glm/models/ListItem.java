package seclass.gatech.edu.glm.models;

/** Does NOT extend item. Instead, keeps a reference to the corresponding Item. */
public class ListItem extends Item {

    private long mListItemId;
    private float mQuantity;
    private boolean mIsChecked;

    public ListItem(){}

    public ListItem(long listItemId, Item item, float quantity, boolean isChecked) {
        super(item.getItemId(), item.getName(), item.getType());
        mListItemId = listItemId;
        mQuantity = quantity;
        mIsChecked = isChecked;
    }

    public long getListItemId() {
        return mListItemId;
    }

    public void setListItemId(long listItemId) {
        mListItemId = listItemId;
    }

    public float getQuantity() {
        return mQuantity;
    }

    public void setQuantity(float quantity) {
        mQuantity = quantity;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean isChecked) {
        mIsChecked = isChecked;
    }


    /** Over-riding ListItem equals/hashCode so they will properly match each other in a HashSet */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
