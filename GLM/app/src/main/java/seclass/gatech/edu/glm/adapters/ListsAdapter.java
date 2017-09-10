package seclass.gatech.edu.glm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import seclass.gatech.edu.glm.models.GroceryList;
import seclass.gatech.edu.glm.R;

public class ListsAdapter extends ArrayAdapter {

    private List<GroceryList> mGroceryLists;

    public ListsAdapter(Context context, int resource, List<GroceryList> groceryLists) {
        super(context, resource, groceryLists);
        mGroceryLists = groceryLists;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.lists_item, null);
        }
        TextView tv = (TextView) v.findViewById(R.id.lists_item_tv);

        /** Associate name, id of groceryList on listView*/
        tv.setText(mGroceryLists.get(position).getName());
        tv.setTag(mGroceryLists.get(position).getListId());
        return v;
    }
}
