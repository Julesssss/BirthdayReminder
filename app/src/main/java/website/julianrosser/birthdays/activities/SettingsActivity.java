package website.julianrosser.birthdays.activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import website.julianrosser.birthdays.AlarmsHelper;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    public Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        // hide drop shadow if running lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {
            findViewById(R.id.drop_shadow_settings).setVisibility(View.GONE);
        }

        // Pass Toolbar so it can be used like ActionBar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Create Settings Fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();

        // Obtain the shared Tracker instance.
        mTracker = getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("Settings");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Detect if Activity is closing, and recreate BirthdayListActivity to apply new theme
        if (this.isFinishing()) {
            setResult(BirthdayListActivity.RC_SETTINGS);
        }
    }

    // Sets theme based on users preference
    public void setTheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("0")) {
            setTheme(R.style.PreferenceThemeBlue);
        } else if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("1")) {
            setTheme(R.style.PreferenceThemePink);
        } else {
            setTheme(R.style.PreferenceThemeGreen);
        }
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell set prop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    protected void onStop() {
        super.onStop();
        AlarmsHelper.setAllNotificationAlarms(this);
    }

}
