package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class OverviewCustomizationFragment extends Fragment
        implements OverviewCustomizationRecyclerAdapter.ItemClickListener {

    private OverviewCustomizationRecyclerAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_overview_customization, container, false);

        // Get setting names to use in adapter
        ArrayList<String> settings = new ArrayList<String>(Arrays.asList(
                getResources().getStringArray(R.array.overview_preferences)
        ));


        // Set up recycler view
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.overviewCustomizationRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));


        // Set the adapter for the RecyclerView we are linking from XML
        adapter = new OverviewCustomizationRecyclerAdapter(getContext(), settings);
        adapter.setClickListener(this);
        recycler.setAdapter(adapter);

        return view;
    }



    @Override
    public void onItemClick(View view, int position) {

        // TODO: Change / toggle setting at given row
        // TODO: Will selecting the row also toggle? else look into Switch listener
    }
}