package website.julianrosser.birthdays;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

    static Context mContext;

    String TAG = getClass().getSimpleName();

    public RecyclerListFragment() {
    }

    public static RecyclerListFragment newFragment(Context context) {

        mContext = context;

        return new RecyclerListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        emptyText = (TextView) view.findViewById(R.id.empty_view);

        emptyListViewVisibility();

        // Set layout properties
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
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
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }

    public static void newAdapter() {
        recyclerView.setAdapter(new RecyclerViewAdapter(MainActivity.birthdaysList));
        emptyListViewVisibility();
    }

}

