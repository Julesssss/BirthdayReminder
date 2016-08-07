package website.julianrosser.birthdays.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import website.julianrosser.birthdays.adapter.ContactAdapter;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.fragments.ImportContactFragment;

public class ImportContactsActivity extends AppCompatActivity {

    private ImportContactFragment recyclerListFragment;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_contacts);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show home button on toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create new RecyclerListFragment
        recyclerListFragment = ImportContactFragment.newInstance();
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
}
