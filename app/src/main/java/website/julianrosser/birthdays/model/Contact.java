package website.julianrosser.birthdays.model;

import java.util.Date;

import website.julianrosser.birthdays.Utils;

public class Contact {

    private String name;
    private Date birthday;
    private boolean alreadyAdded;
    private boolean hasYear;

    public Contact(String name, String birthdayString, boolean alreadyAdded) {
        this.name = name;
        this.birthday = Utils.stringToDate(birthdayString);
        this.alreadyAdded = alreadyAdded;
        this.hasYear = Utils.hasYearOfBirth(birthdayString);
    }

    public String getName() {
        return name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public boolean isAlreadyAdded() {
        return alreadyAdded;
    }

    public void setAlreadyAdded(boolean alreadyAdded) {
        this.alreadyAdded = alreadyAdded;
    }

    public boolean hasYear() {
        return hasYear;
    }
}
