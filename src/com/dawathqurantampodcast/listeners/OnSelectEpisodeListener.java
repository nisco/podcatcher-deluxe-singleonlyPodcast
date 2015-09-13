

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Episode;

/**
 * Interface definition for a callback to be invoked when an episode is
 * selected.
 */
public interface OnSelectEpisodeListener {

    /**
     * Called on listener to reflect that an episode has been selected.
     * 
     * @param selectedEpisode Episode selected by the user.
     */
    public void onEpisodeSelected(Episode selectedEpisode);

    /**
     * Called on listener to reflect that no episode is selected anymore.
     */
    public void onNoEpisodeSelected();
}
