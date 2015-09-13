

package com.dawathqurantampodcast.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.dawathqurantampodcast.model.EpisodeManager;
import com.dawathqurantampodcast.model.PodcastManager;


/**
 * An abstract super list item view class for the podcatcher's actual list view
 * items. Defines some commonly used functionality.
 */
abstract class PodcatcherListItemView extends RelativeLayout {

    /** Our podcast manager handle */
    protected final PodcastManager podcastManager;
    /** Our episode manager handle */
    protected final EpisodeManager episodeManager;

    /** Flag for progress bar visibility, needed for correct crossfade */
    protected boolean isShowingProgress;
    /** Last object's id show by this view, needed for correct crossfade */
    protected int lastItemId;

    /** The animation duration to use for crossfade */
    private final int crossfadeDuration;

    /**
     * Create an item list view.
     * 
     * @param context Context for the view to live in.
     * @param attrs View attributes.
     */
    public PodcatcherListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.podcastManager = PodcastManager.getInstance();
        this.episodeManager = EpisodeManager.getInstance();

        this.crossfadeDuration = context.getResources()
                .getInteger(android.R.integer.config_shortAnimTime);
    }

    /**
     * Quickly crossfade two views.
     * 
     * @param incoming View to make visible.
     * @param outgoing View to hide.
     */
    protected void crossfade(final View incoming, final View outgoing) {
        incoming.setAlpha(0f);
        incoming.setVisibility(View.VISIBLE);

        // Animate the incoming view to 100% opacity, and clear any animation
        // listener set on the view
        incoming.animate()
                .alpha(1f).setDuration(crossfadeDuration).setListener(null);

        // Animate the outgoing view to 0% opacity and finally hide it
        outgoing.animate()
                .alpha(0f).setDuration(crossfadeDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        outgoing.setVisibility(View.GONE);
                        // Set it back to opaque because the view might be
                        // recycled and we need it to show
                        outgoing.setAlpha(1f);
                    }
                });
    }
}
