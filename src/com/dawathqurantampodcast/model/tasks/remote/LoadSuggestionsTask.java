

package com.dawathqurantampodcast.model.tasks.remote;

import android.content.Context;
import android.util.Log;

import com.dawathqurantampodcast.listeners.OnLoadSuggestionListener;
import com.dawathqurantampodcast.model.tags.JSON;
import com.dawathqurantampodcast.model.types.Genre;
import com.dawathqurantampodcast.model.types.Language;
import com.dawathqurantampodcast.model.types.MediaType;
import com.dawathqurantampodcast.model.types.Progress;
import com.dawathqurantampodcast.model.types.Suggestion;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A task that loads and reads suggested podcasts.
 */
public class LoadSuggestionsTask extends LoadRemoteFileTask<Void, List<Suggestion>> {

    /** Call back */
    private OnLoadSuggestionListener listener;
    /** The task's context */
    private final Context context;

    /** The file encoding */
    private static final String SUGGESTIONS_ENCODING = "utf8";
    /** The online resource to find suggestions */
    private static final String SOURCE = "https://dl.dropboxusercontent.com/u/78388550/generated.json";
    /** The text that marks isExplicit() == true */
    private static final String EXPLICIT_POSITIVE_STRING = "yes";

    /** Flag to indicate the max age that would trigger re-load. */
    private int maxAge = 60 * 24 * 3;

    /**
     * Create new task.
     * 
     * @param context The context the task is carried out in.
     * @param listener Callback to be alerted on progress and completion.
     */
    public LoadSuggestionsTask(Context context, OnLoadSuggestionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Suggestion> doInBackground(Void... params) {
        List<Suggestion> result = new ArrayList<Suggestion>();
        byte[] suggestions = null;

        // 1. Load the file from the cache or the Internet
        try {
            publishProgress(Progress.CONNECT);
            // 1.1 This the simple case where we have the local version and
            // it is fresh enough. Use that one.
            if (isCachedLocally() && getCachedLogoAge() <= maxAge)
                suggestions = restoreSuggestionsFromFileCache();
            // 1.2 If that is not the case, we need to go over the air.
            else {
                // We store a cached version ourselves
                // useCaches = false;
                suggestions = loadFile(new URL(SOURCE));

                storeSuggestionsToFileCache(suggestions);
            }
        } catch (Throwable throwable) {
            // Use cached version even if it is stale
            if (isCachedLocally())
                try {
                    suggestions = restoreSuggestionsFromFileCache();
                } catch (IOException ioe) {
                    cancel(true);
                    return null; // Nothing more we could do here
                }
            else {
                Log.w(getClass().getSimpleName(), "Load failed for podcast suggestions file",
                        throwable);

                cancel(true);
                return null;
            }
        }

        // 2. Parse the result
        try {
            // 2.1 Get result as a document
            publishProgress(Progress.PARSE);
            JSONObject completeJson = new JSONObject(new String(suggestions, SUGGESTIONS_ENCODING));
            if (isCancelled())
                return null;

            // 2.2 Add all featured podcasts
            addSuggestionsFromJsonArray(completeJson.getJSONArray(JSON.FEATURED), result, true);
            if (isCancelled())
                return null;

            // 2.3 Add all suggestions
            addSuggestionsFromJsonArray(completeJson.getJSONArray(JSON.SUGGESTION), result, false);
            if (isCancelled())
                return null;

            // 2.4 Sort the result
            Collections.sort(result);
            publishProgress(Progress.DONE);
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), "Parse failed for podcast suggestions ", e);

            cancel(true);
            return null;
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Progress... progress) {
        if (listener != null)
            listener.onSuggestionsLoadProgress(progress[0]);
        else if (listener == null)
            Log.w(getClass().getSimpleName(),
                    "Suggestions progress update, but no listener attached");
    }

