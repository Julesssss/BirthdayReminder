package website.julianrosser.birthdays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Listen for device restarts, so we can reset all the canceled alarms.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {

        // Launch service to set alarms if ACTION_BOOT_COMPLETE is received
        if (Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())) {

            Intent serviceIntent = new Intent(context, SetAlarmsService.class);
            context.startService(serviceIntent);
        }
    }
}