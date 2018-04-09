package com.rorsethj.expertexpense;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class ReportsExpenseFragment extends Fragment {

    private ListView listView ;

    public ReportsExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.reusable_report_list, container, false);

        listView = (ListView) view.findViewById(R.id.reusableReportListView);
        
        String[] textString = {"Item1", "Item2", "Item3", "Item4"};
        int[] drawableIds = {R.drawable.ic_add, R.drawable.ic_book, R.drawable.ic_accounts, R.drawable.ic_edit};

        SimpleIconTextRowAdapter adapter = new SimpleIconTextRowAdapter(getContext(),  textString, drawableIds);

        listView.setAdapter(adapter);


        return view;
    }

}
