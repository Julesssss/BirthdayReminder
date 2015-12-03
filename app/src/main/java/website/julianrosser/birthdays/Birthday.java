package website.julianrosser.birthdays;

import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

// Ignore Date method deprecation, todo : check eventually.
@SuppressWarnings({"deprecation", "unused"})

public class Birthday {

    // Logging string
    private final String LOG_TAG = "Birthday.java";

    // JSON keys
    private static final String JSON_NAME = "name";
    private static final String JSON_DATE = "date";
    private static final String JSON_REMIND = "remind";

    // References to data
    private String name;
    private Date date;
    private boolean remind;

    /**
     * Constructor for creating new birthday.
     * Todo : Rename parameters in constructor, after ensuring this wont break references!
     */
    public Birthday(String name, Date date, boolean notifyUserOfBirthday) {

        this.name = name;
        this.date = date;
        this.remind = notifyUserOfBirthday;
    }

    /**
     * For updating Birthday information without creating new
     */
    public void edit(String editName, Date editDate, boolean editRemind) {

        this.name = editName;
        this.date = editDate;
        this.remind = editRemind;
    }

    /**
     * Constructor for parsing JSON data to Birthday objects. For each Birthday variable, check JSON data and update if found.
     */
    public Birthday(JSONObject json) throws JSONException {

        // Find String name of person attached to this birthday.
        if (json.has(JSON_NAME)) {
            name = json.getString(JSON_NAME);
        }

        // Check whether user wants to be reminded for this birthday.
        if (json.has(JSON_REMIND)) {
            remind = json.getBoolean(JSON_REMIND);
        } else {
            // Default to true if not found, log message.
            remind = true;
            Log.e(LOG_TAG, "Birthday constructor - ERROR_11: Reminder boolean not found in JSON data."); // todo - needed???
        }

        // Date of birthday in millis.
        if (json.has(JSON_DATE)) {
            date = new Date();
            date.setTime(json.getLong(JSON_DATE));
        }
    }

    /**
     * Convert current Birthday to JSON format and return.
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_NAME, this.getName());
        json.put(JSON_DATE, this.getDate().getTime());
        json.put(JSON_REMIND, this.getRemind());
        return json;
    }

    /**
     * Getters & setters for variables
     */

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public boolean getRemind() {
        return remind;
    }

    public void setRemind(boolean newRemindPref) {
        remind = newRemindPref;
    }

    public String getBirthMonth() {
        return (String) DateFormat.format("MMM", date);
    }

    public String getBirthDay() {
        return "" + date.getDate() + getDateSuffix();
    }

    public String getFormattedDaysRemainingString() {
        int i = getDaysBetween();

        if (i == -1) {
            return "Yesterday";
        } else if (i == 0) {
            return "Today";
        } else if (i == 1) {
            return "Tomorrow";
        } else if (i > 99) {
            return String.valueOf(i) + " Days";
        } else if (i > 9) {
            return " " + String.valueOf(i) + " Days";
        } else {
            return "  " + String.valueOf(i) + " Days";
        }
    }

    private int getDaysBetween() {
        Date now = new Date();
        now.setYear(2015);
        return (int) ((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
    }

    private String getDateSuffix() {
        // d stands for date of birthday
        int d = this.getDate().getDate();

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
