package website.julianrosser.birthdays.fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import website.julianrosser.birthdays.BirthdayReminder;
import website.julianrosser.birthdays.Constants;
import website.julianrosser.birthdays.Preferences;
import website.julianrosser.birthdays.R;
import website.julianrosser.birthdays.adapter.BirthdayViewAdapter;
import website.julianrosser.birthdays.database.DatabaseHelper;
import website.julianrosser.birthdays.model.Birthday;
import website.julianrosser.birthdays.model.FirebaseBirthday;
import website.julianrosser.birthdays.model.events.BirthdaysLoadedEvent;

/**
 * Main view. Fragment which holds the RecyclerView.
 */
public class RecyclerListFragment extends android.support.v4.app.Fragment {

    // Reference to mAdapter
    private BirthdayViewAdapter mAdapter;

    // Reference to view which shows when list empty.
    private View emptyView;
    private ValueEventListener loadBirthdaysEventListener;
    private DatabaseReference databaseReference;

    // Required empty constructor
    public RecyclerListFragment() {

    }

    /* Use newInstance in case in the future we want to add construction parameters or initialisation here */
    public static RecyclerListFragment newInstance() {
        return new RecyclerListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Initialise important reference to the main view: RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Reference empty TextView
        emptyView = view.findViewById(R.id.empty_view);

        // hide drop shadow if running lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.drop_shadow).setVisibility(View.GONE);
        }

        // Set layout properties
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
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
        mAdapter = new BirthdayViewAdapter();
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (loadBirthdaysEventListener != null) {
            databaseReference.removeEventListener(loadBirthdaysEventListener);
            loadBirthdaysEventListener = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Preferences.isUsingFirebase(getActivity())) {
            loadBirthdays();
        }
    }

    public void loadBirthdays() {
        if (Preferences.isUsingFirebase(getActivity())) {
            setUpBirthdayListener();
        } else {
            try {
                loadJSONData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpBirthdayListener() {
        FirebaseUser user = BirthdayReminder.getInstance().getCurrentUser();
        if (null == user) {
            Log.i(DatabaseHelper.class.getSimpleName(), "User not loaded yet");
            return;
        }
        // load birthdays from FB
        databaseReference = BirthdayReminder.getInstance().getDatabaseReference().child(user.getUid()).child(Constants.TABLE_BIRTHDAYS);
        if (databaseReference == null) {
            Log.i(DatabaseHelper.class.getSimpleName(), "Database not loaded yet");
            return;
        }
        if (loadBirthdaysEventListener == null) {
            loadBirthdaysEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    ArrayList<Birthday> birthdays = new ArrayList<>();
                    for (DataSnapshot birthdaySnap : dataSnapshot.getChildren()) {
                        FirebaseBirthday firebaseBirthday = birthdaySnap.getValue(FirebaseBirthday.class);
                        Birthday birthday = Birthday.fromFB(firebaseBirthday);
                        birthdays.add(birthday);
                    }
                    EventBus.getDefault().post(new BirthdaysLoadedEvent(birthdays));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(BirthdayReminder.getInstance(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
        }
        databaseReference.addValueEventListener(loadBirthdaysEventListener);
    }

    private void loadJSONData() throws IOException { // todo - refactor
        ArrayList<Birthday> birthdays = new ArrayList<>();
        // Load birthdays
        BufferedReader reader = null;
        try {
            // Open and read the file into a StringBuilder
            InputStream in = getContext().openFileInput(Constants.FILENAME);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();

            // Build the array of birthdays from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                birthdays.add(new Birthday(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Ignore this one; it happens when starting fresh
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) reader.close();
        }
        mAdapter.setData(birthdays);
        showEmptyMessageIfRequired(birthdays);

    }

    @Subscribe
    public void onBirthdaysLoaded(BirthdaysLoadedEvent event) {
        mAdapter.setData(event.getBirthdays());
        showEmptyMessageIfRequired(event.getBirthdays());
    }

    // Show or hide the 'no birthdays found' message depending on size of birthday Array
    private void showEmptyMessageIfRequired(ArrayList<Birthday> birthdays) {
        if (birthdays.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    public BirthdayViewAdapter getAdapter() {
        return mAdapter;
    }
}
