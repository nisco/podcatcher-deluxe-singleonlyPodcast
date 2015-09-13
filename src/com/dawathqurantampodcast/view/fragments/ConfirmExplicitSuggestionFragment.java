

package com.dawathqurantampodcast.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dawathqurantampodcast.R;

/**
 * A confirmation dialog for the user to make sure he/she really wants an
 * explicit podcast to be added.
 * <p>
 * <b>Register call-back:</b> The fragment will use the activity it is part of
 * as its listener. To make this work, the activity needs to implement
 * {@link OnConfirmExplicitSuggestionListener}.
 * <p>
 */
public class ConfirmExplicitSuggestionFragment extends DialogFragment {

    /** The tag we identify our confirmation dialog fragment with */
    public static final String TAG = "confirm_explicit_suggestion";

    /** The callback we are working with */
    private OnConfirmExplicitSuggestionListener listener;

    /** The call-back for listeners to implement */
    public interface OnConfirmExplicitSuggestionListener {

        /**
         * The user confirmed the addition.
         */
        public void onConfirmExplicit();

        /**
         * The user cancelled the process.
         */
        public void onCancelExplicit();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Make sure our listener is present
        try {
            this.listener = (OnConfirmExplicitSuggestionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnConfirmExplicitSuggestionListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String title = getString(R.string.podcast_add_title);
        final String message = getString(R.string.suggestion_confirm_explicit);

        // Define context to use (parent activity might have no theme)
        final ContextThemeWrapper context = new ContextThemeWrapper(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog);

        // Inflate our custom view
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View content = inflater.inflate(R.layout.confirm, null);

        // Set message
        final TextView messageTextView = (TextView) content.findViewById(R.id.message);
        messageTextView.setText(message);

        // Add click listeners
        final Button confirmButton = (Button) content.findViewById(R.id.confirm_button);
        confirmButton.setText(R.string.podcast_add_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onConfirmExplicit();

                dismiss();
            }
        });
        final Button cancelButton = (Button) content.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onCancel(ConfirmExplicitSuggestionFragment.this.getDialog());
                dismiss();
            }
        });

        // Build the dialog
        final AlertDialog.Builder abuilder = new AlertDialog.Builder(context);
        abuilder.setTitle(title)
                .setView(content);

        return abuilder.create();
    }

    @Override
    public void onPause() {
        super.onPause();

        // We auto-dismiss here, because the fragment should not survive
        // configuration changes
        dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (listener != null)
            listener.onCancelExplicit();
    }
}
