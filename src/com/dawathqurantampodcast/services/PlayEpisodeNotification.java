

package com.dawathqurantampodcast.services;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.dawathqurantampodcast.EpisodeActivity.EPISODE_URL_KEY;
import static com.dawathqurantampodcast.EpisodeListActivity.PODCAST_URL_KEY;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

import com.dawathqurantampodcast.EpisodeListActivity;
import com.dawathqurantampodcast.PodcastActivity;
import com.dawathqurantampodcast.Podcatcher;
import com.dawathqurantampodcast.BaseActivity.ContentMode;
import com.dawathqurantampodcast.model.EpisodeManager;
import com.dawathqurantampodcast.model.types.Episode;
import com.dawathqurantampodcast.model.types.Podcast;
import com.dawathqurantampodcast.R;


import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for the {@link PlayEpisodeService} to encapsulate the complexity
 * of notifications.
 */
public class PlayEpisodeNotification {

    /** The single instance */
    private static PlayEpisodeNotification instance;
    /** The context the notifications live in */
    private Context context;

    /** The actual intent that brings back the app */
    private final Intent appIntent;
    /** The pending intents for the actions */
    private final PendingIntent stopPendingIntent;
    private final PendingIntent tooglePendingIntent;
    private final PendingIntent nextPendingIntent;

    /** Our builder */
    private NotificationCompat.Builder notificationBuilder;
    /** The cache for the scaled bitmaps */
    private Map<String, Bitmap> bitmapCache = new HashMap<String, Bitmap>();

    private PlayEpisodeNotification(Context context) {
        this.context = context;

        // Create all the static intents we need for every build
        appIntent = new Intent(context, PodcastActivity.class)
                .putExtra(EpisodeListActivity.MODE_KEY, ContentMode.SINGLE_PODCAST)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final Intent stopIntent = new Intent(context, PlayEpisodeService.class);
        stopIntent.setAction(PlayEpisodeService.ACTION_STOP);
        stopPendingIntent = PendingIntent.getService(context, 0, stopIntent,
                FLAG_UPDATE_CURRENT);

        final Intent toogleIntent = new Intent(context, PlayEpisodeService.class);
        toogleIntent.setAction(PlayEpisodeService.ACTION_TOGGLE);
        tooglePendingIntent = PendingIntent.getService(context, 0, toogleIntent,
                FLAG_UPDATE_CURRENT);

        final Intent nextIntent = new Intent(context, PlayEpisodeService.class);
        nextIntent.setAction(PlayEpisodeService.ACTION_SKIP);
        nextPendingIntent = PendingIntent.getService(context, 0, nextIntent,
                FLAG_UPDATE_CURRENT);
    }

    /**
     * Get the single instance representing the service notification.
     * 
     * @param context The context notifications should life in.
     * @return The single instance.
     */
    public static PlayEpisodeNotification getInstance(Context context) {
        if (instance == null)
            instance = new PlayEpisodeNotification(context);

        return instance;
    }

    /**
     * Build a new notification using default values for all but the episode.
     * 
     * @param episode The episode playing.
     * @return The notification to display.
     * @see #build(Episode, boolean, int, int)
     */
    public Notification build(Episode episode) {
        return build(episode, false, 0, 0);
    }

    /**
     * Build a new notification. To update the progress on the notification, use
     * {@link #updateProgress(int, int)} instead.
     * 
     * @param episode The episode playing.
     * @param paused Playback state, <code>true</code> for paused.
     * @param position The current playback progress.
     * @param duration The length of the current episode.
     * @return The notification to display.
     */
    public Notification build(Episode episode, boolean paused, int position, int duration) {
        // Prepare the main intent (leading back to the app)
        appIntent.putExtra(PODCAST_URL_KEY, episode.getPodcast().getUrl());
        appIntent.putExtra(EPISODE_URL_KEY, episode.getMediaUrl());
        final PendingIntent backToAppIntent = PendingIntent.getActivity(context, 0,
                appIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the notification builder and set values
        notificationBuilder = new NotificationCompat.Builder(context)
                .setContentIntent(backToAppIntent)
                .setTicker(episode.getName())
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(episode.getName())
                .setContentText(episode.getPodcast().getName())
                .setWhen(0)
                .setProgress(duration, position, false)
                .setOngoing(true);

        // Add stop action
        notificationBuilder.addAction(R.drawable.ic_media_stop,
                context.getString(R.string.stop), stopPendingIntent);

        // Add other actions according to playback state
        if (paused)
            notificationBuilder.addAction(R.drawable.ic_media_resume,
                    context.getString(R.string.resume), tooglePendingIntent);
        else
            notificationBuilder.addAction(R.drawable.ic_media_pause,
                    context.getString(R.string.pause), tooglePendingIntent);

        if (!EpisodeManager.getInstance().isPlaylistEmptyBesides(episode))
            notificationBuilder.addAction(R.drawable.ic_media_next,
                    context.getString(R.string.next), nextPendingIntent);

        // Apply the notification style

        return notificationBuilder.build();
    }

    /**
     * Update the last notification build with a new progress and duration and
     * rebuild it leaving all the other data intact. Only call this after having
     * called one of the build() methods before.
     * 
     * @param position The new progrss position.
     * @param duration The length of the current episode.
     * @return The updated notification to display.
     */
    public Notification updateProgress(int position, int duration) {
        notificationBuilder.setProgress(duration, position, false);

        return notificationBuilder.build();
    }

    private boolean isPodcastLogoAvailable(Episode episode) {
        return episode.getPodcast().isLogoCached();
    }

    private boolean isLargeDevice() {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= Podcatcher.MIN_PIXEL_LARGE;
    }

    private Bitmap getScaledBitmap(Podcast podcast) {
        final String cacheKey = podcast.getUrl();

        if (!bitmapCache.containsKey(cacheKey)) {
            final Resources res = context.getResources();
            int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
            int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);

            bitmapCache.put(cacheKey,
                    Bitmap.createScaledBitmap(podcast.getLogo(), width, height, false));
        }

        return bitmapCache.get(cacheKey);
    }
}
