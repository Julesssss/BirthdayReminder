package website.julianrosser.birthdays.model.events;

public class BirthdayItemClickEvent {

    private final int currentPosition;

    public BirthdayItemClickEvent(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
