

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Podcast;

import java.util.List;

/**
 * Interface definition for a callback to be invoked when a podcast list is
 * loaded.
 */
public interface OnLoadPodcastListListener {

    /**
     * Called on completion.
     * 
     * @param podcastList Podcast list loaded.
     */
    public void onPodcastListLoaded(List<Podcast> podcastList);
}
