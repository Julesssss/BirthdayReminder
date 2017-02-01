package website.julianrosser.birthdays.model;

import java.util.Date;

@SuppressWarnings("deprecation")
public class FirebaseBirthday {

    public String name;
    public int dateDay;
    public int dateMonth;
    public int dateYear;
    public boolean remind;
    public boolean showYear;
    public String uID;

    // Required empty constructor
    public FirebaseBirthday() {

    }

    /**
     * Constructor for creating new birthday.
     */
    public FirebaseBirthday(String name, Date dateOfBirthday, int year, boolean notifyUserOfBirthday, boolean includeYear, String uID) {

        this.name = name;
        this.remind = notifyUserOfBirthday;
        this.showYear = includeYear;
        dateDay = dateOfBirthday.getDate();
        dateMonth = dateOfBirthday.getMonth();
        dateYear = year;
        this.uID = uID;
    }

    public static FirebaseBirthday convertToFirebaseBirthday(Birthday birthday) {
        return new FirebaseBirthday(birthday.getName(), birthday.getDate(), birthday.getYear(),
                birthday.getRemind(), birthday.shouldIncludeYear(), birthday.getUID());
    }

}