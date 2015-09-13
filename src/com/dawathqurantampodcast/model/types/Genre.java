

package com.dawathqurantampodcast.model.types;

import java.util.Locale;

/**
 * Genre (category) of the podcast.
 */
@SuppressWarnings("javadoc")
public enum Genre {
    ARTS("Arts"),
    BUSINESS("Business"),
    COMEDY("Comedy"),
    EDUCATION("Education"),
    GAMES_HOBBIES("Games & Hobbies"),
    GOVERNMENT_ORGANIZATIONS("Government & Organizations"),
    HEALTH("Health"),
    KIDS_FAMILY("Kids & Family"),
    MUSIC("Music"),
    NEWS_POLITICS("News & Politics"),
    RELIGION_SPIRITUALITY("Religion & Spirituality"),
    SCIENCE_MEDICINE("Science & Medicine"),
    SOCIETY_CULTURE("Society & Culture"),
    SPORTS_RECREATION("Sports & Recreation"),
    TECHNOLOGY("Technology"),
    TV_FILM("TV & Film");

    private final String label;

    Genre(String label) {
        this.label = label;
    }

    /**
     * Find the genre by a given label.
     * 
     * @param label The label to search for.
     * @return The genre instance.
     * @throws IllegalArgumentException If the label is not recognized.
     */
    public static Genre forLabel(String label) {
        for (Genre genre : Genre.values())
            if (genre.label.toLowerCase(Locale.US).equals(label.toLowerCase(Locale.US).trim()))
                return genre;

        throw new IllegalArgumentException("Label \"" + label + "\" does not match any genre!");
    }
}
