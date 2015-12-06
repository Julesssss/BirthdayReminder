package website.julianrosser.birthdays;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements AddEditBirthdayFragment.NoticeDialogListener {

    public static ArrayList<Birthday> birthdaysList = new ArrayList<Birthday>();
    final public String TAG = getClass().getSimpleName();

    static MainActivity mContext;

    /**
     * For easy access to MainActivity context from multiple Classes
     */
    public static MainActivity getContext () {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initilize context reference
        mContext = this;

        addTestBirthday();

        // Create and show RecyclerListFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new RecyclerListFragment())
                    .commit();
        }
    }

    // Method for adding test birthday
    public void addTestBirthday() {

        for (int x = 0; x < 5; x++) {
            Date f = new Date();
            Random r = new Random();
            f.setMonth(r.nextInt(12));
            f.setDate(r.nextInt(31) + 1);
            f.setYear(2015);
            String[] name_array = {"Peter", "Tammy", "Ron", "Liz", "Tom", "Gary", "Rachael", "Marie", "Buzz", "Tyler"};
            String name = name_array[r.nextInt(name_array.length)];
            boolean reminder = r.nextInt(3) != 1;

            Birthday b = new Birthday(name, f, reminder);
            birthdaysList.add(b);
        }

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
        if (id == R.id.action_settings) {
            return true;

        } else if (id == R.id.action_add) {
            showNoticeDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        AddEditBirthdayFragment dialog = new AddEditBirthdayFragment();

        // Create bundle for storing mode information
        Bundle bundle = new Bundle();
        bundle.putInt(AddEditBirthdayFragment.MODE_KEY, AddEditBirthdayFragment.MODE_ADD);
        dialog.setArguments(bundle);

        dialog.show(getSupportFragmentManager(), "AddEditBirthdayFragment");
    }

    @Override
    public void onDialogPositiveClick(AddEditBirthdayFragment dialog, String name, int day, int month) {
        Log.i(TAG, "pos click: " + name);

        Date birthdate = new Date();
        birthdate.setDate(day);
        birthdate.setMonth(month);
        birthdate.setYear(2015); // TODO - TEMP TEMP TEMP! use alternative for date, year is unneccesary

        // todo - Temporary workaround. Adapter should update view automatically when array changes
        Birthday newBirthday = new Birthday(name, birthdate, true);
        birthdaysList.add(newBirthday);
        RecyclerListFragment.newAdapter();
    }
}
