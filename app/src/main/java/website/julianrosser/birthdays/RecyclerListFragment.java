package website.julianrosser.birthdays;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecyclerListFragment extends android.support.v4.app.Fragment {

    RecyclerViewAdapter adapter;
    private ArrayList<Birthday> birthdaysData;

    String TAG = getClass().getSimpleName();

    public RecyclerListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Set layout properties
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        // Get adapter and pass to RecyclerView
        adapter = new RecyclerViewAdapter(MainActivity.birthdaysList);
        recyclerView.setAdapter(adapter);

        return view;
    }


}

