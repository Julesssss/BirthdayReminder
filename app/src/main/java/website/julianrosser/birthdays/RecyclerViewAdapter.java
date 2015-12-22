package website.julianrosser.birthdays;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RecyclerViewAdapter
        extends RecyclerView.Adapter
        <RecyclerViewAdapter.ListItemViewHolder> {

    private int lastPosition = -1;

    Context appContext;

    // Constructor
    public RecyclerViewAdapter(ArrayList<Birthday> birthdayData, Context c) { //

        appContext = c;

        if (birthdayData == null) {
            MainActivity.birthdaysList = new ArrayList<>();
        } else if (birthdayData.size() == 0) {
            // After Adapter is constructed, start the process of loading data
            MainActivity.getContext().launchLoadBirthdaysTask();
        }
    }


    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.birthday_list_view, viewGroup, false);

        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListItemViewHolder viewHolder, final int position) {
        // Get reference to birthday
        final Birthday birthday = MainActivity.birthdaysList.get(position);

        viewHolder.container.setVisibility(View.VISIBLE);

        // Pass data to the TextViews
        viewHolder.textName.setText(birthday.getName());

        viewHolder.textDaysRemaining.setText(birthday.getFormattedDaysRemainingString());

        viewHolder.textDateDay.setText(birthday.getBirthDay());

        viewHolder.textDateMonth.setText(birthday.getBirthMonth());

        // Set correct icon depending on Alarm on or off
        viewHolder.imageAlarm.setImageDrawable(birthday.getRemindAlarmDrawable());
        viewHolder.imageAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Change birthdays remind bool
                birthday.toggleReminder();

                // Notify user of change. If birthday is today, let user know alarm is set for next year
                if (birthday.getDaysBetween() == 0 && birthday.getRemind()) {
                    Snackbar.make(viewHolder.imageAlarm, "Reminder for " + birthday.getName() +
                            birthday.getReminderString() + " for next year", Snackbar.LENGTH_LONG).show();

                } else {
                    Snackbar.make(viewHolder.imageAlarm, "Reminder for " + birthday.getName() +
                            birthday.getReminderString(), Snackbar.LENGTH_LONG).show();
                }

                // Get correct position, as deleted views may have altered pos int
                int currentPosition = RecyclerListFragment.recyclerView.getChildAdapterPosition(viewHolder.itemView);

                // Callback to MainActivity.
                MainActivity.getContext().alarmToggled(currentPosition);
            }
        });

        // When item is clicked, show context menu for that item
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get actual position, accounting for deletion
                int currentPosition = RecyclerListFragment.recyclerView.getChildAdapterPosition(viewHolder.itemView);

                // Open ItemOption menu for selected birthday
                MainActivity.getContext().showItemOptionsFragment(currentPosition);
            }
        });
    }

    @Override
    public void onViewRecycled(ListItemViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    // Sort Birthday array by closest date
    public static void sortBirthdaysByDate() {

        for (Birthday b : MainActivity.birthdaysList)
            b.setYearOfDate(Birthday.getYearOfNextBirthday(b.getDate()));

        //Sorting
        Collections.sort(MainActivity.birthdaysList, new Comparator<Birthday>() {
            @Override
            public int compare(Birthday b1, Birthday b2) {
                return b1.getDate().compareTo(b2.getDate());
            }
        });
    }

    // Sort Birthday array by first name
    public static void sortBirthdaysByName() {
        Collections.sort(MainActivity.birthdaysList, new Comparator<Birthday>() {
            @Override
            public int compare(Birthday b1, Birthday b2) {
                return b1.getName().compareTo(b2.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return MainActivity.birthdaysList.size();
    }

    /**
     * ViewHolder class to hold view references to be used in recyclerview.
     */
    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {

        // TextView references
        TextView textDateDay;
        TextView textDateMonth;
        TextView textName;
        TextView textDaysRemaining;
        ImageView imageAlarm;
        View container;
        Typeface typeLight;

        public ListItemViewHolder(View itemView) {
            super(itemView);

            // Set up references
            container = itemView.findViewById(R.id.list_container);
            textName = (TextView) itemView.findViewById(R.id.name);
            textDaysRemaining = (TextView) itemView.findViewById(R.id.days_remaining);
            textDateDay = (TextView) itemView.findViewById(R.id.dateDay);
            textDateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
            imageAlarm = (ImageView) itemView.findViewById(R.id.alarmImage);
            typeLight = Typeface.createFromAsset(MainActivity.getAppContext().getResources().getAssets(), "Roboto-Light.ttf");
        }
    }
}