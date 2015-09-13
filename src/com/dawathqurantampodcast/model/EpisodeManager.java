

package com.dawathqurantampodcast.model;

import com.dawathqurantampodcast.Podcatcher;

/**
 * Manager to handle episode specific activities. This is the bottom end of the
 * episode manager stack and finally provides access to the singleton object of
 * the manager.
 * 
 * @see EpisodeBaseManager
 * @see EpisodeDownloadManager
 * @see EpisodePlaylistManager
 * @see EpisodeStateManager
 */
public class EpisodeManager extends EpisodeStateManager {

    /** The single instance */
    private static EpisodeManager manager;

    /**
     * Init the episode manager.
     * 
     * @param app The podcatcher application object (also a singleton).
     */
    private EpisodeManager(Podcatcher app) {
        super(app);
    }

    /**
     * Get the singleton instance of the episode manager.
     * 
     * @param podcatcher Application handle.
     * @return The singleton instance.
     */
    public static EpisodeManager getInstance(Podcatcher podcatcher) {
        // If not done, create single instance
        if (manager == null)
            manager = new EpisodeManager(podcatcher);

        return manager;
    }

    /**
     * Get the singleton instance of the podcast manager.
     * 
     * @return The singleton instance.
     */
    public static EpisodeManager getInstance() {
        // In Application.onCreate() we make sure that this method is not called
        // unless the other one (with the application instance actually set) ran
        // to least once
        return manager;
    }
}
