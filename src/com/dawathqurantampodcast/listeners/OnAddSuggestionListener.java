

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Suggestion;

/**
 * Interface definition for a callback to be invoked when a podcast suggestions
 * is added.
 */
public interface OnAddSuggestionListener {

    /**
     * Called on listener when podcast suggestion is selected.
     * 
     * @param suggestion Podcast to add.
     */
    public void onAddSuggestion(Suggestion suggestion);
}
