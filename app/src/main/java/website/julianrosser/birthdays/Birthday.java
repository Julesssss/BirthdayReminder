package website.julianrosser.birthdays;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Ignore Date method deprecation, todo : check eventually.
@SuppressWarnings({"deprecation","unused"})

public class Birthday {

    // Context, logging and daysTillBirthday references
    Context mAlarmContext;
    String TAG = "Birthday.java";
    int daysUntil; // todo old note: Have to update this every time

    // JSON keys
    private static final String JSON_NAME = "name";
    private static final String JSON_DATE = "date";
    private static final String JSON_REMIND = "remind";
    private static final String JSON_DAYS_REMIND = "days_remind";

    // References to data todo: rename references
    String name;
    Date date;
    boolean remind;
    int daysBeforeNotification;

    /**
     * Constructor for creating new birthday.
     * Todo : Rename parameters in constructor, after ensuring this wont break references!
     *
     * @param n : name
     * @param d : date
     * @param notifyUserOfBirthday : remind
     * @param mContext : app context
     * @param remindDays : days left untill reminder
     */
    public Birthday(String n, Date d, boolean notifyUserOfBirthday, Context mContext, int remindDays) {

        name = n;
        date = d;
        daysUntil = getDaysToBirthday(d);

        if (daysUntil > 365) {
            int iYear = d.getYear();
            d.setYear(iYear - 1);
            daysUntil = getDaysToBirthday(d);
        }

        if (daysUntil < 0) {
            int iYear = d.getYear();
            d.setYear(iYear + 1);
            daysUntil = getDaysToBirthday(d);
        }

        remind = notifyUserOfBirthday;
        daysBeforeNotification = remindDays;
        mAlarmContext = mContext;
    }

    /**
     * I think this is for replacing deleted birthday with new, todo: find out.
     * @param editName : name
     * @param editDate : date
     * @param editRemind : remind user boolean
     * @param editContext : context
     * @param remindDays : days till reminder
     */
    public void edit(String editName, Date editDate, boolean editRemind, Context editContext, int remindDays) {

        name = editName;
        date = editDate;
        daysUntil = getDaysToBirthday(editDate);

        if (daysUntil > 365) {
            int iYear = editDate.getYear();
            editDate.setYear(iYear - 1);
            daysUntil = getDaysToBirthday(editDate);
        }
        if (daysUntil < 0) {
            int iYear = editDate.getYear();
            editDate.setYear(iYear + 1);
            daysUntil = getDaysToBirthday(editDate);
        }

        remind = editRemind;
        mAlarmContext = editContext;
        daysBeforeNotification = remindDays;
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
            Log.e(TAG, "Birthday constructor - ERROR_11: Reminder boolean not found in JSON data.");
        }

        // Find days till reminder integer
        if (json.has(JSON_DAYS_REMIND)) {
            daysBeforeNotification = json.getInt(JSON_DAYS_REMIND);
        }

        // Date of birthday in millis.
        if (json.has(JSON_DATE)) {
            date = new Date();
            date.setTime(json.getLong(JSON_DATE));

            // Update days untill variable
            daysUntil = getDaysToBirthday(date);

            // If over a year away, reset to this year
            if (daysUntil > 365) {

                // Get current year and advance by 1.
                int iYear = date.getYear();
                date.setYear(iYear - 1);

                // Update countdown with correct days
                daysUntil = getDaysToBirthday(date);


            } else if (daysUntil < 0) {

                while (daysUntil < 0) {

                    // Get current year, advance by 1.
                    int iiYear = date.getYear();
                    date.setYear(iiYear + 1);

                    // Update countdown with HOPEFULLY correct days
                    daysUntil = getDaysToBirthday(date);

                    // TODO - check for infinate loop, add randomness to avoid?
                }
            }

            /* Old code, unsure above works before deleting.
            // Looped to ensure no more than 4 years have passed.
            for (int i = 0; i < 5; i++) {
                // If birthday has passed, advance date by a year.
                if (daysUntil < 0) {
                    // Get current year, advance by 1.
                    int iiYear = date.getYear();
                    date.setYear(iiYear + 1);
                    daysUntil = getDaysToBirthday(date);
                }} */
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
        json.put(JSON_DAYS_REMIND, this.getDaysNoti());
        return json;
    }

    /**
     * Getters & setters for variables
     */

    public int getDaysNoti() {
        return daysBeforeNotification;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public boolean getRemind() {
        return remind;
    }

    public void setRemind(boolean bool) {
        remind = bool;
    }

    public int getDaysUntil() {
        return daysUntil;
    }

    /**
     * Return correct amount of days until birthday. Todo: check while loop works correctly.
     */
    private int getDaysToBirthday(Date birthdayDate) {

        // Date variable
        Date currentDate = new Date();

        // Format dates
        String dateBirthday = getFormattedDateString(birthdayDate.getYear());
        String dateToday = getFormattedDateString(currentDate.getYear() + 1900);

        // Pass date strings to getDayCount() method.
        int returnedDayCount = (int) getDayCount(dateToday, dateBirthday);


        int yearOfBirth = birthdayDate.getYear();

        // Check that days is inside desired range.
        while (returnedDayCount < 0) {

            yearOfBirth += 1;

            // Update date format
            dateBirthday = getFormattedDateString(yearOfBirth);

            // Recalculate
            returnedDayCount = (int) getDayCount(dateToday, dateBirthday);
        }

        // Return final day count as an int.
        return returnedDayCount;
    }

    /**
     * Helper method which returns the amount of full days remaining between two dates.
     */
    public static long getDayCount(String start, String end) {

        // Date format reference
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        // Local reference for storing difference
        long difference = 0;

        // Attempt to parse dates, and calculate the difference between dates.
        try {

            // Calculate difference between dates
            difference = (simpleDateFormat.parse(end).getTime() - simpleDateFormat.parse(start).getTime());

            // Round difference to nearest full day
            difference =  Math.round(difference / (double) 86400000);

        } catch (Exception e) {
            // Log any exception
            e.printStackTrace();
            Log.i("Birthday.java", "Exception in getDayCount(), probably while parsing date.");
        }

        return difference;
    }

    /**
     * Helper method for formatting dat strings
     */
    public String getFormattedDateString(int yearOfBirth) {

        return String.valueOf(date.getDate()) + "."
                + String.valueOf(date.getMonth() + 1) + "." // todo: why '+1' ??
                + String.valueOf(yearOfBirth);
    }

    /**
     * Return context for some unknown method. Todo: find this method in old project.
     */
    public Context getmAlarmContext() {
        return mAlarmContext;
    }

    /**
     * Set context for some unknown reason. Todo: find method and reason.
     */
    public void setmAlarmContext(Context mContext) {
        mAlarmContext = mContext;

    }

}
