package website.julianrosser.birthdays;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import website.julianrosser.birthdays.DialogFragments.AddEditFragment;

/**
 * Main view. Fragment which holds the RecyclerView.
 */
public class RecyclerListFragment extends android.support.v4.app.Fragment {

    // Reference to mAdapter
    public static RecyclerViewAdapter mAdapter;

    // Reference to recyclerView
    static RecyclerView recyclerView;

    // Reference to view which shows when list empty. todo - needed globally?
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

        // Floating Action Button
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open New Birthday Fragment
                MainActivity.getContext().showAddEditBirthdayFragment(AddEditFragment.MODE_ADD, 0);
            }
        });

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

        // Can use this to optimize performance as RecyclerView will NOT change size.
        recyclerView.setHasFixedSize(true);

        mAdapter = new RecyclerViewAdapter(MainActivity.birthdaysList);

        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        emptyView = null;
        recyclerView = null;
    }

    // Show or hide the 'no birthdays found' message depending on size of birthday Array
    public static void showEmptyMessageIfRequired() { // todo - remove then replace to prevent redraw?

        if (MainActivity.birthdaysList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open New Birthday Fragment
                    MainActivity.getContext().showAddEditBirthdayFragment(AddEditFragment.MODE_ADD, 0);
                }
            });

        } else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }
}