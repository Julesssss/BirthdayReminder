package website.julianrosser.birthdays;

import android.graphics.Typeface;
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

public class ContactAdapter
        extends RecyclerView.Adapter
        <ContactAdapter.ListItemViewHolder> {

    ArrayList<Contact> allContacts;

    public ContactAdapter(ArrayList<Contact> contacts) {
        allContacts = contacts;
        if (allContacts == null) {
            allContacts = new ArrayList<>();
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
        if (allContacts.size() <= position) {
            return;
        }

        final Contact contact = allContacts.get(position);

        viewHolder.container.setVisibility(View.VISIBLE);

        // Pass data to the TextViews
        viewHolder.textName.setText(contact.getName());
        viewHolder.textDaysRemaining.setText(contact.getBirthday());
//        viewHolder.textDateDay.setText(contact.getBirthDay());
//        viewHolder.textDateMonth.setText(contact.getBirthMonth());
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

    public void setData(ArrayList<Contact> contactsList) {
        allContacts = contactsList;
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