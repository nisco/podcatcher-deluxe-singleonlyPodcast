

package com.dawathqurantampodcast.model.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.dawathqurantampodcast.listeners.OnLoadDownloadsListener;
import com.dawathqurantampodcast.model.EpisodeManager;
import com.dawathqurantampodcast.model.types.Episode;


import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Get the list of downloads from the episode manager.
 */
public class LoadDownloadsTask extends AsyncTask<Void, Void, List<Episode>> {

    /** Call back */
    private WeakReference<OnLoadDownloadsListener> listener;

    /**
     * Create new task.
     * 
     * @param listener Callback to be alerted on completion. The listener is
     *            held as a weak reference, so you can safely call this from an
     *            activity without leaking it.
     */
    public LoadDownloadsTask(OnLoadDownloadsListener listener) {
        this.listener = new WeakReference<OnLoadDownloadsListener>(listener);
    }

    @Override
    protected List<Episode> doInBackground(Void... nothing) {
        try {
            // Block if episode metadata not yet available
            EpisodeManager.getInstance().blockUntilEpisodeMetadataIsLoaded();
            // Get the list of downloads
            return EpisodeManager.getInstance().getDownloads();
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), "Load failed for download list", e);

            cancel(true);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Episode> downloads) {
        // List of downloads available
        final OnLoadDownloadsListener listener = this.listener.get();

        if (listener != null)
            listener.onDownloadsLoaded(downloads);
        else
            Log.w(getClass().getSimpleName(),
                    "List of downloads available loaded, but no listener attached");
    }
}
