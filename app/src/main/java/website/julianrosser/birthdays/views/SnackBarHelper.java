package website.julianrosser.birthdays.views;

import android.support.design.widget.Snackbar;
import android.view.View;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.model.Birthday;

public class SnackBarHelper {

    public static void alarmToggle(View view, Birthday birthday) {
        // Notify user of change. If birthday is today, let user know alarm is set for next year
        if (birthday.getDaysBetween() == 0 && birthday.getRemind()) {
            Snackbar.make(view, view.getContext().getString(R.string.reminder_for) + birthday.getName() + " " +
                    birthday.getReminderString() + view.getContext().getString(R.string.for_next_year), Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(view, view.getContext().getString(R.string.reminder_for) + birthday.getName() + " " +
                    birthday.getReminderString(), Snackbar.LENGTH_LONG).show();
        }
    }

    public static void birthdayDeleted(View view, Birthday birthday) {
        Snackbar.make(view, birthday.getName() + " " + view.getContext().getString(R.string.deleted)
                , Snackbar.LENGTH_LONG).show();
    }

}
