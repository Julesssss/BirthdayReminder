package website.julianrosser.birthdays.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

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

import website.julianrosser.birthdays.Constants;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.adapter.BirthdayViewAdapter;
import website.julianrosser.birthdays.model.tasks.LoadBirthdaysTask;
import website.julianrosser.birthdays.recievers.NotificationBuilderReceiver;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.services.SetAlarmsService;
import website.julianrosser.birthdays.fragments.DialogFragments.AddEditFragment;
import website.julianrosser.birthdays.fragments.DialogFragments.ItemOptionsFragment;
import website.julianrosser.birthdays.fragments.RecyclerListFragment;

@SuppressWarnings("deprecation")
public class BirthdayListActivity extends AppCompatActivity implements AddEditFragment.NoticeDialogListener, ItemOptionsFragment.ItemOptionsListener {
    ;
    public static ArrayList<Birthday> birthdaysList = new ArrayList<>();
    public static Tracker mTracker;
    static RecyclerListFragment recyclerListFragment;
    static BirthdayListActivity mContext;
    static Context mAppContext;
    private static FloatingActionButton floatingActionButton;
    // Keys for orientation change reference
    final String ADD_EDIT_INSTANCE_KEY = "fragment_add_edit";
    final String ITEM_OPTIONS_INSTANCE_KEY = "fragment_item_options";
    final String RECYCLER_LIST_INSTANCE_KEY = "fragment_recycler_list";
    // Fragment references
    AddEditFragment addEditFragment;
    ItemOptionsFragment itemOptionsFragment;
    LoadBirthdaysTask loadBirthdaysTask;
    // App indexing
    private GoogleApiClient mClient;
    private String mUrl;
    private String mTitle;
    private String mDescription;
    private String mSchemaType;

