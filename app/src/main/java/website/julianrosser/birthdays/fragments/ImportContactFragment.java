package website.julianrosser.birthdays.fragments;


import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import website.julianrosser.birthdays.activities.BirthdayListActivity;
import website.julianrosser.birthdays.model.Contact;
import website.julianrosser.birthdays.adapter.ContactAdapter;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.model.events.ContactsLoadedEvent;

/**
 * Main view. Fragment which
 */
public class ImportContactFragment extends android.support.v4.app.Fragment {

    // Reference to mAdapter
    public static ContactAdapter mAdapter;

    // Reference to recyclerView
    public static RecyclerView recyclerView;

    // Reference to view which shows when list empty.
    static View emptyView;

    public ArrayList<Contact> contacts;
    private ProgressBar progressBar;
    private LoadContactsTask loadContactsTask;

    // Required empty constructor
    public ImportContactFragment() {
    }


    /* Use newInstance in case in the future we want to add construction parameters or initialisation here */
    public static ImportContactFragment newInstance() {
        return new ImportContactFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        loadContactsTask = new LoadContactsTask();
        loadContactsTask.execute();
    }

    @Override
    public void onStop() {
        if (loadContactsTask != null) {
            loadContactsTask.cancel(true);
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_import_contacts, container, false);

        // Initialise important reference to the main view: RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Reference empty TextView
        emptyView = view.findViewById(R.id.empty_view);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        // hide drop shadow if running lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.drop_shadow).setVisibility(View.GONE);
        }

        // Set layout properties
        LinearLayoutManager llm = new LinearLayoutManager(BirthdayListActivity.getAppContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        // Can use this to optimize performance as RecyclerView will NOT change size.
        recyclerView.setHasFixedSize(true);

        mAdapter = new ContactAdapter();
        recyclerView.setAdapter(mAdapter);
        if (contacts != null) {
            mAdapter.setData(contacts);
            showEmptyMessageIfRequired();
        }
        return view;
    }

    private ArrayList<Contact> loadContacts() {
        ArrayList<Contact> contactsList = new ArrayList<>();
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                ContentResolver bd = getActivity().getContentResolver();
                Cursor bdc = bd.query(android.provider.ContactsContract.Data.CONTENT_URI, new String[] { ContactsContract.CommonDataKinds.Event.DATA }, android.provider.ContactsContract.Data.CONTACT_ID+" = "+id+" AND "+ ContactsContract.Contacts.Data.MIMETYPE+" = '"+ ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE+"' AND "+ ContactsContract.CommonDataKinds.Event.TYPE+" = "+ ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY, null, android.provider.ContactsContract.Data.DISPLAY_NAME);
                if (bdc != null && bdc.getCount() > 0) {
                    while (bdc.moveToNext()) {
                        String birthday = bdc.getString(0);
                        Log.i(getClass().getSimpleName(), "Name: " + name + "  //  Birth: " + birthday);
                        // now "id" is the user's unique ID, "name" is his full name and "birthday" is the date and time of his birth
                        Contact con = new Contact(name, birthday);
                        contactsList.add(con);
                    }
                    bdc.close();
                }
            }
            cur.close();
        }
        return contactsList;
    }

    private void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        if (mAdapter != null) {
            mAdapter.setData(contacts);
            showEmptyMessageIfRequired();
            progressBar.setVisibility(View.GONE);
        }
    }

    /** Detect whether contacts were found, and display empty message if necessary. */
    public void showEmptyMessageIfRequired() {
        if (contacts.size() == 0){
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private class LoadContactsTask extends AsyncTask<Void, Integer, ArrayList<Contact>> {

        @Override
        protected ArrayList<Contact> doInBackground(Void... params) {
            contacts = loadContacts();
            return contacts;
        }

        protected void onPostExecute(ArrayList<Contact> result) {
            EventBus.getDefault().post(new ContactsLoadedEvent(result));
        }
    }

    @Subscribe
    public void onMessageEvent(ContactsLoadedEvent event) {
        setContacts(event.getContacts());
    }
}