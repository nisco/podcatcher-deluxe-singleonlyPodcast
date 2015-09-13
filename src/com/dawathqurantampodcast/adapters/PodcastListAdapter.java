

package com.dawathqurantampodcast.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.dawathqurantampodcast.model.types.Podcast;
import com.dawathqurantampodcast.view.PodcastListItemView;
import com.dawathqurantampodcast.R;


import java.util.List;

/**
 * Adapter class used for the list of podcasts.
 */
public class PodcastListAdapter extends PodcatcherBaseListAdapter {

    /** The list our data resides in */
    protected List<Podcast> list;
    /** Member flag to indicate whether we show the podcast logo */
    protected boolean showLogoView = false;

    /**
     * Create new adapter.
     * 
     * @param context The current context.
     * @param podcastList List of podcasts to wrap (not <code>null</code>).
     */
    public PodcastListAdapter(Context context, List<Podcast> podcastList) {
        super(context);

        this.list = podcastList;
    }

    /**
     * Replace the current podcast list with a new one.
     * 
     * @param newList The new list (not <code>null</code>).
     */
    public void updateList(List<Podcast> newList) {
        this.list = newList;

        notifyDataSetChanged();
    }

    /**
     * Set whether the podcast logo should be shown. This will redraw the list
     * and take effect immediately.
     * 
     * @param show Whether to show each podcast's logo.
     */
    public void setShowLogo(boolean show) {
        this.showLogoView = show;

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
        final PodcastListItemView returnView = (PodcastListItemView)
                findReturnView(convertView, parent, R.layout.podcast_list_item);

        // Make sure the coloring is right
        setBackgroundColorForPosition(returnView, position);
        // Make the view represent podcast at given position
        returnView.show((Podcast) getItem(position), showLogoView, selectAll);

        return returnView;
    }
}
