

package com.dawathqurantampodcast.model.types;

import android.graphics.Bitmap;
import android.text.Html;
import android.util.Base64;

import com.dawathqurantampodcast.model.ParserUtils;
import com.dawathqurantampodcast.model.tags.RSS;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * The podcast type. This represents the most important type in the podcatcher
 * application. To create a podcast, give its name and an online location to
 * load its RSS/XML file from. The online location is not verified or checked
 * inside the podcast type.
 * <p>
 * <b>Parsing:</b> Call {@link #parse(XmlPullParser)} with the parser set up to
 * the correct content in order to make the podcast object read and refresh its
 * members. Use {@link #getLastLoaded()} to find out whether and when this last
 * happened to a given podcast instance.
 * </p>
 * <p>
 * <b>Comparisons and Equals:</b> For the purpose of the podcatcher app, two
 * podcasts are equal iff they point at the same online feed resource. The
 * {@link #compareTo(Podcast)} method works on the podcast's name though and is
 * therefore <em>not</em> consistent with {@link #equals(Object)}.
 * </p>
 * <p>
 * <b>Logo:</b> Podcast often have logos. This podcast type allows for access to
 * the logo's online location (after {@link #parse(XmlPullParser)}, of course)
 * and for caching of the logo using {@link #setLogo(Bitmap)}. Use
 * {@link #isLogoCached()} to find the current state, {@link #getLogo()} will
 * return an immutable copy.
 * </p>
 */
public class Podcast extends FeedEntity implements Comparable<Podcast> {

    /** Broadcast language */
    protected Language language;
    /** Podcast genre */
    protected Genre genre;
    /** Podcast media type */
    protected MediaType mediaType;

    /** The podcast's image (logo) location */
    protected String logoUrl;
    /** The cached logo bitmap */
    protected Bitmap logo;

    /** Username for http authorization */
    protected String username;
    /** Password for http authorization */
    protected String password;

    /** The point in time when the RSS file as last been set */
    protected Date lastLoaded;
    /** The podcasts list of episodes */
    protected List<Episode> episodes = new ArrayList<Episode>();

    /**
     * Create a new podcast by name and RSS file location. The name will not be
     * read from the file, but remains as given (unless you give
     * <code>null</code> as the name). All other data on the podcast will only
     * be available after {@link #parse(XmlPullParser)} was called.
     * 
     * @param name The podcast's name, if you give <code>null</code> the name
     *            will be read from the RSS file on
     *            {@link #parse(XmlPullParser)}.
     * @param url The location of the podcast's RSS file.
     * @see #parse(XmlPullParser)
     */
    public Podcast(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * @return The language.
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * @return The genre.
     */
    public Genre getGenre() {
        return genre;
    }

    /**
     * @return The mediaType.
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * @return The user name for this podcast. Maybe <code>null</code> if
     *         unknown or unneeded.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the user name for this podcast.
     * 
     * @param username Name to use. Give <code>null</code> to reset.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return The password for this podcast. Maybe <code>null</code> if unknown
     *         or unneeded.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password for this podcast.
     * 
     * @param password Password to use. Give <code>null</code> to reset.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Authorization string to be used as a HTTP request header or
     *         <code>null</code> if user name or password are not set.
     */
    public String getAuthorization() {
        String result = null;

        if (username != null && password != null) {
            final String userpass = username + ":" + password;
            result = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.NO_WRAP);
        }

        return result;
    }

    /**
     * Find and return all episodes for this podcast. Will never return
     * <code>null</code> but an empty list when encountering problems. Set and
     * parse the RSS file before expecting any results.
     * 
     * @return The list of episodes as listed in the feed.
     * @see #parse(XmlPullParser)
     */
    public List<Episode> getEpisodes() {
        // Need to return a copy, so nobody can change this on us and changes
        // made in the model do not make problems in the UI
        return new ArrayList<Episode>(episodes);
    }

    /**
     * Clean all explicit episodes from the podcast. Once called, the
     * {@link #getEpisodes()} method will only return episodes <em>not</em>
     * marked explicit until {@link #parse(XmlPullParser)} is called.
     * 
     * @return The number of clean episodes left.
     */
    public int removeExplicitEpisodes() {
        Iterator<Episode> episodeIterator = episodes.iterator();

        while (episodeIterator.hasNext()) {
            final Episode episode = episodeIterator.next();

            if (episode.isExplicit())
                episodeIterator.remove();
        }

        return episodes.size();
    }

    /**
     * @return The number of episode for this podcast (always >= 0).
     * @see #parse(XmlPullParser)
     */
    public int getEpisodeCount() {
        return episodes.size();
    }

    /**
     * Find and return the podcast's image location (logo).
     * 
     * @return URL pointing at the logo location.
     * @see #parse(XmlPullParser)
     */
    public String getLogoUrl() {
        return logoUrl;
    }

    /**
     * Get a cached logo for this podcast.
     * 
     * @return The cached logo if it was previously set using
     *         {@link #setLogo(Bitmap)}, <code>null</code> otherwise.
     */
    public Bitmap getLogo() {
        return logo == null ? null : Bitmap.createBitmap(logo);
    }

    /**
     * @return Whether the podcast's logo is currently cached and returned by
     *         {@link #getLogo()}.
     */
    public boolean isLogoCached() {
        return logo != null;
    }

    /**
     * Cache the podcast logo given.
     * 
     * @param logo Logo to use for this podcast.
     */
    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

    /**
     * @return The point in time this podcast has last been loaded or
     *         <code>null</code> iff it had not been loaded before.
     */
    public Date getLastLoaded() {
        return lastLoaded == null ? null : new Date(lastLoaded.getTime());
    }

    @Override
    public String toString() {
        return name + " at " + url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        else if (!(o instanceof Podcast))
            return false;

        final Podcast another = (Podcast) o;

        // Podcasts are equal iff they point at the same online content
        return url == null ? false : url.equals(another.url);
    }

    @Override
    public int hashCode() {
        return 42 + (url == null ? 0 : url.hashCode());
    }

    @Override
    public int compareTo(Podcast another) {
        if (name != null && another.name != null)
            return name.compareToIgnoreCase(another.name);
        else if (name == null && another.name != null)
            return -1;
        else if (name != null && another.name == null)
            return 1;
        else
            return 0;
    }

    /**
     * Set the RSS file parser representing this podcast. This is were the
     * object gets its information from. Many of its methods will not return
     * valid results unless this method was called. Calling this method resets
     * all episode information that might have been read earlier, other meta
     * data is preserved and will only change if the feed has actually changed.
     * 
     * @param parser Parser used to read the RSS/XML file.
     * @throws IOException If we encounter problems read the file.
     * @throws XmlPullParserException On parsing errors.
     */
    public void parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        // Reset state
        episodes.clear();

        // Start parsing
        int eventType = parser.next();
        int episodeIndex = 0;

        // Read complete document
        while (eventType != XmlPullParser.END_DOCUMENT) {
            // We only need start tags here
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = parser.getName();

                // Podcast name found and not set yet
                if (tagName.equalsIgnoreCase(RSS.TITLE) && name == null)
                    name = Html.fromHtml(parser.nextText().trim()).toString();
                // Explicit info found
                else if (tagName.equalsIgnoreCase(RSS.EXPLICIT))
                    explicit = parseExplicit(parser.nextText());
                // Image found
                else if (tagName.equalsIgnoreCase(RSS.IMAGE))
                    parseLogo(parser);
                // Thumbnail found (used by some podcast instead of image)
                else if (tagName.equalsIgnoreCase(RSS.THUMBNAIL) && logoUrl == null)
                    logoUrl = parser.getAttributeValue("", RSS.URL);
                // Episode found
                else if (tagName.equalsIgnoreCase(RSS.ITEM))
                    parseEpisode(parser, episodeIndex++);
            }

            // Done, get next parsing event
            eventType = parser.next();
        }

        // Parsing completed without errors, mark as updated
        lastLoaded = new Date();
    }

    protected void parseLogo(XmlPullParser parser) throws XmlPullParserException, IOException {
        try {
            // HREF attribute used?
            if (parser.getAttributeValue("", RSS.HREF) != null)
                logoUrl = parser.getAttributeValue("", RSS.HREF);
            // URL tag used! We do not override any previous setting, because
            // the HREF is from the <itunes:image> tag which tends to have
            // better pics.
            else if (logoUrl == null) {
                // Make sure we start at image tag
                parser.require(XmlPullParser.START_TAG, "", RSS.IMAGE);

                // Look at all start tags of this image
                while (parser.nextTag() == XmlPullParser.START_TAG) {
                    // URL tag found
                    if (parser.getName().equalsIgnoreCase(RSS.URL))
                        logoUrl = parser.nextText();
                    // Unneeded node, skip...
                    else
                        ParserUtils.skipSubTree(parser);
                }

                // Make sure we end at image tag
                parser.require(XmlPullParser.END_TAG, "", RSS.IMAGE);
            }
        } catch (XmlPullParserException e) {
            // The podcast logo information could not be read from the RSS file,
            // skip...
        }
    }

    protected void parseEpisode(XmlPullParser parser, int index)
            throws XmlPullParserException, IOException {
        // Create episode and parse the data
        Episode newEpisode = new Episode(this, index);
        newEpisode.parse(parser);

        // Only add if there is some actual content to play
        if (newEpisode.getMediaUrl() != null)
            episodes.add(newEpisode);
    }
}
