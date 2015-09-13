

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Episode;

import java.util.List;

/**
 * Interface definition for a callback to be invoked when the playlist is
 * available.
 */
public interface OnLoadPlaylistListener {

    /**
     * Called on listener when the playlist is available.
     * 
     * @param playlist The actual list.
     */
    public void onPlaylistLoaded(List<Episode> playlist);
}
