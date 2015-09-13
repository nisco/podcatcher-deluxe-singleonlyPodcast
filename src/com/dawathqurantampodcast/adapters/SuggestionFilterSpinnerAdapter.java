

package com.dawathqurantampodcast.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dawathqurantampodcast.R;

import java.util.TreeMap;

/**
 * Abstract base for suggestion filter spinner adapters. This default
 * implementation will use the tree map {@link #values} to label the returned
 * list and spinner views.
 */
public abstract class SuggestionFilterSpinnerAdapter extends PodcatcherBaseAdapter {

    /**
     * The sorted map to store our values in, this is needed to account for the
     * sorting in different languages.
     */
    protected final TreeMap<String, Object> values = new TreeMap<String, Object>();

    /**
     * Create the adapter.
     * 
     * @param context Context we live in.
     */
    public SuggestionFilterSpinnerAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getLabel(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getLabel(position, convertView, parent, true);
    }

    private View getLabel(int position, View convertView, ViewGroup parent, boolean dropDown) {
        TextView result = null;

        // Get the correct return view
        if (dropDown)
            result = (TextView) findReturnView(convertView, parent,
                    android.R.layout.simple_spinner_dropdown_item);
        else
            result = (TextView) findReturnView(convertView, parent,
                    android.R.layout.simple_spinner_item);

        // Apply the appropriate text label
        if (position == 0)
            result.setText(resources.getString(R.string.wildcard));
        else
            result.setText((String) values.keySet().toArray()[position - 1]);

        return result;
    }

    @Override
    public long getItemId(int position) {
        // Since there are only enums behind this, it is actually okay to simply
        // return the position...
        return position;
    }
}
