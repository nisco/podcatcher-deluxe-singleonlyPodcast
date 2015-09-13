

package com.dawathqurantampodcast.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.preference.Preference;
import android.util.AttributeSet;

import com.dawathqurantampodcast.SelectFileActivity;
import com.dawathqurantampodcast.SelectFileActivity.SelectionMode;


import java.io.File;

/**
 * The custom download folder preference. Shows folder selection dialog when
 * clicked.
 */
public class DownloadFolderPreference extends Preference {

    /** Our request code for the folder selection dialog */
    public static final int REQUEST_CODE = 99;

    /** Currently set download folder */
    private File downloadFolder;

    /**
     * Create new preference.
     * 
     * @param context Context the preference lives in.
     * @param attrs Values from the XML.
     */
    public DownloadFolderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        // We want to init this before any other method is called to avoid a
        // situation where downloadFolder == null
        onSetInitialValue(false, null);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        // The default is the public podcast directory
        downloadFolder = new File(getPersistedString(getDefaultDownloadFolder().getAbsolutePath()));
    }

    @Override
    protected void onClick() {
        // Create select folder intent
        Intent selectFolderIntent = new Intent(getContext(), SelectFileActivity.class);
        selectFolderIntent
                .putExtra(SelectFileActivity.SELECTION_MODE_KEY, SelectionMode.FOLDER);
        selectFolderIntent
                .putExtra(SelectFileActivity.INITIAL_PATH_KEY, downloadFolder.getAbsolutePath());

        // Start activity. Result will be caught by the SettingsActivity.
        ((Activity) getContext()).startActivityForResult(selectFolderIntent, REQUEST_CODE);
    }

    @Override
    public CharSequence getSummary() {
        return downloadFolder.getAbsolutePath();
    }

    /**
     * Set new value for the preference.
     * 
     * @param newFolder Updated folder to use.
     */
    public void update(File newFolder) {
        this.downloadFolder = newFolder;

        if (newFolder != null)
            persistString(newFolder.getAbsolutePath());
    }

    /**
     * @return The default podcast episode download folder.
     */
    public static File getDefaultDownloadFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
    }
}
