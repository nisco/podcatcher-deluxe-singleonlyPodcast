

package com.dawathqurantampodcast.model.types;

import com.dawathqurantampodcast.model.tags.RSS;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The abstract root type of the main podcatcher app types, including
 * {@link Podcast}, {@link Episode}, and {@link Suggestion}. Defines some
 * members needed in all of them.
 */
public abstract class FeedEntity {

    /** The date format used by RSS feeds */
    private static final String DATE_FORMAT_TEMPLATE = "EEE, dd MMM yy HH:mm:ss zzz";
    /** Our formatter used when reading the episode item's date string */
    private static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat(DATE_FORMAT_TEMPLATE, Locale.US);
    /**
     * The alternative date formats supported because they are used by some
     * feeds, these are all tried in the given order if the default fails
     */
    private static final String[] DATE_FORMAT_TEMPLATE_ALTERNATIVES = {
            "EEE, dd MMM yy", "yy-MM-dd"
    };

    /** Name of the podcast */
    protected String name;
    /** Location of the podcast's RSS file */
    protected String url;
    /** Podcast's description */
    protected String description;

    /** Whether the element contains explicit language or pics */
    protected boolean explicit = false;

    /**
     * @return The entity's title.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The entity's online location.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return The entity's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Whether the entity is considered explicit, i.e. contains
     *         adult-only material.
     */
    public boolean isExplicit() {
        return explicit;
    }

    /**
     * Check whether the given string values indicated that the feed entity is
     * considered explicit.
     * 
     * @param value The string value from the feed.
     * @return The explicit flag.
     */
    protected boolean parseExplicit(String value) {
        return value != null
                && value.trim().toLowerCase(Locale.US).equals(RSS.EXPLICIT_POSITIVE_VALUE);
    }

    /**
     * Parse a string into a date. Can be used for last feed updates or
     * publication dates. The method will try to read different formats.
     * 
     * @param dateString The string from the RSS/XML feed to parse.
     * @return The date or <code>null</code> if the string could not be parsed.
     */
    protected Date parseDate(String dateString) {
        try {
            // SimpleDateFormat is not thread safe
            synchronized (DATE_FORMATTER) {
                return DATE_FORMATTER.parse(dateString);
            }
        } catch (ParseException e) {
            // The default format is not available, try all the other formats we
            // support...
            for (String format : DATE_FORMAT_TEMPLATE_ALTERNATIVES)
                try {
                    return new SimpleDateFormat(format, Locale.US).parse(dateString);
                } catch (ParseException e1) {
                    // Does not fit the format, pass and try next
                }
        }

        // None of the formats matched
        return null;
    }
}
