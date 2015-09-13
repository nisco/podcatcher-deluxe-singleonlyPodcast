

package com.dawathqurantampodcast.view.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dawathqurantampodcast.adapters.GenreSpinnerAdapter;
import com.dawathqurantampodcast.adapters.LanguageSpinnerAdapter;
import com.dawathqurantampodcast.adapters.MediaTypeSpinnerAdapter;
import com.dawathqurantampodcast.adapters.SuggestionListAdapter;
import com.dawathqurantampodcast.listeners.OnAddSuggestionListener;
import com.dawathqurantampodcast.model.types.Genre;
import com.dawathqurantampodcast.model.types.Language;
import com.dawathqurantampodcast.model.types.MediaType;
import com.dawathqurantampodcast.model.types.Progress;
import com.dawathqurantampodcast.model.types.Suggestion;
import com.dawathqurantampodcast.view.ProgressView;
import com.dawathqurantampodcast.R;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fragment to show podcast suggestions.
 */
public class SuggestionFragment extends DialogFragment {

    /** The filter wildcard */
    public static final String FILTER_WILDCARD = "ALL";

    /** Mail address to send new suggestions to */
    private static final String SUGGESTION_MAIL_ADDRESS = "suggestion@podcatcher-deluxe.com";
    /** Subject for mail with new suggestions */
    private static final String SUGGESTION_MAIL_SUBJECT = "A proposal for a podcast suggestion in the Podcatcher Android apps";

    /** The call back we work on */
    private OnAddSuggestionListener listener;
    /** The list of suggestions to show */
    private List<Suggestion> suggestionList;
    /** The suggestion list adapter */
    private SuggestionListAdapter suggestionListAdapter;

    /** The language filter */
    private Spinner languageFilter;
    /** The genre filter */
    private Spinner genreFilter;
    /** The media type filter */
    private Spinner mediaTypeFilter;
    /** The progress view */
    private ProgressView progressView;
    /** The suggestions list view */
    private ListView suggestionsListView;
    /** The no suggestions view */
    private TextView noSuggestionsView;
    /** The send a suggestion view */
    private TextView sendSuggestionView;

    /** Bundle key for language filter position */
    private static final String LANGUAGE_FILTER_POSITION = "language_filter_position";
    /** Bundle key for genre filter position */
    private static final String GENRE_FILTER_POSITION = "genre_filter_position";
    /** Bundle key for media type filter position */
    private static final String MEDIATYPE_FILTER_POSITION = "mediatype_filter_position";

    /** The listener to update the list on filter change */
    private final OnItemSelectedListener selectionListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            updateList();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Make sure our listener is present
        try {
            this.listener = (OnAddSuggestionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddSuggestionListener");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout
        final View layout = inflater.inflate(R.layout.suggestion_list, container, false);

        // Get the display dimensions
        Rect displayRectangle = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        // Adjust the layout minimum height so the dialog always has the same
        // height and does not bounce around depending on the list content
        layout.setMinimumHeight((int) (displayRectangle.height() * 0.9f));

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.suggested_podcasts);

        languageFilter = (Spinner) view.findViewById(R.id.suggestion_language_select);
        languageFilter.setAdapter(new LanguageSpinnerAdapter(getDialog().getContext()));
        languageFilter.setOnItemSelectedListener(selectionListener);

        genreFilter = (Spinner) view.findViewById(R.id.suggestion_genre_select);
        genreFilter.setAdapter(new GenreSpinnerAdapter(getDialog().getContext()));
        genreFilter.setOnItemSelectedListener(selectionListener);

        mediaTypeFilter = (Spinner) view.findViewById(R.id.suggestion_type_select);
        mediaTypeFilter.setAdapter(new MediaTypeSpinnerAdapter(getDialog().getContext()));
        mediaTypeFilter.setOnItemSelectedListener(selectionListener);

        progressView = (ProgressView) view.findViewById(R.id.suggestion_list_progress);

        suggestionsListView = (ListView) view.findViewById(R.id.suggestion_list);
        noSuggestionsView = (TextView) view.findViewById(R.id.suggestion_none);

        sendSuggestionView = (TextView) view.findViewById(R.id.suggestion_send);
        sendSuggestionView.setText(Html.fromHtml("<a href=\"mailto:" + SUGGESTION_MAIL_ADDRESS +
                "?subject=" + SUGGESTION_MAIL_SUBJECT + "\">" +
                getString(R.string.suggestions_send) + "</a>"));
        sendSuggestionView.setMovementMethod(LinkMovementMethod.getInstance());

        // Set/restore filter settings
        // Coming from configuration change
        if (savedInstanceState != null) {
            languageFilter.setSelection(savedInstanceState.getInt(LANGUAGE_FILTER_POSITION));
            genreFilter.setSelection(savedInstanceState.getInt(GENRE_FILTER_POSITION));
            mediaTypeFilter.setSelection(savedInstanceState.getInt(MEDIATYPE_FILTER_POSITION));
        } // Initial opening of the dialog
        else
            setInitialFilterSelection();
    }

