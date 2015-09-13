

package com.dawathqurantampodcast.model.tasks.remote;

import android.util.Log;

import com.dawathqurantampodcast.listeners.OnLoadPodcastListener;
import com.dawathqurantampodcast.model.EpisodeManager;
import com.dawathqurantampodcast.model.types.Podcast;
import com.dawathqurantampodcast.model.types.Progress;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Loads a podcast's RSS file from the server and parses its contents
 * asynchronously.
 * <p>
 * <b>Usage:</b> Implement the {@link OnLoadPodcastListener} interface and give
 * it to the task's constructor to be alerted on completion, progress, or
 * failure. The downloaded file will be used as the podcast's content via
 * {@link Podcast#parse(XmlPullParser)}, use the podcast object given (and
 * returned via callbacks) to access it.
 * </p>
 * <p>
 * <b>Authorization:</b> The task will send the credentials returned by
 * {@link Podcast#getAuthorization()} when requesting the file from the server.
 * If not present or wrong, the task fails and
 * {@link OnLoadPodcastListener#onPodcastLoadFailed(Podcast, PodcastLoadError)}
 * will be called with the code set to {@link PodcastLoadError#AUTH_REQUIRED}.
 * </p>
 */
public class LoadPodcastTask extends LoadRemoteFileTask<Podcast, Void> {

    /** Maximum byte size for the RSS file to load */
    public static final int MAX_RSS_FILE_SIZE = 2000000;

    /**
     * Podcast load error codes as returned by
     * {@link OnLoadPodcastListener#onPodcastLoadFailed(Podcast, PodcastLoadError)}
     * .
     */
    public static enum PodcastLoadError {
        /**
         * An error occurred, but the reason is unknown and/or does not fit any
         * of the other codes.
         */
        UNKNOWN,

        /**
         * Authorization is required.
         */
        AUTH_REQUIRED,

        /**
         * The authorization failed.
         */
        ACCESS_DENIED,

        /**
         * The restricted profile blocks explicit podcasts
         */
        EXPLICIT_BLOCKED,

        /**
         * The remote server could not be reached.
         */
        NOT_REACHABLE,

        /**
         * The URL does not point at a valid feed file
         */
        NOT_PARSEABLE
    }

    /** Call back */
    private OnLoadPodcastListener listener;

    /** Podcast currently loading */
    private Podcast podcast;
    /** The error code */
    private PodcastLoadError errorCode = PodcastLoadError.UNKNOWN;

    /** Flag whether we strip out explicit episodes */
    private boolean blockExplicit = false;

    /**
     * Create new task.
     * 
     * @param listener Callback to be alerted on progress and completion.
     */
    public LoadPodcastTask(OnLoadPodcastListener listener) {
        this.listener = listener;
        // We disable the load limit for the podcast feeds because there are
        // huge feeds out there and user's really do not understand why they are
        // unable to access them.
        // this.loadLimit = MAX_RSS_FILE_SIZE;
    }

    /**
     * @param block Whether the task should block explicit episodes from showing
     *            up in the episode list of the loaded podcast. If set and the
     *            episode list collapses to zero episodes, the task will fail
     *            with {@link PodcastLoadError#EXPLICIT_BLOCKED}.
     */
    public void setBlockExplicitEpisodes(boolean block) {
        this.blockExplicit = block;
    }

    @Override
    protected Void doInBackground(Podcast... podcasts) {
        this.podcast = podcasts[0];

        try {
            // 1. Load the file from the Internet
            publishProgress(Progress.CONNECT);

            // Set auth
            this.authorization = podcast.getAuthorization();
            // ... and go get the file
            byte[] podcastRssFile = loadFile(new URL(podcast.getUrl()));

            if (isCancelled())
                return null;
            else
                publishProgress(Progress.PARSE);

            // 2. Create the parser to use
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new ByteArrayInputStream(podcastRssFile), null);

            // 3. Parse as podcast content
            if (!isCancelled())
                podcast.parse(parser);

            // 4. Clean out explicit episodes
            if (!isCancelled() && blockExplicit) {
                final int episodeCount = podcast.getEpisodeCount();
                final int cleanEpisodeCount = podcast.removeExplicitEpisodes();

                if (cleanEpisodeCount == 0 && episodeCount > 0) {
                    errorCode = PodcastLoadError.EXPLICIT_BLOCKED;

                    cancel(true);
                }
            }

            // 5. We need to wait here and make sure the episode metadata is
            // available before we return
            EpisodeManager.getInstance().blockUntilEpisodeMetadataIsLoaded();
        } catch (XmlPullParserException xppe) {
            errorCode = PodcastLoadError.NOT_PARSEABLE;

            cancel(true);
        } catch (IOException ioe) {
            // This is also catch mal-formed URLs
            errorCode = PodcastLoadError.NOT_REACHABLE;

            cancel(true);
        } catch (Throwable t) {
            Log.w(getClass().getSimpleName(), "Load failed for podcast \"" + podcast + "\"", t);

            cancel(true);
        } finally {
            publishProgress(Progress.DONE);
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Progress... progress) {
        if (listener != null)
            listener.onPodcastLoadProgress(podcast, progress[0]);
        else if (listener == null)
            Log.w(getClass().getSimpleName(), "Podcast progress update, but no listener attached");
    }

    @Override
    protected void onPostExecute(Void nothing) {
        // Podcast was loaded
        if (listener != null)
            listener.onPodcastLoaded(podcast);
        else
            Log.w(getClass().getSimpleName(), "Podcast loaded, but no listener attached");
    }

    @Override
    protected void onCancelled(Void nothing) {
        // Background task failed to complete
        if (listener != null)
            if (needsAuthorization)
                listener.onPodcastLoadFailed(podcast, PodcastLoadError.AUTH_REQUIRED);
            else
                listener.onPodcastLoadFailed(podcast, errorCode);
        else
            Log.w(getClass().getSimpleName(), "Podcast failed to load, but no listener attached");
    }
}