    /**
     * For easy access to BirthdayListActivity context from multiple Classes
     */
    public static BirthdayListActivity getContext() {
        return mContext;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    // This builds an identical PendingIntent to the alarm and cancels when
    private static void cancelAlarm(Birthday deletedBirthday) {

        // CreateIntent to start the AlarmNotificationReceiver
        Intent mNotificationReceiverIntent = new Intent(BirthdayListActivity.getAppContext(),
                NotificationBuilderReceiver.class);

        // Create pending Intent using Intent we just built
        PendingIntent mNotificationReceiverPendingIntent = PendingIntent
                .getBroadcast(getAppContext(), deletedBirthday.getName().hashCode(),
                        mNotificationReceiverIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Finish by passing PendingIntent and delay time to AlarmManager
        AlarmManager mAlarmManager = (AlarmManager) getAppContext().getSystemService(ALARM_SERVICE);
        mAlarmManager.cancel(mNotificationReceiverPendingIntent);
    }

    /**
     * Save Birthdays to JSON file, then Update alarms by starting Service
     **/
    public static void saveBirthdays()
            throws JSONException, IOException {

        if (birthdaysList != null) {

            try {
                // Build an array in JSON
                JSONArray array = new JSONArray();
                for (Birthday b : birthdaysList)
                    array.put(b.toJSON());

                // Write the file to disk
                Writer writer = null;
                try {
                    OutputStream out = mAppContext.openFileOutput(Constants.FILENAME,
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

            // Launch service to update alarms when data changed
            Intent serviceIntent = new Intent(BirthdayListActivity.getAppContext(), SetAlarmsService.class);
            BirthdayListActivity.getAppContext().startService(serviceIntent);
        }
    }

    // Force UI thread to ensure mAdapter updates RecyclerView list
    public static void dataChangedUiThread() {
        // Reorder ArrayList to sort by desired method
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(BirthdayListActivity.getAppContext());

        // Get users sort preference
        if (Integer.valueOf(sharedPref.getString(getAppContext().getString(R.string.pref_sort_by_key), "0")) == 1) {
            BirthdayViewAdapter.sortBirthdaysByName();
        } else {
            BirthdayViewAdapter.sortBirthdaysByDate();
        }

        if (floatingActionButton != null && floatingActionButton.getVisibility() == View.INVISIBLE) {
            floatingActionButton.show();
        }

        RecyclerListFragment.mAdapter.notifyDataSetChanged();
        RecyclerListFragment.showEmptyMessageIfRequired();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pass toolbar as ActionBar for functionality
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open New Birthday Fragment
                showAddEditBirthdayFragment(AddEditFragment.MODE_ADD, 0);
            }
        });

        // Initialize context reference
        mContext = this;
        mAppContext = getApplicationContext();

        // Find RecyclerListFragment reference
        if (savedInstanceState != null) {
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

        // This is to help the fragment keep its state on rotation
        recyclerListFragment.setRetainInstance(true);

        // Obtain the shared Tracker instance.
        mTracker = getDefaultTracker();

        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        mUrl = "http://julianrosser.website";
        mTitle = "Birthday Reminders";
        mDescription = "Simple birthday reminders for loved-ones";
        mSchemaType = "http://schema.org/Article";

        if (getIntent().getExtras() != null && getIntent().getExtras().getInt(Constants.INTENT_FROM_KEY, 10) == Constants.INTENT_FROM_NOTIFICATION) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Notification Touch")
                    .build());
        }


        // Get sample of theme choice
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Random r = new Random();
        if (r.nextInt(10) == 5) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Samples")
                    .setAction("Theme")
                    .setValue(Long.valueOf(prefs.getString(getResources().getString(R.string.pref_theme_key), "0")))
                    .build());
        }
    }

    public Action getAction() {
        Thing object = new Thing.Builder()
                .setName(mTitle)
                .setDescription(mDescription)
                .setUrl(Uri.parse(mUrl))
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
        AppIndex.AppIndexApi.start(mClient, getAction());
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

        dataChangedUiThread();

        // Tracker
        mTracker.setScreenName("BirthdayListActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onStop() {
        try {
            saveBirthdays();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Random r = new Random();
        if (r.nextInt(10) == 5) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Data")
                    .setAction("Birthdays count")
                    .setValue(birthdaysList.size())
                    .build());
        }

        AppIndex.AppIndexApi.end(mClient, getAction());
        mClient.disconnect();
        super.onStop();
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (recyclerListFragment != null && recyclerListFragment.isAdded()) {
            //Save the fragment's instance (IF THEY EXIST!)
            getSupportFragmentManager().putFragment(outState, RECYCLER_LIST_INSTANCE_KEY, recyclerListFragment);
        }

        if (itemOptionsFragment != null && itemOptionsFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, ITEM_OPTIONS_INSTANCE_KEY, itemOptionsFragment);
        }
        if (addEditFragment != null && addEditFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, ADD_EDIT_INSTANCE_KEY, addEditFragment);
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
            Birthday editBirthday = birthdaysList.get(birthdayListPosition);
            // Pass birthday's data to Fragment
            bundle.putInt(AddEditFragment.DATE_KEY, editBirthday.getDate().getDate());
            bundle.putInt(AddEditFragment.MONTH_KEY, editBirthday.getDate().getMonth());
            bundle.putInt(AddEditFragment.YEAR_KEY, editBirthday.getYear());
            bundle.putInt(AddEditFragment.POS_KEY, birthdayListPosition);
            bundle.putString(AddEditFragment.NAME_KEY, editBirthday.getName());
            bundle.putBoolean(AddEditFragment.SHOW_YEAR_KEY, editBirthday.shouldIncludeYear());
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
    public void onDialogPositiveClick(AddEditFragment dialog, String name, int day, int month, int year, boolean includeYear, int addEditMode, final int position) {

        // Build date object which will be used by new Birthday
        Date dateOfBirth = new Date();
        dateOfBirth.setYear(year);
        dateOfBirth.setMonth(month);
        dateOfBirth.setDate(day);


        // Format name by capitalizing name
        name = WordUtils.capitalize(name);

        final Birthday birthday;

        // Decide whether to create new or edit old birthday
        if (addEditMode == AddEditFragment.MODE_EDIT) {
            // Edit text
            birthday = birthdaysList.get(position);
            birthday.edit(name, dateOfBirth, true, includeYear);

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    /** Logic for edit animation. Depending first on sorting preference, check whether the sorting will change.
                     * if so, used adapter moved animation, else just refresh information */
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(BirthdayListActivity.getAppContext());

                    // Get users sorting preference
                    if (Integer.valueOf(sharedPref.getString(getAppContext().getString(R.string.pref_sort_by_key), "0")) != 1) {

                        // PREF SORT: DATE
                        if (BirthdayViewAdapter.willChangeDateOrder(birthday)) {

                            // Order will change, sort, then notify adapter of move
                            BirthdayViewAdapter.sortBirthdaysByDate();
                            RecyclerListFragment.mAdapter.notifyItemMoved(position, birthdaysList.indexOf(birthday));

                        } else {
                            // No order change, so just notify item changed
                            RecyclerListFragment.mAdapter.notifyItemChanged(birthdaysList.indexOf(birthday));
                        }

                    } else {

                        // PREF SORT: NAME. If order changes, sort the notify adapter
                        if (BirthdayViewAdapter.willChangeNameOrder(birthday)) {

                            // Order will change, sort, then notify adapter of move
                            BirthdayViewAdapter.sortBirthdaysByName();
                            RecyclerListFragment.mAdapter.notifyItemMoved(position, birthdaysList.indexOf(birthday));

                        } else {
                            // order not changes, so forget sot, and just notify item changed
                            RecyclerListFragment.mAdapter.notifyItemChanged(birthdaysList.indexOf(birthday));
                        }
                    }

                    // Delay update until after animation has finished
                    Runnable r = new Runnable() {
                        public void run() {
                            RecyclerListFragment.mAdapter.notifyDataSetChanged();
                        }
                    };
                    new Handler().postDelayed(r, 500);


                }


            });

        } else {
            // Create birthday, add to array and notify adapter
            birthday = new Birthday(name, dateOfBirth, true, includeYear);
            birthdaysList.add(birthday);

            // Notify adapter
            mContext.runOnUiThread(new Runnable() {
                public void run() {

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(BirthdayListActivity.getAppContext());

                    // Get users sort preference
                    if (Integer.valueOf(sharedPref.getString(getAppContext().getString(R.string.pref_sort_by_key), "0")) == 1) {
                        BirthdayViewAdapter.sortBirthdaysByName();
                    } else {
                        BirthdayViewAdapter.sortBirthdaysByDate();
                    }
                    RecyclerListFragment.mAdapter.notifyItemInserted(birthdaysList.indexOf(birthday));
                    RecyclerListFragment.showEmptyMessageIfRequired();
                }
            });

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("New Birthday")
                    .build());

