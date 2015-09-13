

package com.dawathqurantampodcast.listeners;

import java.io.File;

/**
 * Interface definition for a callback to be invoked when an file or folder is
 * selected.
 */
public interface OnSelectFileListener {

    /**
     * A file/folder was selected by the user in the dialog.
     * 
     * @param selectedFile The file/folder selected.
     */
    public void onFileSelected(File selectedFile);

    /**
     * The current folder set in the file dialog changed.
     * 
     * @param path The new path.
     */
    public void onDirectoryChanged(File path);

    /**
     * The user tried to navigate to an unavailable path.
     * 
     * @param path The path.
     */
    public void onAccessDenied(File path);
}
