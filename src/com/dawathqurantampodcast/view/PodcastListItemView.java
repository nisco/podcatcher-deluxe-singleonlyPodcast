

package com.dawathqurantampodcast.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dawathqurantampodcast.model.types.Podcast;
import com.dawathqurantampodcast.model.types.Progress;
import com.dawathqurantampodcast.R;

/**
 * A list item view to represent a podcast.
 */
public class PodcastListItemView extends PodcatcherListItemView {

    /** The title text view */
    private TextView titleTextView;
    /** The caption text view */
    private TextView captionTextView;
    /** The podcast logo view */
    private ImageView logoView;
    /** The load progress view */
    private HorizontalProgressView progressView;

    /**
     * Create a podcast item list view.
     * 
     * @param context Context for the view to live in.
     * @param attrs View attributes.
     */
    public PodcastListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        titleTextView = (TextView) findViewById(R.id.list_item_title);
        captionTextView = (TextView) findViewById(R.id.list_item_caption);
        logoView = (ImageView) findViewById(R.id.podcast_logo);
        progressView = (HorizontalProgressView) findViewById(R.id.list_item_progress);
    }

    /**
     * Make the view update all its child to represent input given.
     * 
     * @param podcast Podcast to represent.
     * @param showLogo Whether the podcast logo should show (if available).
     * @param showProgress Whether the progress view should be visible.
     */
    public void show(final Podcast podcast, boolean showLogo, boolean showProgress) {
        // 0. Check podcast state
        final boolean loading = podcastManager.isLoading(podcast);
        final int episodeNumber = podcast.getEpisodeCount();
        final boolean showLogoView = showLogo && podcast.isLogoCached();
        final boolean progressShouldFade = podcast.hashCode() == lastItemId;

        // 1. Set podcast title
        titleTextView.setText(podcast.getName());

        // 2. Set caption text and visibility
        captionTextView.setText(createCaption(podcast, episodeNumber));
        // The caption should only show if there are episodes or there is
        // progress to display
        ((View) captionTextView.getParent())
                .setVisibility(episodeNumber > 0 || (loading && showProgress) ? VISIBLE : GONE);
        // Whether the caption show episode numbers, either faded-in or directly
        if (episodeNumber > 0 && !loading && isShowingProgress && progressShouldFade)
            crossfade(captionTextView, progressView);
        else
            captionTextView.setVisibility(episodeNumber > 0 && !loading ? VISIBLE : GONE);

        // 3. Show/hide progress view, either fade-in or directly
        if (loading && showProgress && !isShowingProgress && progressShouldFade)
            crossfade(progressView, captionTextView);
        else
            progressView.setVisibility(loading && showProgress ? VISIBLE : GONE);
        // We need to reset the progress here, because the view might be
        // recycled and it should not show another podcast's progress
        progressView.publishProgress(Progress.WAIT);

        // 4. Set podcast logo if available
        logoView.setVisibility(showLogoView ? VISIBLE : GONE);
        logoView.setImageBitmap(showLogoView ? podcast.getLogo() : null);

        // 5. Store state to make sure it is available next time show() is
        // called and we can decide whether to crossfade or not
        this.isShowingProgress = loading;
        this.lastItemId = podcast.hashCode();
    }

    /**
     * Update the podcast progress indicator to the progress given. Does not
     * change the visibility of the progress view.
     * 
     * @param progress Progress to show.
     */
    public void updateProgress(Progress progress) {
        progressView.publishProgress(progress);
    }

    private String createCaption(Podcast podcast, int episodeCount) {
        final int newEpisodeCount = episodeManager.getNewEpisodeCount(podcast);

        String caption = "";

        if (newEpisodeCount == 0)
            caption += getResources().getString(R.string.episodes_no_new);
        else
            caption += getResources().getQuantityString(R.plurals.episodes_new, newEpisodeCount,
                    newEpisodeCount);

        return caption + " (" + episodeCount + " "
                + getResources().getString(R.string.episodes_total) + ")";
    }
}
