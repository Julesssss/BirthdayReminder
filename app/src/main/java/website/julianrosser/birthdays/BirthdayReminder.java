package website.julianrosser.birthdays;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BirthdayReminder extends Application {

    private static BirthdayReminder sInstance;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    public static BirthdayReminder getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initializeFirebase();
    }

    private void initializeFirebase() {
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.setPersistenceEnabled(true);
        mDatabase = mFirebaseDatabase.getReference();
    }

    public DatabaseReference getDatabaseReference() {
        if (mDatabase == null) {
            if (mFirebaseDatabase == null) {
                mFirebaseDatabase = FirebaseDatabase.getInstance();
            }
            mDatabase = mFirebaseDatabase.getReference();
        }
        return mDatabase;
    }

    public void setUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

}
