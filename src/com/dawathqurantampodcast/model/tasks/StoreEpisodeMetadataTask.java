

package com.dawathqurantampodcast.model.tasks;

import static com.dawathqurantampodcast.model.tags.METADATA.DOWNLOAD_ID;
import static com.dawathqurantampodcast.model.tags.METADATA.EPISODE_DATE;
import static com.dawathqurantampodcast.model.tags.METADATA.EPISODE_DESCRIPTION;
import static com.dawathqurantampodcast.model.tags.METADATA.EPISODE_NAME;
import static com.dawathqurantampodcast.model.tags.METADATA.EPISODE_RESUME_AT;
import static com.dawathqurantampodcast.model.tags.METADATA.EPISODE_STATE;
import static com.dawathqurantampodcast.model.tags.METADATA.EPISODE_URL;
import static com.dawathqurantampodcast.model.tags.METADATA.LOCAL_FILE_PATH;
import static com.dawathqurantampodcast.model.tags.METADATA.METADATA;
import static com.dawathqurantampodcast.model.tags.METADATA.PLAYLIST_POSITION;
import static com.dawathqurantampodcast.model.tags.METADATA.PODCAST_NAME;
import static com.dawathqurantampodcast.model.tags.METADATA.PODCAST_URL;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.dawathqurantampodcast.listeners.OnStoreEpisodeMetadataListener;
import com.dawathqurantampodcast.model.EpisodeManager;
import com.dawathqurantampodcast.model.types.EpisodeMetadata;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Stores the episode metadata information to the file system.
 */
public class StoreEpisodeMetadataTask extends StoreFileTask<Map<String, EpisodeMetadata>> {

    /** Our context */
    protected Context context;
    /** The call-back */
    protected OnStoreEpisodeMetadataListener listener;

    /** The exception that might have been occurred */
    protected Exception exception;

    /**
     * Create a new persistence task.
     * 
     * @param context Context to use for file writing.
     * @param listener Call-back to alert on completion or failure.
     */
    public StoreEpisodeMetadataTask(Context context, OnStoreEpisodeMetadataListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Map<String, EpisodeMetadata>... params) {
        try {
            // 1. Do house keeping and remove all metadata instances without
            // data
            cleanMetadata(params[0]);

            // 2. Open the file and get a writer
            OutputStream fileStream =
                    context.openFileOutput(EpisodeManager.METADATA_FILENAME, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(fileStream, FILE_ENCODING));

            // 3. Write new file content
            writeHeader();
            for (Entry<String, EpisodeMetadata> entry : params[0].entrySet())
                writeRecord(entry.getKey(), entry.getValue());
            writeFooter();
        } catch (Exception ex) {
            Log.e(getClass().getSimpleName(), "Cannot store episode metadata file", ex);
            this.exception = ex;

            cancel(true);
        } finally {
            // Make sure we close the file stream
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException e) {
                    /* Nothing we can do here */
                    Log.w(getClass().getSimpleName(),
                            "Failed to close episode metadata file writer!", e);
                }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void nothing) {
        if (listener != null)
            listener.onEpisodeMetadataStored();
    }

    @Override
    protected void onCancelled(Void nothing) {
        if (listener != null)
            listener.onEpisodeMetadataStoreFailed(exception);
    }

    private void writeRecord(String key, EpisodeMetadata value) throws IOException {
        writeLine(1, "<" + METADATA + " " + EPISODE_URL + "=\"" + TextUtils.htmlEncode(key) + "\">");

        writeData(value.episodeName, EPISODE_NAME);
        if (value.episodePubDate != null)
            writeData(value.episodePubDate.getTime(), EPISODE_DATE);
        writeData(value.episodeDescription, EPISODE_DESCRIPTION);
        writeData(value.podcastName, PODCAST_NAME);
        writeData(value.podcastUrl, PODCAST_URL);
        writeData(value.downloadId, DOWNLOAD_ID);
        writeData(value.filePath, LOCAL_FILE_PATH);
        writeData(value.resumeAt, EPISODE_RESUME_AT);
        if (value.isOld != null && value.isOld)
            writeData("true", EPISODE_STATE);
        writeData(value.playlistPosition, PLAYLIST_POSITION);

        writeLine(1, "</" + METADATA + ">");
    }

    private void writeData(String data, String tag) throws IOException {
        // For all fields: only write data that is actually there!
        if (data != null)
            writeLine(2, "<" + tag + ">" + TextUtils.htmlEncode(data) + "</" + tag + ">");
    }

    private void writeData(Long data, String tag) throws IOException {
        // For all fields: only write data that is actually there!
        if (data != null)
            writeLine(2, "<" + tag + ">" + data + "</" + tag + ">");
    }

    private void writeData(Integer data, String tag) throws IOException {
        if (data != null)
            writeData(Long.valueOf(data), tag);
    }

    private void writeHeader() throws IOException {
        writeLine(0, "<?xml version=\"1.0\" encoding=\"" + FILE_ENCODING + "\"?>");
        writeLine(0, "<xml dateModified=\"" + new Date().getTime() + "\">");
    }

    private void writeFooter() throws IOException {
        writeLine(0, "</xml>");
    }

    private void cleanMetadata(Map<String, EpisodeMetadata> metadata) {
        Iterator<Entry<String, EpisodeMetadata>> iterator = metadata.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, EpisodeMetadata> entry = iterator.next();

            if (!entry.getValue().hasData())
                iterator.remove();
        }
    }
}
