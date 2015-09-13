

package com.dawathqurantampodcast.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dawathqurantampodcast.R;

/**
 * A podcast authorization dialog. Let's the user supply a name/password
 * combination.
 * <p>
 * <b>Register call-back:</b> The fragment will try to use the activity it is
 * part of as its listener. To make this work, the activity needs to implement
 * {@link OnEnterAuthorizationListener}. Showing this fragment from another
 * context will <em>not</em> fail, but you need to use
 * {@link #setListener(OnEnterAuthorizationListener)} to register and override
 * the call-back. Once the listener is called, the fragment will auto-dismiss
 * itself.
 * </p>
 * <p>
 * <b>Presets: </b> You might also want to use {@link #setArguments(Bundle)}
 * with a string value to pre-set the user name using the key
 * {@link #USERNAME_PRESET_KEY}. (This needs to be done before showing the
 * dialog.)
 * </p>
 */
public class AuthorizationFragment extends DialogFragment {

    /** Argument key for the user name to pre-set */
    public static final String USERNAME_PRESET_KEY = "username_preset";
    /** The tag we identify our authorization dialog fragment with */
    public static final String TAG = "authorization";

    /** The user name to display onShow() */
    private String usernamePreset = null;

    /** The username text view */
    private EditText usernameTextView;
    /** The password text view */
    private EditText passwordTextView;

    /** Flag on whether our activity listens to us */
    private boolean autoDismissOnPause = false;

    /** The callback we are working with */
    private OnEnterAuthorizationListener listener;

    /**
     * The callback definition, needs to implemented by the activity showing
     * this dialog.
     */
    public interface OnEnterAuthorizationListener {
        /**
         * Called on the listener if the user submitted credentials.
         * 
         * @param username User name entered.
         * @param password Password entered.
         */
        public void onSubmitAuthorization(String username, String password);

        /**
         * Called on the listener if the user cancelled the dialog.
         */
        public void onCancelAuthorization();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        this.usernamePreset = args.getString(USERNAME_PRESET_KEY);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Let's see whether the activity implements our call-back, we will only
        // pick it if the listener is not yet set:
        if (listener == null)
            try {
                this.listener = (OnEnterAuthorizationListener) activity;
            } catch (ClassCastException e) {
                // Our activity does not listen to us, so we want to dismiss the
                // fragment when it pauses since the listener is likely to be
                // gone onRestart()
                autoDismissOnPause = true;
            }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Define context to use (parent activity might have no theme)
        final ContextThemeWrapper context = new ContextThemeWrapper(getActivity(),
                android.R.style.Theme_Holo_Light_Dialog);

        // Inflate our custom view
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View content = inflater.inflate(R.layout.authorization, null);

        this.usernameTextView = (EditText) content.findViewById(R.id.username);
        usernameTextView.setText(usernamePreset);
        this.passwordTextView = (EditText) content.findViewById(R.id.password);
        passwordTextView.setTypeface(Typeface.SANS_SERIF);

        // Add click listeners
        final Button submitButton = (Button) content.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final CharSequence username = usernameTextView.getText();
                final CharSequence password = passwordTextView.getText();

                if (listener != null)
                    listener.onSubmitAuthorization(username.toString(), password.toString());
                dismiss();
            }
        });
        final Button cancelButton = (Button) content.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onCancel(AuthorizationFragment.this.getDialog());
                dismiss();
            }
        });

        // Build the dialog
        final AlertDialog.Builder abuilder = new AlertDialog.Builder(context);
        abuilder.setTitle(R.string.auth_required)
                .setView(content);

        return abuilder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (listener != null)
            listener.onCancelAuthorization();
    }

    @Override
    public void onPause() {
        super.onPause();

        // We auto-dismiss here, because the fragment should not survive
        // configuration changes when the activity does not implement our
        // listener
        if (autoDismissOnPause)
            dismiss();
    }

    /**
     * Register the callback. This will override any existing listener,
     * including the owning activity that might have been or will be set as the
     * call-back {@link Fragment#onAttach(Activity)}. Setting the listener using
     * this method will cause the fragment to auto-dismiss {@link #onPause()}.
     * 
     * @param listener Listener to call on user action.
     */
    public void setListener(OnEnterAuthorizationListener listener) {
        this.listener = listener;
        this.autoDismissOnPause = true;
    }
}
