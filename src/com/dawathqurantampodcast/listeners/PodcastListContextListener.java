

package com.dawathqurantampodcast.listeners;

import static com.dawathqurantampodcast.BaseActivity.PODCAST_POSITION_LIST_KEY;
import static com.dawathqurantampodcast.view.fragments.AuthorizationFragment.USERNAME_PRESET_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;

import com.dawathqurantampodcast.ExportOpmlActivity;
import com.dawathqurantampodcast.RemovePodcastActivity;
import com.dawathqurantampodcast.adapters.PodcastListAdapter;
import com.dawathqurantampodcast.model.PodcastManager;
import com.dawathqurantampodcast.model.types.Podcast;
import com.dawathqurantampodcast.view.fragments.AuthorizationFragment;
import com.dawathqurantampodcast.view.fragments.PodcastListFragment;
import com.dawathqurantampodcast.view.fragments.AuthorizationFragment.OnEnterAuthorizationListener;
import com.dawathqurantampodcast.R;


import java.util.ArrayList;

/**
 * Listener for the podcast list context mode.
 */
public class PodcastListContextListener implements MultiChoiceModeListener {

    /** The owning fragment */
    private final PodcastListFragment fragment;

    /** The edit authorization menu item */
    private MenuItem editAuthMenuItem;

    /**
     * Create new listener for the podcast list context mode.
     * 
     * @param fragment The podcast list fragment to call back to.
     */
    public PodcastListContextListener(PodcastListFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.podcast_list_context, menu);

        editAuthMenuItem = menu.findItem(R.id.edit_auth_contextmenuitem);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        update(mode);

        return true;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
        // Get the checked positions
        SparseBooleanArray checkedItems = fragment.getListView().getCheckedItemPositions();
        ArrayList<Integer> positions = new ArrayList<Integer>();

        // Prepare list of podcast positions to send to the triggered activity
        for (int index = 0; index < fragment.getListView().getCount(); index++)
            if (checkedItems.get(index))
                positions.add(index);

        switch (item.getItemId()) {
            case R.id.edit_auth_contextmenuitem:
                // There is only one podcast checked...
                final Podcast podcast =
                        (Podcast) fragment.getListAdapter().getItem(positions.get(0));

                // Show dialog for authorization
                final AuthorizationFragment authorizationFragment = new AuthorizationFragment();

                // Create bundle to make dialog aware of username to pre-set
                if (podcast.getUsername() != null) {
                    final Bundle args = new Bundle();
                    args.putString(USERNAME_PRESET_KEY, podcast.getUsername());
                    authorizationFragment.setArguments(args);
                }

                // Set the callback
                authorizationFragment.setListener(new OnEnterAuthorizationListener() {

                    @Override
                    public void onSubmitAuthorization(String username, String password) {
                        PodcastManager.getInstance().setCredentials(podcast, username, password);

                        // Action picked, so close the CAB
                        mode.finish();
                    }

                    @Override
                    public void onCancelAuthorization() {
                        // No action
                    }
                });

                // Finally show the dialog
                authorizationFragment
                        .show(fragment.getFragmentManager(), AuthorizationFragment.TAG);

                return true;
            case R.id.podcast_remove_contextmenuitem:
                // Prepare deletion activity
                Intent remove = new Intent(fragment.getActivity(), RemovePodcastActivity.class);
                remove.putIntegerArrayListExtra(PODCAST_POSITION_LIST_KEY, positions);

                // Go remove podcasts
                fragment.startActivity(remove);

                // Action picked, so close the CAB
                mode.finish();
                return true;
            case R.id.opml_export_contextmenuitem:
                // Prepare export activity
                Intent export = new Intent(fragment.getActivity(), ExportOpmlActivity.class);
                export.putIntegerArrayListExtra(PODCAST_POSITION_LIST_KEY, positions);

                // Go export podcasts
                fragment.startActivity(export);

                // Action picked, so close the CAB
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        ((PodcastListAdapter) fragment.getListAdapter()).setCheckedPositions(null);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        update(mode);
    }

    private void update(ActionMode mode) {
        // Let list adapter know which items to mark checked (row color)
        ((PodcastListAdapter) fragment.getListAdapter()).setCheckedPositions(
                fragment.getListView().getCheckedItemPositions());

        // Update the mode title text
        final int checkedItemCount = fragment.getListView().getCheckedItemCount();
        mode.setTitle(fragment.getResources()
                .getQuantityString(R.plurals.podcasts, checkedItemCount, checkedItemCount));

        // Show/hide edit auth menu item
        editAuthMenuItem.setVisible(checkedItemCount == 1);
    }
}
