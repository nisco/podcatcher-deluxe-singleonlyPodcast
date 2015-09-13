

package com.dawathqurantampodcast;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.dawathqurantampodcast.SelectFileActivity.SelectionMode;
import com.dawathqurantampodcast.listeners.OnStorePodcastListListener;
import com.dawathqurantampodcast.model.tasks.StorePodcastListTask;
import com.dawathqurantampodcast.model.types.Podcast;
import com.dawathqurantampodcast.R;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity that exports a list of selected podcasts to an OPML file.
 */
public class ExportOpmlActivity extends BaseActivity implements OnStorePodcastListListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the list of positions to export
        List<Integer> positions = getIntent().getIntegerArrayListExtra(PODCAST_POSITION_LIST_KEY);

        // If list is there, export podcasts at given positions
        if (positions != null) {
            // Only do this initial creation to avoid multiple folder selection
            // dialogs
            if (savedInstanceState == null) {
                Intent selectFolderIntent = new Intent(this, SelectFileActivity.class);
                selectFolderIntent
                        .putExtra(SelectFileActivity.SELECTION_MODE_KEY, SelectionMode.FOLDER);

                startActivityForResult(selectFolderIntent, 1);
            }
        } else
            // Nothing to do
            finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode && data != null) {
            // Get the list of positions to export
            List<Integer> positions = getIntent()
                    .getIntegerArrayListExtra(PODCAST_POSITION_LIST_KEY);

            // If list is there, export podcasts at given positions
            if (positions != null) {
                List<Podcast> podcasts = new ArrayList<Podcast>();

                for (Integer position : positions)
                    podcasts.add(podcastManager.getPodcastList().get(position));

                StorePodcastListTask exportTask = new StorePodcastListTask(this, this);
                // Determine and set output folder
                File exportFolder = new File(
                        data.getStringExtra(SelectFileActivity.RESULT_PATH_KEY));
                exportTask.setCustomLocation(exportFolder);

                exportTask.execute(podcasts);
            }
        }

        // Make sure we finish here
        finish();
    }

    @Override
    public void onPodcastListStored(List<Podcast> podcastList, File outputFile) {
        showToast(getString(R.string.opml_export_success, outputFile.getAbsolutePath()),
                Toast.LENGTH_LONG);
    }

    @Override
    public void onPodcastListStoreFailed(List<Podcast> podcastList, File outputFile,
            Exception exception) {
        showToast(getString(R.string.opml_export_failed));
    }
}
