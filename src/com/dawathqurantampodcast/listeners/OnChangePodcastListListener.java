

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Podcast;

/**
 * Interface definition for a callback to be invoked when the podcast list is
 * changed, i.e. podcasts are added or removed.
 */
public interface OnChangePodcastListListener {

    /**
     * Called when the podcast manager adds a new podcast.
     * 
     * @param podcast Podcast added to list.
     */
    public void onPodcastAdded(Podcast podcast);

    /**
     * Called on listener when the podcast manager removed a podcast from its
     * list.
     * 
     * @param podcast Podcast being removed.
     */
    public void onPodcastRemoved(Podcast podcast);
}
