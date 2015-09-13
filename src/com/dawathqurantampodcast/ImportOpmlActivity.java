

package com.dawathqurantampodcast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.dawathqurantampodcast.SelectFileActivity.SelectionMode;
import com.dawathqurantampodcast.listeners.OnLoadPodcastListListener;
import com.dawathqurantampodcast.model.tasks.LoadPodcastListTask;
import com.dawathqurantampodcast.model.types.Podcast;
import com.dawathqurantampodcast.R;


import java.io.File;
import java.util.List;

/**
 * Activity that imports podcasts from an OPML file.
 */
public class ImportOpmlActivity extends BaseActivity implements OnLoadPodcastListListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Only do this initial creation to avoid multiple file selection
        // dialogs
        if (savedInstanceState == null) {
            Intent selectFolderIntent = new Intent(this, SelectFileActivity.class);
            // Set file deialog mode
            selectFolderIntent
                    .putExtra(SelectFileActivity.SELECTION_MODE_KEY, SelectionMode.FILE);
            // Set initial folder selection
            final File downloadDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadDir.mkdirs();

            selectFolderIntent
                    .putExtra(SelectFileActivity.INITIAL_PATH_KEY, downloadDir.getAbsolutePath());

            startActivityForResult(selectFolderIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode && data != null) {
            // Get file handle
            File opmlFile = new File(data.getStringExtra(SelectFileActivity.RESULT_PATH_KEY));

            // Run the import task
            LoadPodcastListTask importTask = new LoadPodcastListTask(this, this);
            importTask.setCustomLocation(opmlFile);

            importTask.execute();
        }
        // Nothing more to do
        else
            finish();
    }

    @Override
    public void onPodcastListLoaded(List<Podcast> podcastList) {
        // Iff the list is empty, the import went wrong
        if (podcastList.isEmpty())
            showToast(getString(R.string.opml_import_failed));
        else
            // Add all podcasts to the list
            for (Podcast podcast : podcastList)
                podcastManager.addPodcast(podcast);

        // End the activity
        finish();
    }
}
