package website.julianrosser.birthdays;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

public class AlarmNotificationBuilder extends BroadcastReceiver {

    // Birthday data
    public static String STRING_MESSAGE_KEY = "message_key";

    // Use same ID's, so that only 1 notification can be shown at any time.
    int PENDING_INTENT_ID = 0;
    int MY_NOTIFICATION_ID = 100;

    // get sound from context
    Uri notificationSound = Uri.parse("android.resource://"
            + MainActivity.mAppContext.getPackageName() + "/" + R.raw.birthday_notification);

    // Vibration pattern used on notification
    int delay = 100;
    private long[] mVibratePattern = {0, delay, delay, delay, delay, delay};

    @Override
    public void onReceive(Context context, Intent intent) {

        // Intent which opens App when notification is clicked
        Intent mNotificationIntent = new Intent(context, MainActivity.class);

        // Use Intent and other information to build PendingIntent
        PendingIntent mContentIntent = PendingIntent.getActivity(context, PENDING_INTENT_ID,
                mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get desired notification message we received from intent Bundle
        String mMessageString = intent.getExtras().getString(STRING_MESSAGE_KEY);

        // Build notification
        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(mMessageString)
                .setSmallIcon(R.drawable.ic_cake_white_24dp)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(mMessageString)
                .setContentIntent(mContentIntent)
                .setSound(notificationSound)
                .setVibrate(mVibratePattern);

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

        // Output
        Log.i(getClass().getSimpleName(), "" + mMessageString + " - notification at: " + DateFormat.getDateTimeInstance().format(new Date()));
    }
}
