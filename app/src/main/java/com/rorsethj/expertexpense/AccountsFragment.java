package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AccountsFragment extends Fragment
        implements AccountsAccountsRecyclerAdapter.ItemClickListener {


    public interface AccountsInterface {
        void didSelectAddAccount();
    }

    public AccountsInterface parentDelegate;

    private AccountsAccountsRecyclerAdapter accountsCADAdapter;
    private AccountsAccountsRecyclerAdapter accountsUSDAdapter;
    private AccountsAccountsRecyclerAdapter accountsGBPAdapter;
    private AccountsAccountsRecyclerAdapter accountsEURAdapter;

    private Database db;

    private RecyclerView accountsCADRecyclerView;
    private RecyclerView accountsUSDRecyclerView;
    private RecyclerView accountsGBPRecyclerView;
    private RecyclerView accountsEURRecyclerView;

    private TextView accountsCADNewBalanceTextView;
    private TextView accountsUSDNewBalanceTextView;
    private TextView accountsGBPNewBalanceTextView;
    private TextView accountsEURNewBalanceTextView;

    private LinearLayout cadLayout;
    private LinearLayout usdLayout;
    private LinearLayout gbpLayout;
    private LinearLayout eurLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        cadLayout = view.findViewById(R.id.accountsCADLinearLayout);
        usdLayout = view.findViewById(R.id.accountsUSDLinearLayout);
        gbpLayout = view.findViewById(R.id.accountsGBPLinearLayout);
        eurLayout = view.findViewById(R.id.accountsEURLinearLayout);

        accountsCADNewBalanceTextView = view.findViewById(R.id.accountsCADNewBalanceTextView);
        accountsUSDNewBalanceTextView = view.findViewById(R.id.accountsUSDNewBalanceTextView);
        accountsGBPNewBalanceTextView = view.findViewById(R.id.accountsGBPNewBalanceTextView);
        accountsEURNewBalanceTextView = view.findViewById(R.id.accountsEURNewBalanceTextView);


        // Set up recycler views
        accountsCADRecyclerView = (RecyclerView) view.findViewById(R.id.accountsCADAccountsRecycler);
        accountsUSDRecyclerView = (RecyclerView) view.findViewById(R.id.accountsUSDAccountsRecycler);
        accountsGBPRecyclerView = (RecyclerView) view.findViewById(R.id.accountsGBPAccountsRecycler);
        accountsEURRecyclerView = (RecyclerView) view.findViewById(R.id.accountsEURAccountsRecycler);


        accountsCADRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));
        accountsUSDRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));
        accountsGBPRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));
        accountsEURRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));

        final AccountsFragment thisRef = this;

        // Get Accounts from DB
        db = Database.getCurrentUserDatabase();
        loadAccountsForAllCurrencies(view);

        // Configure floating action button listener
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.accountsAddButton);
        addButton.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        parentDelegate.didSelectAddAccount();
                    }
                }
        );

        return view;
    }


    private void loadAccountsForAllCurrencies(final View view) {

        db.getUserAccountsOfCurrency("CAD", new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {
                populateRecycler(accountsCADAdapter, accountsCADRecyclerView,
                        accountsCADNewBalanceTextView, cadLayout, accounts);
            }
        });

        db.getUserAccountsOfCurrency("USD", new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {
                populateRecycler(accountsUSDAdapter, accountsUSDRecyclerView,
                        accountsUSDNewBalanceTextView, usdLayout, accounts);
            }
        });

        db.getUserAccountsOfCurrency("GBP", new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {
                populateRecycler(accountsGBPAdapter, accountsGBPRecyclerView,
                        accountsGBPNewBalanceTextView, gbpLayout, accounts);
            }
        });

        db.getUserAccountsOfCurrency("EUR", new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {
                populateRecycler(accountsEURAdapter, accountsEURRecyclerView,
                        accountsEURNewBalanceTextView, eurLayout, accounts);
            }
        });
    }


    // Populate a recycler
    private void populateRecycler(AccountsAccountsRecyclerAdapter adapter, RecyclerView recycler,
                                  TextView textView, LinearLayout layout, List<Account> accounts) {

        if (accounts.isEmpty()) {
            layout.setVisibility(View.GONE);
            return;
        }

        adapter = new AccountsAccountsRecyclerAdapter(getContext(), accounts);
        adapter.setClickListener(this);

        // Set adapters to recycler views
        recycler.setAdapter(adapter);

        // Update total balance label
        // Calculate total across all accounts to display
        double sum = 0.0;
        for (Account a: accounts) { sum += a.getBalance(); }
        textView.setText(
                String.format(getResources().getString(R.string.net_balance_amount), sum)
        );
    }



    @Override
    public void onItemClick(View view, int position) {
    }
}