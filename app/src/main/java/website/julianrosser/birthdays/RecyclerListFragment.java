package website.julianrosser.birthdays;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecyclerListFragment extends android.support.v4.app.Fragment {

    public static RecyclerViewAdapter adapter;

    static RecyclerView recyclerView;
    static TextView emptyText;

    String TAG = getClass().getSimpleName();

    public RecyclerListFragment() {
    }

    public static RecyclerListFragment newInstance() {
        return new RecyclerListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        emptyText = (TextView) view.findViewById(R.id.empty_view);

        //registerForContextMenu(recyclerView); / tod why not needed?


        emptyListViewVisibility();

        // Set layout properties
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        // Get adapter and pass to RecyclerView
        adapter = new RecyclerViewAdapter(MainActivity.birthdaysList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    /**
     * Hide/show the empty list TextView & RecyclerView depending on the birthdaylist Array
     */
    public static void emptyListViewVisibility() {

        if (MainActivity.birthdaysList.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }


    public static void newAdapter() {
        recyclerView.setAdapter(new RecyclerViewAdapter(MainActivity.birthdaysList));
        emptyListViewVisibility();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position;

        position = RecyclerViewAdapter.contextListPos;

        if (item.getTitle().equals(getString(R.string.context_edit))) {
            // Edit
            showEditDialog(position);


        } else if (item.getTitle().equals(getString(R.string.context_delete))) {
            // Delete
            //MainActivity.birthdaysList.remove(position);
            MainActivity.deleteFromArray(position);


        } else {
            Log.d(TAG, "CONTEXT TITLE NO MATCH: " + item.getTitle());
        }

        return super.onContextItemSelected(item);
    }

    public void showEditDialog(int positionInList) {
        // Get new Fragment
        AddEditBirthdayFragment dialog = AddEditBirthdayFragment.newInstance();

        Birthday birthdayToPass = MainActivity.birthdaysList.get(positionInList);

        if (birthdayToPass == null) {
            Log.d(TAG, "birthday returned null"); // todo replace with toast
            return;
        }

        Log.d(TAG, "LaunchEditFor: " + birthdayToPass.getName());

        // Create bundle for MODE_EDIT detection and to use current date, month value.
        Bundle bundle = new Bundle();
        bundle.putInt(AddEditBirthdayFragment.MODE_KEY, AddEditBirthdayFragment.MODE_EDIT);

        bundle.putInt(AddEditBirthdayFragment.DATE_KEY, birthdayToPass.getDate().getDate());
        bundle.putInt(AddEditBirthdayFragment.MONTH_KEY, birthdayToPass.getDate().getMonth());
        bundle.putInt(AddEditBirthdayFragment.POS_KEY, positionInList);
        bundle.putString(AddEditBirthdayFragment.NAME_KEY, birthdayToPass.getName());
        dialog.setArguments(bundle);

        dialog.show(MainActivity.getContext().getSupportFragmentManager(), "AddEditBirthdayFragment");
    }

}

