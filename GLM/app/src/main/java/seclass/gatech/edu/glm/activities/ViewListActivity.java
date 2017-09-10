package seclass.gatech.edu.glm.activities;


import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import seclass.gatech.edu.glm.adapters.ExpandableListAdapter;
import seclass.gatech.edu.glm.R;
import seclass.gatech.edu.glm.models.DBUtility;
import seclass.gatech.edu.glm.models.GroceryList;
import seclass.gatech.edu.glm.models.ListItem;

public class ViewListActivity extends Activity {

    DBUtility mDbUtil;

    ExpandableListAdapter mListAdapter;
    List<String> mItemTypes;
    HashMap<String, List<ListItem>> mListItems;

    private TextView mListNameView;
    private GroceryList mGroceryList;
    private ExpandableListView mExpListView;
    private Button mUncheckAllButton;

    private long mListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        mDbUtil = DBUtility.getInstance(this);

        Intent intent = getIntent();
        mListId = intent.getLongExtra("list_id", (long) -1.0);
        mGroceryList = mDbUtil.getList(mListId);

        /** Set Original Text, handle text changes in list_name */
        mListNameView = (EditText) findViewById(R.id.list_name);
        mListNameView.setText(mGroceryList.getName());
        mListNameView.addTextChangedListener(autoSaveTextWatcher);

        /** Set up and populate List (the ExpandableListView) */
        mExpListView = (ExpandableListView) findViewById(R.id.list);

        /** Adapter */
        mItemTypes = mDbUtil.getItemTypes(mListId);
        mListItems = mDbUtil.getListItems(mListId);
        mListAdapter = new ExpandableListAdapter(this, mItemTypes, mListItems);
        mExpListView.setAdapter(mListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mItemTypes.clear();
        mListItems.clear();
        mItemTypes.addAll(mDbUtil.getItemTypes(mListId));
        mListItems.putAll(mDbUtil.getListItems(mListId));
        mListAdapter.notifyDataSetChanged();
    }

    /** Go to DBLookupActivity to add listItem */
    public void addItemHandler(View view) {
        Intent intent = new Intent(ViewListActivity.this, DBLookupActivity.class);
        intent.putExtra("listId", mGroceryList.getListId());
        startActivity(intent);
    }

    /**
     * TextWatcher used to save listName immediately when user changes it
     */
    TextWatcher autoSaveTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mGroceryList.setName(s.toString());
            mDbUtil.renameList(mGroceryList.getListId(), s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void deleteListItem(long listItemId, int groupPos, int childPos) {
        Log.d("ViewListActivity", "ListItemId to delete: " + listItemId);
        mDbUtil.deleteListItem(listItemId);
        if ((groupPos < mListAdapter.getGroupCount()) && (childPos < mListAdapter.getChildrenCount(groupPos))) {
            mListAdapter.removeListItem(groupPos, childPos);
            if (mListItems.get(mItemTypes.get(groupPos)).size()==0) {
                mItemTypes.remove(groupPos);
            }
            mListAdapter.notifyDataSetChanged();
        }
    }

    public void setChecked(long listItemId, boolean checked) {
        mDbUtil.setItemChecked(listItemId, checked);
        Log.d("ViewListActivity", "setting listItem state to: " + checked);
    }

    public void setQuantity(long listItemId, float quantity) {
        mDbUtil.changeQuantity(listItemId, quantity);
    }

    public void uncheckAll(View view) {
        for (String itemType : mItemTypes) {
            for (ListItem listItem : mListItems.get(itemType)) {
                listItem.setChecked(false);
                setChecked(listItem.getListItemId(), false);
            }
        }
        mListAdapter.notifyDataSetChanged();
    }

    /** When back-button is pressed from this activity, go to main listsView */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intent = new Intent(this, ViewListsActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}


