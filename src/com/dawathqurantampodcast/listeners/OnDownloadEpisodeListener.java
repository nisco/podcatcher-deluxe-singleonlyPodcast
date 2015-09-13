

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Episode;

/**
 * Interface for the controller to implement when the user requests an episode
 * to be downloaded locally.
 */
public interface OnDownloadEpisodeListener {

    /**
     * Start/stop the download for the current episode.
     */
    public void onToggleDownload();

    /**
     * Called on the listener to alert it about a download progress update.
     * 
     * @param episode The episode the progress was made for.
     * @param percent The percentage of episode downloaded [0..100].
     */
    public void onDownloadProgress(Episode episode, int percent);

    /**
     * Called on the listener once a download finished successfully.
     * 
     * @param episode The episode now available offline.
     */
    public void onDownloadSuccess(Episode episode);

    /**
     * Called on the listener if a download failed.
     * 
     * @param episode The episode that failed to download.
     */
    public void onDownloadFailed(Episode episode);

    /**
     * Called on the listener if a download is removed.
     * 
     * @param episode The episode the local copy was delete of.
     */
    public void onDownloadDeleted(Episode episode);
}
