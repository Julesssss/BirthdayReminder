package website.julianrosser.birthdays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Listen for device restarts, so we can reset all the canceled alarms.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent i) {

        // Launch service to set alarms if ACTION_BOOT_COMPLETE is recieved
        if (Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())) {

            Toast.makeText(context, "Action = BOOT_COMPLETED...", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Action = BOOT_COMPLETED...", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Action = BOOT_COMPLETED...", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Action = BOOT_COMPLETED...", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Action = BOOT_COMPLETED...", Toast.LENGTH_LONG).show();

            Intent serviceIntent = new Intent(context, SetAlarmsService.class);
            context.startService(serviceIntent);
        }
    }
}