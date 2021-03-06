package com.example.benth.leaguedelta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightMode = sharedPref.getBoolean("Night Mode", false);
        simulateDayNight(nightMode ? 1 : 0);

        Element e = new Element();
        e.setIconDrawable(R.drawable.porosmooch);
        e.setAutoApplyIconTint(false);
        e.setTitle("Help keep us ad-free");
        e.setIntent(new Intent(this, DonationPage.class));

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.porosmile)
                .setDescription("Check us out!")
                .addItem(new Element().setTitle("Version " + getString(R.string.version)))
                .addGroup("Connect with the dev")
                .addTwitter("benthecarman", "Follow him on Twitter")
                .addGitHub("benthecarman", "Fork him on GitHub")
                .addItem(e)
                .addItem(new Element().setTitle(getString(R.string.copyright)))
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