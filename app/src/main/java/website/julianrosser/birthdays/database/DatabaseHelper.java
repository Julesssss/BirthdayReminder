package website.julianrosser.birthdays.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import website.julianrosser.birthdays.AlarmsHelper;
import website.julianrosser.birthdays.BirthdayReminder;
import website.julianrosser.birthdays.Constants;
import website.julianrosser.birthdays.Preferences;
import website.julianrosser.birthdays.Utils;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.model.FirebaseBirthday;
import website.julianrosser.birthdays.model.events.BirthdaysLoadedEvent;

public class DatabaseHelper {

    public interface BirthdaysLoadedListener {
        void onBirthdaysReturned(ArrayList<Birthday> birthdays);
        void onCancelled(String errorMessage);
    }

    public static void loadFirebaseBirthdays(final BirthdaysLoadedListener birthdaysLoadedListener) {
        FirebaseUser user = BirthdayReminder.getInstance().getCurrentUser();
        if (null == user) {
            Log.i(DatabaseHelper.class.getSimpleName(), "User not loaded yet");
            return;
        }
        // load birthdays from FB
        final DatabaseReference databaseReference = BirthdayReminder.getInstance().getDatabaseReference().child(user.getUid()).child(Constants.TABLE_BIRTHDAYS);
        if (databaseReference == null) {
            Log.i(DatabaseHelper.class.getSimpleName(), "Database not initialised yet");
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

    public static void saveBirthdayChange(Birthday birthday, Update state) {
        if (Preferences.isUsingFirebase(BirthdayReminder.getInstance())) {
            saveFirebaseChange(birthday, state);
        } else {
            try {
                saveJSONChange(birthday, state);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(BirthdayReminder.getInstance(), "ERROR saving data, try signing in!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void saveFirebaseChange(Birthday birthday, Update state) {
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
            dbr.child("email").setValue(user.getEmail()); // todo refactor
        }
    }

    private static void saveJSONChange(Birthday birthday, Update state) throws IOException {
        ArrayList<Birthday> birthdays = new ArrayList<>();
        Context context = BirthdayReminder.getInstance();

        // Load birthdays
        BufferedReader reader = null;
        try {
            // Open and read the file into a StringBuilder
            InputStream in = context.openFileInput(Constants.FILENAME);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();

            // Build the array of birthdays from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                birthdays.add(new Birthday(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Ignore this one; it happens when starting fresh
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) reader.close();
        }

        // Perform action
        switch (state) {
            case CREATE:
                birthdays.add(birthday);
                break;
            case UPDATE:
                for (Birthday b : birthdays) {
                    if (b.getUID().equals(birthday.getUID())) {
                        birthdays.remove(b);
                        birthdays.add(birthday);
                        break;
                    }
                }
                break;
            case DELETE:
                for (int i = 0; i < 5; i++) {
                    if (birthdays.get(i).getUID().equals(birthday.getUID())) {
                        birthdays.remove(i);
                        break;
                    }
                }
                break;
        }

        // Save JSON data
        try {
            // Build an array in JSON
            JSONArray array = new JSONArray();
            for (Birthday b : birthdays)
                array.put(b.toJSON());

            // Write the file to disk
            Writer writer = null;
            try {
                OutputStream out = context.openFileOutput(Constants.FILENAME,
                        Context.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(array.toString());

            } finally {
                if (writer != null)
                    writer.close();
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(new BirthdaysLoadedEvent(birthdays));

        // Set alarms
        AlarmsHelper.setAllNotificationAlarms(BirthdayReminder.getInstance());
    }

    public enum Update {
        CREATE,
        UPDATE,
        DELETE,
    }

}
