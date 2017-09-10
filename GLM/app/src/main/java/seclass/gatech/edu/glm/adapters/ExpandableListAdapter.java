package seclass.gatech.edu.glm.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import seclass.gatech.edu.glm.R;
import seclass.gatech.edu.glm.activities.ViewListActivity;
import seclass.gatech.edu.glm.listeners.ListItemTouchListener;
import seclass.gatech.edu.glm.models.ListItem;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    Context mContext;
    List<String> mItemTypes;
    HashMap<String, List<ListItem>> mListItems;

    public ExpandableListAdapter(Context context, List<String> itemTypes, HashMap<String, List<ListItem>> listItems) {
        mContext = context;
        mItemTypes = itemTypes;
        mListItems = listItems;
    }

    @Override
    public int getGroupCount() {
        return mItemTypes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListItems.get(mItemTypes.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mItemTypes.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListItems.get(mItemTypes.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }
        TextView itemTypeView = (TextView) convertView.findViewById(R.id.item_type);
        itemTypeView.setText((String) getGroup(groupPosition));

        return convertView;
    }

    @Override
    /* TODO: Application lagging due to large workload on main thread (UI) - perhaps attach listeners, etc on another created thread? */
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        final ListItem listItem = (ListItem) getChild(groupPosition, childPosition);
        final long listItemId = ((ListItem) getChild(groupPosition, childPosition)).getListItemId();
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checked);
        final EditText quantityView = (EditText) convertView.findViewById(R.id.quantity);

        /******** Set Info ********/
        convertView.setTag(listItemId);
        TextView listItemTestView = (TextView) convertView.findViewById(R.id.list_item_name);
        listItemTestView.setText(listItem.getName());
        checkBox.setChecked(listItem.isChecked());
        quantityView.setText("" + listItem.getQuantity());

        /******** Add listeners ********/
        /** CheckBox Click Listener */
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItem.setChecked(checkBox.isChecked());
                ((ViewListActivity) mContext).setChecked(listItemId, checkBox.isChecked());
            }
        });

        /** Touch Listener for swiping/dismissing listItems */
        ListItemTouchListener touchListener = new ListItemTouchListener(
                convertView,
                groupPosition,
                childPosition,
                new ListItemTouchListener.DismissCallbacks() {
                    @Override
                    public void onDismiss(long listItemId, int groupPos, int childPos) {
                        ((ViewListActivity) mContext).deleteListItem(listItemId, groupPos, childPos);
                    }
                });
        convertView.setOnTouchListener(touchListener);

        /** TextWatcher used to save quantity immediately when user changes it */
        quantityView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float newQuantity = !s.toString().equals("") ? Float.parseFloat(s.toString()) : 0;
                listItem.setQuantity(newQuantity);
                ((ViewListActivity) mContext).setQuantity(listItemId, newQuantity);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void removeListItem(int groupPos, int childPos) {
        String itemType = (String) getGroup(groupPos);
        if (childPos < mListItems.get(itemType).size()) {
            mListItems.get(itemType).remove(childPos);
        }
    }
}
