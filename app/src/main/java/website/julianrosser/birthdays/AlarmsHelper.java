package website.julianrosser.birthdays;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.recievers.NotificationBuilderReceiver;
import website.julianrosser.birthdays.services.SetAlarmsService;

public class AlarmsHelper {

    public static void setAllNotificationAlarms(Context context) {
        Intent serviceIntent = new Intent(context, SetAlarmsService.class);
        context.startService(serviceIntent);
    }

    public static void cancelAllAlarms(Context context, ArrayList<Birthday> birthdays) {
        for (Birthday b: birthdays) {
            cancelAlarm(context, b.getName().hashCode());
        }
    }

    // This builds an identical PendingIntent to the alarm and cancels when
    public static void cancelAlarm(Context context, int id) {

        // CreateIntent to start the AlarmNotificationReceiver
        Intent mNotificationReceiverIntent = new Intent(context,
                NotificationBuilderReceiver.class);

        // Create pending Intent exactly as it was set previously
        PendingIntent mNotificationReceiverPendingIntent = PendingIntent
                .getBroadcast(context.getApplicationContext(),
                        id,
                        mNotificationReceiverIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel alarm
        AlarmManager mAlarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (mAlarmManager != null) {
            mAlarmManager.cancel(mNotificationReceiverPendingIntent);
        }
    }

}