package website.julianrosser.birthdays;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import website.julianrosser.birthdays.recievers.NotificationBuilderReceiver;
import website.julianrosser.birthdays.services.SetAlarmsService;

public class AlarmsHelper {

    public static void setAllNotificationAlarms(Context context) {
        Intent serviceIntent = new Intent(context, SetAlarmsService.class);
        context.startService(serviceIntent);
    }

    // This builds an identical PendingIntent to the alarm and cancels when
    public static void cancelAlarm(Context context, int id) {

        // CreateIntent to start the AlarmNotificationReceiver
        Intent mNotificationReceiverIntent = new Intent(context,
                NotificationBuilderReceiver.class);

        // Create pending Intent using Intent we just built
        PendingIntent mNotificationReceiverPendingIntent = PendingIntent
                .getBroadcast(context.getApplicationContext(),
                        id,
                        mNotificationReceiverIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel alarm
        AlarmManager mAlarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.cancel(mNotificationReceiverPendingIntent);
    }

}