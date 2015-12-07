package website.julianrosser.birthdays;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
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

    static int contextListPos = -1;

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
    public void onBindViewHolder(final ListItemViewHolder viewHolder, final int position) {
        final Birthday birthday = birthdayArrayList.get(position);
        viewHolder.textName.setText(birthday.getName());
        viewHolder.textDaysRemaining.setText(birthday.getFormattedDaysRemainingString());
        viewHolder.textDateDay.setText(birthday.getBirthDay());
        viewHolder.textDateMonth.setText(birthday.getBirthMonth());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Pass position to static reference
                contextListPos = position;

                TextView tv = (TextView) v.findViewById(R.id.name);

                Log.d(TAG, "tv of view:: " + tv.getText().toString());

                MainActivity.getContext().openContextMenu(v);
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
        return birthdayArrayList.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
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
            itemView.setOnCreateContextMenuListener(this);
            itemView.setLongClickable(false);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            TextView tv = (TextView) v.findViewById(R.id.name);
            menu.setHeaderTitle(tv.getText().toString());

            menu.add(0, v.getId(), 0, "Edit"); //groupId, itemId, order, title
            menu.add(0, v.getId(), 1, "Delete"); //groupId, itemId, order, title
        }

    }
}