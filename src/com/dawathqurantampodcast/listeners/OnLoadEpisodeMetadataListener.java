

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.EpisodeMetadata;

import java.net.URL;
import java.util.Map;

/**
 * Interface definition for a callback to be invoked when the episode metadata
 * is loaded.
 */
public interface OnLoadEpisodeMetadataListener {

    /**
     * Called on completion.
     * 
     * @param metadata Episode metadata loaded.
     */
    public void onEpisodeMetadataLoaded(Map<String, EpisodeMetadata> metadata);

}
