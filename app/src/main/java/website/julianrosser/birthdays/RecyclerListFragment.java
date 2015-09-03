package website.julianrosser.birthdays;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecyclerListFragment extends android.support.v4.app.Fragment {

    private static List<Model> demoData;
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;

    public RecyclerListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.myList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        demoData = new ArrayList<Model>();
        char c = 'A';
        for (byte i = 0; i < 20; i++) {
            Model model = new Model();
            model.name = c++;
            model.age = (byte) (20 + i);
            demoData.add(model);
        }
        adapter = new RecyclerViewAdapter(demoData);
        recyclerView.setAdapter(adapter);
    }
}

