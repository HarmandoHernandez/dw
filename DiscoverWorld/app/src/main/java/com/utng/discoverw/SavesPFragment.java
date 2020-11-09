package com.utng.discoverw;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A fragment representing a list of Items.
 */
public class SavesPFragment extends Fragment {

    List<SavesP> SavesPList; //Lista de SavesP
    SavesPRecyclerViewAdapter adapterSavesP; //Adaptador

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SavesPFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SavesPFragment newInstance(int columnCount) {
        SavesPFragment fragment = new SavesPFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savesp_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            SavesPList = new ArrayList<>();
            SavesPList.add(new SavesP("Celebration",
                    "https://digitalsevilla.com/wp-content/uploads/2019/03/celebraci%C3%B3n-de-eventos.jpg",
                    3.0f,
                    "Celebrate who you are in your deepest heart. Love your self and the world will love you."));
            SavesPList.add(new SavesP("Party",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQK4WEZ21bnJ1v60mpxn47IBJRtTmG0lQ4edQ&usqp=CAU",
                    4.2f,
                    "You gotta have life your way."));
            SavesPList.add(new SavesP("Exercise",
                    "https://static01.nyt.com/images/2020/03/10/well/physed-immune1/physed-immune1-mobileMasterAt3x.jpg",
                    2.2f,
                    "Whenever I feel the need to exercise, I like down until it goes away."));
            SavesPList.add(new SavesP("Nature",
                    "https://assets.unenvironment.org/styles/article_billboard_image/s3/2020-05/nature-3294681_1920%20%281%29.jpg?null&amp;h=ebad6883&amp;itok=iV1MUd_a",
                    4.6f,
                    "In every walk in with nature on receives for more tha he seeks."));

            adapterSavesP = new SavesPRecyclerViewAdapter(getActivity(), SavesPList);
            recyclerView.setAdapter(adapterSavesP);
        }
        return view;
    }
}