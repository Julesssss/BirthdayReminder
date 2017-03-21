package website.julianrosser.birthdays.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import website.julianrosser.birthdays.Constants;
import website.julianrosser.birthdays.Preferences;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.database.DatabaseHelper;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.recievers.NotificationBuilderReceiver;

/**
 * This service sets notifications alarms for each birthday.
 */
public class SetAlarmsService extends Service {

    private AlarmManager mAlarmManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Preferences.isUsingFirebase(this)) {
            loadBirthdaysFromFirebase();
        } else {
            try {
                loadBirthdaysFromJSON();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Birthdays - IO Error",
                        Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Birthdays - JSON Error",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onBirthdaysLoaded(ArrayList<Birthday> birthdays) {
        // If user wants notifications, set alarms
        if (getNotificationAllowedPref()) {
            for (int i = 0; i < birthdays.size(); i++) {
                Birthday b = birthdays.get(i);

                // If reminder not toggled off, set alarm
                if (b.getRemind()) {
                    setAlarm(b);
                }
            }
        }
        // Service has to control its own life cycles, so call stopSelf here
        stopSelf();
    }

    private void loadBirthdaysFromFirebase() {
        DatabaseHelper.loadFirebaseBirthdays(new DatabaseHelper.BirthdaysLoadedListener() {
            @Override
            public void onBirthdaysReturned(ArrayList<Birthday> birthdays) {
                onBirthdaysLoaded(birthdays);
            }

            @Override
            public void onCancelled(String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.i(getClass().getSimpleName(), errorMessage);
                stopSelf();
            }
        });
    }

    // Function which loads Birthdays from JSON
    private void loadBirthdaysFromJSON() throws IOException,
            JSONException {
        ArrayList<Birthday> birthdays = new ArrayList<>();
        BufferedReader reader = null;
        try {
            // Open and read the file into a StringBuilder
            InputStream in = openFileInput(Constants.FILENAME);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();

            // Build the array of birthdays from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                birthdays.add(new Birthday(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Ignore this one; it happens when starting fresh
        } finally {
            if (reader != null) reader.close();
        }
        onBirthdaysLoaded(birthdays);
    }

    @SuppressWarnings("deprecation")
    private void setAlarm(Birthday birthday) {
        // Get milliseconds remaining in current day
        Date currentTimeDate = new Date();
        int remHour = 23 - currentTimeDate.getHours(); // extra hour
        int remMinute = 60 - currentTimeDate.getMinutes();

        long millisRemainingInDay = (remHour * Constants.HOUR_IN_MILLIS)
                + (remMinute * 60 * 1000);

        // Get days between in milliseconds
        long fullDaysBetweenInMillis = ((birthday.getDaysBetween() - 1) * Constants.DAY_IN_MILLIS);

        // Alarm time in milliseconds
        int hourOfAlarm = getTimeOfReminderPref();
        long millisExtraAlarmHour = hourOfAlarm * Constants.HOUR_IN_MILLIS;

        // For each extra day before notification, add the amount of millis in day
        long dayOfReminderMillis = Constants.DAY_IN_MILLIS * getDaysBeforeReminderPref();

        // //////// millisTotalAlarmDelay
        long alarmDelayInMillis = fullDaysBetweenInMillis + millisExtraAlarmHour
                + millisRemainingInDay - dayOfReminderMillis;

        // Get alarm manager if needed
        if (null == mAlarmManager) {
            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }

        /** If notification time is in the future, build receiver. Else, don't set alarm */
        if (alarmDelayInMillis > 0) {

            // get unique id for each notification from name
            int id = birthday.getName().hashCode();

            // CreateIntent to start the AlarmNotificationReceiver
            Intent mNotificationReceiverIntent = new Intent(this,
                    NotificationBuilderReceiver.class);

            // Build message String
            String messageString = "" + birthday.getName() + "'s " + getResources().getString(R.string.birthday)
                    + " " + getResources().getString(R.string.date_is) + " " +
                    Birthday.getFormattedStringDay(birthday, this);

            mNotificationReceiverIntent.putExtra(NotificationBuilderReceiver.STRING_MESSAGE_KEY, messageString);

            // Create pending Intent using Intent we just built
            PendingIntent mNotificationReceiverPendingIntent = PendingIntent
                    .getBroadcast(this, id,
                            mNotificationReceiverIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            // Finish by passing PendingIntent and delay time to AlarmManager
            mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + alarmDelayInMillis,
                    mNotificationReceiverPendingIntent);

            Log.i(getClass().getSimpleName(), "Set alarm for " + birthday.getName());
        }
    }

    private int getDaysBeforeReminderPref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.valueOf(sharedPref.getString(getString(R.string.pref_days_before_key), "1"));
    }

    private int getTimeOfReminderPref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.valueOf(sharedPref.getString(getString(R.string.pref_time_before_key), getString(R.string.pref_time_12)));
    }

    private boolean getNotificationAllowedPref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getBoolean(getString(R.string.pref_enable_notifications_key), true);
    }
}