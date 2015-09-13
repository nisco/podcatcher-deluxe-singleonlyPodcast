

package com.dawathqurantampodcast;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.util.Log;

import com.dawathqurantampodcast.model.EpisodeManager;
import com.dawathqurantampodcast.model.PodcastManager;
import com.dawathqurantampodcast.model.SuggestionManager;
import com.dawathqurantampodcast.model.tasks.LoadEpisodeMetadataTask;
import com.dawathqurantampodcast.model.tasks.LoadPodcastListTask;


import java.io.File;
import java.io.IOException;

/**
 * Our application subclass. Holds global state and model. The Podcatcher
 * application object is created on application startup and will be alive for
 * all the app's lifetime. Its main purpose is to hold handles to the singleton
 * instances of our model data and data managers. In addition, it provides some
 * generic convenience methods.
 */
public class Podcatcher extends Application {

    /**
     * The amount of dp establishing the border between small and large screen
     * buckets
     */
    public static final int MIN_PIXEL_LARGE = 600;

    /** The http request header field key for the user agent */
    public static final String USER_AGENT_KEY = "User-Agent";
    /** The user agent string we use to identify us */
    public static final String USER_AGENT_VALUE = "Podcatcher Deluxe";
    /** The http request header field key for the authorization */
    public static final String AUTHORIZATION_KEY = "Authorization";

    /** The HTTP cache size */
    public static final long HTTP_CACHE_SIZE = 8 * 1024 * 1024; // 8 MiB

    /** Thread to move the http cache flushing off the UI thread */
    private static class FlushCacheThread extends Thread {

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            HttpResponseCache cache = HttpResponseCache.getInstalled();
            if (cache != null)
                cache.flush();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // This will only run once in the lifetime of the app
        // since the application is an implicit singleton. We create the other
        // singletons here to make sure they know their application instance.
        PodcastManager.getInstance(this);
        // And this one as well
        EpisodeManager.getInstance(this);
        // dito
        SuggestionManager.getInstance(this);

        // Enabled caching for our HTTP connections
        try {
            File httpCacheDir = new File(getCacheDir(), "http");
            HttpResponseCache.install(httpCacheDir, HTTP_CACHE_SIZE);
        } catch (IOException ioe) {
            Log.w(getClass().getSimpleName(), "HTTP response cache installation failed:" + ioe);
        }

        // Now we will trigger the preparation on start-up, steps include:
        // 1. Load podcast list from file async, once this is finished the
        // podcast manager is alerted and in turn tells the controller activity.
        // Then the UI can show the list and we are ready to go
        new LoadPodcastListTask(this, PodcastManager.getInstance())
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        // 2. At the same time we load episode metadata from file async (this
        // has the potential to take a lot of time, since the amount of data
        // might be quite big). The UI is functional without this having
        // completed, but loading of podcasts, downloads or the playlist will
        // block until the data is available.
        new LoadEpisodeMetadataTask(this, EpisodeManager.getInstance())
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
    }

    /**
     * Write http cache data to disk (async).
     */
    public void flushHttpCache() {
        new FlushCacheThread().start();
    }

    /**
     * Checks whether the device is currently online and can receive data from
     * the internets.
     * 
     * @return <code>true</code> iff we have Internet access.
     */
    public boolean isOnline() {
        final NetworkInfo activeNetwork = getNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Checks whether the device is currently on a fast network (such as wifi)
     * as opposed to a mobile network.
     * 
     * @return <code>true</code> iff we have fast (and potentially free)
     *         Internet access.
     */
    public boolean isOnFastConnection() {
        final NetworkInfo activeNetwork = getNetworkInfo();

        if (activeNetwork == null)
            return false;
        else
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_ETHERNET:
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_WIMAX:
                    return true;
                default:
                    return false;
            }
    }

    /**
     * Checks whether the app is in debug mode.
     * 
     * @return <code>true</code> iff in debug.
     */
    public boolean isInDebugMode() {
        boolean debug = false;

        PackageManager manager = getApplicationContext().getPackageManager();
        try
        {
            ApplicationInfo info = manager.getApplicationInfo(
                    getApplicationContext().getPackageName(), 0);
            debug = (0 != (info.flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (Exception e) {
            // pass
        }

        return debug;
    }

    private NetworkInfo getNetworkInfo() {
        ConnectivityManager manager =
                (ConnectivityManager) getApplicationContext()
                        .getSystemService(CONNECTIVITY_SERVICE);

        return manager.getActiveNetworkInfo();
    }
}
