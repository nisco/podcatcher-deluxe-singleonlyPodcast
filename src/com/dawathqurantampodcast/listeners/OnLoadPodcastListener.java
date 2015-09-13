

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.tasks.remote.LoadPodcastTask.PodcastLoadError;
import com.dawathqurantampodcast.model.types.Podcast;
import com.dawathqurantampodcast.model.types.Progress;


/**
 * Interface definition for a callback to be invoked when a podcast is loaded.
 * Also provides some means to monitor the load progress.
 */
public interface OnLoadPodcastListener {

    /**
     * Called on progress update.
     * 
     * @param podcast Podcast loading.
     * @param progress Percent of podcast RSS file loaded or flag from
     *            {@link Progress}. Note that this only works if the http
     *            connection reports its content length correctly. Otherwise
     *            (and this happens in the wild out there) percent might be
     *            >100.
     */
    public void onPodcastLoadProgress(Podcast podcast, Progress progress);

    /**
     * Called on completion.
     * 
     * @param podcast Podcast loaded.
     */
    public void onPodcastLoaded(Podcast podcast);

    /**
     * Called when loading the podcast failed.
     * 
     * @param podcast Podcast failing to load.
     * @param code The reason for the failure.
     */
    public void onPodcastLoadFailed(Podcast podcast, PodcastLoadError code);
}
