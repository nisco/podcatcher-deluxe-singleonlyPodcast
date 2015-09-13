

package com.dawathqurantampodcast.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.dawathqurantampodcast.model.types.Episode;
import com.dawathqurantampodcast.view.EpisodeListItemView;
import com.dawathqurantampodcast.R;


import java.util.List;

/**
 * Adapter class used for the list of episodes.
 */
public class EpisodeListAdapter extends PodcatcherBaseListAdapter {

    /** The list our data resides in */
    protected List<Episode> list;
    /** Whether the podcast name should be shown */
    protected boolean showPodcastNames = false;

    /**
     * Create new adapter.
     * 
     * @param context The activity.
     * @param episodeList The list of episodes to show in list.
     */
    public EpisodeListAdapter(Context context, List<Episode> episodeList) {
        super(context);

        this.list = episodeList;
    }

    /**
     * Replace the current episode list with a new one.
     * 
     * @param episodeList The new list (not <code>null</code>).
     */
    public void updateList(List<Episode> episodeList) {
        this.list = episodeList;

        notifyDataSetChanged();
    }

    /**
     * Set whether the podcast name for the episode should be shown. This will
     * redraw the list and take effect immediately.
     * 
     * @param show Whether to show each episode's podcast name.
     */
    public void setShowPodcastNames(boolean show) {
        this.showPodcastNames = show;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final EpisodeListItemView returnView = (EpisodeListItemView)
                findReturnView(convertView, parent, R.layout.episode_list_item);

        // Make sure the coloring is right
        setBackgroundColorForPosition(returnView, position);
        // Make the view represent episode at given position
        returnView.show((Episode) getItem(position), showPodcastNames);

        return returnView;
    }
}
