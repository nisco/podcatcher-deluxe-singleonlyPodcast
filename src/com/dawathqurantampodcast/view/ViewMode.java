

package com.dawathqurantampodcast.view;

import android.content.res.Configuration;
import android.content.res.Resources;

import com.dawathqurantampodcast.Podcatcher;


/**
 * These are the four view modes we want adapt to.
 */
public enum ViewMode {

    /**
     * Small and normal screens (smallest width < 600dp) in portrait orientation
     */
    SMALL_PORTRAIT,

    /**
     * Small and normal screens (smallest width < 600dp) in square or landscape
     * orientation
     */
    SMALL_LANDSCAPE,

    /**
     * Large and extra-large screens (smallest width >= 600dp) in portrait
     * orientation
     */
    LARGE_PORTRAIT,

    /**
     * Large and extra-large screens (smallest width >= 600dp) in square or
     * landscape orientation
     */
    LARGE_LANDSCAPE;

    /**
     * @return Whether we are showing on a small device
     */
    public boolean isSmall() {
        return SMALL_LANDSCAPE.equals(this) || SMALL_PORTRAIT.equals(this);
    }

    /**
     * @return Whether we are showing on a small device held in portrait
     */
    public boolean isSmallPortrait() {
        return SMALL_PORTRAIT.equals(this);
    }

    /**
     * @return Whether we are showing on a small device held in landscape
     */
    public boolean isSmallLandscape() {
        return SMALL_LANDSCAPE.equals(this);
    }

    /**
     * @return Whether we are showing on a large device held in portrait
     */
    public boolean isLargePortrait() {
        return LARGE_PORTRAIT.equals(this);
    }

    /**
     * @return Whether we are showing on a large device held in landscape
     */
    public boolean isLargeLandscape() {
        return LARGE_LANDSCAPE.equals(this);
    }

    /**
     * Use the app's resources to find the current view mode the device is in.
     * 
     * @param resources Resources to get information from.
     * @return The current view mode.
     */
    public static ViewMode determineViewMode(Resources resources) {
        // Get config information
        Configuration config = resources.getConfiguration();

        // Determine view mode
        switch (config.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                return config.smallestScreenWidthDp >= Podcatcher.MIN_PIXEL_LARGE ?
                        LARGE_PORTRAIT : SMALL_PORTRAIT;
            default: // Landscape and square
                return config.smallestScreenWidthDp >= Podcatcher.MIN_PIXEL_LARGE ?
                        LARGE_LANDSCAPE : SMALL_LANDSCAPE;
        }
    }
}
