

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Episode;

/**
 * Interface definition for a callback to be invoked when an episode is
 * reordered.
 */
public interface OnReorderEpisodeListener {

    /**
     * Called on listener to reflect that an episode should move up.
     * 
     * @param episode Episode moved by the user.
     */
    public void onMoveEpisodeUp(Episode episode);

    /**
     * Called on listener to reflect that an episode should move down.
     * 
     * @param episode Episode moved by the user.
     */
    public void onMoveEpisodeDown(Episode episode);
}
