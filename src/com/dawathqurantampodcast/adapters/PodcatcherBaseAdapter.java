

package com.dawathqurantampodcast.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Abstract super class for this app's adapters. Allows access to resources.
 */
public abstract class PodcatcherBaseAdapter extends BaseAdapter {

    /** The resources handle */
    protected final Resources resources;
    /** The inflater we use */
    private final LayoutInflater inflater;

    /**
     * Create new adapter.
     * 
     * @param context The current context.
     */
    public PodcatcherBaseAdapter(Context context) {
        this.resources = context.getResources();
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /**
     * Check whether a view can be recycled and inflate a new one if not.
     * 
     * @param convertView View to check.
     * @param parent View group to attach to.
     * @param inflateId Id of view to inflate if recycling is not possible.
     * @return A view to use (not <code>null</code>).
     */
    protected View findReturnView(View convertView, ViewGroup parent, int inflateId) {
        // Can we recycle the convert view?
        // No:
        if (convertView == null)
            return inflater.inflate(inflateId, parent, false);
        // Yes:
        else
            return convertView;
    }
}
