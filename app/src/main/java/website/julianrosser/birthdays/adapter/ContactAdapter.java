package website.julianrosser.birthdays.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.database.DatabaseHelper;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.model.Contact;
import website.julianrosser.birthdays.views.viewholder.ContactViewHolder;

public class ContactAdapter
        extends RecyclerView.Adapter
        <ContactViewHolder> implements ContactViewHolder.ContactCallback {

    private ArrayList<Contact> allContacts;

    public ContactAdapter() {
        this.allContacts = new ArrayList<>();
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.contact_list_view, viewGroup, false);
        return new ContactViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder viewHolder, final int position) {
        // Get reference to birthday
        if (allContacts.size() <= position) {
            return;
        }
        Contact contact = allContacts.get(position);
        viewHolder.setTag(contact);
        viewHolder.setName(contact.getName());
        viewHolder.setDate(contact.getBirthday());
        viewHolder.setImageIcon(contact.isAlreadyAdded());
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

    // Callback from ViewHolder
    @Override
    public void addContact(Contact contact) {
        Birthday contactBirthday = new Birthday(contact.getName(), contact.getBirthday(), true, contact.hasYear());
        DatabaseHelper.saveBirthdayChange(contactBirthday, DatabaseHelper.Update.CREATE);
    }
}