package website.julianrosser.birthdays.model.events;

public class BirthdayAlarmToggleEvent {

    private final int currentPosition;

    public BirthdayAlarmToggleEvent(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }
}
