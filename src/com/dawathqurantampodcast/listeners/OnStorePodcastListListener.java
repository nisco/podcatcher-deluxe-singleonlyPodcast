

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.model.types.Podcast;

import java.io.File;
import java.util.List;

/**
 * Interface definition for a callback to be invoked when a podcast list is
 * written to disk.
 */
public interface OnStorePodcastListListener {

    /**
     * Called on successful completion.
     * 
     * @param podcastList Podcast list stored.
     * @param outputFile The file the list was written to.
     */
    public void onPodcastListStored(List<Podcast> podcastList, File outputFile);

    /**
     * Called on failure.
     * 
     * @param podcastList Podcast list attempted to store.
     * @param outputFile The file the list was not written to.
     * @param exception The reason for the failure.
     */
    public void onPodcastListStoreFailed(List<Podcast> podcastList, File outputFile,
            Exception exception);
}
