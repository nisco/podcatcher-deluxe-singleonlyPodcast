

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Episode;

import java.util.List;

/**
 * Interface definition for a callback to be invoked when the list of downloads
 * is available.
 */
public interface OnLoadDownloadsListener {

    /**
     * Called on listener when the list of downloads is available.
     * 
     * @param downloads The actual list.
     */
    public void onDownloadsLoaded(List<Episode> downloads);
}
