

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Podcast;

/**
 * Interface definition for a callback to be invoked when a podcast is selected.
 */
public interface OnSelectPodcastListener {

    /**
     * Called on listener to reflect that a podcast has been selected.
     * 
     * @param selectedPodcast Podcast selected by the user (not
     *            <code>null</code>).
     */
    public void onPodcastSelected(Podcast selectedPodcast);

    /**
     * Called on listener to reflect that all podcasts are selected.
     */
    public void onAllPodcastsSelected();

    /**
     * Called on listener to reflect that downloads are selected.
     */
    public void onDownloadsSelected();

    /**
     * Called on listener to reflect that the playlist is selected.
     */
    public void onPlaylistSelected();

    /**
     * Called on listener to reflect that no podcast is selected anymore.
     */
    public void onNoPodcastSelected();
}
