package seclass.gatech.edu.glm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

import seclass.gatech.edu.glm.R;
import seclass.gatech.edu.glm.adapters.ItemArrayAdapter;
import seclass.gatech.edu.glm.models.DBUtility;
import seclass.gatech.edu.glm.models.Item;

public class DBLookupActivity extends AppCompatActivity {

    DBUtility mDbUtil;

    /**
     * The list we're adding to, and the current listItems in that list
     */
    private long mListId;
    private Set<Item> mCurrentListItems;

    private List<String> mItemTypes;

    private SearchView mSearchView;
    private Spinner mSpinner;
    private ArrayAdapter<String> mSpinnerAdapter;
    private ListView mFilteredList;
    private List<Item> mDisplayItems;
    private ItemArrayAdapter mFilteredAdapter;
    private Button mAddItemButton;
    private TextView mNoItemFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dblookup);

        mDbUtil = DBUtility.getInstance(this);

        /** Get all the items we currently already have in the list */
        mListId = getIntent().getLongExtra("listId", 0);
        mCurrentListItems = mDbUtil.getListItemsSet(mListId);

        /** TextView */
        mSearchView = (SearchView) findViewById(R.id.item_query);
        mSearchView.setOnQueryTextListener(queryListener);

        /** Spinner */
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mItemTypes = mDbUtil.getAllItemTypes();
        /* Add default value of empty */
        mItemTypes.add(0, "    Select a Category");
        mSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mItemTypes);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(itemSelectedListener);

        /** Filtered list */
        ListView filteredList = (ListView) findViewById(R.id.return_query_list);
        mFilteredList = (ListView) findViewById(R.id.return_query_list);
        mDisplayItems = mDbUtil.getItemsByPartialName("");
        mFilteredAdapter = new ItemArrayAdapter(this, R.layout.item, mDisplayItems, filteredListItemClickCallbacks);
        filteredList.setAdapter(mFilteredAdapter);

        /** Add Item Button */
        mAddItemButton = (Button) findViewById(R.id.add_item);

        /** No Item Found TextView */
        mNoItemFound = (TextView) findViewById(R.id.no_item_found);
    }

    /**
     * Callback function for when item-click event is triggered for available items displayed.
     * The user is attempting to add an item to their groceryList.
     */
    ItemArrayAdapter.ClickCallbacks filteredListItemClickCallbacks = new ItemArrayAdapter.ClickCallbacks() {
        @Override
        public void onClick(View view) {
            long itemId = (long) view.getTag();
            Item toBeAdded = mDbUtil.getItem(itemId);
            if (mCurrentListItems.contains(toBeAdded)) {
                Toast.makeText(DBLookupActivity.this, "Item Already in Grocery List!", Toast.LENGTH_SHORT).show();
            } else {
                mCurrentListItems.add(toBeAdded);
                mDbUtil.addListItem(mListId, itemId, 1, false);
                goToViewListActivity(mListId);
            }
        }
    };

    AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateUI();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    SearchView.OnQueryTextListener queryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
            updateUI();
            return false;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

    public void addNewItemDB(View view) {
        if (((String) mSpinner.getSelectedItem()).equals("    Select a Category")) {
            Toast.makeText(DBLookupActivity.this, "Please Select an Item Type", Toast.LENGTH_SHORT).show();
        } else if (mSearchView.getQuery().toString().equals("")) {
            Toast.makeText(DBLookupActivity.this, "Please Indicate a Valid Item Name", Toast.LENGTH_SHORT).show();
        } else if (mDisplayItems.contains(new Item(fixCase(mSearchView.getQuery().toString()), fixCase((String) mSpinner.getSelectedItem())))) {
            Toast.makeText(DBLookupActivity.this, "Item already exists!", Toast.LENGTH_SHORT).show();
        } else {
            mDbUtil.addItem(fixCase(mSearchView.getQuery().toString()), fixCase((String) mSpinner.getSelectedItem()));
            updateUI();
        }
    }

    void updateUI() {
        String itemType = (String) mSpinner.getSelectedItem();
        if (((String) mSpinner.getSelectedItem()).equals("    Select a Category")) {
            itemType = "";
        }
        List<Item> partialMatches = mDbUtil.getItemsByPartialNameAndType(fixCase(mSearchView.getQuery().toString()), fixCase(itemType));

        mFilteredAdapter.clear();
        mFilteredAdapter.addAll(partialMatches);

        if (partialMatches.size()==0) {
            mFilteredList.setVisibility(View.GONE);
            mNoItemFound.setVisibility(View.VISIBLE);
        } else {
            mFilteredList.setVisibility(View.VISIBLE);
            mNoItemFound.setVisibility(View.GONE);
        }

        mFilteredAdapter.notifyDataSetChanged();
    }

    void goToViewListActivity(long listId) {
        Intent intent = new Intent(this, ViewListActivity.class);
        intent.putExtra("list_id", listId);
        startActivity(intent);
    }

    private String fixCase(String s) {
        if (s.length()==0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
