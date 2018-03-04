package website.julianrosser.birthdays.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import website.julianrosser.birthdays.AlarmsHelper;
import website.julianrosser.birthdays.BirthdayReminder;
import website.julianrosser.birthdays.Constants;
import website.julianrosser.birthdays.Preferences;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.database.DatabaseHelper;
import website.julianrosser.birthdays.fragments.DialogFragments.AddEditFragment;
import website.julianrosser.birthdays.fragments.DialogFragments.ItemOptionsFragment;
import website.julianrosser.birthdays.fragments.RecyclerListFragment;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.model.events.BirthdayItemClickEvent;
import website.julianrosser.birthdays.views.CircleTransform;
import website.julianrosser.birthdays.views.SnackBarHelper;

import static website.julianrosser.birthdays.activities.BirthdayListActivity.NavHeaderState.LOGGED_OUT;

@SuppressWarnings("deprecation")
public class BirthdayListActivity extends GoogleSignInActivity implements ItemOptionsFragment.ItemOptionsListener, View.OnClickListener {

    public static final int RC_SETTINGS = 5311;

    // Keys for orientation change reference
    final String ADD_EDIT_INSTANCE_KEY = "fragment_add_edit";
    final String ITEM_OPTIONS_INSTANCE_KEY = "fragment_item_options";
    final String RECYCLER_LIST_INSTANCE_KEY = "fragment_recycler_list";
    public Tracker mTracker;
    private RecyclerListFragment recyclerListFragment;

    // Fragment references
    private AddEditFragment addEditFragment;
    private ItemOptionsFragment itemOptionsFragment;
    private FloatingActionButton floatingActionButton;

    // App indexing
    private GoogleApiClient mClient;
    private String mUrl;
    private String mTitle;
    private String mDescription;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navigationView;

    private LinearLayout userDetailLayout;
    private SignInButton signInButton;
    private TextView textNavHeaderUserName;
    private TextView textNavHeaderEmail;
    private ImageView imageNavHeaderProfile;

    private String themePref = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pass toolbar as ActionBar for functionality
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //Setting Navigation View Item Selected FirebaseAuthListener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {

