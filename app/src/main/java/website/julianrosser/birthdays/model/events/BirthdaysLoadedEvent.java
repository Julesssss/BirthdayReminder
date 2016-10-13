package website.julianrosser.birthdays.model.events;

import java.util.ArrayList;

import website.julianrosser.birthdays.model.Birthday;

public class BirthdaysLoadedEvent {

    private ArrayList<Birthday> birthdays;

    public BirthdaysLoadedEvent(ArrayList<Birthday> birthday) {
        this.birthdays = birthday;
    }

    public ArrayList<Birthday> getBirthdays() {
        return birthdays;
    }

}
