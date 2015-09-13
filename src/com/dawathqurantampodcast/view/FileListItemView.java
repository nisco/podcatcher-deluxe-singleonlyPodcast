

package com.dawathqurantampodcast.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dawathqurantampodcast.R;

import java.io.File;

/**
 * A list item view to represent a file/folder.
 */
public class FileListItemView extends LinearLayout {

    /** The icon view */
    private ImageView iconView;
    /** The name text view */
    private TextView nameTextView;

    /**
     * Create a file item list view.
     * 
     * @param context Context for the view to live in.
     * @param attrs View attributes.
     */
    public FileListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        iconView = (ImageView) findViewById(R.id.file_icon);
        nameTextView = (TextView) findViewById(R.id.file_name);
    }

    /**
     * Make the view update all its child to represent input given.
     * 
     * @param file File to represent.
     */
    public void show(final File file) {
        // 1. Set icon
        iconView.setImageResource(
                file.isDirectory() ? R.drawable.ic_file_folder : R.drawable.ic_file);

        // 2. Set the file name as text
        nameTextView.setText(file.getName());
    }
}