//            mTracker.send(new HitBuilders.EventBuilder()
//                    .setCategory("Data")
//                    .setAction("Name")
//                    .setLabel(name)
//                    .build());

//            mTracker.send(new HitBuilders.EventBuilder()
//                    .setCategory("Data")
//                    .setAction("Date")
//                    .setLabel(dateOfBirth.getDate() + " / " + dateOfBirth.getMonth())
//                    .build());
        }

        try {
            saveBirthdays();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // We only use this method to delete data from Birthday array and pass a reference to the cancel alarm method.
    public void deleteFromArray(final int position) {

        // Cancel the notification PendingIntent
        cancelAlarm(birthdaysList.get(position));

        // Notify adapter
        mContext.runOnUiThread(new Runnable() {
            public void run() {

                final Birthday birthdayToDelete = birthdaysList.get(position);

                birthdaysList.remove(position);

                RecyclerListFragment.mAdapter.notifyItemRemoved(position);
                RecyclerListFragment.showEmptyMessageIfRequired();

                if (floatingActionButton != null && floatingActionButton.getVisibility() == View.INVISIBLE) {
                    floatingActionButton.show();
                }
                try {
                    saveBirthdays();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Snackbar.make(floatingActionButton, BirthdayListActivity.getAppContext().getString(R.string.deleted) + " "
                        + birthdayToDelete.getName(), Snackbar.LENGTH_LONG).setAction(R.string.undo,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                birthdaysList.add(birthdayToDelete);
                                // Notify adapter
                                mContext.runOnUiThread(new Runnable() {
                                    public void run() {

                                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(BirthdayListActivity.getAppContext());

                                        // Get users sort preference
                                        if (Integer.valueOf(sharedPref.getString(getAppContext().getString(R.string.pref_sort_by_key), "0")) == 1) {
                                            BirthdayViewAdapter.sortBirthdaysByName();
                                        } else {
                                            BirthdayViewAdapter.sortBirthdaysByDate();
                                        }

                                        RecyclerListFragment.mAdapter.notifyItemInserted(birthdaysList.indexOf(birthdayToDelete));
                                        RecyclerListFragment.showEmptyMessageIfRequired();
                                    }
                                });
                                try {
                                    saveBirthdays();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).show();
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
        if (id == R.id.action_import_contacts) {
            checkContactPermissionAndLaunchImportActivity();

        } else if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);

        } else if (id == R.id.action_help) {
            Intent intentHelp = new Intent(this, HelpActivity.class);
            startActivity(intentHelp);
            return true;
        } else if (id == R.id.action_privacy_policy) {
            Intent intentPrivacy = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intentPrivacy);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Call this method from Adapter so reference can be kept here in BirthdayListActivity
    public void launchLoadBirthdaysTask() {
        loadBirthdaysTask = new LoadBirthdaysTask();
        loadBirthdaysTask.execute();
    }

    // Check if Async task is currently running, to prevent errors when exiting
    private boolean isTaskRunning() {
        return (loadBirthdaysTask != null) && (loadBirthdaysTask.getStatus() == AsyncTask.Status.RUNNING);
    }

    // Set theme based on users preference
    // todo - refactor
    public void setTheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("0")) {
            setTheme(R.style.BlueTheme);
        } else if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("1")) {
            setTheme(R.style.PinkTheme);
        } else if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("2")) {
            setTheme(R.style.GreenTheme);
        } else {
            setTheme(R.style.PinkTheme);
        }
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

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Edit")
                .build());
    }

    @Override
    public void onItemDelete(ItemOptionsFragment dialog, int position) {
        itemOptionsFragment.dismiss();
        deleteFromArray(position);

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Delete")
                .build());
    }

    @Override
    public void onItemToggleAlarm(ItemOptionsFragment dialog, int position) {
        itemOptionsFragment.dismiss();
        // Change birthdays remind bool
        birthdaysList.get(position).toggleReminder();
        alarmToggled(position);

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Toggle Alarm OPTION")
                .build());
    }

    // This is in a separate method so it can be called from different classes
    public void alarmToggled(int position) {

        // Use position parameter to get Birthday reference
        Birthday birthday = birthdaysList.get(position);

        // Cancel the previously set alarm, without re-calling service
        cancelAlarm(birthday);

        // Notify adapter of change, so that UI is updated
        dataChangedUiThread();

        // Notify user of change. If birthday is today, let user know alarm is set for next year
        if (birthday.getDaysBetween() == 0 && birthday.getRemind()) {
            Snackbar.make(floatingActionButton, floatingActionButton.getContext().getString(R.string.reminder_for) + birthday.getName() + " " +
                    birthday.getReminderString() + floatingActionButton.getContext().getString(R.string.for_next_year), Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(floatingActionButton, BirthdayListActivity.getAppContext().getString(R.string.reminder_for) + birthday.getName() + " " +
                    birthday.getReminderString(), Snackbar.LENGTH_LONG).show();
        }

        // Attempt to save updated Birthday data
        try {
            saveBirthdays();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Toggle Alarm")
                .build());
    }

    public void checkContactPermissionAndLaunchImportActivity() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    Constants.CONTACT_PERMISSION_CODE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            launchImportContactActivity();
        }
    }

    public static boolean isContactAlreadyAdded(Birthday contact) {
        boolean onList = false;
        for (Birthday b : birthdaysList) {
            if (b.getName().equals(contact.getName())) {
                onList = true;
            }
        }
        return onList;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == Constants.CONTACT_PERMISSION_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImportContactActivity();

            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Snackbar.make(floatingActionButton, R.string.contact_permission_denied_message, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    public void launchImportContactActivity() {
        Intent intent = new Intent(this, ImportContactsActivity.class);
        startActivity(intent);
    }
}