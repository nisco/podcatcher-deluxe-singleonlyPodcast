

package com.dawathqurantampodcast.adapters;

import android.content.Context;

import com.dawathqurantampodcast.model.types.Language;
import com.dawathqurantampodcast.view.fragments.SuggestionFragment;
import com.dawathqurantampodcast.R;


/**
 * Adapter for the language spinner in the suggestion dialog.
 */
public class LanguageSpinnerAdapter extends SuggestionFilterSpinnerAdapter {

    /**
     * Create new adapter.
     * 
     * @param context The current context.
     */
    public LanguageSpinnerAdapter(Context context) {
        super(context);

        // Put all languages into the value map where they are sorted by
        // language because we are using the corresponding resources as keys
        for (int index = 0; index < Language.values().length; index++) {
            final String key = resources.getStringArray(R.array.languages)[index];
            values.put(key, Language.values()[index]);
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
        return Language.values().length + 1;
    }
}
