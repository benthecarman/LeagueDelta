package com.example.benth.leaguedelta;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class DonationPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightMode = sharedPref.getBoolean("Night Mode", false);
        simulateDayNight(nightMode ? 1 : 0);

        String btcAddress = "325UiftBi8cFuDTmkLMjNsNcS8HkC6Gaa9";

        Element btc = new Element();
        btc.setIconDrawable(R.drawable.btc);
        btc.setAutoApplyIconTint(false);
        btc.setTitle(btcAddress);
        btc.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("btc", btcAddress);
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(DonationPage.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        Element ty = new Element();
        ty.setGravity(Gravity.CENTER_HORIZONTAL);
        ty.setTitle(getString(R.string.ty).toUpperCase());

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(getString(R.string.crypto))
                .addItem(btc)
                .addItem(ty)
                .create();
        setContentView(aboutPage);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true); //Show the Up button in the action bar.
    }

    private void simulateDayNight(int currentSetting) {
        final int DAY = 0;
        final int NIGHT = 1;
        final int FOLLOW_SYSTEM = 3;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (currentSetting == FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