    @Override
    public void onResume() {
        super.onResume();

        // The list might have changed while we were paused
        updateList();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(LANGUAGE_FILTER_POSITION, languageFilter.getSelectedItemPosition());
        outState.putInt(GENRE_FILTER_POSITION, genreFilter.getSelectedItemPosition());
        outState.putInt(MEDIATYPE_FILTER_POSITION, mediaTypeFilter.getSelectedItemPosition());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // Make sure the parent activity knows when we are closing
        if (listener instanceof OnCancelListener)
            ((OnCancelListener) listener).onCancel(dialog);

        super.onCancel(dialog);
    }

    /**
     * Set list of suggestions to show and update the UI.
     * 
     * @param suggestions Podcasts to show.
     */
    public void setList(List<Suggestion> suggestions) {
        // Set the list to show
        this.suggestionList = suggestions;

        // Filter list and update UI (if ready)
        if (isResumed())
            updateList();
    }

    /**
     * Notify the fragment that a suggestion as been added and the list might
     * have to update.
     */
    public void notifySuggestionAdded() {
        if (suggestionListAdapter != null)
            suggestionListAdapter.notifyDataSetChanged();
    }

    /**
     * Show load suggestions progress.
     * 
     * @param progress Progress information to give.
     */
    public void showLoadProgress(Progress progress) {
        progressView.publishProgress(progress);
    }

    /**
     * Show load failed for podcast suggestions.
     */
    public void showLoadFailed() {
        progressView.showError(R.string.suggestions_load_error);
    }

    private void setInitialFilterSelection() {
        // Set according to locale
        final Locale currentLocale = getActivity().getResources().getConfiguration().locale;
        if (currentLocale.getLanguage().equalsIgnoreCase(Locale.ENGLISH.getLanguage()))
            languageFilter.setSelection(1);
        else if (currentLocale.getLanguage().equalsIgnoreCase(Locale.FRENCH.getLanguage()))
            languageFilter.setSelection(4);
        else if (currentLocale.getLanguage().equalsIgnoreCase(Locale.GERMAN.getLanguage()))
            languageFilter.setSelection(1);
        else if (currentLocale.getLanguage().equalsIgnoreCase("es"))
            languageFilter.setSelection(2);
        // No filter for this language, set to "all"
        else
            languageFilter.setSelection(0);

        // Set to "all"
        genreFilter.setSelection(0);
        // Set to audio, since this is an audio version
        mediaTypeFilter.setSelection(1);
    }

    private void updateList() {
        // Filter the suggestion list
        if (suggestionList != null) {
            // Resulting list
            List<Suggestion> filteredSuggestionList = new ArrayList<Suggestion>();
            // Do filter!
            for (Suggestion suggestion : suggestionList)
                if (matchesFilter(suggestion))
                    filteredSuggestionList.add(suggestion);

            // Set filtered list
            suggestionListAdapter = new SuggestionListAdapter(
                    getDialog().getContext(), filteredSuggestionList, listener);
            suggestionListAdapter.setFilterConfiguration(
                    languageFilter.getSelectedItemPosition() == 0,
                    genreFilter.getSelectedItemPosition() == 0,
                    mediaTypeFilter.getSelectedItemPosition() == 0);
            suggestionsListView.setAdapter(suggestionListAdapter);

            // Update UI
            if (filteredSuggestionList.isEmpty()) {
                suggestionsListView.setVisibility(GONE);
                noSuggestionsView.setVisibility(VISIBLE);
            }
            else {
                noSuggestionsView.setVisibility(GONE);
                suggestionsListView.setVisibility(VISIBLE);
            }

            progressView.setVisibility(GONE);
        }
    }

    /**
     * Checks whether the given podcast matches the filter selection.
     * 
     * @param suggestion Podcast to check.
     * @return <code>true</code> if the podcast fits.
     */
    private boolean matchesFilter(Suggestion suggestion) {
        return (languageFilter.getSelectedItemPosition() == 0 ||
                ((Language) languageFilter.getSelectedItem()).equals(suggestion.getLanguage())) &&
                (genreFilter.getSelectedItemPosition() == 0 ||
                ((Genre) genreFilter.getSelectedItem()).equals(suggestion.getGenre())) &&
                (mediaTypeFilter.getSelectedItemPosition() == 0 ||
                ((MediaType) mediaTypeFilter.getSelectedItem()).equals(suggestion.getMediaType()));
    }
}
