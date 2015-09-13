

package com.dawathqurantampodcast.view;

import android.text.format.DateUtils;

import com.dawathqurantampodcast.model.types.Episode;


/**
 * Some utility functions used in the view.
 */
public class Utils {

    /**
     * Create a nice user-friendly publication date string by means of
     * {@link DateUtils#getRelativeTimeSpanString(long)}.
     * 
     * @param episode The Episode to create date string for. Cannot be
     *            <code>null</code> and needs valid publication date.
     * @return A relative date string describing the relation between 'now' and
     *         the episode's release time or an empty string ("") if the
     *         publication date cannot be determinated for the episode.
     */
    public static String getRelativePubDate(Episode episode) {
        if (episode != null && episode.getPubDate() != null) {
            final long pubTime = episode.getPubDate().getTime();

            // Get a nice time span string for the age of the episode
            String dateString = DateUtils.getRelativeTimeSpanString(pubTime,
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS).toString();

            // Make sure date string starts with upper case
            if (dateString.length() > 1 && Character.isLetter(dateString.charAt(0)))
                dateString = Character.toUpperCase(dateString.charAt(0)) + dateString.substring(1);

            return dateString;
        } else
            return "";
    }
}
