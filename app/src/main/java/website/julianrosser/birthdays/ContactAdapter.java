package website.julianrosser.birthdays;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactAdapter
        extends RecyclerView.Adapter
        <ContactAdapter.ContactViewHolder> {

    ArrayList<Contact> allContacts;

    public ContactAdapter() {
            allContacts = new ArrayList<>();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.contact_list_view, viewGroup, false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder viewHolder, final int position) {
        // Get reference to birthday
        if (allContacts.size() <= position) {
            return;
        }

        final Contact contact = allContacts.get(position);

        viewHolder.container.setVisibility(View.VISIBLE);

        // Pass data to the TextViews
        viewHolder.textName.setText(contact.getName() + " - " + contact.getBirthday());
//        viewHolder.textDateDay.setText(contact.);
//        viewHolder.textDateMonth.setText(contact.getBirthMonth());
    }

    @Override
    public void onViewRecycled(ContactViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return allContacts.size();
    }

    public void setData(ArrayList<Contact> contactsList) {
        allContacts = contactsList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class to hold view references to be used in recyclerview.
     */
    public final class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // TextView references
        TextView textDateDay;
        TextView textDateMonth;
        TextView textName;
        ImageView imageAdd;
        View container;
        Typeface typeLight;

        public ContactViewHolder(View itemView) {
            super(itemView);

            // Set up references
            container = itemView.findViewById(R.id.list_container);
            textName = (TextView) itemView.findViewById(R.id.name);
            textDateDay = (TextView) itemView.findViewById(R.id.dateDay);
            textDateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
            imageAdd = (ImageView) itemView.findViewById(R.id.addImage);
            imageAdd.setOnClickListener(this);
            typeLight = Typeface.createFromAsset(MainActivity.getAppContext().getResources().getAssets(), "Roboto-Light.ttf");
        }

        @Override
        public void onClick(View v) {
//            v.getTag();

        }
    }
}