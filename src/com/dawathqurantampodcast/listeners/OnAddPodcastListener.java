

package com.dawathqurantampodcast.listeners;

/**
 * Interface definition for a callback to be invoked when a podcast is added.
 */
public interface OnAddPodcastListener {

    /**
     * Called on listener when podcast url is given.
     * 
     * @param podcastUrl Podcast URL spec to add.
     */
    public void onAddPodcast(String podcastUrl);

    /**
     * Called on listener if the user wants to see suggestions for podcasts to
     * add.
     */
    public void onShowSuggestions();

    /**
     * Called on listener if the user wants to import an OPML file.
     */
    public void onImportOpml();
}
