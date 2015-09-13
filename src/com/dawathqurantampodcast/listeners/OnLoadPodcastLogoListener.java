

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Podcast;

/**
 * Interface definition for a callback to be invoked when a podcast logo is
 * loaded.
 */
public interface OnLoadPodcastLogoListener {

    /**
     * Called on completion.
     * 
     * @param podcast The podcast we are loading the logo for.
     */
    public void onPodcastLogoLoaded(Podcast podcast);

    /**
     * Called when loading the podcast logo failed.
     * 
     * @param podcast Podcast logo could not be loaded for.
     */
    public void onPodcastLogoLoadFailed(Podcast podcast);
}
