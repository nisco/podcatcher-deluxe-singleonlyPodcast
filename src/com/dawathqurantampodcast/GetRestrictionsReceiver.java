

package com.dawathqurantampodcast;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.os.Build;
import android.os.Bundle;
import com.dawathqurantampodcast.R;

import java.util.ArrayList;

/**
 * Creates the app's configurable restrictions for restricted users.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class GetRestrictionsReceiver extends BroadcastReceiver {

    /** Key to identify the hide explicit restriction */
    public static final String BLOCK_EXPLICIT_RESTRICTION_KEY = "block_explicit";

    @Override
    public void onReceive(final Context context, Intent intent) {
        final PendingResult result = goAsync();

        new Thread() {
            public void run() {
                final Bundle extras = new Bundle();

                // Create the restriction
                final RestrictionEntry hideExplicit = new RestrictionEntry(
                        BLOCK_EXPLICIT_RESTRICTION_KEY, true);
                hideExplicit.setTitle(context.getString(R.string.podcast_block_explicit));

                // Put everything together and send it back
                final ArrayList<RestrictionEntry> list = new ArrayList<RestrictionEntry>();
                list.add(hideExplicit);

                extras.putParcelableArrayList(Intent.EXTRA_RESTRICTIONS_LIST, list);
                result.setResult(Activity.RESULT_OK, null, extras);
                result.finish();
            };
        }.start();
    }
}
