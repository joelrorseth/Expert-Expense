package com.rorsethj.expertexpense;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsBalanceFragment extends Fragment {

    private ListView listView;

    public ReportsBalanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.reusable_report_list, container, false);

        listView = (ListView) view.findViewById(R.id.reusableReportListView);

        // Load report options and their icons
        String[] reportOptions = getResources().getStringArray(R.array.reports_balance);
        int[] drawableIds = {R.drawable.ic_bar_chart};

        // Use this adapter on the list view
        SimpleIconTextRowAdapter adapter = new SimpleIconTextRowAdapter(getContext(), reportOptions, drawableIds);
        listView.setAdapter(adapter);

        return view;
    }
}
