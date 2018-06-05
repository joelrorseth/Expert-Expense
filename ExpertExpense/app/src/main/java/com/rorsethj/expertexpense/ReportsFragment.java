package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ReportsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        setRetainInstance(true);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = view.findViewById(R.id.reportsViewPager);

        // Create an adapter that knows which fragment should be shown on each page

        ReusableReportListAdapter adapter = new ReusableReportListAdapter(
                getContext(), getFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = view.findViewById(R.id.reportsTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}