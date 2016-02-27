package website.julianrosser.birthdays;

import android.app.Application;

public class BirthdayReminder extends Application {

    // Application reference
    private static BirthdayReminder sInstance;

    public static BirthdayReminder getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

}
