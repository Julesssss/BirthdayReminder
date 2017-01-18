package website.julianrosser.birthdays.model;

import java.util.Date;

public class Contact {

    private String name;
    private Date birthday;
    private boolean alreadyAdded;

    public Contact(String name, Date birthday, boolean alreadyAdded) {
        this.name = name;
        this.birthday = birthday;
        this.alreadyAdded = alreadyAdded;
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
}
