package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class OverviewFragment extends Fragment
        implements MyAccountsRecyclerAdapter.ItemClickListener,
        RecentTransactionsRecyclerAdapter.ItemClickListener,
        UpcomingBillsRecyclerAdapter.ItemClickListener{


    // Define interface to containing Activity to respond to events taking place here
    public interface OverviewInterface {
        void didSelectCustomizationIcon();
        void didSelectAddAccountIcon();
        void didSelectAddTransactionIcon();
        void didSelectAddBillIcon();
    }

    OverviewInterface parentDelegate;
    Database db;

    private TextView newBalanceTextView;
    private RecyclerView accountsRecyclerView;
    private RecyclerView transRecyclerView;
    private RecyclerView billsRecyclerView;

    private MyAccountsRecyclerAdapter myAccountsAdapter;
    private RecentTransactionsRecyclerAdapter recentTransactionsAdapter;
    private UpcomingBillsRecyclerAdapter upcomingBillsAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        db = Database.getCurrentUserDatabase();

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        newBalanceTextView = view.findViewById(R.id.newBalanceTextView);

        // Add references and set listeners to buttons inside this fragment
        view.findViewById(R.id.overviewMyAccountsAddButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        parentDelegate.didSelectAddAccountIcon();
                    }
                }
        );

        view.findViewById(R.id.overviewTransactionsAddButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        parentDelegate.didSelectAddTransactionIcon();
                    }
                }
        );

        view.findViewById(R.id.overviewBillsAddButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        parentDelegate.didSelectAddBillIcon();
                    }
                }
        );


        // Set up recycler views
        accountsRecyclerView = (RecyclerView) view.findViewById(R.id.myAccountsRecycler);
        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));

        transRecyclerView = (RecyclerView) view.findViewById(R.id.recentTransactionsRecycler);
        transRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));

        billsRecyclerView = (RecyclerView) view.findViewById(R.id.upcomingBillsRecycler);
        billsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));


        // Setup up adapters and populate the Overview screen with database content
        refreshUserOverviewContent();


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


    // Display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_overview_menu, menu);
    }

    // Handle menu item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Selecting the customize toolbar icon should prompt activity to push fragment
            case R.id.action_customize:

                parentDelegate.didSelectCustomizationIcon();
                return true;

            case R.id.action_refresh:

                refreshUserOverviewContent();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(View view, int position) {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = new GeneralPopupFragment();
        newFragment.show(ft, "dialog");


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


    // Refresh the accounts, transactions, bills etc from the database and update views
    private void refreshUserOverviewContent() {

        final OverviewFragment thisRef = this;

        // Asynchronously get accounts from DB, update UI when downloaded
        db.getUserAccounts(new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {

                // Calculate total across all accounts to display
                double sum = 0.0;
                for (Account a: accounts) { sum += a.getBalance(); }
                newBalanceTextView.setText(
                        String.format(getResources().getString(R.string.net_balance_amount), sum)
                );

                myAccountsAdapter = new MyAccountsRecyclerAdapter(getContext(), accounts);
                myAccountsAdapter.setClickListener(thisRef);
                accountsRecyclerView.setAdapter(myAccountsAdapter);
            }
        });

        // Load transactions
        db.getUserTransactions(new Database.DBGetTransactionsInterface() {
            @Override
            public void didGet(List<Transaction> transactions, Exception e) {

                recentTransactionsAdapter = new RecentTransactionsRecyclerAdapter(getContext(), transactions);
                recentTransactionsAdapter.setClickListener(thisRef);
                transRecyclerView.setAdapter(recentTransactionsAdapter);
            }
        });

        // Load bills
        db.getUserBills(new Database.DBGetBillsInterface() {
            @Override
            public void didGet(List<Bill> bills, Exception e) {

                upcomingBillsAdapter = new UpcomingBillsRecyclerAdapter(getContext(), bills);
                upcomingBillsAdapter.setClickListener(thisRef);
                billsRecyclerView.setAdapter(upcomingBillsAdapter);
            }
        });
    }
}