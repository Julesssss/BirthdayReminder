package website.julianrosser.birthdays.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.Utils;

public class PrivacyPolicyActivity extends BaseActivity {

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        TextView textPrivacySummary = (TextView) findViewById(R.id.textPrivacySummary);
        setPrivacyTitleColour(textPrivacySummary);
        TextView textPrivacyFull = (TextView) findViewById(R.id.textPrivacyFull);
        setPrivacyTitleColour(textPrivacyFull);

        // Set up toolbar reference
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show home button on toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the shared Tracker instance.
        mTracker = getDefaultTracker();

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Privacy Policy Activity")
                .build());
    }

    private void setPrivacyTitleColour(TextView textPrivacySummary) {
        int textColor = Utils.getHighlightColor(getApplicationContext());
        textPrivacySummary.setTextColor(getResources().getColor(textColor));
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
        mTracker.setScreenName("Privacy Policy");
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



}
