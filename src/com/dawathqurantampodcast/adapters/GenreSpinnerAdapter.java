

package com.dawathqurantampodcast.adapters;

import android.content.Context;

import com.dawathqurantampodcast.model.types.Genre;
import com.dawathqurantampodcast.view.fragments.SuggestionFragment;
import com.dawathqurantampodcast.R;


/**
 * Adapter for the genre spinner in the suggestion dialog.
 */
public class GenreSpinnerAdapter extends SuggestionFilterSpinnerAdapter {

    /**
     * Create new adapter.
     * 
     * @param context The current context.
     */
    public GenreSpinnerAdapter(Context context) {
        super(context);

        // Put all genres into the value map where they are sorted by language
        // because we are using the corresponding resources as keys
        for (int index = 0; index < Genre.values().length; index++) {
            final String key = resources.getStringArray(R.array.genres)[index];
            values.put(key, Genre.values()[index]);
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
        return Genre.values().length + 1;
    }
}
