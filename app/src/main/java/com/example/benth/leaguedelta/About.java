package com.example.benth.leaguedelta;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.jinx)
                .setDescription("Check us out!")
                .addItem(new Element().setTitle("Version 0.1.0"))
                .addGroup("Connect with the dev")
                .addTwitter("benthecarman", "Follow him on Twitter")
                .addGitHub("benthecarman", "Fork him on GitHub")
                .addItem(new Element().setTitle(getString(R.string.copyright)))
                .create();

        setContentView(aboutPage);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}