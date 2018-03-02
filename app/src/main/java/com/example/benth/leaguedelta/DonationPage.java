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

        String btcAddress = "38qvnY3dwsUSZ79jupUwRrp3wfeX329M4A";
        String ethAddress = "0xff01ed523b971c313b73e27871f1b011f25c1aa8";
        String ltcAddress = "MCx8KSqHzUPAsgdxXEp6tbKrqZ1xF3Vcd2";
        String nanoAddress = "xrb_3i91x9xp9jczrrq5dxggbgshg933o6mq918rcm5n54quyzca95hjpni1zqxw";

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

        Element eth = new Element();
        eth.setTitle(ethAddress);
        eth.setIconDrawable(R.drawable.eth);
        eth.setAutoApplyIconTint(false);
        eth.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("eth", ethAddress);
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(DonationPage.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        Element ltc = new Element();
        ltc.setTitle(ltcAddress);
        ltc.setIconDrawable(R.drawable.ltc);
        ltc.setAutoApplyIconTint(false);
        ltc.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("ltc", ltcAddress);
            assert clipboard != null;
            clipboard.setPrimaryClip(clip);
            Toast.makeText(DonationPage.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        Element nano = new Element();
        nano.setTitle(nanoAddress);
        nano.setIconDrawable(R.drawable.nano);
        nano.setAutoApplyIconTint(false);
        nano.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("nano", nanoAddress);
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
                .addItem(eth)
                .addItem(ltc)
                .addItem(nano)
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
