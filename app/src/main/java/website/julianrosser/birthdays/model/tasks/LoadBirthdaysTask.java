package website.julianrosser.birthdays.model.tasks;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import website.julianrosser.birthdays.BirthdayReminder;
import website.julianrosser.birthdays.Constants;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.model.events.BirthdaysLoadedEvent;

public class LoadBirthdaysTask extends AsyncTask<Void, Void, ArrayList<Birthday>> {

    @Override
    protected ArrayList<Birthday> doInBackground(Void... params) {
        ArrayList<Birthday> loadedBirthdays;
        try {
            loadedBirthdays = loadBirthdays();
        } catch (Exception e) {
            loadedBirthdays = new ArrayList<>();
        }
        return loadedBirthdays;
    }

    @Override
    protected void onPostExecute(ArrayList<Birthday> loadedBirthdays) {
        super.onPostExecute(loadedBirthdays);

        ArrayList<Birthday> birthdaysList = new ArrayList<>();

        for (Birthday b : loadedBirthdays) {
            birthdaysList.add(b);
        }
        EventBus.getDefault().post(new BirthdaysLoadedEvent(birthdaysList));
    }

    private ArrayList<Birthday> loadBirthdays() throws IOException,
            JSONException {
        ArrayList<Birthday> loadedBirthdays = new ArrayList<>();

        BufferedReader reader = null;
        try {
            // Open and read the file into a StringBuilder
            InputStream in = BirthdayReminder.getInstance().openFileInput(Constants.FILENAME);
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
                loadedBirthdays.add(new Birthday(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Ignore this one; it happens when starting fresh
        } finally {
            if (reader != null)
                reader.close();
        }
        return loadedBirthdays;
    }
}