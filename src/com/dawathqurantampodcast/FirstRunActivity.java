

package com.dawathqurantampodcast;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.dawathqurantampodcast.view.fragments.FirstRunFragment;
import com.dawathqurantampodcast.view.fragments.FirstRunFragment.FirstRunListener;


/**
 * Activity to run on the very first app start. Welcomes the user and gives some
 * hints.
 */
public class FirstRunActivity extends BaseActivity implements FirstRunListener {

    /** The tag we identify our fragment with */
    private static final String FIRST_RUN_FRAGMENT_TAG = "first_run";

    /** The fragment containing the UI */
    private FirstRunFragment firstRunFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure we only run once
        preferences.edit().putBoolean(SettingsActivity.KEY_FIRST_RUN, false).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Try to find existing fragment
        firstRunFragment = (FirstRunFragment) getFragmentManager().findFragmentByTag(
                FIRST_RUN_FRAGMENT_TAG);

        // No fragment found, create it
        if (firstRunFragment == null) {
            firstRunFragment = new FirstRunFragment();
            firstRunFragment.setStyle(DialogFragment.STYLE_NORMAL,
                    android.R.style.Theme_Holo_Light_Dialog);

            // Show the fragment
            firstRunFragment.show(getFragmentManager(), FIRST_RUN_FRAGMENT_TAG);
        }
    }

    @Override
    public void onAddPodcasts() {
        firstRunFragment.dismiss();
        finish();

        startActivity(new Intent(this, AddPodcastActivity.class));
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }
}
