

package com.dawathqurantampodcast.listeners;

/**
 * Interface definition for a callback to be invoked when the episode meta data
 * is written to disk.
 */
public interface OnStoreEpisodeMetadataListener {

    /**
     * Called on successful completion.
     */
    public void onEpisodeMetadataStored();

    /**
     * Called on failure.
     * 
     * @param exception The reason for the failure.
     */
    public void onEpisodeMetadataStoreFailed(Exception exception);
}
