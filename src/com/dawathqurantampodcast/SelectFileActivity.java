

package com.dawathqurantampodcast;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;

import com.dawathqurantampodcast.listeners.OnSelectFileListener;
import com.dawathqurantampodcast.preferences.DownloadFolderPreference;
import com.dawathqurantampodcast.view.fragments.SelectFileFragment;
import com.dawathqurantampodcast.R;


import java.io.File;

/**
 * Non-UI activity to select files and folders. Use the intent and constants
 * defined here to configure its behavior. Start the activity with
 * {@link Activity#startActivityForResult(Intent, int)} to be alerted on
 * selection.
 */
public class SelectFileActivity extends BaseActivity implements OnSelectFileListener,
        OnCancelListener {

    /** The tag we identify our file selection fragment with */
    private static final String SELECT_FILE_FRAGMENT_TAG = "select_file";

    /** The key to store initial path under in intent */
    public static final String INITIAL_PATH_KEY = "initial_path";
    /** The key to store result path under in intent */
    public static final String RESULT_PATH_KEY = "result_path";

    /** The key to store wanted selection mode under in intent */
    public static final String SELECTION_MODE_KEY = "file_selection_mode";
    /** The current selection mode */
    private SelectionMode selectionMode = SelectionMode.FILE;

    /** The selection mode options */
    public static enum SelectionMode {
        /** File selection */
        FILE,

        /** Folder selection */
        FOLDER
    }

    /** The fragment containing the select file UI */
    private SelectFileFragment selectFileFragment;

    @Override
    protected void onStart() {
        super.onStart();

        // Try to find existing fragment
        selectFileFragment = (SelectFileFragment) getFragmentManager().findFragmentByTag(
                SELECT_FILE_FRAGMENT_TAG);

        // No fragment found, create it
        if (selectFileFragment == null) {
            selectFileFragment = new SelectFileFragment();
            selectFileFragment.setStyle(DialogFragment.STYLE_NORMAL,
                    android.R.style.Theme_Holo_Light_Dialog);
        }

        // Use getIntent() to configure selection mode
        final SelectionMode modeFromIntent =
                (SelectionMode) getIntent().getSerializableExtra(SELECTION_MODE_KEY);
        if (modeFromIntent != null)
            selectionMode = modeFromIntent;
        // Set the selection mode
        selectFileFragment.setSelectionMode(selectionMode);

        // Use getIntent() to configure initial path
        final String initialPathString = getIntent().getStringExtra(INITIAL_PATH_KEY);
        // Set the initial path
        if (initialPathString != null && new File(initialPathString).exists())
            selectFileFragment.setPath(new File(initialPathString));
        else {
            // No path set, use default
            final File podcastDir = DownloadFolderPreference.getDefaultDownloadFolder();
            podcastDir.mkdirs();

            selectFileFragment.setPath(podcastDir);
        }

        // Set theme colors
        selectFileFragment.setThemeColors(themeColor, lightThemeColor);

        // Show the fragment
        selectFileFragment.show(getFragmentManager(), SELECT_FILE_FRAGMENT_TAG);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onFileSelected(File selectedFile) {
        selectFileFragment.dismiss();

        Intent result = new Intent();
        result.putExtra(RESULT_PATH_KEY, selectedFile.getAbsolutePath());

        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onDirectoryChanged(File path) {
        getIntent().putExtra(INITIAL_PATH_KEY, path.getAbsolutePath());
    }

    @Override
    public void onAccessDenied(File path) {
        showToast(getString(R.string.file_select_access_denied));
    }
}
