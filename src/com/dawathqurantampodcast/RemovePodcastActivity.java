

package com.dawathqurantampodcast;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Non-UI activity to remove podcasts.
 */
public class RemovePodcastActivity extends BaseActivity {

    @Override
    protected void onStart() {
        super.onStart();

        // Get the list of positions to remove
        List<Integer> positions = getIntent().getIntegerArrayListExtra(PODCAST_POSITION_LIST_KEY);

        // If list is there, remove podcasts at given positions
        if (positions != null) {
            // Make sure positions are ordered lowest to highest
            Collections.sort(positions);

            // We need to iterate backwards, so positions are not screwed up
            ListIterator<Integer> li = positions.listIterator(positions.size());

            // Remove podcasts starting with the last one
            while (li.hasPrevious())
                podcastManager.removePodcast(li.previous());
        }

        // Make sure we stop here
        finish();
    }
}
