package website.julianrosser.birthdays;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter
        extends RecyclerView.Adapter
        <RecyclerViewAdapter.ListItemViewHolder> {

    String TAG = getClass().getSimpleName();

    // Constructor
    public RecyclerViewAdapter(ArrayList<Birthday> birthdayData) { //
        if (birthdayData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }

        // After Adapter is contructed, start the process of loading data
        MainActivity.getContext().launchLoadBirthdaysTask();

        Log.d(TAG, "newAdapter");

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

        // Pass data to the TextViews
        viewHolder.textName.setText(birthday.getName());

        viewHolder.textDaysRemaining.setText(birthday.getFormattedDaysRemainingString());

        viewHolder.textDateDay.setText(birthday.getBirthDay());

        viewHolder.textDateMonth.setText(birthday.getBirthMonth());

        // When item is clicked, show context menu for that item
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open ItemOption menu for selected birthday
                MainActivity.getContext().showItemOptionsFragment(position);
            }
        });
    }

    @Override
    public void onViewRecycled(ListItemViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
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
        Typeface typeLight;

        public ListItemViewHolder(View itemView) {
            super(itemView);

            // Set up references
            textName = (TextView) itemView.findViewById(R.id.name);
            textDaysRemaining = (TextView) itemView.findViewById(R.id.days_remaining);
            textDateDay = (TextView) itemView.findViewById(R.id.dateDay);
            textDateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
            typeLight = Typeface.createFromAsset(MainActivity.getContext().getResources().getAssets(), "Roboto-Light.ttf");
        }
    }
}