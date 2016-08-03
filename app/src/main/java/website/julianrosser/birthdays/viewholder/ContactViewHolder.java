package website.julianrosser.birthdays.viewholder;

import android.graphics.Typeface;
import android.net.ParseException;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.activities.MainActivity;
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
    private Typeface typeLight;

    public ContactViewHolder(View itemView) {
        super(itemView);

        // Set up references
        container = itemView;
        textName = (TextView) itemView.findViewById(R.id.name);
        textDateDay = (TextView) itemView.findViewById(R.id.dateDay);
        textDateMonth = (TextView) itemView.findViewById(R.id.dateMonth);
        imageAdd = (ImageView) itemView.findViewById(R.id.addImage);
        imageAdd.setOnClickListener(this);
        typeLight = Typeface.createFromAsset(MainActivity.getAppContext().getResources().getAssets(), "Roboto-Light.ttf");
    }

    @Override
    public void onClick(View v) {
        Contact contact = (Contact) v.getTag();
        Toast.makeText(v.getContext(), "ADDING " + contact.getName() + " TO BIRTHDAYS...", Toast.LENGTH_SHORT).show();
    }

    public void setName(String name) {
        textName.setText(name);
    }

    public void setTag(Contact contact) {
        container.setTag(contact);
        imageAdd.setTag(contact);
    }

    public void setDate(String birthday) {
        stringToDate(birthday);
    }

    private String stringToDate(String birthdayString) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(birthdayString);
            System.out.println(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        textDateDay.setText("" + getBirthDay(date));
        textDateMonth.setText("" + getBirthMonth(date));

        return null;
    }

    public String getBirthMonth(Date date) {
        return (String) DateFormat.format("MMM", date);
    }

    public String getBirthDay(Date date) {
        return "" + date.getDate() + getDateSuffix(date);
    }

    // todo - refactor
    private String getDateSuffix(Date date) {
        // d stands for date of birthday
        int d = date.getDate();

        if (d == 11 || d == 12 || d == 13) {
            return "th";
        } else if (d % 10 == 1) {
            return "st";
        } else if (d % 10 == 2) {
            return "nd";
        } else if (d % 10 == 3) {
            return "rd";
        } else {
            return "th";
        }
    }
}