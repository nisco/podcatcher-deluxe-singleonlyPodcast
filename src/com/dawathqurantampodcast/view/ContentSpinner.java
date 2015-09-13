

package com.dawathqurantampodcast.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dawathqurantampodcast.listeners.OnSelectPodcastListener;
import com.dawathqurantampodcast.model.EpisodeManager;
import com.dawathqurantampodcast.model.PodcastManager;
import com.dawathqurantampodcast.R;


/**
 * The spinner for the main action bar menu, that allows for the selection of
 * the content mode. This is a bit of a hack since it does not work exactly like
 * usual spinner, but allows the user to pick an action twice. The initial
 * default selection is ignored. In addition, we override the default behavior
 * to show the selected view when closed. Instead, we show another view with
 * name and status information.
 */
public class ContentSpinner extends Spinner implements
        AdapterView.OnItemSelectedListener {

    /** The listener call-back to alert on content mode selection */
    private OnSelectPodcastListener listener;

    /** Flag allowing use to skip the first selection on creation */
    private boolean isInitialSelection = true;

    /** Handle to the closed spinner title text view */
    private TextView closedTitleView;
    /** Handle to the closed spinner subtitle text view */
    private TextView closedSubtitleView;

    /** Our spinner handle */
    private NavigationSpinnerAdapter spinnerAdapter;

    /** The data adapter used to populate the spinner */
    private static class NavigationSpinnerAdapter extends BaseAdapter {

        /** The inflater we use */
        private final LayoutInflater inflater;
        /** The view we return for a closed spinner */
        private final View closedView;

        public NavigationSpinnerAdapter(ViewGroup parent) {
            this.inflater = LayoutInflater.from(parent.getContext());
            this.closedView = inflater.inflate(R.layout.content_spinner_item, parent, false);

            // FOr the closed view, no padding is needed
            closedView.setPadding(0, 0, 0, 0);

            // Set the initial name and status
            ((TextView) closedView.findViewById(R.id.title)).setText(R.string.app_name);
            ((TextView) closedView.findViewById(R.id.subtitle)).setVisibility(View.GONE);

            // Hide icon view for the closed view
            closedView.findViewById(R.id.icon).setVisibility(View.GONE);
        }

        /**
         * @return The view shown when the spinner is closed. This is never
         *         <code>null</code>.
         */
        public View getClosedView() {
            return closedView;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            switch (position) {
                case 0:
                    return R.string.podcast_select_all;
                case 1:
                    return R.string.downloads;
                case 2:
                    return R.string.playlist;
                default:
                    return 0;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // This is shown if the spinner is closed
            return closedView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            // Create the return view (this should not be recycled)
            final View spinnerItemView = inflater.inflate(
                    R.layout.content_spinner_item, parent, false);

            // Get handles on the view we need to update
            final ImageView imageView = (ImageView) spinnerItemView.findViewById(R.id.icon);
            final TextView titleView = (TextView) spinnerItemView.findViewById(R.id.title);
            final TextView subtitleView = (TextView) spinnerItemView.findViewById(R.id.subtitle);

            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.ic_menu_select_all);
                    titleView.setText(R.string.podcast_select_all);

                    // Set the subtitle
                    final int podcastCount = PodcastManager.getInstance().size();
                    if (podcastCount == 0)
                        subtitleView.setText(R.string.podcast_none);
                    else
                        subtitleView.setText(parent.getContext().getResources()
                                .getQuantityString(R.plurals.podcasts, podcastCount, podcastCount));
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.ic_menu_download);
                    titleView.setText(R.string.downloads);

                    // Set the subtitle
                    final int downloadsCount = EpisodeManager.getInstance().getDownloadsSize();
                    setEpisodeNumberText(parent, subtitleView, downloadsCount);

                    break;
                case 2:
                    imageView.setImageResource(R.drawable.ic_menu_playlist);
                    titleView.setText(R.string.playlist);

                    // Set the subtitle
                    final int playlistCount = EpisodeManager.getInstance().getPlaylistSize();
                    setEpisodeNumberText(parent, subtitleView, playlistCount);

                    break;
            }

            // Make sure to hide empty sub-titles
            if (subtitleView.getText().length() == 0)
                subtitleView.setVisibility(View.GONE);

            return spinnerItemView;
        }

        private void setEpisodeNumberText(ViewGroup parent, final TextView subtitleView,
                final int count) {
            if (count == 0)
                subtitleView.setText(null);
            else
                subtitleView.setText(parent.getContext().getResources()
                        .getQuantityString(R.plurals.episodes, count, count));
        }
    }

    /**
     * Create a new content mode spinner to be added as a custom view to the
     * app's action bar.
     * 
     * @param context Context the view lives in.
     * @param listener The action call-back to alert when a content selection is
     *            made.
     */
    public ContentSpinner(Context context, OnSelectPodcastListener listener) {
        super(context, null, android.R.attr.actionDropDownStyle);

        this.listener = listener;
        this.spinnerAdapter = new NavigationSpinnerAdapter(this);

        closedTitleView = (TextView) spinnerAdapter.getClosedView().findViewById(R.id.title);
        closedSubtitleView = (TextView) spinnerAdapter.getClosedView().findViewById(R.id.subtitle);

        setAdapter(spinnerAdapter);
        setOnItemSelectedListener(this);
    }

    /**
     * Set the main text for the closed spinner view.
     * 
     * @param title Text to show.
     */
    public void setTitle(String title) {
        closedTitleView.setText(title);
    }

    /**
     * Set the sub title for the closed spinner view.
     * 
     * @param subtitle The subtitle, set to <code>null</code> to hide.
     */
    public void setSubtitle(String subtitle) {
        closedSubtitleView.setText(subtitle);
        closedSubtitleView.setVisibility(subtitle == null ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!isInitialSelection)
            switch (Long.valueOf(id).intValue()) {
                case R.string.podcast_select_all:
                    listener.onAllPodcastsSelected();
                    break;
                case R.string.downloads:
                    listener.onDownloadsSelected();
                    break;
                case R.string.playlist:
                    listener.onPlaylistSelected();
                    break;
            }

        // This invalidates the selection, so the same item can be picked again
        setSelection(getAdapter().getCount());
        // Reset flag to check whether this is the activity start-up selection
        isInitialSelection = false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // pass
    }
}
