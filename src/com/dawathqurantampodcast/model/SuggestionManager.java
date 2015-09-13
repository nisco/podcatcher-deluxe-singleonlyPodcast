

package com.dawathqurantampodcast.model;

import com.dawathqurantampodcast.Podcatcher;
import com.dawathqurantampodcast.listeners.OnLoadSuggestionListener;
import com.dawathqurantampodcast.model.tasks.remote.LoadSuggestionsTask;
import com.dawathqurantampodcast.model.types.Progress;
import com.dawathqurantampodcast.model.types.Suggestion;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The podcast suggestions manager, persistent and global singleton.
 */
public class SuggestionManager implements OnLoadSuggestionListener {

    /** The single instance */
    private static SuggestionManager manager;
    /** The application itself */
    private Podcatcher podcatcher;

    /** The list of podcast suggestions */
    private List<Suggestion> podcastSuggestions;

    /** The suggestions load task */
    private LoadSuggestionsTask loadTask;

    /** The call-back set for the suggestion list load listeners */
    private Set<OnLoadSuggestionListener> loadSuggestionListListeners = new HashSet<OnLoadSuggestionListener>();

    /**
     * Init the suggestion manager.
     * 
     * @param app The podcatcher application object (also a singleton).
     */
    private SuggestionManager(Podcatcher app) {
        this.podcatcher = app;
    }

    /**
     * Get the suggestion manager instance, which grants access to the global
     * suggestion data model. The returned manager object is a singleton, all
     * calls to this method will always return the same single instance of the
     * suggestion manager.
     * 
     * @param podcatcher The main app object.
     * @return The suggestion manager handle.
     */
    public static SuggestionManager getInstance(Podcatcher podcatcher) {
        if (manager == null)
            manager = new SuggestionManager(podcatcher);

        return manager;
    }

    /**
     * Get the suggestion manager instance, which grants access to the global
     * suggestion data model. The returned manager object is a singleton, all
     * calls to this method will always return the same single instance of the
     * suggestion manager.
     * 
     * @return The suggestion manager handle.
     */
    public static SuggestionManager getInstance() {
        // We make sure in Application.onCreate() that this method is not called
        // unless the other one with the application instance actually set ran
        // to least once
        return manager;
    }

    /**
     * Register a listener call-back.
     * 
     * @param listener Call-back to alert.
     */
    public void addLoadSuggestionListListener(OnLoadSuggestionListener listener) {
        loadSuggestionListListeners.add(listener);
    }

    /**
     * Unregister a listener call-back.
     * 
     * @param listener Call-back to remove.
     */
    public void removeLoadSuggestionListListener(OnLoadSuggestionListener listener) {
        loadSuggestionListListeners.remove(listener);
    }

    /**
     * Load the suggestions over the wire/air. After the first successful load
     * this will always return the cached result (via call-backs) unless you
     * restart the whole app.
     */
    public void load() {
        // Suggestion have not been loaded before (and are not currently
        // loading)
        if (podcastSuggestions == null && loadTask == null) {
            loadTask = new LoadSuggestionsTask(podcatcher, this);
            loadTask.execute((Void) null);
        } // Suggestions already present
        else if (podcastSuggestions != null)
            onSuggestionsLoaded(podcastSuggestions);
    }

    @Override
    public void onSuggestionsLoadProgress(Progress progress) {
        for (OnLoadSuggestionListener listener : loadSuggestionListListeners)
            listener.onSuggestionsLoadProgress(progress);
    }

    @Override
    public void onSuggestionsLoaded(List<Suggestion> suggestions) {
        // Cache the load result
        this.podcastSuggestions = suggestions;
        // Reset task
        this.loadTask = null;

        for (OnLoadSuggestionListener listener : loadSuggestionListListeners)
            listener.onSuggestionsLoaded(suggestions);
    }

    @Override
    public void onSuggestionsLoadFailed() {
        // Reset task
        this.loadTask = null;

        for (OnLoadSuggestionListener listener : loadSuggestionListListeners)
            listener.onSuggestionsLoadFailed();
    }
}
