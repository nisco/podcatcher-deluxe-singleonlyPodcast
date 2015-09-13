

package com.dawathqurantampodcast;

import static com.dawathqurantampodcast.view.fragments.AuthorizationFragment.USERNAME_PRESET_KEY;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.dawathqurantampodcast.listeners.OnLoadDownloadsListener;
import com.dawathqurantampodcast.listeners.OnLoadPlaylistListener;
import com.dawathqurantampodcast.listeners.OnLoadPodcastListener;
import com.dawathqurantampodcast.listeners.OnLoadPodcastLogoListener;
import com.dawathqurantampodcast.listeners.OnReorderEpisodeListener;
import com.dawathqurantampodcast.listeners.OnReverseSortingListener;
import com.dawathqurantampodcast.listeners.OnSelectPodcastListener;
import com.dawathqurantampodcast.listeners.OnToggleFilterListener;
import com.dawathqurantampodcast.model.tasks.LoadDownloadsTask;
import com.dawathqurantampodcast.model.tasks.LoadPlaylistTask;
import com.dawathqurantampodcast.model.tasks.remote.LoadPodcastTask.PodcastLoadError;
import com.dawathqurantampodcast.model.types.Episode;
import com.dawathqurantampodcast.model.types.Podcast;
import com.dawathqurantampodcast.model.types.Progress;
import com.dawathqurantampodcast.view.ContentSpinner;
import com.dawathqurantampodcast.view.fragments.AuthorizationFragment;
import com.dawathqurantampodcast.view.fragments.EpisodeListFragment;
import com.dawathqurantampodcast.view.fragments.AuthorizationFragment.OnEnterAuthorizationListener;
import com.dawathqurantampodcast.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Show list of episodes activity. This is thought of as an abstract activity
 * for an app only consisting of an episode list view, the player and the
 * ability to show an {@link ShowEpisodeActivity} on top. Sub-classes could
 * extend or simply show this layout.
 */
