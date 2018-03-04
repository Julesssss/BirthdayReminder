package website.julianrosser.birthdays;

public class Constants {

    /**
     * Filename for storing JSON birthday information file.
     */
    public static final String FILENAME = "birthdays.json";

    public static int DEFAULT_YEAR_OF_BIRTH = 1990;
    public static long DAY_IN_MILLIS = 86400000L; // / 86,400,000 milliseconds in a day
    public static long HOUR_IN_MILLIS = 3600000L; // Amount of milliseconds in an hour

    public static int INTENT_FROM_NOTIFICATION = 30;
    public static int CONTACT_PERMISSION_CODE = 3;
    public static String INTENT_FROM_KEY = "intent_from_key";
    public static String GOOGLE_SIGN_IN_KEY = "1072707269724-3v8qbbu86kmfs252eu44amna8cpqqj9c.apps.googleusercontent.com";

    /**
     * Firebase constants
     */
    public static String TABLE_BIRTHDAYS = "birthdays";

}
