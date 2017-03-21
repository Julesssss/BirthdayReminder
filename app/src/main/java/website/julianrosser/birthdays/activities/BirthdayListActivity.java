package website.julianrosser.birthdays.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import website.julianrosser.birthdays.AlarmsHelper;
import website.julianrosser.birthdays.BirthdayReminder;
import website.julianrosser.birthdays.BuildConfig;
import website.julianrosser.birthdays.Constants;
import website.julianrosser.birthdays.Preferences;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.database.DatabaseHelper;
import website.julianrosser.birthdays.fragments.DialogFragments.AddEditFragment;
import website.julianrosser.birthdays.fragments.DialogFragments.ItemOptionsFragment;
import website.julianrosser.birthdays.fragments.RecyclerListFragment;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.model.events.BirthdayItemClickEvent;
import website.julianrosser.birthdays.model.events.BirthdaysLoadedEvent;
import website.julianrosser.birthdays.views.CircleTransform;
import website.julianrosser.birthdays.views.SnackBarHelper;

@SuppressWarnings("deprecation")
public class BirthdayListActivity extends BaseActivity implements ItemOptionsFragment.ItemOptionsListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 6006; /// todo - refactor
    public static final int RC_SETTINGS = 5311;

    private ArrayList<Birthday> birthdaysList = new ArrayList<>();

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

    // Sign In
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

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
                        startActivity(new Intent(getApplicationContext(), PrivacyPolicyActivity.class));
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

        setUpGoogleSignInButton(headerView);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);

        } else if (requestCode == RC_SETTINGS) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String newTheme = prefs.getString(getString(R.string.pref_theme_key), "0");
            if (! newTheme.equals(themePref)) {
                recreate();
                themePref = newTheme;
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("SIGN IN", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            setNavHeaderUserState(NavHeaderState.LOGGED_OUT);
            Snackbar.make(floatingActionButton, "Error while logging in", Snackbar.LENGTH_SHORT).show(); // todo - translate
        }
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("Auth", "firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Auth", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Auth", "signInWithCredential", task.getException());
                            Toast.makeText(BirthdayListActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setUpGoogleSignInButton(View headerView) {
        GoogleSignInOptions gso = setUpGoogleSignInOptions();
        signInButton = (SignInButton) headerView.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser(); // todo - move to HERE
                if (user != null) {
                    // User is signed in
                    handleUserAuthenticated(user);
                    Log.d("Auth", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    setNavHeaderUserState(NavHeaderState.LOGGED_OUT);
                    Snackbar.make(floatingActionButton, "Signed OUT", Snackbar.LENGTH_SHORT).show();
                    Log.d("Auth", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void handleUserAuthenticated(FirebaseUser user) {
        textNavHeaderUserName.setText(user.getDisplayName());
        textNavHeaderEmail.setText(user.getEmail());
        Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).transform(new CircleTransform()).into(imageNavHeaderProfile);
        setNavHeaderUserState(NavHeaderState.SIGNED_IN);
        Snackbar.make(floatingActionButton, user.getDisplayName() + " signed in", Snackbar.LENGTH_SHORT).show();
        BirthdayReminder.getInstance().setUser(user);
        recyclerListFragment.loadBirthdays();
    }

    private GoogleSignInOptions setUpGoogleSignInOptions() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_SIGN_IN_KEY)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        return gso;
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(navigationView, "Sign in failed", Snackbar.LENGTH_SHORT).show();
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
        mAuth.addAuthStateListener(mAuthListener);
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
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

        if (id == R.id.action_sign_out) {
            signOutGoogle();
            return true;
        } else if (id == R.id.action_firebase) {
            boolean bool = Preferences.isUsingFirebase(this);
            Preferences.setIsUsingFirebase(this, ! bool);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOutGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        setNavHeaderUserState(NavHeaderState.LOGGED_OUT);
                        Snackbar.make(floatingActionButton, "signOutGoogle: " + status.getStatusMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    // todo - unlink Google
    private void revokeAccessGoogle() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Snackbar.make(floatingActionButton, "revokeAccessGoogle: " + status.getStatusMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
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
            setTheme(R.style.PinkTheme);
            pref = "1";
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
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
        AlarmsHelper.cancelAlarm(this, birthday.hashCode());
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

    // Birthdays array required to prevent importing duplicate contacts //todo - reduce to string names
    @Subscribe
    public void onBirthdaysLoaded(BirthdaysLoadedEvent event) {
        birthdaysList = event.getBirthdays();
    }

    enum NavHeaderState {
        LOGGED_OUT,
        SIGNED_IN
    }
}