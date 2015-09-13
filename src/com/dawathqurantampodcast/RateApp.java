
package com.dawathqurantampodcast;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dawathqurantampodcast.R;

public class RateApp extends Activity implements OnClickListener {

    TextView txtTitle;
    TextView txtInfo;
    Button btnLike;

    String packageName;

    // onCreate
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_app);

        // setup TextViews and Buttons...

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("Kindly Rate this app...");

        txtInfo = (TextView) findViewById(R.id.txtInfo);
        txtInfo.setText("If you like this app, consider to rate it on Play Store and let other users discover it. "
                + "" + " Jazhakha'Allah Khairan!!!");

        btnLike = (Button) findViewById(R.id.btnLike);

        // set click listener for each button...
        btnLike.setOnClickListener(this);

        // get package name so we can open market...

        // email to address and subject...
    }

    // handle button clicks...

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnLike:

                // device must have market app installed...

                try {
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.androbayyinah‎"));
                    startActivity(i);
                    break;
                } catch (Exception e) {
                }
                break;

        }
    }

}
