package website.julianrosser.birthdays.viewholder;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.activities.BirthdayListActivity;
import website.julianrosser.birthdays.fragments.RecyclerListFragment;
import website.julianrosser.birthdays.model.Birthday;

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
    private Typeface typeLight;

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
        typeLight = Typeface.createFromAsset(BirthdayListActivity.getAppContext().getResources().getAssets(), "Roboto-Light.ttf");
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
    public void onClick(View v) {
        Birthday birthday = (Birthday) v.getTag();

        int id = v.getId();
        if (id == R.id.alarmImage) {
            birthday.toggleReminder();

            // Get correct position, as deleted views may have altered pos int
            int currentPosition = RecyclerListFragment.recyclerView.getChildAdapterPosition(itemView);

            BirthdayListActivity.mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Toggle Alarm ICON")
                    .build());

            // Callback to BirthdayListActivity.
            BirthdayListActivity.getContext().alarmToggled(currentPosition);

        } else {
            // Get actual position, accounting for deletion
            int currentPosition = RecyclerListFragment.recyclerView.getChildAdapterPosition(itemView);

            // Open ItemOption menu for selected birthday
            if (currentPosition != RecyclerView.NO_POSITION) {
                BirthdayListActivity.getContext().showItemOptionsFragment(currentPosition);
            } else {
                Snackbar.make(container, R.string.error_try_again, Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}