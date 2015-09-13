

package com.dawathqurantampodcast.listeners;

/**
 * Interface for the controller to implement when the user requests an episode
 * list re-ordering.
 */
public interface OnReverseSortingListener {

    /**
     * Called on the listener if the sorting state toggles.
     */
    public void onReverseOrder();
}
