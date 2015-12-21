package website.julianrosser.birthdays;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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

/**
 * This service sets notifications alarms for each birthday.
 */
public class SetAlarmsService extends Service {

    ArrayList<Birthday> mBirthdayList = new ArrayList<>();

    long dayInMillis = 86400000l; // / 86,400,000 milliseconds in a day

    long hourInMillis = 3600000l; // Amount of milliseconds in an hour

    long fullDaysBetweenInMillis, millisExtraAlarmHour, millisRemainingInDay,
            dayOfReminderMillis, alarmDelayInMillis;

    private AlarmManager mAlarmManager;

    static Context mContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        try {
            mBirthdayList = loadBirthdays();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Birthdays - IO Error",
                    Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Birthdays - JSON Error",
                    Toast.LENGTH_LONG).show();
        }

        // If user wants notifications, set alarms
        if (getNotificationAllowedPref()) {
            for (int i = 0; i < mBirthdayList.size(); i++) {
                Birthday b = mBirthdayList.get(i);

                // If reminder not toggled off, set alarm
                if (b.getRemind()) {
                    setAlarm(b);
                }
            }
        }
        // Service has to control its own life cycles, so call stopSelf here
        stopSelf();
    }

    // Function which loads Birthdays from JSON
    public static ArrayList<Birthday> loadBirthdays() throws IOException,
            JSONException {
        ArrayList<Birthday> birthdays = new ArrayList<Birthday>();
        BufferedReader reader = null;
        try {
            // Open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(MainActivity.FILENAME); // Causes crash if no MainActivity??????
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
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
            if (reader != null)
                reader.close();
        }
        return birthdays;
    }

    private void setAlarm(Birthday b) {
        // Get milliseconds remaining in current day
        Date currentTimeDate = new Date();
        int remHour = 23 - currentTimeDate.getHours(); // extra hour
        int remMinute = 60 - currentTimeDate.getMinutes();

        millisRemainingInDay = (remHour * hourInMillis)
                + (remMinute * 60 * 1000);

        // Get days between in milliseconds
        fullDaysBetweenInMillis = ((b.getDaysBetween() - 1) * dayInMillis);

        // Alarm time in milliseconds
        int hourOfAlarm = getTimeOfReminderPref();
        millisExtraAlarmHour = hourOfAlarm * hourInMillis; // Set alarm to 12th hour of day

        // For each extra day before notification, add the amount of millis in day
        dayOfReminderMillis = dayInMillis * getDaysBeforeReminderPref();

        // //////// millisTotalAlarmDelay
        alarmDelayInMillis = fullDaysBetweenInMillis + millisExtraAlarmHour
                + millisRemainingInDay - dayOfReminderMillis; // + days

        // Get alarm manager if needed
        if (null == mAlarmManager) {
            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }

        /** If notification time is in the future, build receiver. Else, don't set alarm */
        if (alarmDelayInMillis > 0) {

            // get unique id for each notification from name
            int id = b.getName().hashCode();

            // CreateIntent to start the AlarmNotificationReceiver
            Intent mNotificationReceiverIntent = new Intent(mContext,
                    AlarmNotificationBuilder.class);

            // Build message String
            String messageString = "" + b.getName() + "'s birthday is " + Birthday.getFormattedStringDay(b, mContext);

            mNotificationReceiverIntent.putExtra(AlarmNotificationBuilder.STRING_MESSAGE_KEY, messageString); //

            // Create pending Intent using Intent we just built
            PendingIntent mNotificationReceiverPendingIntent = PendingIntent
                    .getBroadcast(mContext, id,
                            mNotificationReceiverIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            // Finish by passing PendingIntent and delay time to AlarmManager
            mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + alarmDelayInMillis,
                    mNotificationReceiverPendingIntent);

            Date dateOfAlarm = new Date();
            dateOfAlarm.setTime(dateOfAlarm.getTime() + alarmDelayInMillis);
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