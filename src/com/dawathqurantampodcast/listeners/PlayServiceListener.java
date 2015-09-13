

package com.dawathqurantampodcast.listeners;

import com.dawathqurantampodcast.services.PlayEpisodeService;

/**
 * Listener interface to implement if you are interested to be alerted by the
 * play episode service on a couple of important events.
 */
public interface PlayServiceListener {

    /**
     * Called by the service on the listener if an episode is loaded and ready
     * to play (the service might in fact already have started playback...)
     */
    public void onPlaybackStarted();

    /**
     * Called by the service on the listener if the state of the service
     * (playing/paused) is changed externally, e.g. via the headsets media
     * buttons.
     */
    public void onPlaybackStateChanged();

    /**
     * Called by the service on the listener if an episode is temporarily
     * stopped for filling the media player's buffer.
     */
    public void onStopForBuffering();

    /**
     * Called by the service on the listener if an episode was temporarily
     * stopped for filling the media player's buffer and now resumes.
     */
    public void onResumeFromBuffering();

    /**
     * Called by the service on the listener if the media player buffer state
     * changed.
     * 
     * @param seconds Seconds from the media start currently buffered.
     */
    public void onBufferUpdate(int seconds);

    /**
     * Called by the service on the listener if an episode finished playing. The
     * service does not free resources on completion automatically, you might
     * want to call {@link PlayEpisodeService#reset()}.
     */
    public void onPlaybackComplete();

    /**
     * Called by the service on the listener if an episode fails to play or any
     * other error occurs.
     */
    public void onError();
}
