package website.julianrosser.birthdays.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import website.julianrosser.birthdays.BuildConfig;
import website.julianrosser.birthdays.R;

public class HelpActivity extends BaseActivity {

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Set up toolbar reference
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up FloatingActionButton ref and listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.email_me_text, Snackbar.LENGTH_LONG)
                        .setAction(R.string.send_email, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                emailMe();
                            }
                        }).show();

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Email FAB")
                        .build());

            }
        });

        // Show home button on toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the shared Tracker instance.
        mTracker = getDefaultTracker();
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Help");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    // Set Activity theme depending on user preference
    public void setTheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("0")) {
            setTheme(R.style.BlueTheme);
        } else if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("1")) {
            setTheme(R.style.PinkTheme);
        } else if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("2")) {
            setTheme(R.style.GreenTheme);
        } else {
            setTheme(R.style.PinkTheme);
        }
    }


    public void emailMe() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "julianrosser91@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Birthday Reminder");

        emailIntent.putExtra(Intent.EXTRA_TEXT, "App Version: " + BuildConfig.VERSION_NAME + "\n\n");

        startActivity(Intent.createChooser(emailIntent, getString(R.string.preffered_email)));

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Send email button")
                .build());
    }

}
