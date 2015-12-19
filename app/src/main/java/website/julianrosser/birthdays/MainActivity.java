package website.julianrosser.birthdays;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

    // Keys for orientation change reference
    final String ADD_EDIT_INSTANCE_KEY = "fragment_add_edit";
    final String ITEM_OPTIONS_INSTANCE_KEY = "fragment_item_options";
    final String RECYCLER_LIST_INSTANCE_KEY = "fragment_recycler_list";

    static RecyclerListFragment recyclerListFragment;

    // Fragment references
    AddEditFragment addEditFragment;
    ItemOptionsFragment itemOptionsFragment;

    static MainActivity mContext;
    static Context mAppContext;

    LoadBirthdaysTask loadBirthdaysTask;

    /**
     * For easy access to MainActivity context from multiple Classes
     */
    public static MainActivity getContext() {
        return mContext;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pass toolbar as ActionBar for functionality
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Initialize context reference
        mContext = this;
        mAppContext = getApplicationContext();

        // Set default preference values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // Find RecyclerListFragment reference
        if (savedInstanceState != null) {
            Log.d("RecyclerListFragment", "RECYCLE newFragment");
            //Restore the fragment's instance
            recyclerListFragment = (RecyclerListFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, RECYCLER_LIST_INSTANCE_KEY);


            itemOptionsFragment = (ItemOptionsFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, ITEM_OPTIONS_INSTANCE_KEY);

            addEditFragment = (AddEditFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, ADD_EDIT_INSTANCE_KEY
            );

        } else {
            // Create new RecyclerListFragment
            recyclerListFragment = RecyclerListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, recyclerListFragment)
                    .commit();
        }

        // This is to help the fragment keep it;s state on rotation
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

    @Override
    protected void onResume() {
        super.onResume();

        if (mContext == null) {
            mContext = this;
        }
        if (mAppContext == null) {
            mAppContext = getApplicationContext();
        }

        MainActivity.dataChangedUiThread();

    }

    /**
     * Ensure birthday array is saved, but not if replacing with empty
     */
    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance (IF THEY EXIST!)
        getSupportFragmentManager().putFragment(outState, RECYCLER_LIST_INSTANCE_KEY, recyclerListFragment);

        if (itemOptionsFragment != null && itemOptionsFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, ITEM_OPTIONS_INSTANCE_KEY, itemOptionsFragment);
        }
        if (addEditFragment != null && addEditFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, ADD_EDIT_INSTANCE_KEY, addEditFragment);
        }
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

            Birthday b = new Birthday(name, f, true);
            birthdaysList.add(b);

            dataChangedUiThread();

            try {
                saveBirthdays();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void showAddEditBirthdayFragment(int mode, int birthdayListPosition) {
        // Create an instance of the dialog fragment and show it
        addEditFragment = AddEditFragment.newInstance();

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
        addEditFragment.setArguments(bundle);
        addEditFragment.show(getSupportFragmentManager(), "AddEditBirthdayFragment");
    }

    // This method creates and shows a new ItemOptionsFragment, this replaces ContextMenu
    public void showItemOptionsFragment(int position) {
        // Create an instance of the dialog fragment and show it
        itemOptionsFragment = ItemOptionsFragment.newInstance(position);
        itemOptionsFragment.setRetainInstance(true);
        itemOptionsFragment.show(getSupportFragmentManager(), "AddEditBirthdayFragment");
    }

    // Callback from AddEditFragment, create new Birthday object and add to array
    @Override
    public void onDialogPositiveClick(AddEditFragment dialog, String name, int day, int month, int addEditMode, int position) {
        // Build date object which will be used by new Birthday
        Date dateOfBirth = new Date();
        dateOfBirth.setDate(day);
        dateOfBirth.setMonth(month);
        dateOfBirth.setYear(Birthday.getYearOfNextBirthday(dateOfBirth));

        // Format name by capitalizing name
        name = WordUtils.capitalize(name);

        // Decide whether to create new or edit old birthday
        if (addEditMode == AddEditFragment.MODE_EDIT) {
            birthdaysList.get(position).edit(name, dateOfBirth, true);
        } else {
            Birthday newBirthday = new Birthday(name, dateOfBirth, true);
            birthdaysList.add(newBirthday);
        }
        dataChangedUiThread();

        // Attempt to save updated Birthday data
        try {
            saveBirthdays();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // We only use this method to delete data from Birthday array and pass a reference to the cancel alarm method.
    public static void deleteFromArray(int position) {

        // Cancel the notification PendingIntent
        cancelAlarm(birthdaysList.get(position));

        // Remove from Array
        birthdaysList.remove(position);

        // Use UI Thread to notify adapter to data change
        dataChangedUiThread();

        // Attempt to save updated Birthday data
        try {
            saveBirthdays();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This builds an identical PendingIntent to the alarm and cancels when
    private static void cancelAlarm(Birthday deletedBirthday) {

        // CreateIntent to start the AlarmNotificationReceiver
        Intent mNotificationReceiverIntent = new Intent(MainActivity.getAppContext(),
                AlarmNotificationBuilder.class);

        // Create pending Intent using Intent we just built
        PendingIntent mNotificationReceiverPendingIntent = PendingIntent
                .getBroadcast(getAppContext(), deletedBirthday.getName().hashCode(),
                        mNotificationReceiverIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Finish by passing PendingIntent and delay time to AlarmManager
        AlarmManager mAlarmManager = (AlarmManager) getAppContext().getSystemService(ALARM_SERVICE);
        mAlarmManager.cancel(mNotificationReceiverPendingIntent);
    }

    // Force UI thread to ensure mAdapter updates RecyclerView list
    public static void dataChangedUiThread() {
        // Reorder ArrayList to sort by desired method
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());

        // Get users sort preference
        if (Integer.valueOf(sharedPref.getString(getAppContext().getString(R.string.pref_sort_by_key), "0")) == 1) {
            RecyclerViewAdapter.sortBirthdaysByName();
        } else {
            RecyclerViewAdapter.sortBirthdaysByDate();
        }

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
            showAddEditBirthdayFragment(AddEditFragment.MODE_ADD, 0); // todo why 0?
            return true;

        } else if (id == R.id.action_delete_all) {
            birthdaysList.clear();
            MainActivity.dataChangedUiThread();

        } else if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save Birthdays to JSON file, then Update alarms by starting Service
     **/
    public static void saveBirthdays()
            throws JSONException, IOException {

        Log.i("SaveBirthdays", "SAVING BIRTHDAYS");

        if (birthdaysList != null && birthdaysList.size() != 0) {

            try {
                // Build an array in JSON
                JSONArray array = new JSONArray();
                for (Birthday b : birthdaysList)
                    array.put(b.toJSON());

                // Write the file to disk
                Writer writer = null;
                try {
                    OutputStream out = mAppContext.openFileOutput(FILENAME,
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
        }

        // Launch service to update alarms when data changed
        Intent serviceIntent = new Intent(MainActivity.getAppContext(), SetAlarmsService.class);
        MainActivity.getAppContext().startService(serviceIntent);
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
        itemOptionsFragment.dismiss();
        showAddEditBirthdayFragment(AddEditFragment.MODE_EDIT, position);
    }

    @Override
    public void onItemDelete(ItemOptionsFragment dialog, int position) {
        itemOptionsFragment.dismiss();
        deleteFromArray(position);
    }

    @Override
    public void onItemToggleAlarm(ItemOptionsFragment dialog, int position) {
        itemOptionsFragment.dismiss();
        alarmToggled(position);
    }

    // This is in a separate method so it can be called from different classes
    public void alarmToggled(int position) {

        Birthday b = birthdaysList.get(position);

        b.toggleReminder();

        Toast.makeText(this, "Reminder for " + b.getName() + b.getReminderString(), Toast.LENGTH_SHORT).show();

        dataChangedUiThread();

        // Attempt to save updated Birthday data
        try {
            saveBirthdays();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getDaysBeforeReminderPref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mAppContext);
        return Integer.valueOf(sharedPref.getString(getAppContext().getString(R.string.pref_days_before_key), "1"));
    }
}