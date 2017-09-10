package seclass.gatech.edu.glm.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

import seclass.gatech.edu.glm.R;
import seclass.gatech.edu.glm.models.Item;

public class ItemArrayAdapter extends ArrayAdapter {

    private List<Item> mItems;
    ClickCallbacks mClickCallBacks;

    public ItemArrayAdapter(Context context, int resource, List<Item> items, ClickCallbacks callBacks) {
        super(context, resource, items);
        mItems = items;
        mClickCallBacks = callBacks;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        v = super.getView(position, v, parent);
        v.setTag(mItems.get(position).getItemId());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ItemArrayAdapter", "Click detected");
                mClickCallBacks.onClick(v);
            }
        });
        return v;
    }

    public interface ClickCallbacks {
        void onClick(View view);
    }
}
