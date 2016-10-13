package website.julianrosser.birthdays.viewholder;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.Utils;
import website.julianrosser.birthdays.activities.BirthdayListActivity;
import website.julianrosser.birthdays.model.Birthday;
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

    public ContactViewHolder(View itemView) {
        super(itemView);

        // Set up references
        container = itemView;
        textName = (TextView) itemView.findViewById(R.id.name);
        textDateDay = (TextView) itemView.findViewById(R.id.dateDay);
        textDateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
        imageAdd = (ImageView) itemView.findViewById(R.id.addImage);
        imageAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Contact contact = (Contact) v.getTag();

        Date birthdate = Utils.stringToDate(contact.getBirthday());
        if ((birthdate.getYear() + 1900) < 1902) {
            birthdate.setYear(1990);
        } else {
            birthdate.setYear(birthdate.getYear() + 1900);
        }
        Birthday birthday = new Birthday(contact.getName(), birthdate, true, false);

        if (BirthdayListActivity.isContactAlreadyAdded(birthday)) {
            Snackbar.make(container, contact.getName() + " " + container.getContext().getString(R.string.contact_already_added), Snackbar.LENGTH_SHORT).show();
        } else {
            BirthdayListActivity.birthdaysList.add(birthday);
            Snackbar.make(container, contact.getName() + " " + container.getContext().getString(R.string.added), Snackbar.LENGTH_SHORT).show();
            setImageIcon(birthday.getName());
        }
    }

    public void setName(String name) {
        textName.setText(name);
    }

    public void setTag(Contact contact) {
        container.setTag(contact);
        imageAdd.setTag(contact);
    }

    public void setDate(String birthday) {
        Date birthdate = Utils.stringToDate(birthday);
        textDateDay.setText("" + getBirthDay(birthdate));
        textDateMonth.setText("" + getBirthMonth(birthdate));
    }

    private String getBirthMonth(Date date) {
        return (String) DateFormat.format("MMM", date);
    }

    private String getBirthDay(Date date) {
        return "" + date.getDate() + Utils.getDateSuffix(date.getDate());
    }

    public void setImageIcon(String name) {
        ArrayList<Birthday> birthdaysList = BirthdayListActivity.birthdaysList;

        for (Birthday birthday : birthdaysList) {
            if (birthday.getName().equals(name)) {
                imageAdd.setImageDrawable(imageAdd.getContext().getResources().getDrawable(R.drawable.ic_done_white_24dp));
                return;
            }
        }
        imageAdd.setImageDrawable(imageAdd.getContext().getResources().getDrawable(R.drawable.ic_add_circle_outline_white_24dp));
    }
}