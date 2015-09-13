

package com.dawathqurantampodcast.model.tasks.remote;

import static android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR;
import static android.app.DownloadManager.COLUMN_LOCAL_FILENAME;
import static android.app.DownloadManager.COLUMN_STATUS;
import static android.app.DownloadManager.COLUMN_TOTAL_SIZE_BYTES;
import static android.app.DownloadManager.STATUS_FAILED;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;
import static com.dawathqurantampodcast.Podcatcher.AUTHORIZATION_KEY;
import static com.dawathqurantampodcast.Podcatcher.USER_AGENT_KEY;
import static com.dawathqurantampodcast.Podcatcher.USER_AGENT_VALUE;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.dawathqurantampodcast.Podcatcher;
import com.dawathqurantampodcast.SettingsActivity;
import com.dawathqurantampodcast.model.EpisodeDownloadManager;
import com.dawathqurantampodcast.model.types.Episode;
import com.dawathqurantampodcast.preferences.DownloadFolderPreference;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Async task that triggers the download of an episode. The task will be alive
 * and busy in its doInBackground() method as long as the download takes. It
 * will publish updates of the download's progress to the call-back attached.
 * Use a new task for each episode you want to download. Make sure not to give
 * <code>null</code> as an episode to the {@link #doInBackground(Episode...)}
 * method or things will break.
 */
public class DownloadEpisodeTask extends AsyncTask<Episode, Long, Void> {

    /**
     * The amount of time we wait (in ms) before checking on the download's
     * status again.
     */
    private static final long DOWNLOAD_STATUS_POLL_INTERVALL = 1000;

    /** The podcatcher app handle */
    private Podcatcher podcatcher;
    /** The listener (episode manager) we report to */
    private DownloadTaskListener listener;
    /** The system download manager */
    private DownloadManager downloadManager;

    /** The episode we are downloading */
    private Episode episode;
    /** The file the episode is downloaded to */
    private File episodeFile;
    /** The current percentage state of the download [0...100] */
    private int percentProgress;
    /** Flag on whether the download needs moving after the fact */
    private boolean needsPostDownloadMove = false;

    /** The interface to implement by the call-back for this task */
    public interface DownloadTaskListener {

        /**
         * Called on the listener when the episode is enqueued.
         * 
         * @param episode The episode now downloading.
         * @param id The download manager id for the download.
         */
        public void onEpisodeEnqueued(Episode episode, long id);

        /**
         * Called on the listener when the progress of the download for the
         * episode advanced.
         * 
         * @param episode The episode downloading.
         * @param percent The percent value currently downloaded [0...100].
         */
        public void onEpisodeDownloadProgressed(Episode episode, int percent);

        /**
         * Called on the listener if the episode requested to be downloaded is
         * already available on the device's storage.
         * 
         * @param episode The episode the task was started for.
         * @param episodeFile The local file.
         */
        public void onEpisodeDownloaded(Episode episode, File episodeFile);

        /**
         * Called on the listener when the download for the episode fails for
         * some reason.
         * 
         * @param episode The episode the download failed for.
         */
        public void onEpisodeDownloadFailed(Episode episode);
    }

    /**
     * Create a new task.
     * 
     * @param podcatcher The podcatcher app handle.
     * @param listener The call-back used by the task.
     */
    public DownloadEpisodeTask(Podcatcher podcatcher, DownloadTaskListener listener) {
        this.podcatcher = podcatcher;
        this.listener = listener;

        // Get handle to the system download manager which does all the
        // downloading for us
        downloadManager = (DownloadManager)
                podcatcher.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    protected Void doInBackground(Episode... params) {
        this.episode = params[0];

        // Find the podcast directory and the path to store episode under
        final File podcastDir = new File(PreferenceManager.getDefaultSharedPreferences(podcatcher)
                .getString(SettingsActivity.DOWNLOAD_FOLDER_KEY,
                        DownloadFolderPreference.getDefaultDownloadFolder().getAbsolutePath()));
        final String subPath = EpisodeDownloadManager.sanitizeAsFilePath(
                episode.getMediaUrl(), episode.getName(), episode.getPodcast().getName());
        // The actual episode file
        final File localFile = new File(podcastDir, subPath);

        // The episode is already there, alert listener
        if (localFile.exists()) {
            // Wait one round in order to give the "download started" animation
            // time to complete. Otherwise the UI will show artifacts because we
            // return too early (i.e. before the animation completed).
            try {
                Thread.sleep(podcatcher.getResources().getInteger(
                        android.R.integer.config_longAnimTime));
            } catch (InterruptedException e) {
            }

            this.episodeFile = localFile;
        }
        // Start download because the episode is not there
        else {
            // Make sure podcast directory exists
            localFile.getParentFile().mkdirs();

            // Create the request
            Request download = new Request(Uri.parse(episode.getMediaUrl()))
                    .setDestinationUri(Uri.fromFile(localFile))
                    .setTitle(episode.getName())
                    .setDescription(episode.getPodcast().getName())
                    // We overwrite the AndroidDownloadManager user agent
                    // string here because there are servers out there (e.g.
                    // ORF.at) that apparently block downloads based on this
                    // information
                    .addRequestHeader(USER_AGENT_KEY, USER_AGENT_VALUE)
                    // Set auth if available
                    .addRequestHeader(AUTHORIZATION_KEY, episode.getPodcast().getAuthorization())
                    // Make sure our download does not end up in the http cache
                    .addRequestHeader("Cache-Control", "no-store");

            // Start the download
            long downloadId;
            try {
                downloadId = downloadManager.enqueue(download);
            } catch (SecurityException se) {
                // This happens if the download manager has not the rights
                // to write to the selected downloads directory
                needsPostDownloadMove = true;

                // Download the file to a temp folder and move it the the wanted
                // destination once the download completed
                download.setDestinationUri(Uri.fromFile(
                        new File(podcatcher.getExternalCacheDir(), localFile.getName())));
                downloadId = downloadManager.enqueue(download);
            }

            // We need to tell our listener about the download id, to
            // separate it from percentage done, put minus sign. See
            // onProgressUpdate() below.
            publishProgress(downloadId > 0 ? downloadId * -1 : downloadId);

            // Start checking the download manager for status updates
            boolean finished = false;
            while (!isCancelled() && !finished) {
                // Wait between polls
                try {
                    Thread.sleep(DOWNLOAD_STATUS_POLL_INTERVALL);
                } catch (InterruptedException e) {
                    // pass
                }

                // Find download information
                final Cursor info = downloadManager.query(new Query().setFilterById(downloadId));
                // There should be information on the download
                if (info.moveToFirst()) {
                    final int state = info.getInt(info.getColumnIndex(COLUMN_STATUS));
                    switch (state) {
                        case STATUS_SUCCESSFUL:
                            // This is the file the download manager got for us
                            final File downloadedFile = new File(info.getString(info
                                    .getColumnIndex(COLUMN_LOCAL_FILENAME)));

                            // It might need to be moved to its final position
                            if (needsPostDownloadMove) {
                                // This worked
                                if (moveFile(downloadedFile, localFile))
                                    this.episodeFile = localFile;
                                // Move operation failed -> download failed
                                else
                                    cancel(false);

                                // We remove the file from the system's download
                                // manager here, since we moved the downloaded
                                // file (or it failed anyway)
                                downloadManager.remove(downloadId);
                            }
                            else
                                this.episodeFile = downloadedFile;

                            finished = true;
                            break;
                        case STATUS_FAILED:
                            downloadManager.remove(downloadId);

                            cancel(false);
                            break;
                        default:
                            // Update progress
                            final long total = info.getLong(info
                                    .getColumnIndex(COLUMN_TOTAL_SIZE_BYTES));
                            final long progress = info.getLong(info
                                    .getColumnIndex(COLUMN_BYTES_DOWNLOADED_SO_FAR));

                            if (total > 0 && progress > 0 && total >= progress)
                                publishProgress((long) (((float) progress / (float) total) * 100));
                    }
                }
                // Close cursor
                info.close();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        long progress = values[0];

        // This is the download id (because it is < 0, see above)
        if (progress < 0)
            listener.onEpisodeEnqueued(episode, progress * -1);
        // This is the percentage of download done
        else if (progress > 0 && progress != percentProgress) {
            percentProgress = (int) progress;
            listener.onEpisodeDownloadProgressed(episode, percentProgress);
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        // If the episodeFile member is set, the download was successful
        if (episodeFile != null)
            listener.onEpisodeDownloaded(episode, episodeFile);
        else
            onCancelled(result);
    }

    @Override
    protected void onCancelled(Void result) {
        listener.onEpisodeDownloadFailed(episode);
    }

    private boolean moveFile(File from, File to) {
        boolean success = true;

        BufferedInputStream reader = null;
        BufferedOutputStream writer = null;
        // Move file over, put try catch to set return value to false is the
        // move fails
        try {
            reader = new BufferedInputStream(new FileInputStream(from));
            writer = new BufferedOutputStream(new FileOutputStream(to));

            byte[] buffer = new byte[1024];
            while (reader.read(buffer) > 0)
                writer.write(buffer);

        } catch (IOException ioe) {
            success = false;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    // pass
                }
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    // pass
                }

            from.delete();
        }

        return success;
    }
}
