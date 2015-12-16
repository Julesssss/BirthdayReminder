package website.julianrosser.birthdays;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import website.julianrosser.birthdays.DialogFragments.AddEditFragment;
import website.julianrosser.birthdays.DialogFragments.ItemOptionsFragment;

public class MainActivity extends AppCompatActivity implements AddEditFragment.NoticeDialogListener, ItemOptionsFragment.ItemOptionsListener {

    public static ArrayList<Birthday> birthdaysList = new ArrayList<>();
    final public String TAG = getClass().getSimpleName();

    static final String FILENAME = "birthdays.json";

    static RecyclerListFragment recyclerListFragment;

    static MainActivity mContext;

    LoadBirthdaysTask loadBirthdaysTask;

    /**
     * For easy access to MainActivity context from multiple Classes
     */
    public static MainActivity getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize context reference
        mContext = this;

        // Find RecyclerListFragment reference
        if (savedInstanceState != null) {
            Log.d("RecyclerListFragment", "RECYCLE newFragment");
            //Restore the fragment's instance
            recyclerListFragment = (RecyclerListFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");

        } else {
            // Create new RecyclerListFragment
            recyclerListFragment = RecyclerListFragment.newInstance();
            Log.d("RecyclerListFragment", "NEW newFragment");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, recyclerListFragment)
                    .commit();
        }
        // Set default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        recyclerListFragment.setRetainInstance(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove context to prevent memory leaks
        mContext = null;

        // Cancel the task if it's running
        if (isTaskRunning()) {
            loadBirthdaysTask.cancel(true);
        }

        loadBirthdaysTask = null;
    }

    /**
     * Ensure birthday array is saved, but not if replacing with empty
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (birthdaysList != null && birthdaysList.size() != 0) {

            try {
                saveBirthdays(birthdaysList);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "mContent", recyclerListFragment);
    }

    // Method for adding test birthday

    public void addTestBirthday() {

        for (int x = 0; x < 1; x++) {
            Date f = new Date();
            Random r = new Random();
            f.setMonth(r.nextInt(12));
            f.setDate(r.nextInt(31) + 1);
            f.setYear(2015);
            String[] nameArray = getResources().getStringArray(R.array.name_array);
            String name = nameArray[r.nextInt(nameArray.length)];
            boolean reminder = r.nextInt(3) != 1;

            Birthday b = new Birthday(name, f, reminder);
            birthdaysList.add(b);

            dataChangedUiThread();
        }

    }

    public void showAddEditBirthdayFragment(int mode, int birthdayListPosition) {
        // Create an instance of the dialog fragment and show it
        AddEditFragment dialog = AddEditFragment.newInstance();

        // Create bundle for storing mode information
        Bundle bundle = new Bundle();
        // Pass mode parameter onto Fragment
        bundle.putInt(AddEditFragment.MODE_KEY, mode);

        // If we are editing an old birthday, pass its information to fragment
        if (mode == AddEditFragment.MODE_EDIT) {
            // Reference to birthday we're editing
            Birthday editBirthday = birthdaysList.get(birthdayListPosition); // todo - checks: don't open fragment if null
            // Pass birthday's data to Fragment
            bundle.putInt(AddEditFragment.DATE_KEY, editBirthday.getDate().getDate());
            bundle.putInt(AddEditFragment.MONTH_KEY, editBirthday.getDate().getMonth());
            bundle.putInt(AddEditFragment.POS_KEY, birthdayListPosition);
            bundle.putString(AddEditFragment.NAME_KEY, editBirthday.getName());
        }

        // Pass bundle to Dialog, get FragmentManager and show
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "AddEditBirthdayFragment");
    }

    // This method creates and shows a new ItemOptionsFragment, this replaces ContextMenu
    public void showItemOptionsFragment(int position) {
        // Create an instance of the dialog fragment and show it
        ItemOptionsFragment dialog = ItemOptionsFragment.newInstance(position);
        dialog.show(getSupportFragmentManager(), "AddEditBirthdayFragment");
    }

    // Callback from AddEditFragment, create new Birthday object and add to array
    @Override
    public void onDialogPositiveClick(AddEditFragment dialog, String name, int day, int month, int addEditMode, int position) {
        Date dateOfBirth = new Date();
        dateOfBirth.setDate(day);
        dateOfBirth.setMonth(month);
        dateOfBirth.setYear(Birthday.getYearOfNextBirthday(dateOfBirth));

        name = WordUtils.capitalize(name);

        if (addEditMode == AddEditFragment.MODE_EDIT) {
            birthdaysList.get(position).edit(name, dateOfBirth, true);
        } else {
            Birthday newBirthday = new Birthday(name, dateOfBirth, true);
            birthdaysList.add(newBirthday);
        }
        dataChangedUiThread();
    }

    // We only use this method to delete data from Birthday array
    public static void deleteFromArray(int position) {
        birthdaysList.remove(position);
        dataChangedUiThread();
    }

    // Force UI thread to ensure mAdapter updates RecyclerView list
    public static void dataChangedUiThread() {
        // Reorder ArrayList to sort by nearest birthday.
        RecyclerViewAdapter.sortBirthdaysByDate();

        mContext.runOnUiThread(new Runnable() {
            public void run() {
                Log.d("UI thread", "Casting magic spell on mAdapter...");
                RecyclerListFragment.mAdapter.notifyDataSetChanged();
                RecyclerListFragment.showEmptyMessageIfRequired();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            addTestBirthday();
            return true;

        } else if (id == R.id.action_add) {
            showAddEditBirthdayFragment(AddEditFragment.MODE_ADD, 0);
            return true;

        } else if (id == R.id.action_delete_all) {
            birthdaysList.clear();
            MainActivity.dataChangedUiThread();

        } else if (id == R.id.action_test_noti) {
            Intent serviceIntent = new Intent(getApplicationContext(), SetAlarmsService.class);
            getApplicationContext().startService(serviceIntent);

        } else if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save Birthdays to JSON file
     **/
    public void saveBirthdays(ArrayList<Birthday> birthdays)
            throws JSONException, IOException {

        Log.i(TAG, "SAVING BIRTHDAYS");

        // Build an array in JSON
        JSONArray array = new JSONArray();
        for (Birthday b : birthdays)
            array.put(b.toJSON());

        // Write the file to disk
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(FILENAME,
                    Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    // Call this method from Adapter so reference can be kept here in MainActivity
    public void launchLoadBirthdaysTask() {
        loadBirthdaysTask = new LoadBirthdaysTask();
        loadBirthdaysTask.execute();
    }

    // Check if Async task is currently running, to prevent errors when exiting
    private boolean isTaskRunning() {
        return (loadBirthdaysTask != null) && (loadBirthdaysTask.getStatus() == AsyncTask.Status.RUNNING);
    }

    /**
     * Interface Methods
     * - onItemEdit: Launch AddEditFragment to edit current birthday
     * - onItemDelete: Delete selected birthday from array
     */
    @Override
    public void onItemEdit(ItemOptionsFragment dialog, int position) {
        showAddEditBirthdayFragment(AddEditFragment.MODE_EDIT, position);
    }

    @Override
    public void onItemDelete(ItemOptionsFragment dialog, int position) {
        deleteFromArray(position);
    }
}