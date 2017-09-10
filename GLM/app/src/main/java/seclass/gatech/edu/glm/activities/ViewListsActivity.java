package seclass.gatech.edu.glm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import seclass.gatech.edu.glm.R;
import seclass.gatech.edu.glm.adapters.ListsAdapter;
import seclass.gatech.edu.glm.listeners.ListsTouchListener;
import seclass.gatech.edu.glm.models.DBUtility;
import seclass.gatech.edu.glm.models.GroceryList;

public class ViewListsActivity extends AppCompatActivity {

    DBUtility mDbUtil;

    private List<GroceryList> mGroceryLists;
    ListsAdapter mListsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lists);
        ListView listView = (ListView) findViewById(R.id.lists);

        /** Database Interactions */
        mDbUtil = DBUtility.getInstance(this);
        mGroceryLists = mDbUtil.getLists();

        /** test of DB utility functions **/
        //testDB();

        /** Adapter */
        mListsAdapter = new ListsAdapter(this, R.layout.lists_item, mGroceryLists);
        listView.setAdapter(mListsAdapter);

        /** Touch Listener */
        ListsTouchListener touchListener = new ListsTouchListener(
                listView,
                new ListsTouchListener.DismissCallbacks() {
                    @Override
                    public void onDismiss(ListView listView, long viewId, int position) {
                        deleteList(listView, viewId, position);
                    }
                },
                new ListsTouchListener.ClickCallbacks() {
                    @Override
                    public void onClick(long viewId) {
                        goToList(viewId);
                    }
                });
        listView.setOnTouchListener(touchListener);
    }

    /*public void testDB(){
        //test of list
        mDbUtil.deleteAllLists();
        mDbUtil.deleteAllItems();
        mDbUtil.deleteAllListItems();

        mDbUtil.addList("list1");
        mDbUtil.addList("list2");
        mDbUtil.addList("list3");
        String testname = mDbUtil.getListbyName("list1").getName();

        // test of item
        mDbUtil.addItem("apple","fruit");
        mDbUtil.addItem("shirt","cloth");
        mDbUtil.addItem("fish", "food");
        mDbUtil.addItem("beef", "food");
        mDbUtil.addItem("banana", "fruit");
        String fishName = mDbUtil.getItembyName("fish").getName();
        List<Item> fruititems = mDbUtil.getItembyType("fruit");
        Set<String> types = mDbUtil.getAllItemTypes();

        // test of list_item

        //mDbUtil.deleteAllListItems();
        mDbUtil.addListItembyName("list1", "apple", 2, "NO");
        mDbUtil.addListItembyName("list1", "shirt", 1, "NO");
        mDbUtil.addListItembyName("list1", "beef", 2, "NO");

        List<Item> list1_items = mDbUtil.getListItemsByListName("list1");

        List<ListItem> listItems1= mDbUtil.getOrderedListItemsByListId(mDbUtil.getLisID("list1"));
        List<ListItem> listItems2= mDbUtil.getOrderedListItemsByListName("list1");

        ListItem listItems = listItems1.get(0);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        mGroceryLists.clear();
        mGroceryLists.addAll(mDbUtil.getLists());
        mListsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDbUtil != null) {
            mDbUtil.close();
        }
    }

    /** Handles action of user clicking on the Add-list button */
    void addListHandler(View view) {
        GroceryList newList = addNewList();
        mListsAdapter.add(newList);
        mListsAdapter.notifyDataSetChanged();
        goToList(newList.getListId());
    }

    void goToList(long listId) {
        Intent intent = new Intent(this, ViewListActivity.class);
        intent.putExtra("list_id", listId);
        startActivity(intent);
    }

    /* Database interactions */
    public GroceryList addNewList() {
        return mDbUtil.addList("New List");
    }

    public void deleteList(ListView listView, long listId, int position) {
        mDbUtil.deleteList(listId);
        if (position < mListsAdapter.getCount()) {
            mListsAdapter.remove(mListsAdapter.getItem(position));
            mListsAdapter.notifyDataSetChanged();
        }
    }

    /** When back-button is pressed from this activity, go to main listsView */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}