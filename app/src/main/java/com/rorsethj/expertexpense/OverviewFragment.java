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
        RecentTransactionsRecyclerAdapter.ItemClickListener {


    private MyAccountsRecyclerAdapter myAccountsAdapter;
    private RecentTransactionsRecyclerAdapter recentTransactionsAdapter;


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
        tempTrans.add("Hydro Bill");


        // Set up recycler views
        RecyclerView accountsRecyclerView = (RecyclerView) view.findViewById(R.id.myAccountsRecycler);
        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));


        RecyclerView transRecyclerView = (RecyclerView) view.findViewById(R.id.recentTransactionsRecycler);
        transRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));


        myAccountsAdapter = new MyAccountsRecyclerAdapter(getContext(), tempAccounts);
        recentTransactionsAdapter = new RecentTransactionsRecyclerAdapter(getContext(), tempTrans);

        myAccountsAdapter.setClickListener(this);
        recentTransactionsAdapter.setClickListener(this);

        // Set adapters to recycler views
        accountsRecyclerView.setAdapter(myAccountsAdapter);
        transRecyclerView.setAdapter(recentTransactionsAdapter);





        // Configure floating action button listener
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.addButton);
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
        Toast.makeText(getContext(), "You clicked " +
                myAccountsAdapter.getItem(position) + " on item position " +
                position, Toast.LENGTH_SHORT).show();
    }
}