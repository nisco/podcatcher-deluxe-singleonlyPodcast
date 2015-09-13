

package com.dawathqurantampodcast.listeners;

/**
 * Interface for the controller to implement when the user requests an episode
 * filter toggle.
 */
public interface OnToggleFilterListener {

    /**
     * Called on the listener if the filter state toggles.
     */
    public void onToggleFilter();
}
