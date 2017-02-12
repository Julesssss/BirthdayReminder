package website.julianrosser.birthdays;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import website.julianrosser.birthdays.services.SetAlarmsService;

public class AlarmsHelper {

    public static void setAllNotificationAlarms(Context context) {
        Intent serviceIntent = new Intent(context, SetAlarmsService.class);
        context.startService(serviceIntent);
        Toast.makeText(context, "SETTING ALARMS", Toast.LENGTH_SHORT).show(); // tod - remove
    }

}