package website.julianrosser.birthdays.views.viewholder;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.database.DatabaseHelper;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.model.events.BirthdayItemClickEvent;
import website.julianrosser.birthdays.views.SnackBarHelper;

/**
 * ViewHolder class to hold view references to be used in recyclerview.
 */
public class BirthdayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // TextView references
    private TextView textDateDay;
    private TextView textDateMonth;
    private TextView textName;
    private TextView textAge;
    private TextView textDaysRemaining;
    private ImageView imageAlarm;
    private View container;

    public BirthdayViewHolder(View itemView) {
        super(itemView);

        // Set up references
        container = itemView;
        textName = (TextView) itemView.findViewById(R.id.name);
        textDaysRemaining = (TextView) itemView.findViewById(R.id.days_remaining);
        textAge = (TextView) itemView.findViewById(R.id.textViewAge);
        textDateDay = (TextView) itemView.findViewById(R.id.dateDay);
        textDateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
        imageAlarm = (ImageView) itemView.findViewById(R.id.alarmImage);
        setImageClickListener();
    }

    public void setTag(Birthday birthday) {
        container.setTag(birthday);
        imageAlarm.setTag(birthday);
    }

    public void showView() {
        container.setVisibility(View.VISIBLE);
    }

    public void setText(String name) {
        textName.setText(name);
    }

    public void setDaysRemaining(String formattedDaysRemainingString) {
        textDaysRemaining.setText(formattedDaysRemainingString);
    }

    public void setBirthday(String birthDay, String birthMonth) {
        textDateDay.setText(birthDay);
        textDateMonth.setText(birthMonth);
    }

    public void displayAgeIfNeeded(boolean includeYear, int year, String age) {
        // Should display age?
        if (includeYear) {
            textAge.setVisibility(View.VISIBLE);
        } else {
            textAge.setVisibility(View.GONE);
        }
        if (year != 0) {
            textAge.setText(age);
        } else {
            // don't show age field
            textAge.setText(R.string.not_available);
        }
    }

    public void setImageIcon(Drawable remindAlarmDrawable) {
        imageAlarm.setImageDrawable(remindAlarmDrawable);

    }

    public void setImageClickListener() {
        container.setOnClickListener(this);
        imageAlarm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Birthday birthday = (Birthday) view.getTag();

        int id = view.getId();

        if (id == R.id.alarmImage) {
            birthday.toggleReminder();
            DatabaseHelper.saveBirthdayChange(birthday, DatabaseHelper.Update.UPDATE);
            SnackBarHelper.alarmToggle(view, birthday);
        } else {
            // Callback
            EventBus.getDefault().post(new BirthdayItemClickEvent(birthday));
        }
    }
}