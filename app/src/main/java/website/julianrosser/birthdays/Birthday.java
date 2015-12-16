package website.julianrosser.birthdays;

import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("deprecation")
public class Birthday {

    // Logging string
    private final String LOG_TAG = getClass().getSimpleName();

    // JSON keys
    private static final String JSON_NAME = "name";
    private static final String JSON_DATE = "date";
    private static final String JSON_REMIND = "remind";

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "dd.MM.yyyy");

    public static final int DAY_IN_MILLIS = 86400000;

    // References to data
    private String name;
    private Date date;
    private boolean remind;

    // logging tag
    private String TAG = getClass().getSimpleName();

    /**
     * Constructor for creating new birthday.
     */
    public Birthday(String name, Date dateOfBirthday, boolean notifyUserOfBirthday) {

        this.name = name;
        this.remind = notifyUserOfBirthday;
        this.date = dateOfBirthday;
    }

    /**
     * For updating Birthday information without creating new
     */
    public void edit(String editName, Date editDate, boolean editRemind) {

        this.name = editName;
        this.remind = editRemind;
        this.date = editDate;
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

    public void setYearOfDate(int year) { this.date.setYear(year);}

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

        if (i > 365 || i < 0) {
            Log.d(getClass().getSimpleName(), "DATE OUT OF BOUNDS: " + i);
        }

        if (i == -1) {
            return "Yesterday";
        } else if (i == 0) {
            return "Today";
        } else if (i == 1) {
            return "Tomorrow";
        } else if (i > 1 && i <= 6) {
            return Birthday.getWeekdayName(getDate());
        } else if (i > 99) {
            return String.valueOf(i) + " Days";
        } else if (i > 9) {
            return " " + String.valueOf(i) + " Days";
        } else {
            return "  " + String.valueOf(i) + " Days";
        }
    }


    // Return a formatted int of exact amount of days until the next birthday
    public int getDaysBetween() {

        Date dateBirthday = getDate();
        String birthday = String.valueOf(dateBirthday.getDate()) + "."
                + String.valueOf(dateBirthday.getMonth() + 1) + "."
                + String.valueOf(getYearOfNextBirthday(dateBirthday));

        Date dateNow = new Date();
        String today = String.valueOf((dateNow.getDate()) + "."
                + String.valueOf(dateNow.getMonth() + 1) + "."
                + String.valueOf(dateNow.getYear() + 1900));

        // use below method to calculate days until next birthday occurance
        int daysBetween = (int) getDayCount(today, birthday);

        // If exactly a year until next, we know the birthday is today
        if (daysBetween == 366) {
            daysBetween = 0;
        }
        return daysBetween;
    }

    /**
     * Helper method, convert dates to millis and use to calculate full days between them
     */
    public static long getDayCount(String start, String end) {

        long dayCount = 0;
        try {
            Date dateStart = simpleDateFormat.parse(start);
            Date dateEnd = simpleDateFormat.parse(end);
            dayCount = Math.round((dateEnd.getTime() - dateStart.getTime())
                    / (double) DAY_IN_MILLIS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayCount;
    }

    /**
     * Helper method which returns the year of next birthday occurrence of passed date
     */
    public static int getYearOfNextBirthday(Date date) { // TODO - Prevent serious out of bound infinite loops

        int year = 2014;
        date.setYear(year);

        boolean nowAhead = dateInFuture(date);

        // While date instance is in the past, increase by a year and check again
        while (nowAhead) {

            year += 1;
            date.setYear(year);

            nowAhead = dateInFuture(date);
        }

        return year;
    }

    /**
     * This method returns false when the passed date is in the future or already passed.
     */
    private static boolean dateInFuture(Date queryDate) {
        // Use calender reference to get correct date
        Calendar nowCal = Calendar.getInstance();

        // Set date to desired time
        Date now = new Date();
        now.setYear(nowCal.get(Calendar.YEAR));
        now.setMonth(nowCal.get(Calendar.MONTH));
        now.setDate(nowCal.get(Calendar.DATE));

        // Get dates in form of milliseconds
        long millisNow = now.getTime();
        long millisBDAY = queryDate.getTime();

        // use this to ensure a birthday
        return millisNow > millisBDAY + DAY_IN_MILLIS;
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

    /** Returns a formatted day string built for notification display. */
    public static String getFormattedStringDay(Birthday b) {

        String dayFormatted = "";

        int daysFromNotiUntilDay = 0; // todo - delay from noti to reminder
        // Log.i("Birthday-FormatDay", "Days till: " + daysFromNotiUntilDay);

        if (daysFromNotiUntilDay == 0) {
            dayFormatted += "today";
        } else if (daysFromNotiUntilDay == 1) {
            dayFormatted += "tomorrow";
        } else {
            dayFormatted += "this " + (getWeekdayName(b.getDate()));
        }
        dayFormatted += "!";

        return dayFormatted;
    }

    /** Used by aabove method and RecyclerAdapter to name day if within a week */
    public static String getWeekdayName(Date date) {

        // TODO - modifier --> How far ahead is the notification?

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());

        switch (c.get(Calendar.DAY_OF_WEEK)) {

            case 1:
                return "Saturday";
            case 2:
                return "Sunday";
            case 3:
                return "Monday";
            case 4:
                return "Tuesday";
            case 5:
                return "Wednesday";
            case 6:
                return "Thursday";
            case 7:
                return "Friday";
            default:
                return "soon";
        }
    }
}