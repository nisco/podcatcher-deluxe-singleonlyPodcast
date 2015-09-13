

package com.dawathqurantampodcast.view.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dawathqurantampodcast.listeners.OnDownloadEpisodeListener;
import com.dawathqurantampodcast.model.types.Episode;
import com.dawathqurantampodcast.view.Utils;
import com.dawathqurantampodcast.R;


/**
 * Fragment showing episode details.
 */
public class EpisodeFragment extends Fragment {

    /** The listener for the menu item */
    private OnDownloadEpisodeListener listener;
    /** The currently shown episode */
    private Episode currentEpisode;

    /** Flag for show download menu item state */
    private boolean showDownloadMenuItem = false;
    /** Flag for the state of the download menu item */
    private boolean downloadMenuItemState = true;
    /** Flag to indicate whether the episode date should be shown */
    private boolean showEpisodeDate = false;
    /** Flag for show new icon state */
    private boolean showNewStateIcon = false;
    /** Flag for show download icon state */
    private boolean showDownloadIcon = false;
    /** Flag for the state of the download icon */
    private boolean downloadIconState = true;

    /** Separator for date and podcast name */
    private static final String SEPARATOR = " • ";

    /** Status flag indicating that our view is created */
    private boolean viewCreated = false;
    /** Flag for transition animation fix */
    private boolean needsLayoutTransitionFix = true;

    /** The download episode menu bar item */
    private MenuItem downloadMenuItem;

