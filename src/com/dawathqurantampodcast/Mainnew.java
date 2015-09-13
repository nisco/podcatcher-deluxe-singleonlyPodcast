
package com.dawathqurantampodcast;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;

import org.codechimp.apprater.AppRater;

public class Mainnew extends Activity {

    UITableView tableView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainb);
        tableView = (UITableView) findViewById(R.id.tableView);
        createList();
        Log.d("MainActivity", "total items: " + tableView.getCount());
        tableView.commit();
        AppRater.app_launched(this);

    }

    private void createList() {
        CustomClickListener listener = new CustomClickListener();
        tableView.setClickListener(listener);

        tableView.addBasicItem(R.drawable.arrow, "Quran Tamil Mp3", "Click here to Begin");
        tableView
                .addBasicItem(R.drawable.lock, "SmartLock-Lock All Your Apps", "Link to PlayStore");
        tableView.addBasicItem(R.drawable.dona, "Support Dawath Apps", "Click Here To Know More");
        tableView.addBasicItem(R.drawable.arrow, "Islamic Apps",
                "More Islamic apps here link to Playstore");

        tableView.addBasicItem(R.drawable.arrow, "Rate this App now", "Link to PlayStore");
        tableView.addBasicItem(R.drawable.arrow, "Share This App", "Fb,twitter,email,whatsapp...");
        tableView.addBasicItem(R.drawable.arrow, "About This App", "This an Unoffical App");

    }

    private class CustomClickListener implements ClickListener {

        @Override
        public void onClick(int index) {
            Log.d("MainActivity", "item clicked: " + index);
            if (index == 0) {
                Intent i = new Intent(Mainnew.this, PodcastActivity.class);
                startActivity(i);

            }
            else if (index == 1) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.cricketumpirelearn"));
                startActivity(i);
            }
            else if (index == 2) {
                Intent i = new Intent(Mainnew.this, Inappurchase.class);
                startActivity(i);

            }

            else if (index == 3) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://search?q=pub:dawath+apps"));
                startActivity(i);
            }

            else if (index == 4) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.dawathqurantampodcast"));
                startActivity(i);
            }
            else if (index == 5) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT,
                        "Download Quran Tamil Mp3  for Android");
                i.putExtra(Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=com.dawathqurantampodcast");
                startActivity(Intent.createChooser(i, "Share URL"));
            }
            else if (index == 6) {
                Intent i = new Intent(Mainnew.this, Abouta.class);
                startActivity(i);
            }

            else if (index == 7) {

                tableView.clear();
            }

        }

    }
}
