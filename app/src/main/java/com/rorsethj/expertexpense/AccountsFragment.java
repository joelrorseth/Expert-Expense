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


public class AccountsFragment extends Fragment
        implements AccountsAccountsRecyclerAdapter.ItemClickListener {


    private AccountsAccountsRecyclerAdapter accountsAccountsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);


        // TODO, just temporary
        // Populate data for recycler
        ArrayList<String> tempAccounts = new ArrayList<>();
        tempAccounts.add("Cash");
        tempAccounts.add("Bank");
        tempAccounts.add("Offshore");


        // Set up recycler views
        RecyclerView accountsRecyclerView = (RecyclerView) view.findViewById(R.id.accountsAccountsRecycler);
        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));


        accountsAccountsAdapter = new AccountsAccountsRecyclerAdapter(getContext(), tempAccounts);

        accountsAccountsAdapter.setClickListener(this);

        // Set adapters to recycler views
        accountsRecyclerView.setAdapter(accountsAccountsAdapter);




        // Configure floating action button listener
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.accountsAddButton);
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

    }
}