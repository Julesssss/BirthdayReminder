package website.julianrosser.birthdays;

import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RecyclerViewAdapter
        extends RecyclerView.Adapter
        <RecyclerViewAdapter.ListItemViewHolder> {

    // Constructor
    public RecyclerViewAdapter(ArrayList<Birthday> birthdayData) { //

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

        // Should display age?
        if (birthday.shouldIncludeYear()) {
            viewHolder.textAge.setVisibility(View.VISIBLE);
        } else {
            viewHolder.textAge.setVisibility(View.GONE);
        }

        if (birthday.getYear() != 0) {
            viewHolder.textAge.setText(birthday.getAge());
        } else {
            // don't show age field
            viewHolder.textAge.setText("NA");
        }

        // Set correct icon depending on Alarm on or off
        viewHolder.imageAlarm.setImageDrawable(birthday.getRemindAlarmDrawable());
        viewHolder.imageAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Change birthdays remind bool
                birthday.toggleReminder();

                // Get correct position, as deleted views may have altered pos int
                int currentPosition = RecyclerListFragment.recyclerView.getChildAdapterPosition(viewHolder.itemView);

                MainActivity.mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Toggle Alarm ICON")
                        .build());

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

    // use this method to find out whether edit will change order of birthdays
    public static boolean willChangeDateOrder(Birthday b) {
        ArrayList<Birthday> originalOrder = MainActivity.birthdaysList;

        int originalPos = originalOrder.indexOf(b);

        //Sorting
        Collections.sort(originalOrder, new Comparator<Birthday>() {
            @Override
            public int compare(Birthday b1, Birthday b2) {
                return b1.getDate().compareTo(b2.getDate());
            }
        });

        return originalPos != originalOrder.indexOf(b);
    }

    // use this method to find out whether edit will change order of birthdays
    public static boolean willChangeNameOrder(Birthday b) {
        ArrayList<Birthday> originalOrder = MainActivity.birthdaysList;

        int originalPos = originalOrder.indexOf(b);

        //Sorting
        Collections.sort(originalOrder, new Comparator<Birthday>() {
            @Override
            public int compare(Birthday b1, Birthday b2) {
                return b1.getName().compareTo(b2.getName());
            }
        });

        return originalPos != originalOrder.indexOf(b);
    }

    // Sort Birthday array by closest date
    public static void sortBirthdaysByDate() {


        for (Birthday b : MainActivity.birthdaysList) {
            b.setYearOfDate(Birthday.getYearOfNextBirthday(b.getDate()));
        }

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
        TextView textAge;
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
            textAge = (TextView) itemView.findViewById(R.id.textViewAge);
            textDateDay = (TextView) itemView.findViewById(R.id.dateDay);
            textDateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
            imageAlarm = (ImageView) itemView.findViewById(R.id.alarmImage);
            typeLight = Typeface.createFromAsset(MainActivity.getAppContext().getResources().getAssets(), "Roboto-Light.ttf");
        }
    }
}