package website.julianrosser.birthdays.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import website.julianrosser.birthdays.BirthdayReminder;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.recievers.NotificationBuilderReceiver;
import website.julianrosser.birthdays.services.SetAlarmsService;

public class SettingsActivity extends BaseActivity {

    static Context mContext;
    private Tracker mTracker;

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

        mContext = getApplicationContext();

        // Obtain the shared Tracker instance.
        mTracker = getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("Settings");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    //
    @Override
    protected void onPause() {
        super.onPause();

        // Detect if Activity is closing, and recreate BirthdayListActivity to apply new theme
        if (this.isFinishing()) {
            Intent i = new Intent(getApplicationContext(), BirthdayListActivity.class);
            startActivity(i);
        }
    }

    /**
     * This method launches a test notification if the user wants to see an example of the reminder
     */
    private static void launchTestNotification() {

        Context context = BirthdayReminder.getInstance();

        int PENDING_INTENT_ID = 0;
        int MY_NOTIFICATION_ID = 100;

        // Intent which opens App when notification is clicked
        Intent mNotificationIntent = new Intent(); //(context, BirthdayListActivity.class);

        // Use Intent and other information to build PendingIntent
        PendingIntent mContentIntent = PendingIntent.getActivity(context, PENDING_INTENT_ID, // test noti, diff number
                mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String messageText = "Julian's " + mContext.getString(R.string.birthday) + " " + mContext.getResources().getString(R.string.date_is)
                + " " + mContext.getResources().getString(R.string.date_tomorrow);

        // Build notification
        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(messageText)
                .setSmallIcon(R.drawable.ic_cake_white_24dp)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(messageText)
                .setContentIntent(mContentIntent);

        if (NotificationBuilderReceiver.getVibrationAllowedPref(context)) {
            notificationBuilder.setVibrate(NotificationBuilderReceiver.mVibratePattern);
        }
        if (NotificationBuilderReceiver.getSoundAllowedPref(context)) {
            notificationBuilder.setSound(NotificationBuilderReceiver.notificationSound);
        }

        // Get NotificationManager
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Pass built notification to NotificationManager, depending on API level.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { // api
            // 16+
            mNotificationManager.notify(MY_NOTIFICATION_ID,
                    notificationBuilder.build());
        } else {
            //noinspection deprecation
            mNotificationManager.notify(MY_NOTIFICATION_ID,
                    notificationBuilder.getNotification());
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
        Intent serviceIntent = new Intent(this, SetAlarmsService.class);
        startService(serviceIntent);
    }

    /**
     * todo - REFACTOR
     * Use separate fragment so we can keep the ActionBar
     */
    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_days_before_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_time_before_key)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_by_key)));

            Preference testNotiPref = findPreference(getString(R.string.pref_test_notification_key));
            testNotiPref.setOnPreferenceClickListener(this);

            Preference themePref = findPreference(getString(R.string.pref_theme_key));
            themePref.setOnPreferenceClickListener(this);
            themePref.setOnPreferenceChangeListener(this);
            setThemeSummary(themePref);

            Preference sortByPref = findPreference(getString(R.string.pref_sort_by_key));
            sortByPref.setOnPreferenceChangeListener(this);
        }

        private void setThemeSummary(Preference pref) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }

        /**
         * Attaches a listener so the summary is always updated with the preference value.
         * Also fires the listener once, to initialize the summary (so it shows up before the value
         * is changed.)
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(this);

            // Trigger the listener immediately with the preference's
            // current value.
            onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }

        // Callback method for updating preference summary
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list (since they have separate labels/values).
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }

            } else {
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }

            if (preference.getKey().equals(getString(R.string.pref_theme_key))) {
                // If theme preference is changed, immediately recreate the activity.
                getActivity().recreate();
                // Also remove BirthdayListActivity, so it can be recreated to apply theme

            }

            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {

            // If user clicks 'Test Reminder', launch test notification
            if (preference.getKey().equals(getString(R.string.pref_test_notification_key))) {
                launchTestNotification();
            }

            ((SettingsActivity)getActivity()).mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Preference")
                    .setAction("Pref click")
                    .setLabel(preference.getKey())
                    .build());

            return false;
        }
    }
}