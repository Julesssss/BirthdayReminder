package website.julianrosser.birthdays.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import website.julianrosser.birthdays.activities.BirthdayListActivity;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.model.tasks.LoadBirthdaysTask;
import website.julianrosser.birthdays.views.viewholder.BirthdayViewHolder;

public class BirthdayViewAdapter extends RecyclerView.Adapter<BirthdayViewHolder> {

    // Constructor
    public BirthdayViewAdapter(ArrayList<Birthday> birthdayData) { //

        if (birthdayData == null) {
            BirthdayListActivity.birthdaysList = new ArrayList<>();
        } else if (birthdayData.size() == 0) {
            // After Adapter is constructed, start the process of loading data
            LoadBirthdaysTask loadBirthdaysTask = new LoadBirthdaysTask();
            loadBirthdaysTask.execute();
        }
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
        final Birthday birthday = BirthdayListActivity.birthdaysList.get(position);
        viewHolder.setTag(birthday);
        viewHolder.showView();
        // Pass data to the TextViews
        viewHolder.setText(birthday.getName());
        viewHolder.setDaysRemaining(birthday.getFormattedDaysRemainingString());
        viewHolder.setBirthday(birthday.getBirthDay(), birthday.getBirthMonth());
        viewHolder.displayAgeIfNeeded(birthday.shouldIncludeYear(), birthday.getYear(), birthday.getAge());
        viewHolder.setImageIcon(birthday.getRemindAlarmDrawable());
        viewHolder.setImageClickListener();

    }

    @Override
    public void onViewRecycled(BirthdayViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    // use this method to find out whether edit will change order of birthdays
    public static boolean willChangeDateOrder(Birthday b) {
        ArrayList<Birthday> originalOrder = BirthdayListActivity.birthdaysList;

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
        ArrayList<Birthday> originalOrder = BirthdayListActivity.birthdaysList;

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


        for (Birthday b : BirthdayListActivity.birthdaysList) {
            b.setYearOfDate(Birthday.getYearOfNextBirthday(b.getDate()));
        }

        //Sorting
        Collections.sort(BirthdayListActivity.birthdaysList, new Comparator<Birthday>() {
            @Override
            public int compare(Birthday b1, Birthday b2) {
                return b1.getDate().compareTo(b2.getDate());
            }
        });
    }

    // Sort Birthday array by first name
    public static void sortBirthdaysByName() {
        Collections.sort(BirthdayListActivity.birthdaysList, new Comparator<Birthday>() {
            @Override
            public int compare(Birthday b1, Birthday b2) {
                return b1.getName().compareTo(b2.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return BirthdayListActivity.birthdaysList.size();
    }

}