    @Override
    protected void onPostExecute(List<Suggestion> suggestions) {
        // Suggestions loaded successfully
        if (listener != null)
            listener.onSuggestionsLoaded(suggestions);
        else
            Log.w(getClass().getSimpleName(), "Suggestions loaded, but no listener attached");
    }

    @Override
    protected void onCancelled(List<Suggestion> suggestions) {
        // Suggestions failed to load
        if (listener != null)
            listener.onSuggestionsLoadFailed();
        else
            Log.w(getClass().getSimpleName(),
                    "Suggestions failed to load, but no listener attached");
    }

    /**
     * Add all podcast suggestions in given array to the list.
     * 
     * @param array JSON array to scan.
     * @param list List to add suggestions to.
     */
    private void addSuggestionsFromJsonArray(JSONArray array, List<Suggestion> list,
            boolean featured) {
        for (int index = 0; index < array.length(); index++) {
            JSONObject object;

            try {
                object = array.getJSONObject(index);
            } catch (JSONException e) {
                continue; // If an index fails, try the next one...
            }

            Suggestion suggestion = createSuggestion(object);
            if (suggestion != null) {
                suggestion.setFeatured(featured);
                list.add(suggestion);
            }
        }
    }

    /**
     * Create a podcast suggestion for the given JSON object and set its
     * properties.
     * 
     * @param json The JSON object to work on.
     * @return The podcast suggestion or <code>null</code> if any problem
     *         occurs.
     */
    private Suggestion createSuggestion(JSONObject json) {
        Suggestion suggestion = null;

        try {
            suggestion = new Suggestion(json.getString(JSON.TITLE), json.getString(JSON.URL));
            suggestion.setDescription(json.getString(JSON.DESCRIPTION).trim());
            suggestion.setLanguage(Language.valueOf(json.getString(JSON.LANGUAGE)
                    .toUpperCase(Locale.US).trim()));
            suggestion.setMediaType(MediaType.valueOf(json.getString(JSON.TYPE)
                    .toUpperCase(Locale.US).trim()));
            suggestion.setGenre(Genre.forLabel(json.getString(JSON.CATEGORY)));
            suggestion.setExplicit(EXPLICIT_POSITIVE_STRING.equals(json.getString(JSON.EXPLICIT)
                    .toLowerCase(Locale.US)));
        } catch (JSONException e) {
            Log.w(getClass().getSimpleName(), "JSON parsing failed for: " + suggestion, e);
            return null;
        } catch (IllegalArgumentException e) {
            Log.w(getClass().getSimpleName(), "Enum value missing for: " + suggestion, e);
            return null;
        }

        return suggestion;
    }

    private File getSuggestionsCacheFile() {
        // Create the complete path leading to where we expect the cached file
        return new File(context.getCacheDir(), "suggestions.json");
    }

    private boolean isCachedLocally() {
        return getSuggestionsCacheFile().exists();
    }

    private int getCachedLogoAge() {
        if (isCachedLocally())
            return (int) ((new Date().getTime() - getSuggestionsCacheFile().lastModified())
            / (60 * 1000)); // Calculate to minutes
        else
            return -1;
    }

    private byte[] restoreSuggestionsFromFileCache() throws IOException {
        final File cachedFile = getSuggestionsCacheFile();
        final byte[] result = new byte[(int) cachedFile.length()];

        FileInputStream input = null;
        try {
            input = new FileInputStream(cachedFile);
            input.read(result);
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                input.close();
            } catch (Exception e) {
                // Nothing more we could do here
            }
        }

        return result;
    }

    private void storeSuggestionsToFileCache(byte[] suggestions) {
        FileOutputStream out = null;

        // If this fails, we have no cached version, but that's okay
        try {
            context.getCacheDir().mkdirs();

            out = new FileOutputStream(getSuggestionsCacheFile());
            out.write(suggestions);
            out.flush();
        } catch (Throwable th) {
            // pass
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                // Nothing more we could do here
            }
        }
    }
}
