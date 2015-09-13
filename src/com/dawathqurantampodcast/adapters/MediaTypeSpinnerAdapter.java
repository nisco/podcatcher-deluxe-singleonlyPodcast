

package com.dawathqurantampodcast.adapters;

import android.content.Context;

import com.dawathqurantampodcast.model.types.MediaType;
import com.dawathqurantampodcast.view.fragments.SuggestionFragment;
import com.dawathqurantampodcast.R;


/**
 * Adapter for the media type spinner in the suggestion dialog.
 */
public class MediaTypeSpinnerAdapter extends SuggestionFilterSpinnerAdapter {

    /**
     * Create new adapter.
     * 
     * @param context The current context.
     */
    public MediaTypeSpinnerAdapter(Context context) {
        super(context);

        // Put all types into the value map where they are sorted by language
        // because we are using the corresponding resources as keys
        for (int index = 0; index < MediaType.values().length; index++) {
            final String key = resources.getStringArray(R.array.types)[index];
            values.put(key, MediaType.values()[index]);
        }
    }

    @Override
    public Object getItem(int position) {
        if (position == 0)
            return SuggestionFragment.FILTER_WILDCARD;
        else
            return values.values().toArray()[position - 1];
    }

    @Override
    public int getCount() {
        return MediaType.values().length + 1;
    }
}
