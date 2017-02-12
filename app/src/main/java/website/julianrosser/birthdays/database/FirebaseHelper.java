package website.julianrosser.birthdays.database;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import website.julianrosser.birthdays.AlarmsHelper;
import website.julianrosser.birthdays.BirthdayReminder;
import website.julianrosser.birthdays.Constants;
import website.julianrosser.birthdays.Utils;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.model.FirebaseBirthday;

public class FirebaseHelper {

    public interface BirthdaysLoadedListener {
        void onBirthdaysReturned(ArrayList<Birthday> birthdays);
        void onCancelled(String errorMessage);
    }

    public static void loadFirebaseBirthdays(final BirthdaysLoadedListener birthdaysLoadedListener) {
        FirebaseUser user = BirthdayReminder.getInstance().getCurrentUser();
        if (null == user) {
            Log.i(FirebaseHelper.class.getSimpleName(), "User not loaded yet");
            return;
        }
        // load birthdays from FB
        final DatabaseReference databaseReference = BirthdayReminder.getInstance().getDatabaseReference().child(user.getUid()).child(Constants.TABLE_BIRTHDAYS);
        if (databaseReference == null) {
            Log.i(FirebaseHelper.class.getSimpleName(), "Database not initialised yet");
            return;
        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Birthday> birthdays = new ArrayList<>();
                for (DataSnapshot birthdaySnap : dataSnapshot.getChildren()) {
                    FirebaseBirthday firebaseBirthday = birthdaySnap.getValue(FirebaseBirthday.class);
                    Birthday birthday = Birthday.fromFB(firebaseBirthday);
                    birthdays.add(birthday);
                }
                birthdaysLoadedListener.onBirthdaysReturned(birthdays);
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                birthdaysLoadedListener.onCancelled(databaseError.getMessage());
                databaseReference.removeEventListener(this);
            }
        });
    }

    public static void saveBirthdayChange(Birthday birthday, FirebaseUpdate state) {

        FirebaseUser user = BirthdayReminder.getInstance().getCurrentUser();
        if (user == null || Utils.isStringEmpty(user.getUid())) {
            return;
        }
        DatabaseReference databaseReference = BirthdayReminder.getInstance().getDatabaseReference();
        databaseReference = databaseReference.child(user.getUid()).child(Constants.TABLE_BIRTHDAYS)
                .child(String.valueOf(birthday.getUID()));

        FirebaseBirthday firebaseBirthday = FirebaseBirthday.convertToFirebaseBirthday(birthday);

        switch (state) {
            case CREATE:
                databaseReference.setValue(firebaseBirthday);
                break;
            case UPDATE:
                databaseReference.setValue(firebaseBirthday);
                break;
            case DELETE:
                databaseReference.setValue(null);
                break;
        }
        setLastUpdatedTime();
        AlarmsHelper.setAllNotificationAlarms(BirthdayReminder.getInstance());
    }

    private static void setLastUpdatedTime() {
        FirebaseUser user = BirthdayReminder.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference dbr = BirthdayReminder.getInstance().getDatabaseReference().child(user.getUid());
            dbr.child("lastUpdated").setValue(ServerValue.TIMESTAMP);
            dbr.child("email").setValue(user.getEmail());
        }
    }

    public enum FirebaseUpdate {
        CREATE,
        UPDATE,
        DELETE,
    }

}
