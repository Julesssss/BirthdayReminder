package website.julianrosser.birthdays;

import android.os.Bundle;
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

    public static ArrayList<Birthday> birthdayArrayList;

    String TAG = getClass().getSimpleName();

    // constructor
    public RecyclerViewAdapter(ArrayList<Birthday> birthdayData) { // todo - needed???????????????????????
        if (birthdayData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        birthdayArrayList = birthdayData;
    }


    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.birthday_list_view, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, final int position) {
        final Birthday birthday = birthdayArrayList.get(position);
        viewHolder.textName.setText(birthday.getName());
        viewHolder.textDaysRemaining.setText(birthday.getFormattedDaysRemainingString());
        viewHolder.textDateDay.setText(birthday.getBirthDay());
        viewHolder.textDateMonth.setText(birthday.getBirthMonth());

        // why---> viewHolder.itemView.setActivated(selectedItems.get(position, false));

        // Click listener
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - " + birthday.getName());

                // We can get the fragment manager
                AddEditBirthdayFragment dialog = new AddEditBirthdayFragment();

                // Create bundle for MODE_EDIT detection and to use current date, month value.
                Bundle bundle = new Bundle();
                bundle.putInt(AddEditBirthdayFragment.MODE_KEY, AddEditBirthdayFragment.MODE_EDIT);
                bundle.putInt(AddEditBirthdayFragment.DATE_KEY, birthday.getDate().getDate());
                bundle.putInt(AddEditBirthdayFragment.MONTH_KEY, birthday.getDate().getMonth());
                bundle.putInt(AddEditBirthdayFragment.POSITION_KEY, position);
                bundle.putString(AddEditBirthdayFragment.NAME_KEY, birthday.getName());
                dialog.setArguments(bundle);

                dialog.show(MainActivity.getContext().getSupportFragmentManager(), "AddEditBirthdayFragment");
            }
        });
    }

    @Override
    public int getItemCount() {
        return birthdayArrayList.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textDaysRemaining;
        TextView textDateDay;
        TextView textDateMonth;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.name);
            textDaysRemaining = (TextView) itemView.findViewById(R.id.days_remaining);
            textDateDay = (TextView) itemView.findViewById(R.id.dateDay);
            textDateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
        }
    }


}