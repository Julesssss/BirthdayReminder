package website.julianrosser.birthdays;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter
        extends RecyclerView.Adapter
        <RecyclerViewAdapter.ListItemViewHolder> {

    private ArrayList<Birthday> birthdayArrayList;
    private SparseBooleanArray selectedItems;

    String TAG = getClass().getSimpleName();

    RecyclerViewAdapter(ArrayList<Birthday> modelData) {
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }
        birthdayArrayList = modelData;
        selectedItems = new SparseBooleanArray();
    }


    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.birthday_list_view, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        final Birthday birthday = birthdayArrayList.get(position);
        viewHolder.name.setText(birthday.getName());
        viewHolder.date.setText(birthday.getFormattedDaysRemainingString());
        viewHolder.dateDay.setText(birthday.getBirthDay());
        viewHolder.dateMonth.setText(birthday.getBirthMonth());

        // why---> viewHolder.itemView.setActivated(selectedItems.get(position, false));

        // Click listener
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick - " + birthday.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return birthdayArrayList.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView date;
        TextView dateDay;
        TextView dateMonth;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.days_remaining);
            dateDay = (TextView) itemView.findViewById(R.id.dateDay);
            dateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
        }
    }
}