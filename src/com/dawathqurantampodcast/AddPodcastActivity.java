

package com.dawathqurantampodcast;

import static com.dawathqurantampodcast.EpisodeListActivity.PODCAST_URL_KEY;
import static com.dawathqurantampodcast.view.fragments.AuthorizationFragment.USERNAME_PRESET_KEY;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;

import com.dawathqurantampodcast.listeners.OnAddPodcastListener;
import com.dawathqurantampodcast.listeners.OnLoadPodcastListener;
import com.dawathqurantampodcast.model.tasks.remote.LoadPodcastTask.PodcastLoadError;
import com.dawathqurantampodcast.model.types.Podcast;
import com.dawathqurantampodcast.model.types.Progress;
import com.dawathqurantampodcast.view.fragments.AddPodcastFragment;
import com.dawathqurantampodcast.view.fragments.AuthorizationFragment;
import com.dawathqurantampodcast.view.fragments.AuthorizationFragment.OnEnterAuthorizationListener;


/**
 * Add new podcast(s) activity. This simply shows the add podcast fragment. To
 * preset the feed url edittext, start this activity with an intent that has the
 * feed URL set as its {@link Intent#getData()} return value.
 */
public class AddPodcastActivity extends BaseActivity implements OnLoadPodcastListener,
        OnAddPodcastListener, OnCancelListener, OnEnterAuthorizationListener {

    /** The tag we identify our add podcast fragment with */
    private static final String ADD_PODCAST_FRAGMENT_TAG = "add_podcast";

    /** The fragment containing the add URL UI */
    private AddPodcastFragment addPodcastFragment;

    /** Key to find current load url under */
    private static final String LOADING_URL_KEY = "LOADING_URL";
    /** The URL of the podcast we are currently loading (if any) */
    private String currentLoadUrl;
    /** The last user name that was put in */
    private String lastUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Listen to podcast load events to update UI
        podcastManager.addLoadPodcastListener(this);

        // If we are coming from a config change, we need to know whether there
        // is currently a podcast loading.
        if (savedInstanceState != null)
            currentLoadUrl = savedInstanceState.getString(LOADING_URL_KEY);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Try to find existing fragment
        addPodcastFragment = (AddPodcastFragment) getFragmentManager().findFragmentByTag(
                ADD_PODCAST_FRAGMENT_TAG);

        // No fragment found, create it
        if (addPodcastFragment == null) {
            addPodcastFragment = new AddPodcastFragment();
            addPodcastFragment.setStyle(DialogFragment.STYLE_NORMAL,
                    android.R.style.Theme_Holo_Light_Dialog);

            // Show the fragment
            addPodcastFragment.show(getFragmentManager(), ADD_PODCAST_FRAGMENT_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Make sure we know which podcast we are loading (if any)
        outState.putString(LOADING_URL_KEY, currentLoadUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister from data manager
        podcastManager.removeLoadPodcastListener(this);
    }

    @Override
    public void onAddPodcast(String podcastUrl) {
        // Try to load the given online resource
        final Podcast newPodcast = new Podcast(null, podcastUrl);

        // If the podcast is present, select it
        if (podcastManager.contains(newPodcast)) {
            Intent intent = new Intent(this, PodcastActivity.class);
            intent.putExtra(EpisodeListActivity.MODE_KEY, ContentMode.SINGLE_PODCAST);
            intent.putExtra(PODCAST_URL_KEY, newPodcast.getUrl());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);
            finish();
        }
        // Otherwise try to load it
        else {
            // We need to keep note which podcast we are loading
            currentLoadUrl = podcastUrl;
        
            podcastManager.load(newPodcast);
        }
    }

    @Override
    public void onPodcastLoadProgress(Podcast podcast, Progress progress) {
        if (isCurrentlyLoadingPodcast(podcast))
            addPodcastFragment.showProgress(progress);
    }

    @Override
    public void onPodcastLoaded(Podcast podcast) {
        if (isCurrentlyLoadingPodcast(podcast)) {
            // Reset current load url
            currentLoadUrl = null;

            // Add podcast and finish the activity
            podcastManager.addPodcast(podcast);
            finish();
        }
    }

    @Override
    public void onPodcastLoadFailed(final Podcast podcast, PodcastLoadError code) {
        if (isCurrentlyLoadingPodcast(podcast)) {
            // Podcasts need authorization
            if (code == PodcastLoadError.AUTH_REQUIRED) {
                // Ask the user for authorization
                final AuthorizationFragment authorizationFragment = new AuthorizationFragment();

                if (lastUserName != null) {
                    // Create bundle to make dialog aware of username to pre-set
                    final Bundle args = new Bundle();
                    args.putString(USERNAME_PRESET_KEY, lastUserName);
                    authorizationFragment.setArguments(args);
                }

                authorizationFragment.show(getFragmentManager(), AuthorizationFragment.TAG);
            }
            // Load failed for some other reason
            else {
                // Reset current load url
                currentLoadUrl = null;

                // Show failed UI
                addPodcastFragment.showPodcastLoadFailed(code);
            }
        }
    }

    @Override
    public void onSubmitAuthorization(String username, String password) {
        // We need to keep that in order to pre-fill next time
        lastUserName = username;

        final Podcast newPodcast = new Podcast(null, currentLoadUrl);
        newPodcast.setUsername(username);
        newPodcast.setPassword(password);

        podcastManager.load(newPodcast);
    }

    @Override
    public void onCancelAuthorization() {
        onPodcastLoadFailed(new Podcast(null, currentLoadUrl), PodcastLoadError.ACCESS_DENIED);
    }

    @Override
    public void onShowSuggestions() {
        addPodcastFragment.dismiss();
        finish();

        // TODO what happens if we are currently loading?
        startActivity(new Intent(this, AddSuggestionActivity.class));
    }

    @Override
    public void onImportOpml() {
        addPodcastFragment.dismiss();
        finish();

        // TODO what happens if we are currently loading?
        startActivity(new Intent(this, ImportOpmlActivity.class));
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // TODO Cancel the load task in podcast manager if running
        finish();
    }

    private boolean isCurrentlyLoadingPodcast(Podcast podcast) {
        return podcast.getUrl().equalsIgnoreCase(currentLoadUrl);
    }
}
