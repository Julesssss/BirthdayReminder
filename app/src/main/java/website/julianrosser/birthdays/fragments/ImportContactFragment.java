package website.julianrosser.birthdays.fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.adapter.ContactAdapter;
import website.julianrosser.birthdays.model.Contact;
import website.julianrosser.birthdays.model.events.ContactsLoadedEvent;
import website.julianrosser.birthdays.model.tasks.LoadContactsTask;

/**
 * Fragment which displays contacts and enables users to import
 */
public class ImportContactFragment extends android.support.v4.app.Fragment {

    // Views
    private ContactAdapter mAdapter;
    private View emptyView;
    private ProgressBar progressBar;

    // Data
    private ArrayList<Contact> contacts;
    private ArrayList<String> birthdayNames;

    // Processes
    private LoadContactsTask loadContactsTask;

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
        loadContactsTask = new LoadContactsTask(getActivity(), birthdayNames);
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

        // Initialise view references
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.empty_view);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        // hide drop shadow if running lollipop or higher // todo - refactor to base
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.drop_shadow).setVisibility(View.GONE);
        }

        // Setup Contacts RecyclerView
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
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

    private void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        if (mAdapter != null) {
            mAdapter.setData(contacts);
            showEmptyMessageIfRequired();
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Detect whether contacts were found, and display empty message if necessary.
     */
    public void showEmptyMessageIfRequired() {
        if (contacts.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onMessageEvent(ContactsLoadedEvent event) {
        setContacts(event.getContacts());
    }

    // todo - why does this require it's own method? use constructor?!!
    public void setBirthdayNames(ArrayList<String> birthdayNames) {
        this.birthdayNames = birthdayNames;
    }
}