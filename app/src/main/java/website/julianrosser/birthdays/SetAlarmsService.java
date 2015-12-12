package website.julianrosser.birthdays;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
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

/** This service sets notifications alarms for each birthday. */
public class SetAlarmsService extends Service {

    ArrayList<Birthday> mBirthdayList = new ArrayList<>();

    long dayInMillis = 24 * 60 * 60 * 1000l; // / 86,400,000 milli's per day

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

        Log.i(getClass().getSimpleName(), "Service Started");

        mContext = getApplicationContext();

        try {
            mBirthdayList = loadBirthdays();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Birthdays - IO Exception",
                    Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Birthdays - JSON Exception",
                    Toast.LENGTH_LONG).show();
        }
        for (int i = 0; i < mBirthdayList.size(); i++) {
            Birthday b = mBirthdayList.get(i);
            setAlarm(b);
        }
        // Service has to control its own lifecycles, so stop here
        stopSelf();
    }

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

        // Get desired time in millis until notification fires.

        // Get alarm manager if needed
        if (null == mAlarmManager) {
            mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }

        /**
         * PREVIOUS SET ALARM CODE, NEED TO CHECK
         *
        // Create PendingIntent to start the
        // AlarmNotificationReceiver
        mNotificationReceiverIntent = new Intent(mContext,
                AlarmNotificationReceiver.class);

        // Pass name & day data
        mNotificationReceiverIntent.putExtra("name", b.getName());
        mNotificationReceiverIntent.putExtra("days",
                b.getDaysNoti());

        mNotificationReceiverPendingIntent = PendingIntent
                .getBroadcast(mContext, id,
                        mNotificationReceiverIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // start alarm
        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + alarmDelayInMillis,
                mNotificationReceiverPendingIntent);
         */

    }
}
