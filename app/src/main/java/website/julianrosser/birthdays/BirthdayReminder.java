package website.julianrosser.birthdays;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BirthdayReminder extends Application {

    // Application reference
    private static BirthdayReminder sInstance;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabase;

    public static BirthdayReminder getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        FirebaseApp.initializeApp(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference("message");
    }

    public DatabaseReference getDatabaseReference() {
        if (mDatabase == null) {
            if (mFirebaseDatabase == null) {
                mFirebaseDatabase = FirebaseDatabase.getInstance();
            }
            mDatabase = mFirebaseDatabase.getReference("message");
        }
        return mDatabase;
    }
}
