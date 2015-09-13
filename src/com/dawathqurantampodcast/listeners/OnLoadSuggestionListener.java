

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Progress;
import com.dawathqurantampodcast.model.types.Suggestion;

import java.util.List;

/**
 * Interface definition for a callback to be invoked when podcast suggestions
 * are loaded.
 */
public interface OnLoadSuggestionListener {

    /**
     * Called on progress update.
     * 
     * @param progress Progress of suggestions JSON file loaded or flag from
     *            <code>Progress</code>. Note that this only works if the http
     *            connection reports its content length correctly. Otherwise
     *            (and this happens in the wild out there) percent might be
     *            >100.
     */
    public void onSuggestionsLoadProgress(Progress progress);

    /**
     * Called on completion.
     * 
     * @param suggestions Podcast suggestions loaded.
     */
    public void onSuggestionsLoaded(List<Suggestion> suggestions);

    /**
     * Called when loading the suggestions failed.
     */
    public void onSuggestionsLoadFailed();
}
