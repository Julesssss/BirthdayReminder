package website.julianrosser.birthdays.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.fragments.ImportContactFragment;

public class ImportContactsActivity extends BaseActivity {

    public static String BIRTHDAYS_ARRAY_KEY = "BIRTHDAYS_ARRAY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show backToBirthdays button on toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ArrayList<String> birthdayNames = getIntent().getExtras().getStringArrayList(BIRTHDAYS_ARRAY_KEY);

        // Create new RecyclerListFragment
        ImportContactFragment recyclerListFragment = ImportContactFragment.newInstance();
        recyclerListFragment.setBirthdayNames(birthdayNames);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, recyclerListFragment)
                .commit();
        recyclerListFragment.setRetainInstance(true);
    }

    // Set Activity theme depending on user preference
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            backToBirthdays();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToBirthdays();
    }

    private void backToBirthdays() {
        finish();
    }
}
