

package com.dawathqurantampodcast.services;

import static android.media.MediaMetadataRetriever.METADATA_KEY_ARTIST;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DATE;
import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;
import static android.media.MediaMetadataRetriever.METADATA_KEY_TITLE;
import static android.media.RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK;

import android.app.PendingIntent;
import android.media.RemoteControlClient;

import com.dawathqurantampodcast.model.EpisodeManager;
import com.dawathqurantampodcast.model.types.Episode;
import com.dawathqurantampodcast.view.Utils;


/**
 * Our remote control client used to provide playback information to a remote
 * control that might be present and able to display some episode metadata.
 */
public class PodcatcherRCClient extends RemoteControlClient {

    /** The supported transport modes for the remote control */
    private static final int SUPPORTED_TRANSPORTS = FLAG_KEY_MEDIA_PLAY_PAUSE
            | FLAG_KEY_MEDIA_PAUSE | FLAG_KEY_MEDIA_PLAY | FLAG_KEY_MEDIA_STOP
            | FLAG_KEY_MEDIA_PREVIOUS | FLAG_KEY_MEDIA_REWIND | FLAG_KEY_MEDIA_FAST_FORWARD;

    /**
     * Create the remote control client.
     * 
     * @param mediaButtonIntent The pending intent to use for the media buttons.
     * @param episode The episode to get metadata from.
     */
    public PodcatcherRCClient(PendingIntent mediaButtonIntent, Episode episode) {
        super(mediaButtonIntent);

        showNext(!EpisodeManager.getInstance().isPlaylistEmptyBesides(episode));
        setMetadata(episode);
    }

    /**
     * Set whether the rc should show the option to skip ahead to the next
     * episode.
     * 
     * @param canSkip Give <code>true</code> for the "next" transport control to
     *            be displayed.
     */
    public void showNext(boolean canSkip) {
        setTransportControlFlags(SUPPORTED_TRANSPORTS | (canSkip ? FLAG_KEY_MEDIA_NEXT : 0));
    }

    private void setMetadata(Episode episode) {
        if (episode != null) {
            MetadataEditor editor = editMetadata(true);

            editor.putString(METADATA_KEY_TITLE, episode.getName());
            editor.putString(METADATA_KEY_DATE, Utils.getRelativePubDate(episode));
            editor.putLong(METADATA_KEY_DURATION, episode.getDuration() * 1000);

            if (episode.getPodcast() != null) {
                editor.putString(METADATA_KEY_ARTIST, episode.getPodcast().getName());

                if (episode.getPodcast().isLogoCached())
                    editor.putBitmap(BITMAP_KEY_ARTWORK, episode.getPodcast().getLogo());
            }

            editor.apply();
        }
    }
}
