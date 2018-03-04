package website.julianrosser.birthdays.model.events;

import website.julianrosser.birthdays.model.Birthday;

public class BirthdayItemClickEvent {

    private final Birthday birthday;

    public BirthdayItemClickEvent(Birthday birthday) {
        this.birthday = birthday;
    }

    public Birthday getBirthday() {
        return birthday;
    }
}