                    case R.id.menu_birthdays:
                        return true;
                    case R.id.menu_help:
                        startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                        return true;
                    case R.id.menu_import_contacts:
                        checkContactPermissionAndLaunchImportActivity();
                        return true;
                    case R.id.menu_settings:
                        startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), RC_SETTINGS);
                        return true;
                    case R.id.menu_privacy:
                        startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), RC_SETTINGS);
                        return true;
                    case R.id.menu_logout:
                        signOutGoogle(new GoogleSignOutListener() {
                            @Override public void onComplete() {
                                setNavHeaderUserState(LOGGED_OUT);
                                MenuItem item = navigationView.getMenu().findItem(R.id.menu_logout);
                                if (item != null) {
                                    item.setVisible(true);
                                    invalidateOptionsMenu();
                                }
                                AlarmsHelper.cancelAllAlarms(getApplicationContext(), recyclerListFragment.getAdapter().getBirthdays());
                                navigationView.setCheckedItem(R.id.menu_birthdays);
                                Preferences.setShouldShowWelcomeScreen(BirthdayListActivity.this, true);
                                startActivity(new Intent(BirthdayListActivity.this, WelcomeActivity.class));
                                finish();
                            }
                        });
                        return false;
                    default:
                        return true;
                }
            }
        });
        navigationView.setCheckedItem(R.id.menu_birthdays);

        // Nav header info
        View headerView = navigationView.inflateHeaderView(R.layout.layout_nav_header);

        userDetailLayout = (LinearLayout) headerView.findViewById(R.id.layoutNavHeaderUserInfo);
        userDetailLayout.setOnClickListener(this);

        textNavHeaderUserName = (TextView) headerView.findViewById(R.id.navHeaderUserName);
        textNavHeaderEmail = (TextView) headerView.findViewById(R.id.navHeaderUserEmail);
        imageNavHeaderProfile = (ImageView) headerView.findViewById(R.id.profile_image);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.birthday, R.string.button_negative) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        signInButton = (SignInButton) headerView.findViewById(R.id.sign_in_button);
        setUpGoogleSignIn(signInButton);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setHomeButtonEnabled(true);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open New Birthday Fragment
                showEditBirthdayFragment();
            }
        });

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
        mDescription = "Simple birthday reminder notifications";

        if (getIntent().getExtras() != null && getIntent().getExtras().getInt(Constants.INTENT_FROM_KEY, 10) == Constants.INTENT_FROM_NOTIFICATION) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Notification Touch")
                    .build());
        }
    }

    private void setUpGoogleSignIn(SignInButton signInButton) {
        mDrawerLayout.closeDrawer(Gravity.START);

        setUpGoogleSignInButton(signInButton, new GoogleSignInListener() {
            @Override public void onLogin(@NotNull FirebaseUser firebaseUser) {
                handleUserAuthenticated(firebaseUser);
                Preferences.setIsUsingFirebase(BirthdayListActivity.this, true);
            }

            @Override public void onGoogleFailure(@NotNull String message) {
                setNavHeaderUserState(LOGGED_OUT);
                Preferences.setIsUsingFirebase(BirthdayListActivity.this, false);
            }

            @Override public void onFirebaseFailure(@NotNull String message) {
                setNavHeaderUserState(LOGGED_OUT);
                Preferences.setIsUsingFirebase(BirthdayListActivity.this, false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SETTINGS) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String newTheme = prefs.getString(getString(R.string.pref_theme_key), "0");
            if (!newTheme.equals(themePref)) {
                recreate();
                themePref = newTheme;
            }
        }
    }

    private void handleUserAuthenticated(FirebaseUser user) {
        MenuItem item = navigationView.getMenu().findItem(R.id.menu_logout);
        if (item != null) {
            item.setVisible(true);
            this.invalidateOptionsMenu();
        }

        textNavHeaderUserName.setText(user.getDisplayName());
        textNavHeaderEmail.setText(user.getEmail());
        Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).transform(new CircleTransform()).into(imageNavHeaderProfile);
        setNavHeaderUserState(NavHeaderState.SIGNED_IN);
        BirthdayReminder.getInstance().setUser(user);
        recyclerListFragment.getAdapter().clearBirthdays();
        clearBirthdays();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerListFragment.loadBirthdays();
            }
        }, 2000);
    }

    public void setNavHeaderUserState(NavHeaderState state) {
        switch (state) {
            case LOGGED_OUT:
                userDetailLayout.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
                imageNavHeaderProfile.setVisibility(View.GONE);
                break;
            case SIGNED_IN:
                userDetailLayout.setVisibility(View.VISIBLE);
                signInButton.setVisibility(View.GONE);
                imageNavHeaderProfile.setVisibility(View.VISIBLE);
                break;
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
    protected void onResume() {
        super.onResume();

        if (navigationView != null) {
            navigationView.setCheckedItem(R.id.menu_birthdays);
        }

        // Tracker
        mTracker.setScreenName("BirthdayListActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
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

    public void showEditBirthdayFragment() {
        // Create an instance of the dialog fragment and show it
        addEditFragment = AddEditFragment.newInstance();

        // Create bundle for storing mode information
        Bundle bundle = new Bundle();
        // Pass mode parameter onto Fragment
        bundle.putInt(AddEditFragment.MODE_KEY, AddEditFragment.MODE_ADD);
        addEditFragment.setArguments(bundle);
        // Pass bundle to Dialog, get FragmentManager and show
        addEditFragment.show(getSupportFragmentManager(), "AddEditBirthdayFragment");
    }

    public void showAddBirthdayFragment(Birthday birthday) {
        // Create an instance of the dialog fragment and show it
        addEditFragment = AddEditFragment.newInstance();

        // Create bundle for storing mode information
        Bundle bundle = new Bundle();

        // Pass mode parameter onto Fragment
        bundle.putInt(AddEditFragment.MODE_KEY, AddEditFragment.MODE_EDIT);
        // Reference to birthday we're editing
        // Pass birthday's data to Fragment
        bundle.putInt(AddEditFragment.DATE_KEY, birthday.getDate().getDate());
        bundle.putInt(AddEditFragment.MONTH_KEY, birthday.getDate().getMonth());
        bundle.putInt(AddEditFragment.YEAR_KEY, birthday.getYear());
        bundle.putString(AddEditFragment.NAME_KEY, birthday.getName());
        bundle.putString(AddEditFragment.UID_KEY, birthday.getUID());
        bundle.putBoolean(AddEditFragment.SHOW_YEAR_KEY, birthday.shouldIncludeYear());

        // Pass bundle to Dialog, get FragmentManager and show
        addEditFragment.setArguments(bundle);
        addEditFragment.show(getSupportFragmentManager(), "AddEditBirthdayFragment");
    }

    // This method creates and shows a new ItemOptionsFragment, this replaces ContextMenu
    public void showItemOptionsFragment(Birthday birthday) {
        // Create an instance of the dialog fragment and show it
        itemOptionsFragment = ItemOptionsFragment.newInstance(birthday);
        itemOptionsFragment.setRetainInstance(true);
        itemOptionsFragment.show(getSupportFragmentManager(), "ItemOptionsBirthdayFragment");
    }

    public void clearBirthdays() {
        recyclerListFragment.getAdapter().clearBirthdays();
        recyclerListFragment.showEmptyMessageIfRequired(new ArrayList<Birthday>());
    }

    // Set theme based on users preference
    public void setTheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String pref;
        if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("0")) {
            setTheme(R.style.BlueTheme);
            pref = "0";
        } else if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("1")) {
            setTheme(R.style.PinkTheme);
            pref = "1";
        } else if (prefs.getString(getResources().getString(R.string.pref_theme_key), "0").equals("2")) {
            setTheme(R.style.GreenTheme);
            pref = "2";
        } else {
            setTheme(R.style.BlueTheme);
            pref = "0";
        }
        themePref = pref;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.layoutNavHeaderUserInfo:
                Snackbar.make(v, "Change user display", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.sign_in_button:
                mDrawerLayout.closeDrawer(Gravity.START);
                break;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = (String) title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * Interface Methods
     * - onItemEdit: Launch AddEditFragment to edit current birthday
     * - onItemDelete: Delete selected birthday from array
     */
    @Override
    public void onItemEdit(ItemOptionsFragment dialog, Birthday birthday) {
        itemOptionsFragment.dismiss();

        showAddBirthdayFragment(birthday);

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Edit")
                .build());
    }

    @Override
    public void onItemDelete(ItemOptionsFragment dialog, Birthday birthday) {
        itemOptionsFragment.dismiss();
        DatabaseHelper.saveBirthdayChange(birthday, DatabaseHelper.Update.DELETE);
        AlarmsHelper.cancelAlarm(this, birthday.getName().hashCode());
        SnackBarHelper.birthdayDeleted(floatingActionButton, birthday);
    }

    @Override
    public void onItemToggleAlarm(ItemOptionsFragment dialog, Birthday birthday) {
        birthday.toggleReminder();
        DatabaseHelper.saveBirthdayChange(birthday, DatabaseHelper.Update.UPDATE);
        SnackBarHelper.alarmToggle(floatingActionButton, birthday);
        itemOptionsFragment.dismiss();
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
        Bundle bundle = new Bundle();

        ArrayList<Birthday> bdays = recyclerListFragment.getAdapter().getData();

        ArrayList<String> birthdayNames = new ArrayList<>();
        for (Birthday b : bdays) {
            birthdayNames.add(b.getName());
        }
        bundle.putStringArrayList(ImportContactsActivity.BIRTHDAYS_ARRAY_KEY, birthdayNames);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Subscribe
    public void onBirthdayClicked(BirthdayItemClickEvent event) {
        showItemOptionsFragment(event.getBirthday());
    }

    enum NavHeaderState {
        LOGGED_OUT,
        SIGNED_IN
    }
}