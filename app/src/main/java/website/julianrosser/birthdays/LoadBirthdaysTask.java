package website.julianrosser.birthdays;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoadBirthdaysTask extends AsyncTask<Void, Void, ArrayList<Birthday>> {

    String TAG_ASYNC = getClass().getSimpleName();

    ArrayList<Birthday> loadedBirthdays;

    @Override
    protected ArrayList<Birthday> doInBackground(Void... params) {
        try {
            loadedBirthdays = loadBirthdays();
            // Log.v(TAG, "Loading...");
        } catch (Exception e) {
            loadedBirthdays = new ArrayList<Birthday>();
            Log.v(TAG_ASYNC, "Error loading JSON data: ", e);
        }

        return loadedBirthdays;
    }

    @Override
    protected void onPostExecute(ArrayList<Birthday> loadedBirthdays) {
        super.onPostExecute(loadedBirthdays);

        Log.d(TAG_ASYNC, "onPost: " + loadedBirthdays.size());

        for (Birthday b : loadedBirthdays) {
            MainActivity.birthdaysList.add(b);
        }

        MainActivity.dataChangedUiThread();
    }


    // THis is done in background by
    public ArrayList<Birthday> loadBirthdays() throws IOException,
            JSONException {
        ArrayList<Birthday> loadedBirthdays = new ArrayList<Birthday>();

        BufferedReader reader = null;
        try {
            // Open and read the file into a StringBuilder
            InputStream in = MainActivity.getContext().openFileInput(MainActivity.FILENAME);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();

            String line = null;
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