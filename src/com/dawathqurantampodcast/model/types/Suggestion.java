

package com.dawathqurantampodcast.model.types;

/**
 * The podcast suggestion type. Extends {@link Podcast} by a few fields and
 * methods specific to suggestions.
 */
public class Suggestion extends Podcast {

    /** Whether the podcast is featured */
    protected boolean featured = false;

    /**
     * Create new suggestion. See {@link Podcast} for details.
     * 
     * @param name The name to show.
     * @param url The URL to load feed from.
     */
    public Suggestion(String name, String url) {
        super(name, url);
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param language The language to set.
     */
    public void setLanguage(Language language) {
        this.language = language;
    }

    /**
     * @param genre The genre to set.
     */
    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    /**
     * @param mediaType The mediaType to set.
     */
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    /**
     * @return Whether this suggestion is featured.
     */
    public boolean isFeatured() {
        return featured;
    }

    /**
     * @param featured What to set the flag to.
     */
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    /**
     * Mark a suggestion as containing adult-only material.
     * 
     * @param explicit The flag to set.
     */
    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }
}
