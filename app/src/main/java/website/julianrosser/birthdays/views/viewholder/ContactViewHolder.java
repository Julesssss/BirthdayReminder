package website.julianrosser.birthdays.views.viewholder;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.Utils;
import website.julianrosser.birthdays.model.Contact;

/**
 * ViewHolder class to hold view references to be used in recyclerview.
 */
public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // TextView references
    private TextView textDateDay;
    private TextView textDateMonth;
    private TextView textName;
    private ImageView imageAdd;
    private View container;
    private ContactCallback contactCallback;

    public interface ContactCallback {
        void addContact(Contact contact);
    }

    public ContactViewHolder(View itemView, ContactCallback contactCallback) {
        super(itemView);

        // Set up references
        container = itemView;
        textName = (TextView) itemView.findViewById(R.id.name);
        textDateDay = (TextView) itemView.findViewById(R.id.dateDay);
        textDateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
        imageAdd = (ImageView) itemView.findViewById(R.id.addImage);
        imageAdd.setOnClickListener(this);
        this.contactCallback = contactCallback;
    }

    public void setName(String name) {
        textName.setText(name);
    }

    public void setTag(Contact contact) {
        container.setTag(contact);
        imageAdd.setTag(contact);
    }

    public void setDate(Date birthdate) {
        textDateDay.setText("" + getBirthDay(birthdate));
        textDateMonth.setText("" + getBirthMonth(birthdate));
    }

    private String getBirthMonth(Date date) {
        return (String) DateFormat.format("MMM", date);
    }

    private String getBirthDay(Date date) {
        return "" + date.getDate() + Utils.getDateSuffix(date.getDate());
    }

    public void setImageIcon(boolean alreadyAdded) {
        if (alreadyAdded) {
            imageAdd.setImageDrawable(imageAdd.getContext().getResources().getDrawable(R.drawable.ic_done_white_24dp));
        } else {
            imageAdd.setImageDrawable(imageAdd.getContext().getResources().getDrawable(R.drawable.ic_add_circle_outline_white_24dp));
        }
    }

    @Override
    public void onClick(View v) {
        Contact contact = (Contact) v.getTag();
        int id = v.getId();
        if (id == R.id.addImage) {
           if (contact.isAlreadyAdded()) {
               Snackbar.make(v, contact.getName() + " " + v.getContext().getString(R.string.contact_already_added), Snackbar.LENGTH_SHORT).show();
           } else {
               Snackbar.make(v, contact.getName() + " " + v.getContext().getString(R.string.added), Snackbar.LENGTH_SHORT).show();
               setImageIcon(true);
               contact.setAlreadyAdded(true);

               contactCallback.addContact(contact);
           }
        }
    }
}