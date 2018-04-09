package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AccountsFragment extends Fragment
        implements AccountsAccountsRecyclerAdapter.ItemClickListener {


    private AccountsAccountsRecyclerAdapter accountsAccountsAdapter;
    private RecyclerView accountsRecyclerView;
    private Database db;

    private TextView accountsNewBalanceTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        accountsNewBalanceTextView = view.findViewById(R.id.accountsNewBalanceTextView);


        // Set up recycler views
        accountsRecyclerView = (RecyclerView) view.findViewById(R.id.accountsAccountsRecycler);
        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));

        final AccountsFragment thisRef = this;

        // Get Accounts from DB
        db = Database.getCurrentUserDatabase();
        db.getUserAccounts(new Database.DBGetAccountsInterface() {

            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {

                accountsAccountsAdapter = new AccountsAccountsRecyclerAdapter(getContext(), accounts);
                accountsAccountsAdapter.setClickListener(thisRef);

                // Set adapters to recycler views
                accountsRecyclerView.setAdapter(accountsAccountsAdapter);

                // Update total balance label
                // Calculate total across all accounts to display
                double sum = 0.0;
                for (Account a: accounts) { sum += a.getBalance(); }
                accountsNewBalanceTextView.setText(
                        String.format(getResources().getString(R.string.net_balance_amount), sum)
                );
            }
        });


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