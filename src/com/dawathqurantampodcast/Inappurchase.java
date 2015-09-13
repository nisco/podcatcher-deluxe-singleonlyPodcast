
package com.dawathqurantampodcast;

//import org.codechimp.apprater.AppRater;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;

import com.hdik.inapppurchasewrapper.utils.IabResult;
import com.hdik.inapppurchasewrapper.utils.InAppListener;
import com.hdik.inapppurchasewrapper.utils.InAppWrapper;
import com.hdik.inapppurchasewrapper.utils.Inventory;
import com.hdik.inapppurchasewrapper.utils.Purchase;

import java.util.List;

public class Inappurchase extends Activity implements InAppListener {

    UITableView tableView;
    private InAppWrapper inApp;
    static final String BASE_64_KEY = "your base key here";
    protected final String SKUA = "com.sample";
    protected final String SKUB = "com.sample1";
    protected final String SKUC = "com.sample2";

    private boolean isGoPro;

    private int requestCode = 22211;
    SharedPreferences mPrefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainb);
        tableView = (UITableView) findViewById(R.id.tableView);
        createList();
        Log.d("MainActivity", "total items: " + tableView.getCount());
        tableView.commit();
        inApp = new InAppWrapper(this, this);
        inApp.Init(BASE_64_KEY);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // second argument is the default to use if the preference can't be
        // found
        Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);

        if (!welcomeScreenShown) {
            // here you can launch another activity if you like
            // the code below will display a popup

            String whatsNewTitle = getResources().getString(R.string.whatsNewTitle);
            String whatsNewText = getResources().getString(R.string.whatsNewText);
            new AlertDialog.Builder(this).setIcon(R.drawable.wallet).setTitle(whatsNewTitle)
                    .setMessage(whatsNewText).setPositiveButton(
                            R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(welcomeScreenShownPref, true);
            editor.commit(); // Very important to save the preference
        }

    }

    private void createList() {
        CustomClickListener listener = new CustomClickListener();
        tableView.setClickListener(listener);

        tableView.addBasicItem(R.drawable.dona, "Donate X 1", "");
        tableView.addBasicItem(R.drawable.dona, "Donate X 2", "");
        tableView.addBasicItem(R.drawable.dona, "Donate X 3", "");

    }

    private class CustomClickListener implements ClickListener {

        @Override
        public void onClick(int index) {
            Log.d("MainActivity", "item clicked: " + index);
            if (index == 0) {
                inApp.launchPurchaseFlow(SKUA, requestCode);

            }
            else if (index == 1) {
                inApp.launchPurchaseFlow(SKUB, requestCode);
            }

            else if (index == 2) {
                inApp.launchPurchaseFlow(SKUC, requestCode);
            }

            else if (index == 3) {

                tableView.clear();
            }

        }

    }

    @Override
    public void onBackPressed()
    {

        super.onBackPressed();

    }

    @Override
    public void InAppInvetoryFinished(IabResult result, Inventory inventory) {
        // TODO Auto-generated method stub

        // Have we been disposed of in the meantime? If so, quit.

        // Is it a failure?
        if (result.isFailure()) {
            inApp.complain("Failed to query inventory: " + result);

            return;
        }

        System.out.println("Query inventory was successful.");

        Purchase goPro =
                inventory.getPurchase(SKUA);
        inventory.getPurchase(SKUB);
        inventory.getPurchase(SKUC);

        isGoPro = (goPro != null && inApp.verifyDeveloperPayload(goPro));
        Log.d("InApp", "User is "
                + (isGoPro ? "YES PRO VERSION " : "NOT PRO VERSION"));
        if (isGoPro)
            inApp.consumeAsync(goPro);

    }

    @Override
    public void InAppPurchaseFinished(IabResult result, Purchase purchase) {
        // TODO Auto-generated method stub
        if (result.isFailure()) {
            Toast.makeText(this, "Please Check Your Card number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (purchase.getSku().equals(SKUA)) {
            Log.d("In app Purchase", "Purchase successful.");
            Toast.makeText(this, "JazhakhAllah Khair for Donation", Toast.LENGTH_SHORT)
                    .show();
        }
        if (purchase.getSku().equals(SKUB)) {
            Log.d("In app Purchase", "Purchase successful.");
            Toast.makeText(this, "JazhakhAllah Khair for Donation", Toast.LENGTH_SHORT)
                    .show();
        }
        if (purchase.getSku().equals(SKUC)) {
            Log.d("In app Purchase", "Purchase successful.");
            Toast.makeText(this, "JazhakhAllah Khair for Donation", Toast.LENGTH_SHORT)
                    .show();

        }

    }

    @Override
    public void InAppConsumeFinished(Purchase purchase, IabResult result) {
        // TODO Auto-generated method stub
        Log.d("In app Purchase", "Consumption finished. Purchase: " + purchase
                + ", result: " + result);
        if (result == null || result.isFailure()) {
            Toast.makeText(this, "Error in consumption", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else {
            Toast.makeText(this, "previous purchase successfully consume",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void InApponConsumeMultiFinished(List<Purchase> purchases,
            List<IabResult> results) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("In App Purchase", "onActivityResult(" + requestCode + ","
                + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!inApp.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d("In App Purchase", "onActivityResult handled by IABUtil.");
        }
    }

}
