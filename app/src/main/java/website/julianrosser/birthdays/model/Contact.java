package website.julianrosser.birthdays.model;

import java.util.Date;

public class Contact {

    private String name;
    private Date birthday;

    public Contact(String name, Date birthday) {
        this.name = name;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public Date getBirthday() {
        return birthday;
    }
}
