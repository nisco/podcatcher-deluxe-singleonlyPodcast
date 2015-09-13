

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Episode;

/**
 * Interface definition for a listener to be alerted when the old/new state of
 * an episode changes.
 */
public interface OnChangeEpisodeStateListener {

    /**
     * Called on the listener when the state (old/new) of an episode is altered.
     * 
     * @param episode Episode the state was changed for.
     */
    public void onStateChanged(Episode episode);

}