    /** The empty view */
    private View emptyView;
    /** The episode title view */
    private TextView titleView;
    /** The podcast title view */
    private TextView subtitleView;
    /** The state icon view */
    private ImageView stateIconView;
    /** The download icon view */
    private ImageView downloadIconView;
    /** The divider view between title and description */
    private View dividerView;
    /** The episode description web view */
    private WebView descriptionView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Make sure our listener is present
        try {
            this.listener = (OnDownloadEpisodeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDownloadEpisodeListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.episode, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find UI widgets
        emptyView = getView().findViewById(android.R.id.empty);
        titleView = (TextView) getView().findViewById(R.id.episode_title);
        subtitleView = (TextView) getView().findViewById(R.id.podcast_title);
        stateIconView = (ImageView) getView().findViewById(R.id.state_icon);
        downloadIconView = (ImageView) getView().findViewById(R.id.download_icon);
        descriptionView = (WebView) getView().findViewById(R.id.episode_description);
        dividerView = getView().findViewById(R.id.episode_divider);

        viewCreated = true;

        // This will make sure we show the right information once the view
        // controls are established (the episode might have been set earlier)
        if (currentEpisode != null) {
            setEpisode(currentEpisode);
            setNewIconVisibility(showNewStateIcon);
            setDownloadIconVisibility(showDownloadIcon, downloadIconState);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.episode, menu);

        downloadMenuItem = menu.findItem(R.id.episode_download_menuitem);
        setDownloadMenuItemVisibility(showDownloadMenuItem, downloadMenuItemState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.episode_download_menuitem:
                // Tell activity to load/unload the current episode
                listener.onToggleDownload();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        viewCreated = false;

        super.onDestroyView();
    }

    /**
     * Set the displayed episode, all UI will be updated.
     * 
     * @param selectedEpisode Episode to show.
     */
    public void setEpisode(Episode selectedEpisode) {
        // Set handle to episode in case we are not resumed
        this.currentEpisode = selectedEpisode;

        // If the fragment's view is actually visible and the episode is
        // valid,
        // show episode information
        if (viewCreated && currentEpisode != null) {
            // Title and sub-title
            titleView.setText(currentEpisode.getName());
            subtitleView.setText(currentEpisode.getPodcast().getName());
            // Episode publication data
            if (showEpisodeDate && currentEpisode.getPubDate() != null)
                subtitleView.setText(subtitleView.getText() + SEPARATOR
                        + Utils.getRelativePubDate(currentEpisode));
            // Episode duration
            if (currentEpisode.getDurationString() != null)
                subtitleView.setText(subtitleView.getText() + SEPARATOR
                        + currentEpisode.getDurationString());
            // Find valid episode description
            String description = currentEpisode.getLongDescription();
            if (description == null)
                description = currentEpisode.getDescription();
            if (description == null)
                description = getString(R.string.episode_no_description);
            // Set episode description
            descriptionView.loadDataWithBaseURL(null, description, "text/html", "utf-8", null);
        }

        // Update the UI widget's visibility to reflect state
        updateUiElementVisibility();

        // This is a workaround for the fact that declaring animateLayoutChanges
        // in combination with a webview breaks the fragment on some devices
        // (such as the HP Touchpad). Activating the layout transition after the
        // view has been shown once, works.
        if (needsLayoutTransitionFix && viewCreated) {
            ViewGroup parent = (ViewGroup) getView().getParent();

            // In small view we need to go two steps up
            if (!(parent instanceof LinearLayout))
                parent = (ViewGroup) parent.getParent();

            if (parent.getLayoutTransition() == null) {
                parent.setLayoutTransition(new LayoutTransition());
                needsLayoutTransitionFix = false;
            }
        }
    }

    /**
     * Set whether the fragment should show the download menu item. You can call
     * this any time and can expect it to happen on menu creation at the latest.
     * You also have to set the download menu state, <code>true</code> for
     * "Download" and <code>false</code> for "Delete".
     * 
     * @param show Whether to show the download menu item.
     * @param download State of the download menu item (download / delete)
     */
    public void setDownloadMenuItemVisibility(boolean show, boolean download) {
        this.showDownloadMenuItem = show;
        this.downloadMenuItemState = download;

        // Only do it right away if resumed and menu item is available,
        // otherwise onResume or the menu creation callback will call us.
        if (downloadMenuItem != null) {
            downloadMenuItem.setVisible(show);

            downloadMenuItem.setTitle(download ? R.string.download : R.string.remove);
            downloadMenuItem.setIcon(download ? R.drawable.ic_menu_download
                    : R.drawable.ic_menu_delete);
        }
    }

    /**
     * Set whether the fragment should show the episode state icon to indicate
     * that the episode is new (not marked old).
     * 
     * @param show Whether to show the new icon.
     */
    public void setNewIconVisibility(boolean show) {
        this.showNewStateIcon = show;

        if (viewCreated)
            stateIconView.setVisibility(show ? VISIBLE : GONE);
    }

    /**
     * Set whether the fragment should show the download icon. You can call this
     * any time and can expect it to happen on fragment resume at the latest.
     * You also have to set the download icon state, <code>true</code> for
     * "is downloaded" and <code>false</code> for "is currently downloading".
     * 
     * @param show Whether to show the download menu item.
     * @param downloaded State of the download menu item (download / delete)
     */
    public void setDownloadIconVisibility(boolean show, boolean downloaded) {
        this.showDownloadIcon = show;
        this.downloadIconState = downloaded;

        // Only do it right away if resumed and menu item is available,
        // otherwise onResume or the menu creation callback will call us.
        if (viewCreated) {
            downloadIconView.setVisibility(show ? VISIBLE : GONE);
            downloadIconView.setImageResource(downloaded ?
                    R.drawable.ic_media_downloaded : R.drawable.ic_media_downloading);
        }
    }

    /**
     * Set whether the fragment should show the episode date for the episode
     * shown. Change will be reflected upon next call of
     * {@link #setEpisode(Episode)}
     * 
     * @param show Whether to show the episode date.
     */
    public void setShowEpisodeDate(boolean show) {
        this.showEpisodeDate = show;
    }

    private void updateUiElementVisibility() {
        if (viewCreated) {
            emptyView.setVisibility(currentEpisode == null ? VISIBLE : GONE);

            titleView.setVisibility(currentEpisode == null ? GONE : VISIBLE);
            subtitleView.setVisibility(currentEpisode == null ? GONE : VISIBLE);
            dividerView.setVisibility(currentEpisode == null ? GONE : VISIBLE);
            descriptionView.setVisibility(currentEpisode == null ? GONE : VISIBLE);
        }
    }
}
