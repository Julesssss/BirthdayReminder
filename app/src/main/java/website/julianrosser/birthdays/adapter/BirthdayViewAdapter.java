package website.julianrosser.birthdays.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import website.julianrosser.birthdays.BirthdayReminder;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.views.viewholder.BirthdayViewHolder;

public class BirthdayViewAdapter extends RecyclerView.Adapter<BirthdayViewHolder> {

    private ArrayList<Birthday> birthdays;

    // Constructor
    public BirthdayViewAdapter() {
        birthdays = new ArrayList<>();
    }

    @Override
    public BirthdayViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.birthday_list_view, viewGroup, false);

        return new BirthdayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BirthdayViewHolder viewHolder, final int position) {
        // Get reference to birthday
        final Birthday birthday = birthdays.get(position);
        viewHolder.setTag(birthday);
        viewHolder.showView();
        // Pass data to the TextViews
        viewHolder.setText(birthday.getName());
        viewHolder.setDaysRemaining(birthday.getFormattedDaysRemainingString());
        viewHolder.setBirthday(birthday.getBirthDay(), birthday.getBirthMonth());
        viewHolder.displayAgeIfNeeded(birthday.shouldIncludeYear(), birthday.getYear(), birthday.getAge());
        viewHolder.setImageIcon(birthday.getRemindAlarmDrawable());
    }

    @Override
    public void onViewRecycled(BirthdayViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public void setData(ArrayList<Birthday> birthdays) {
        this.birthdays = birthdays;

        Context context = BirthdayReminder.getInstance();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // Get users sort preference
        if (Integer.valueOf(sharedPref.getString(context.getString(R.string.pref_sort_by_key), "0")) == 1) {
            sortBirthdaysByName();
        } else {
            sortBirthdaysByDate();
        }

        notifyDataSetChanged();
    }

    // Sort Birthday array by closest date
    private void sortBirthdaysByDate() {
        for (Birthday b : birthdays) {
            b.setYearOfDate(Birthday.getYearOfNextBirthday(b.getDate()));
        }
        //Sorting
        Collections.sort(birthdays, new Comparator<Birthday>() {
            @Override
            public int compare(Birthday b1, Birthday b2) {
                return b1.getDate().compareTo(b2.getDate());
            }
        });
    }

    // Sort Birthday array by first name
    private void sortBirthdaysByName() {
        Collections.sort(birthdays, new Comparator<Birthday>() {
            @Override
            public int compare(Birthday b1, Birthday b2) {
                return b1.getName().compareTo(b2.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return birthdays.size();
    }

    public ArrayList<Birthday> getData() {
        return birthdays;
    }
}