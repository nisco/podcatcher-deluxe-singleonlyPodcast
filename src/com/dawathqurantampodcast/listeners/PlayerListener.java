

package com.dawathqurantampodcast.listeners;

import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Interface definition for a callback to be by the player fragment.
 */
public interface PlayerListener extends OnSeekBarChangeListener {

    /**
     * Load/unload of the current episode requested.
     */
    public void onToggleLoad();

    /**
     * Play/pause of the current episode requested.
     */
    public void onTogglePlay();

    /**
     * Rewind of the current episode requested.
     */
    public void onRewind();

    /**
     * Fast-forward of the current episode requested.
     */
    public void onFastForward();

    /**
     * Play next episode in playlist requested.
     */
    public void onNext();

    /**
     * Alert the listener that it should return to the currently played episode.
     */
    public void onReturnToPlayingEpisode();
}
