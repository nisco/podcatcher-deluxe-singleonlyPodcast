

package com.dawathqurantampodcast.view.fragments;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.dawathqurantampodcast.Podcatcher;
import com.dawathqurantampodcast.listeners.OnAddPodcastListener;
import com.dawathqurantampodcast.model.tasks.remote.LoadPodcastTask.PodcastLoadError;
import com.dawathqurantampodcast.model.types.Progress;
import com.dawathqurantampodcast.view.HorizontalProgressView;
import com.dawathqurantampodcast.R;


/**
 * A dialog to let the user add a podcast. The activity that shows this need to
 * implement the {@link OnAddPodcastListener}.
 */
public class AddPodcastFragment extends DialogFragment {

    /** The listener we report back to */
    private OnAddPodcastListener listener;

    /** The podcast URL text field */
    private EditText podcastUrlEditText;
    /** The progress view */
    private HorizontalProgressView progressView;
    /** The show suggestions button */
    private Button showSuggestionsButton;
    /** The add podcast button */
    private Button addPodcastButton;
    /** The import OPML button */
    private Button importOpmlButton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Make sure our listener is present
        try {
            this.listener = (OnAddPodcastListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddPodcastListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.add_podcast, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.podcast_add_title);

        // Prevent automatic display of the soft keyboard
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        podcastUrlEditText = (EditText) view.findViewById(R.id.podcast_url);
        podcastUrlEditText.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_GO:
                        addPodcast();

                        return true;
                    default:
                        return false;
                }
            }
        });

        // Put the URI given in the intent if any
        if (getActivity().getIntent().getData() != null)
            podcastUrlEditText.setText(getActivity().getIntent().getDataString());
        // This is for testing only
        else if (((Podcatcher) getActivity().getApplication()).isInDebugMode())
            podcastUrlEditText.setText("https://www.theskepticsguide.org/premium");
        // This checks for a potential podcast URL in the clipboard
        // and presets it in the text field if available
        else
            checkClipboardForPodcastUrl();

        progressView = (HorizontalProgressView) view.findViewById(R.id.add_podcast_progress);

        showSuggestionsButton = (Button) view.findViewById(R.id.suggestion_add_button);
        showSuggestionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onShowSuggestions();
            }
        });

        addPodcastButton = (Button) view.findViewById(R.id.podcast_add_button);
        addPodcastButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addPodcast();
            }
        });

        importOpmlButton = (Button) view.findViewById(R.id.import_opml_button);
        importOpmlButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onImportOpml();
            }
        });
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // Make sure the parent activity knows when we are closing
        if (listener instanceof OnCancelListener)
            ((OnCancelListener) listener).onCancel(dialog);

        super.onCancel(dialog);
    }

    /**
     * Show progress in the dialog.
     * 
     * @param progress Progress information to show.
     */
    public void showProgress(Progress progress) {
        // Prepare UI
        podcastUrlEditText.setEnabled(false);
        addPodcastButton.setEnabled(false);
        progressView.setVisibility(VISIBLE);

        // Show progress
        progressView.publishProgress(progress);
    }

    /**
     * Show load failure in the dialog UI.
     * 
     * @param code The error code loading the podcast failed with.
     */
    public void showPodcastLoadFailed(PodcastLoadError code) {
        switch (code) {
            case ACCESS_DENIED:
                progressView.showError(R.string.podcast_load_error_access_denied);
                break;

            case NOT_PARSEABLE:
                progressView.showError(R.string.podcast_load_error_not_parseable);
                break;

            case NOT_REACHABLE:
                progressView.showError(R.string.podcast_load_error_not_reachable);
                break;

            default:
                progressView.showError(R.string.podcast_load_error);
                break;
        }

        podcastUrlEditText.setEnabled(true);
        addPodcastButton.setEnabled(true);
    }

    private void addPodcast() {
        showProgress(Progress.WAIT);

        // Try to make the input work as a online resource
        String spec = podcastUrlEditText.getText().toString();
        if (!URLUtil.isNetworkUrl(spec)) {
            spec = "http://podcast.bayyinah.com/category/bayyinahaudiolibrary/feed" + spec;

            podcastUrlEditText.setText(spec);
        }

        listener.onAddPodcast(spec);
    }

    private void checkClipboardForPodcastUrl() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(
                Context.CLIPBOARD_SERVICE);

        // Get the value to paste (make this failsafe)
        if (clipboard != null && clipboard.hasPrimaryClip()
                && clipboard.getPrimaryClip().getItemCount() > 0) {
            CharSequence candidate = clipboard.getPrimaryClip().getItemAt(0).getText();

            // Check whether this might be a podcast RSS online resource, if so
            // set text field
            if (candidate != null && URLUtil.isNetworkUrl(candidate.toString()))
                podcastUrlEditText.setText(candidate);
        }
    }
}
