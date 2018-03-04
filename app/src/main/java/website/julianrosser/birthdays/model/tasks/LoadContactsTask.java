package website.julianrosser.birthdays.model.tasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import website.julianrosser.birthdays.model.Contact;
import website.julianrosser.birthdays.model.events.ContactsLoadedEvent;

public class LoadContactsTask extends AsyncTask<Void, Integer, ArrayList<Contact>> {

    private final Context mContext;
    private final ArrayList<String> birthdayNames;

    public LoadContactsTask(Context context, ArrayList<String> birthdayNames) {
        mContext = context;
        this.birthdayNames = birthdayNames;
    }

    @Override
    protected ArrayList<Contact> doInBackground(Void... params) {
        ArrayList<Contact> contacts = loadContacts();
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact b1, Contact b2) {
                return b1.getName().compareTo(b2.getName());
            }
        });
        return contacts;
    }

    private ArrayList<Contact> loadContacts() {
        ArrayList<Contact> contactsList = new ArrayList<>();
        ContentResolver cr = mContext.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                ContentResolver bd = mContext.getContentResolver();
                Cursor bdc = bd.query(android.provider.ContactsContract.Data.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Event.DATA},
                        android.provider.ContactsContract.Data.CONTACT_ID + " = " + id + " AND " + ContactsContract.Contacts.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.CommonDataKinds.Event.TYPE + " = " + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY, null, android.provider.ContactsContract.Data.DISPLAY_NAME);
                if (bdc != null && bdc.getCount() > 0) {
                    while (bdc.moveToNext()) {
                        String birthday = bdc.getString(0);
                        Contact con = new Contact(name, birthday, isContactAlreadyAdded(name));
                        contactsList.add(con);
                    }
                }
                if (bdc != null) {
                    bdc.close();
                }
            }
            cur.close();
        }
        return contactsList;
    }

    private boolean isContactAlreadyAdded(String name) {
        for (String birthday : birthdayNames) {
            if (birthday.equals(name)) {
                return true;
            }
        }
        return false;
    }

    protected void onPostExecute(ArrayList<Contact> result) {
        EventBus.getDefault().post(new ContactsLoadedEvent(result));
    }
}