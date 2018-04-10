package com.rorsethj.expertexpense;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class ReportsExpenseFragment extends Fragment {

    private ListView listView ;
    private String[] reportOptions;

    public ReportsExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.reusable_report_list, container, false);

        listView = (ListView) view.findViewById(R.id.reusableReportListView);

        // Load report options and their icons
        reportOptions = getResources().getStringArray(R.array.reports_expense);
        int[] drawableIds = {R.drawable.ic_pie_chart, R.drawable.ic_bar_chart, R.drawable.ic_bar_chart};

        // Use this adapter on the list view
        SimpleIconTextRowAdapter adapter = new SimpleIconTextRowAdapter(getContext(), reportOptions, drawableIds);
        listView.setAdapter(adapter);


        // Set up listener to trigger segue, passing the name of selected option
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ReportChartFragment frag = new ReportChartFragment();
                frag.chartType = reportOptions[i];

                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragmentContainer, frag)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