public abstract class EpisodeListActivity extends EpisodeActivity implements
        OnLoadPodcastListener, OnEnterAuthorizationListener, OnLoadPodcastLogoListener,
        OnSelectPodcastListener, OnLoadDownloadsListener, OnLoadPlaylistListener,
        OnReorderEpisodeListener, OnToggleFilterListener, OnReverseSortingListener {

    /** Key used to save the current content mode in bundle */
    public static final String MODE_KEY = "mode_key";
    /** Key used to store podcast URL in intent or bundle */
    public static final String PODCAST_URL_KEY = "podcast_url_key";

    /** The current episode list fragment */
    protected EpisodeListFragment episodeListFragment;
    /** The content mode selection spinner view */
    protected ContentSpinner contentSpinner;

    /** The current episode set (ordered) */
    private SortedSet<Episode> currentEpisodeSet = new TreeSet<Episode>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the content mode spinner and add it to the action bar
        contentSpinner = new ContentSpinner(this, this);
        getActionBar().setCustomView(contentSpinner);
        // Make sure the action bar has the right display options set
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_USE_LOGO);
    }

    @Override
    protected void findFragments() {
        super.findFragments();

        // The episode list fragment
        if (episodeListFragment == null)
            episodeListFragment = (EpisodeListFragment) findByTagId(R.string.episode_list_fragment_tag);

        // Make sure the episode fragment know our theme colors
        if (episodeListFragment != null)
            episodeListFragment.setThemeColors(themeColor, lightThemeColor);
    }

    @Override
    protected void registerListeners() {
        super.registerListeners();

        // We have to do this here instead of onCreate since we can only react
        // on the call-backs properly once we have our fragment
        podcastManager.addLoadPodcastListener(this);
        podcastManager.addLoadPodcastLogoListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Make sure dividers (if any) reflect selection state
        updateDividerUi();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Persist state of episode metadata
        episodeManager.saveState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        podcastManager.removeLoadPodcastListener(this);
        podcastManager.removeLoadPodcastLogoListener(this);
    }

    @Override
    public void onReverseOrder() {
        selection.setEpisodeOrderReversed(!selection.isEpisodeOrderReversed());

        updateEpisodeListUi();
        updateSortingUi();
    }

    @Override
    public void onToggleFilter() {
        selection.setEpisodeFilterEnabled(!selection.isEpisodeFilterEnabled());

        updateEpisodeListUi();
        updateFilterUi();
    }

    @Override
    public void onToggleLoad() {
        super.onToggleLoad();

        // This might have changed the state of some episodes
        episodeListFragment.refresh();
    }

    @Override
    public void onPodcastSelected(Podcast podcast) {
        selection.setPodcast(podcast);
        selection.setMode(ContentMode.SINGLE_PODCAST);

        this.currentEpisodeSet = new TreeSet<Episode>();

        switch (view) {
            case SMALL_LANDSCAPE:
                // This will go back to the list view in case we are showing
                // episode details
                getFragmentManager().popBackStackImmediate();
                // There is no break here on purpose, we need to run the code
                // below as well
            case LARGE_PORTRAIT:
            case LARGE_LANDSCAPE:
                // List fragment is visible, make it show progress UI
                episodeListFragment.resetAndSpin();
                // Update other UI
                updateSortingUi();
                updateFilterUi();
                updateDividerUi();

                // Load podcast
                podcastManager.load(podcast);

                break;
            case SMALL_PORTRAIT:
                // This case should be handled by sub-classes
                break;
        }
    }

    @Override
    public void onAllPodcastsSelected() {
        selection.resetPodcast();
        selection.setMode(ContentMode.ALL_PODCASTS);

        this.currentEpisodeSet = new TreeSet<Episode>();

        switch (view) {
            case SMALL_LANDSCAPE:
                // This will go back to the list view in case we are showing
                // episode details
                getFragmentManager().popBackStackImmediate();
                // There is no break here on purpose, we need to run the code
                // below as well
            case LARGE_PORTRAIT:
            case LARGE_LANDSCAPE:
                // List fragment is visible, make it show progress UI
                if (podcastManager.size() > 0)
                    episodeListFragment.resetAndSpin();
                else
                    episodeListFragment.resetUi();

                episodeListFragment.setShowPodcastNames(true);
                // Update other UI
                updateSortingUi();
                updateFilterUi();
                updateDividerUi();

                // Go load all podcasts
                for (Podcast podcast : podcastManager.getPodcastList())
                    podcastManager.load(podcast);

                // Action bar needs update after loading has started
                updateActionBar();
                break;
            case SMALL_PORTRAIT:
                // This case should be handled by sub-classes
                break;
        }
    }

    @Override
    public void onDownloadsSelected() {
        selection.resetPodcast();
        selection.setMode(ContentMode.DOWNLOADS);

        this.currentEpisodeSet = new TreeSet<Episode>();

        switch (view) {
            case SMALL_LANDSCAPE:
                // This will go back to the list view in case we are showing
                // episode details
                getFragmentManager().popBackStackImmediate();
                // There is no break here on purpose, we need to run the code
                // below as well
            case LARGE_PORTRAIT:
            case LARGE_LANDSCAPE:
                // List fragment is visible, make it show progress UI
                episodeListFragment.resetAndSpin();
                episodeListFragment.setShowPodcastNames(true);

                new LoadDownloadsTask(this).execute((Void) null);

                break;
            case SMALL_PORTRAIT:
                // This case should be handled by sub-classes
                break;
        }
    }

    @Override
    public void onDownloadsLoaded(List<Episode> downloads) {
        if (ContentMode.DOWNLOADS.equals(selection.getMode())) {
            currentEpisodeSet.addAll(downloads);
            updateEpisodeListUi();
        }

        // Update other UI
        updateActionBar();
        updateSortingUi();
        updateFilterUi();
        updateDividerUi();
    }

    @Override
    public void onPlaylistSelected() {
        selection.resetPodcast();
        selection.setMode(ContentMode.PLAYLIST);

        this.currentEpisodeSet = new TreeSet<Episode>(new Comparator<Episode>() {

            @Override
            public int compare(Episode one, Episode another) {
                return episodeManager.getPlaylistPosition(one)
                        - episodeManager.getPlaylistPosition(another);
            }
        });

        switch (view) {
            case SMALL_LANDSCAPE:
                // This will go back to the list view in case we are showing
                // episode details
                getFragmentManager().popBackStackImmediate();
                // There is no break here on purpose, we need to run the code
                // below as well
            case LARGE_PORTRAIT:
            case LARGE_LANDSCAPE:
                // List fragment is visible, make it show progress UI
                episodeListFragment.resetAndSpin();
                episodeListFragment.setShowPodcastNames(true);
                episodeListFragment.setEnableSwipeReorder(true);

                new LoadPlaylistTask(this).execute((Void) null);

                break;
            case SMALL_PORTRAIT:
                // This case should be handled by sub-classes
                break;
        }
    }

    @Override
    public void onPlaylistLoaded(List<Episode> playlist) {
        if (ContentMode.PLAYLIST.equals(selection.getMode())) {
            currentEpisodeSet.addAll(playlist);
            updateEpisodeListUi();
        }

        // Update other UI
        updateActionBar();
        updateSortingUi();
        updateFilterUi();
        updateDividerUi();
    }

    @Override
    public void onNoPodcastSelected() {
        selection.resetPodcast();
        selection.setMode(ContentMode.SINGLE_PODCAST);

        currentEpisodeSet.clear();

        if (!view.isSmallPortrait()) {
            // If there is an episode list visible, reset it
            episodeListFragment.selectNone();
            episodeListFragment.resetUi();

            // Update other UI
            updateSortingUi();
            updateFilterUi();
            updateDividerUi();
        }
    }

    @Override
    public void onPodcastLoadProgress(Podcast podcast, Progress progress) {
        try {
            if (selection.isSingle() && podcast.equals(selection.getPodcast()))
                episodeListFragment.showProgress(progress);
        } catch (NullPointerException nep) {
            // When the load progress comes to quickly, the fragment might not
            // be present yet, pass...
        }
    }

    @Override
    public void onPodcastLoaded(Podcast podcast) {
        // Update list fragment to show episode list
        if (selection.isAll() || selection.isSingle() && podcast.equals(selection.getPodcast())) {
            currentEpisodeSet.addAll(podcast.getEpisodes());
            addSpecialEpisodes(podcast);
            updateEpisodeListUi();
        }

        // Additionally, if on large device, process clever selection update
        if (!view.isSmall()) {
            updateEpisodeListSelection();
            updateDividerUi();
        }

        // We may want to auto-download the latest episode
        if (shouldAutoDownloadLatestEpisode(podcast))
            episodeManager.download(podcast.getEpisodes().get(0));

        // Update other UI
        updateActionBar();
        updateSortingUi();
        updateFilterUi();
    }

    @Override
    public void onPodcastLoadFailed(final Podcast failedPodcast, PodcastLoadError code) {
        // The podcast we are waiting for failed to load
        if (selection.isSingle() && failedPodcast.equals(selection.getPodcast())) {
            // Podcast needs authorization
            if (code == PodcastLoadError.AUTH_REQUIRED) {
                // Ask the user for authorization
                final AuthorizationFragment authorizationFragment = new AuthorizationFragment();

                if (failedPodcast.getUsername() != null) {
                    // Create bundle to make dialog aware of username to pre-set
                    final Bundle args = new Bundle();
                    args.putString(USERNAME_PRESET_KEY, failedPodcast.getUsername());
                    authorizationFragment.setArguments(args);
                }

                authorizationFragment.show(getFragmentManager(), AuthorizationFragment.TAG);
            } else {
                addSpecialEpisodes(failedPodcast);

                // We might at least be able to show downloaded/playlisted
                // episodes
                if (currentEpisodeSet.size() > 0)
                    updateEpisodeListUi(true);
                else
                    episodeListFragment.showLoadFailed(code);
            }
        }
        // One of potentially many podcasts failed
        else if (selection.isAll()) {
            addSpecialEpisodes(failedPodcast);
            updateEpisodeListUi();

            // The last podcast failed and we have no episodes at all
            if (podcastManager.getLoadCount() == 0 && currentEpisodeSet.isEmpty())
                episodeListFragment.showLoadAllFailed();
            // One of many podcasts failed to load
            else
                showToast(getString(R.string.podcast_load_multiple_error, failedPodcast.getName()));
        }

        // Update other UI
        updateActionBar();
        updateSortingUi();
        updateFilterUi();
    }

    @Override
    public void onSubmitAuthorization(String username, String password) {
        if (selection.isPodcastSet()) {
            final Podcast podcast = selection.getPodcast();
            podcastManager.setCredentials(podcast, username, password);

            // We need to unselect the podcast here in order to make it
            // selectable again...
            selection.setPodcast(null);

            onPodcastSelected(podcast);
        }
    }

    @Override
    public void onCancelAuthorization() {
        if (selection.isPodcastSet())
            onPodcastLoadFailed(selection.getPodcast(), PodcastLoadError.ACCESS_DENIED);
    }

    @Override
    public void onPodcastLogoLoaded(Podcast podcast) {
        // pass
    }

    @Override
    public void onPodcastLogoLoadFailed(Podcast podcast) {
        // pass
    }

    @Override
    public void onEpisodeSelected(Episode selectedEpisode) {
        onEpisodeSelected(selectedEpisode, false);
    }

    protected void onEpisodeSelected(Episode selectedEpisode, boolean forceReload) {
        if (forceReload || !selectedEpisode.equals(selection.getEpisode())) {
            super.onEpisodeSelected(selectedEpisode);

            if (!view.isSmall())
                // Make sure selection matches in list fragment
                updateEpisodeListSelection();
            else if (view.isSmallPortrait()) {
                // Send intent to open episode as a new activity
                Intent intent = new Intent(this, ShowEpisodeActivity.class);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }

            updateDividerUi();
        }
    }

    @Override
    public void onNoEpisodeSelected() {
        onNoEpisodeSelected(false);
    }

    protected void onNoEpisodeSelected(boolean forceReload) {
        if (forceReload || selection.getEpisode() != null) {
            super.onNoEpisodeSelected();

            if (episodeListFragment != null)
                episodeListFragment.selectNone();

            updateDividerUi();
        }
    }

    @Override
    public void onMoveEpisodeDown(Episode episode) {
        // Only accept movements if in playlist mode
        if (ContentMode.PLAYLIST.equals(selection.getMode())) {
            final int currentPosition = episodeManager.getPlaylistPosition(episode);
            final int playlistLength = episodeManager.getPlaylistSize();
            // Only move episode if it is actually in the playlist
            if (currentPosition >= 0) {
                episodeManager.removeFromPlaylist(episode);
                // If the episode is at the end of the playlist, send it back up
                episodeManager.insertAtPlaylistPosition(episode,
                        currentPosition == playlistLength - 1 ? 0 : currentPosition + 1);
            }

            // Clear and reset the playlist
            currentEpisodeSet.clear();
            onPlaylistLoaded(episodeManager.getPlaylist());
        }
    }

    @Override
    public void onMoveEpisodeUp(Episode episode) {
        // Only accept movements if in playlist mode
        if (ContentMode.PLAYLIST.equals(selection.getMode())) {
            final int currentPosition = episodeManager.getPlaylistPosition(episode);
            // Only move episode if it is actually in the playlist
            if (currentPosition > 0) {
                episodeManager.removeFromPlaylist(episode);
                episodeManager.insertAtPlaylistPosition(episode, currentPosition - 1);
            } // When at top, move to the bottom end
            else if (currentPosition == 0) {
                episodeManager.removeFromPlaylist(episode);
                episodeManager.appendToPlaylist(episode);
            }

            // Clear and reset the playlist
            currentEpisodeSet.clear();
            onPlaylistLoaded(episodeManager.getPlaylist());
        }
    }

    @Override
    public void onDownloadProgress(Episode episode, int percent) {
        if (!view.isSmallPortrait())
            super.onDownloadProgress(episode, percent);

        // Check whether the episode is potentially currently displayed
        if (currentEpisodeSet.contains(episode))
            episodeListFragment.showProgress(episode, percent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);

        if (key.equals(SettingsActivity.KEY_THEME_COLOR)) {
            // Make the UI reflect the change
            if (episodeListFragment != null)
                episodeListFragment.setThemeColors(themeColor, lightThemeColor);
            updateDividerUi();
        }
    }

    /**
     * Make sure the episode list selection matches current state.
     */
    protected void updateEpisodeListSelection() {
        if (!view.isSmall())
            // Make sure the episode selection in the list is updated
            episodeListFragment.select(selection.getEpisode());
        else
            episodeListFragment.selectNone();
    }

    /**
     * Update the sorting menu icon visibility.
     */
    protected void updateSortingUi() {
        episodeListFragment.setSortMenuItemVisibility(currentEpisodeSet.size() > 1 &&
                !ContentMode.PLAYLIST.equals(selection.getMode()),
                selection.isEpisodeOrderReversed());
    }

    /**
     * Update the filter menu icon visibility.
     */
    protected void updateFilterUi() {
        episodeListFragment.setFilterMenuItemVisibility(!currentEpisodeSet.isEmpty() &&
                !ContentMode.PLAYLIST.equals(selection.getMode()),
                selection.isEpisodeFilterEnabled());
    }

    @Override
    protected void updateDownloadUi() {
        if (!view.isSmallPortrait())
            super.updateDownloadUi();

        episodeListFragment.refresh();
    }

    @Override
    protected void updatePlaylistUi() {
        if (!view.isSmallPortrait())
            super.updatePlaylistUi();

        episodeListFragment.refresh();
    }

    @Override
    protected void updateStateUi() {
        if (!view.isSmallPortrait())
            super.updateStateUi();

        episodeListFragment.refresh();
    }

    /**
     * Update the divider views to reflect current selection state.
     */
    protected void updateDividerUi() {
        colorDivider(R.id.divider_first, selection.isPodcastSet() || !selection.isSingle());
        colorDivider(R.id.divider_second, selection.isEpisodeSet() &&
                currentEpisodeSet.contains(selection.getEpisode()));
    }

    /**
     * Set the action bar subtitle to reflect multiple podcast load progress
     */
    protected void updateActionBarSubtitleOnMultipleLoad() {
        final int podcastCount = podcastManager.size();
        final int loadingPodcastCount = podcastManager.getLoadCount();

        // Load finished for all podcasts and there are episode
        if (loadingPodcastCount == 0 && !currentEpisodeSet.isEmpty()) {
            final int episodeCount = currentEpisodeSet.size();

            if (episodeCount == 0)
                contentSpinner.setSubtitle(null);
            else
                contentSpinner.setSubtitle(getResources()
                        .getQuantityString(R.plurals.episodes, episodeCount, episodeCount));
        }
        // Load finished but no episodes
        else if (loadingPodcastCount == 0)
            contentSpinner.setSubtitle(getResources()
                    .getQuantityString(R.plurals.podcasts, podcastCount, podcastCount));
        // Load in progress
        else
            contentSpinner.setSubtitle(getString(R.string.podcast_load_multiple_progress,
                    (podcastCount - loadingPodcastCount), podcastCount));
    }

    /**
     * Set the current episode list to show in the episode list fragment using
     * {@link #currentEpisodeSet} as the basis. This will filter and reverse the
     * list as needed.
     */
    private void updateEpisodeListUi() {
        updateEpisodeListUi(false);
    }

    /**
     * Set the current episode list to show in the episode list fragment using
     * {@link #currentEpisodeSet} as the basis. This will filter and reverse the
     * list as needed.
     * 
     * @param showPodcastLoadFailedWarning Whether the episode list fragment
     *            should show a warning that some episodes might not be
     *            displayed.
     */
    private void updateEpisodeListUi(boolean showPodcastLoadFailedWarning) {
        final List<Episode> filteredList = new ArrayList<Episode>(currentEpisodeSet);

        // Further refine the episode list if not in playlist mode
        if (!ContentMode.PLAYLIST.equals(selection.getMode())) {
            // Apply the filter
            if (selection.isEpisodeFilterEnabled()) {
                Iterator<Episode> iterator = filteredList.iterator();

                while (iterator.hasNext())
                    if (episodeManager.getState(iterator.next()))
                        iterator.remove();
            }

            // We might need to reverse the order of our list,
            // but there is no need for sorting since we already come
            // from a sorted set.
            if (selection.isEpisodeOrderReversed())
                Collections.reverse(filteredList);
        }

        // Make sure the episode list fragment show the right empty view
        if (ContentMode.DOWNLOADS.equals(selection.getMode()))
            episodeListFragment.setEmptyStringId(R.string.downloads_none);
        else if (ContentMode.PLAYLIST.equals(selection.getMode()))
            episodeListFragment.setEmptyStringId(R.string.playlist_empty);
        else if (selection.isEpisodeFilterEnabled()
                && filteredList.isEmpty() && !currentEpisodeSet.isEmpty())
            episodeListFragment.setEmptyStringId(R.string.episodes_no_new);
        else if (selection.isAll())
            episodeListFragment.setEmptyStringId(R.string.episode_none_all_podcasts);
        else
            episodeListFragment.setEmptyStringId(R.string.episode_none);

        // Make sure the episode list fragment show the right info box
        if (ContentMode.PLAYLIST.equals(selection.getMode()) && filteredList.size() > 1)
            episodeListFragment.setShowTopInfoBox(true,
                    getString(R.string.playlist_swipe_reorder));
        else if (showPodcastLoadFailedWarning)
            episodeListFragment.setShowTopInfoBox(true, getString(R.string.podcast_load_error));
        else if (selection.isEpisodeFilterEnabled()) {
            final int filteredCount = currentEpisodeSet.size() - filteredList.size();

            episodeListFragment.setShowTopInfoBox(
                    filteredCount > 0, getResources().getQuantityString(
                            R.plurals.episodes_filtered, filteredCount, filteredCount));
        }
        else
            episodeListFragment.setShowTopInfoBox(false, null);

        // Finally set the list and make sure selection matches
        episodeListFragment.setEpisodeList(filteredList);
        updateEpisodeListSelection();
    }

    private void colorDivider(int dividerViewId, boolean applyColor) {
        if (getWindow() != null && getWindow().findViewById(dividerViewId) != null) {
            View divider = getWindow().findViewById(dividerViewId);

            if (applyColor)
                divider.setBackgroundColor(themeColor);
            else
                divider.setBackgroundColor(getResources().getColor(R.color.divider_off));
        }
    }

    private void addSpecialEpisodes(Podcast podcast) {
        if (podcast != null) {
            // Downloads
            for (Episode episode : episodeManager.getDownloads())
                if (podcast.equals(episode.getPodcast()))
                    currentEpisodeSet.add(episode);

            // Playlist
            for (Episode episode : episodeManager.getPlaylist())
                if (podcast.equals(episode.getPodcast()))
                    currentEpisodeSet.add(episode);
        }
    }

    private boolean shouldAutoDownloadLatestEpisode(Podcast podcast) {
        if (podcast == null || podcast.getEpisodeCount() == 0)
            return false;
        else {
            final Episode latestEpisode = podcast.getEpisodes().get(0);

            return PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(SettingsActivity.AUTO_DOWNLOAD_KEY, false)
                    && ((Podcatcher) getApplication()).isOnFastConnection()
                    && !episodeManager.getState(latestEpisode);
        }
    }
}
