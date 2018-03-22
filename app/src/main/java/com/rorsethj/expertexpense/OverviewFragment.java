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


public class OverviewFragment extends Fragment
        implements MyAccountsRecyclerAdapter.ItemClickListener,
        RecentTransactionsRecyclerAdapter.ItemClickListener,
        UpcomingBillsRecyclerAdapter.ItemClickListener{


    private MyAccountsRecyclerAdapter myAccountsAdapter;
    private RecentTransactionsRecyclerAdapter recentTransactionsAdapter;
    private UpcomingBillsRecyclerAdapter upcomingBillsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);


        // TODO, just temporary
        // Populate data for recycler
        ArrayList<String> tempAccounts = new ArrayList<>();
        tempAccounts.add("Cash");
        tempAccounts.add("Bank");
        tempAccounts.add("Offshore");

        ArrayList<String> tempTrans = new ArrayList<>();
        tempTrans.add("Groceries");
        tempTrans.add("Eat out");

        ArrayList<String> tempBills = new ArrayList<>();
        tempBills.add("Gas Bill");
        tempBills.add("Hydro Bill");


        // Set up recycler views
        RecyclerView accountsRecyclerView = (RecyclerView) view.findViewById(R.id.myAccountsRecycler);
        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));


        RecyclerView transRecyclerView = (RecyclerView) view.findViewById(R.id.recentTransactionsRecycler);
        transRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));

        RecyclerView billsRecyclerView = (RecyclerView) view.findViewById(R.id.upcomingBillsRecycler);
        billsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));


        myAccountsAdapter = new MyAccountsRecyclerAdapter(getContext(), tempAccounts);
        recentTransactionsAdapter = new RecentTransactionsRecyclerAdapter(getContext(), tempTrans);
        upcomingBillsAdapter = new UpcomingBillsRecyclerAdapter(getContext(), tempBills);

        myAccountsAdapter.setClickListener(this);
        recentTransactionsAdapter.setClickListener(this);
        upcomingBillsAdapter.setClickListener(this);

        // Set adapters to recycler views
        accountsRecyclerView.setAdapter(myAccountsAdapter);
        transRecyclerView.setAdapter(recentTransactionsAdapter);
        billsRecyclerView.setAdapter(upcomingBillsAdapter);




        // Configure floating action button listener
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.overviewAddButton);
        addButton.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // TODO
                    }
                }
        );

        return view;
    }



    @Override
    public void onItemClick(View view, int position) {

        String it = "";
        String tag = view.getTag().toString();

        // TODO: Make transition to fragment
        // Determine which View was clicked -- Several subviews implement onItemClick
        if (tag.equals(getResources().getString(R.string.tag_overview_accounts_view))) {

            // TODO: Handle account click
            it = "accounts";

        } else if (tag.equals(getResources().getString(R.string.tag_overview_transactions_view))) {

            // TODO: Handle transaction click
            it = "trans";

        } else if (tag.equals(getResources().getString(R.string.tag_overview_bills_view))) {

            it = "bill";
        }

        Toast.makeText(getContext(), "You clicked " +
                myAccountsAdapter.getItem(position) + " on " + it + " position " +
                position, Toast.LENGTH_SHORT).show();
    }
}