

package com.dawathqurantampodcast.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.dawathqurantampodcast.view.FileListItemView;
import com.dawathqurantampodcast.R;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * The file list adapter to provide the data for the list in the file/folder
 * selection dialog.
 */
public class FileListAdapter extends PodcatcherBaseListAdapter {

    /** The current path items */
    private final File[] files;
    /** The default file filter to apply */
    private static final FileFilter filter = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            return !pathname.isHidden();
        }
    };

    /**
     * Create new adapter. Sub-files of given path will be sorted. Hidden files
     * are excluded.
     * 
     * @param context Context we live in.
     * @param path Path to represent children of.
     */
    public FileListAdapter(Context context, File path) {
        super(context);

        this.files = path.listFiles(filter);
        Arrays.sort(files);
    }

    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public Object getItem(int position) {
        return files[position];
    }

    @Override
    public long getItemId(int position) {
        return files[position].hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FileListItemView returnView = (FileListItemView)
                findReturnView(convertView, parent, R.layout.file_list_item);

        // Make sure the coloring is right
        setBackgroundColorForPosition(returnView, position);
        // Make the view represent file at given position
        returnView.show((File) getItem(position));

        return returnView;
    }
}
