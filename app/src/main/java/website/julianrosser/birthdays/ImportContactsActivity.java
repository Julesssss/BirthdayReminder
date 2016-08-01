package website.julianrosser.birthdays;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ImportContactsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ContactAdapter mContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show home button on toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));

        loadContacts();
    }

    private void loadContacts() {
        ArrayList<Contact> contactsList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                ContentResolver bd = getContentResolver();
                Cursor bdc = bd.query(android.provider.ContactsContract.Data.CONTENT_URI, new String[] { ContactsContract.CommonDataKinds.Event.DATA }, android.provider.ContactsContract.Data.CONTACT_ID+" = "+id+" AND "+ ContactsContract.Contacts.Data.MIMETYPE+" = '"+ ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE+"' AND "+ ContactsContract.CommonDataKinds.Event.TYPE+" = "+ ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY, null, android.provider.ContactsContract.Data.DISPLAY_NAME);
                if (bdc != null && bdc.getCount() > 0) {
                    while (bdc.moveToNext()) {
                        String birthday = bdc.getString(0);
                        Log.i(getClass().getSimpleName(), "Name: " + name + "  //  Birth: " + birthday);
                        // now "id" is the user's unique ID, "name" is his full name and "birthday" is the date and time of his birth
                        Contact con = new Contact(name, birthday);
                        contactsList.add(con);
                    }
                }
            }
        }
        mContactAdapter = new ContactAdapter(contactsList);
        mRecyclerView.setAdapter(mContactAdapter);
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
