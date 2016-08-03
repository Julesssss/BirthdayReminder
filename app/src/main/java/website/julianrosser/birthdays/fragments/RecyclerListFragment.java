package website.julianrosser.birthdays.fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import website.julianrosser.birthdays.adapter.BirthdayViewAdapter;
import website.julianrosser.birthdays.activities.MainActivity;
import website.julianrosser.birthdays.R;

/**
 * Main view. Fragment which holds the RecyclerView.
 */
public class RecyclerListFragment extends android.support.v4.app.Fragment {

    // Reference to mAdapter
    public static BirthdayViewAdapter mAdapter;

    // Reference to recyclerView
    public static RecyclerView recyclerView;

    // Reference to view which shows when list empty.
    static View emptyView;

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
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Reference empty TextView
        emptyView = view.findViewById(R.id.empty_view);

        /* Detect whether the 'no birthdays found' message should be displayed instead of Rec.view.
         Using empty and recycler references. */
        showEmptyMessageIfRequired();

        // hide drop shadow if running lollipop or higher
        if (Build.VERSION.SDK_INT >= 21) {
            view.findViewById(R.id.drop_shadow).setVisibility(View.GONE);
        }

        // Set layout properties
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.getAppContext());
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

        mAdapter = new BirthdayViewAdapter(MainActivity.birthdaysList);

        recyclerView.setAdapter(mAdapter);

        return view;
    }



    // Show or hide the 'no birthdays found' message depending on size of birthday Array
    public static void showEmptyMessageIfRequired() {
        if (MainActivity.birthdaysList.isEmpty()){
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }
}
