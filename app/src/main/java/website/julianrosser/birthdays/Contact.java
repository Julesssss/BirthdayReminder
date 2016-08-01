package website.julianrosser.birthdays;

public class Contact {

    String name;
    String birthday;

    public Contact(String name, String birthday) {
        this.name = name;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }
}
