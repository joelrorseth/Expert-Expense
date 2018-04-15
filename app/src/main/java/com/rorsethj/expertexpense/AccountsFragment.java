package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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


public class AccountsFragment extends Fragment implements
        AccountPopupFragment.AccountPopupInterface {


    // Reuse interface defined in OverviewFragment
    public OverviewFragment.OverviewInterface parentDelegate;

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

    private AccountPopupFragment accPopupFragment;
    private Account currentlySelectedAccount = null;
    private String currentlySelectedAccountID = "";
    private int currentlySelectedAccountPosition = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        // Configure popup
        accPopupFragment = new AccountPopupFragment();
        accPopupFragment.parentDelegate = this;


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
                        parentDelegate.didSelectAddAccountIcon(false, null, null);
                    }
                }
        );

        return view;
    }


    // Load all accounts currency by currency
    private void loadAccountsForAllCurrencies(final View view) {

        db.getUserAccountsOfCurrency("CAD", new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {
                accountsCADAdapter = new AccountsAccountsRecyclerAdapter(getContext(), accounts);
                populateRecycler(accountsCADAdapter, accountsCADRecyclerView,
                        accountsCADNewBalanceTextView, cadLayout, accounts, accountIDs);
            }
        });

        db.getUserAccountsOfCurrency("USD", new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {
                accountsUSDAdapter = new AccountsAccountsRecyclerAdapter(getContext(), accounts);
                populateRecycler(accountsUSDAdapter, accountsUSDRecyclerView,
                        accountsUSDNewBalanceTextView, usdLayout, accounts, accountIDs);
            }
        });

        db.getUserAccountsOfCurrency("GBP", new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {
                accountsGBPAdapter = new AccountsAccountsRecyclerAdapter(getContext(), accounts);
                populateRecycler(accountsGBPAdapter, accountsGBPRecyclerView,
                        accountsGBPNewBalanceTextView, gbpLayout, accounts, accountIDs);
            }
        });

        db.getUserAccountsOfCurrency("EUR", new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {
                accountsEURAdapter = new AccountsAccountsRecyclerAdapter(getContext(), accounts);
                populateRecycler(accountsEURAdapter, accountsEURRecyclerView,
                        accountsEURNewBalanceTextView, eurLayout, accounts, accountIDs);
            }
        });
    }


    // Populate a recycler
    private void populateRecycler(AccountsAccountsRecyclerAdapter adapter, RecyclerView recycler,
                                  TextView textView, LinearLayout layout, final List<Account> accounts,
                                  final List<String> accountIDs) {

        if (accounts.isEmpty()) {
            layout.setVisibility(View.GONE);
            return;

        } else {
            layout.setVisibility(View.VISIBLE);
        }

        adapter.setClickListener(new AccountsAccountsRecyclerAdapter.ItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) { ft.remove(prev); }
                ft.addToBackStack(null);

                // Prompt popup menu and potentially edit the selected account
                currentlySelectedAccount = accounts.get(position);
                currentlySelectedAccountID = accountIDs.get(position);
                currentlySelectedAccountPosition = position;
                accPopupFragment.show(ft, "dialog");
            }
        });

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



    // MARK: AccountPopupFragment interface
    @Override
    public void acDidSelectShowTransactions() {
        accPopupFragment.dismiss();
        parentDelegate.didRequestTransactionsFrag();
    }

    @Override
    public void acDidSelectAddTransaction() {
        accPopupFragment.dismiss();
        parentDelegate.didSelectAddTransactionIcon(false, null, null);
    }

    @Override
    public void acDidSelectDeleteAccount() {
        accPopupFragment.dismiss();

        db.deleteAccount(currentlySelectedAccountID, new Database.DBDeletionInterface() {
            @Override
            public void didSuccessfullyDelete(boolean success) {

                if (success) {
                    Toast.makeText(getContext(), "Account has been deleted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "An error occurred while deleting the account",
                            Toast.LENGTH_SHORT).show();
                }

                // Refresh
                loadAccountsForAllCurrencies(getView());
            }
        });
    }

    @Override
    public void acDidSelectEditAccount() {

        // Tell parent to show Add Account screen, but set up for editing
        accPopupFragment.dismiss();
        parentDelegate.didSelectAddAccountIcon(true,
                currentlySelectedAccount, currentlySelectedAccountID);
    }

    @Override
    public void acDidSelectHide() {

        switch (currentlySelectedAccount.getCurrency()) {
            case "CAD":
                accountsCADAdapter.removeItem(currentlySelectedAccountPosition);
                break;
            case "USD":
                accountsUSDAdapter.removeItem(currentlySelectedAccountPosition);
                break;
            case "GBP":
                accountsGBPAdapter.removeItem(currentlySelectedAccountPosition);
                break;
            case "EUR":
                accountsEURAdapter.removeItem(currentlySelectedAccountPosition);
                break;
        }
        accPopupFragment.dismiss();
    }

    @Override
    public void acDidSelectTransfer() {
        accPopupFragment.dismiss();
    }
}