

package com.dawathqurantampodcast.model.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dawathqurantampodcast.listeners.OnLoadPlaylistListener;
import com.dawathqurantampodcast.model.EpisodeManager;
import com.dawathqurantampodcast.model.types.Episode;


import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Get the playlist from the episode manager.
 */
public class LoadPlaylistTask extends AsyncTask<Void, Void, List<Episode>> {

    /** Call back */
    private WeakReference<OnLoadPlaylistListener> listener;

    /**
     * Create new task.
     * 
     * @param listener Callback to be alerted on completion. The listener is
     *            held as a weak reference, so you can safely call this from an
     *            activity without leaking it.
     */
    public LoadPlaylistTask(OnLoadPlaylistListener listener) {
        this.listener = new WeakReference<OnLoadPlaylistListener>(listener);
    }

    @Override
    protected List<Episode> doInBackground(Void... nothing) {
        try {
            // Block if episode metadata not yet available
            EpisodeManager.getInstance().blockUntilEpisodeMetadataIsLoaded();
            // Get the playlist
            return EpisodeManager.getInstance().getPlaylist();
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), "Load failed for playlist", e);

            cancel(true);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Episode> playlist) {
        // Playlist available
        final OnLoadPlaylistListener listener = this.listener.get();

        if (listener != null)
            listener.onPlaylistLoaded(playlist);
        else
            Log.w(getClass().getSimpleName(), "Playlist loaded, but no listener attached");
    }
}
