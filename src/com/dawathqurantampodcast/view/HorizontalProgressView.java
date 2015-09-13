

package com.dawathqurantampodcast.view;

import android.content.Context;
import android.util.AttributeSet;

import com.dawathqurantampodcast.model.types.Progress;
import com.dawathqurantampodcast.R;

/**
 * A sophisticated horizontal progress view.
 */
public class HorizontalProgressView extends ProgressView {

    /**
     * The layout id to inflate for this view.
     */
    protected static int LAYOUT = R.layout.progress_horizontal;

    /**
     * Create progress view.
     * 
     * @param context Context view lives in.
     * @param attrs View attributes.
     */
    public HorizontalProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayout() {
        return R.layout.progress_horizontal;
    }

    @Override
    public void publishProgress(Progress progress) {
        super.publishProgress(progress);

        // Show progress in progress bar
        if (progress.getPercentDone() >= 0 && progress.getPercentDone() <= 100) {
            progressBar.setIndeterminate(false);
            progressBar.setProgress(progress.getPercentDone());
        } else
            progressBar.setIndeterminate(true);
    }
}
