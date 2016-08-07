package website.julianrosser.birthdays.recievers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import website.julianrosser.birthdays.Constants;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.activities.BirthdayListActivity;

public class NotificationBuilderReceiver extends WakefulBroadcastReceiver {

    public static String STRING_MESSAGE_KEY = "message_key";
    public static String WAKE_LOCK_TAG = "wake_lock_key";

    // Use same ID's, so that only 1 notification can be shown at any time.
    int PENDING_INTENT_ID = 2000;
    int MY_NOTIFICATION_ID = 100;

    // Hard coded reference to sound file, also used for test notification
    public static Uri notificationSound = Uri.parse("android.resource://website.julianrosser.birthdays/" + R.raw.birthday_notification);

    // Vibration pattern used on notification
    public static long[] mVibratePattern = {0, 100, 100, 100, 100, 100};

    @Override
    public void onReceive(Context context, Intent intent) {

        // Acquire WakeLock to prevent device sleeping before alarms are set.
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                WAKE_LOCK_TAG);
        wakeLock.acquire();

        // Intent which opens App when notification is clicked
        Intent mNotificationIntent = new Intent(context, BirthdayListActivity.class);

        mNotificationIntent.putExtra(Constants.INTENT_FROM_KEY, Constants.INTENT_FROM_NOTIFICATION);

        // Use Intent and other information to build PendingIntent
        PendingIntent mContentIntent = PendingIntent.getActivity(context, PENDING_INTENT_ID,
                mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get desired notification message we received from intent Bundle
        String mMessageString = intent.getExtras().getString(STRING_MESSAGE_KEY);

        // Build notification
        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(mMessageString)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_cake_white_24dp)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(mMessageString)
                .setContentIntent(mContentIntent);

        // These notification parameters depend on users preferences
        if (getVibrationAllowedPref(context)) {
            notificationBuilder.setVibrate(mVibratePattern);
        }
        if (getSoundAllowedPref(context)) {
            notificationBuilder.setSound(notificationSound);
        }

        // Set Priority
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
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

        // Obtain the shared Tracker instance.
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        Tracker mTracker = analytics.newTracker(R.xml.global_tracker);

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Reminder!")
                .build());

        wakeLock.release();

    }

    public static boolean getVibrationAllowedPref(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);

        return sharedPref.getBoolean(c.getString(R.string.pref_vibrate_key), true);
    }

    public static boolean getSoundAllowedPref(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);

        return sharedPref.getBoolean(c.getString(R.string.pref_sound_key), true);
    }

}