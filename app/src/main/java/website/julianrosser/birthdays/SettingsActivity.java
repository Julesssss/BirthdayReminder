package website.julianrosser.birthdays;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Pass Toolbar so it can be used like ActionBar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getFragmentManager().beginTransaction()
                .replace(R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent serviceIntent = new Intent(this, SetAlarmsService.class);
        startService(serviceIntent);
    }

    /**
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

            Preference sortByPref = findPreference(getString(R.string.pref_sort_by_key));
            sortByPref.setOnPreferenceChangeListener(this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = super.onCreateView(inflater, container, savedInstanceState);
            if (view != null) {
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }

            return view;
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

            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {

            // If user clicks 'Test Reminder', launch test notification
            if (preference.getKey().equals(getString(R.string.pref_test_notification_key))) {
                launchTestNotification();
            }
            return false;
        }
    }

    /**
     * This method launches a test notification if the user wants to see an example of the reminder
     */
    private static void launchTestNotification() {

        Context context = MainActivity.getAppContext();

        int MY_NOTIFICATION_ID = 155;

        // Intent which opens App when notification is clicked
        Intent mNotificationIntent = new Intent(); //(context, MainActivity.class);

        // Use Intent and other information to build PendingIntent
        PendingIntent mContentIntent = PendingIntent.getActivity(context, 1, // test noti, diff number
                mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker("(Test!) Julian's birthday is tomorrow!")
                .setSmallIcon(R.drawable.ic_cake_white_24dp)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText("(Test!) Julian's birthday is tomorrow!")
                .setContentIntent(mContentIntent);

        if (AlarmNotificationBuilder.getVibrationAllowedPref(context)) {
            notificationBuilder.setVibrate(AlarmNotificationBuilder.mVibratePattern);
        }
        if (AlarmNotificationBuilder.getSoundAllowedPref(context)) {
            notificationBuilder.setSound(AlarmNotificationBuilder.notificationSound);
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
            mNotificationManager.notify(MY_NOTIFICATION_ID,
                    notificationBuilder.getNotification());
        }
    }
}
