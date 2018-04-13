package com.rorsethj.expertexpense;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TransactionsFragment extends Fragment implements
        TransactionsTransactionsRecyclerAdapter.ItemClickListener {

    public interface TransactionsInterface {
        void didSelectAddTransaction();
    }

    public TransactionsInterface parentDelegate;


    private Set<String> selectedAccountNames;
    private Map<String, String> accountIDToNameLookup;
    private List<String> allAccountNames;
    private List<Account> allAccounts;
    private List<String> allPeriodNames;
    private String currentlySelectedPeriod;
    private Database db;
    private AlertDialog dialog;

    private TransactionsTransactionsRecyclerAdapter transAdapter;
    private RecyclerView transactionsRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        db = Database.getCurrentUserDatabase();
        selectedAccountNames = new HashSet<>();
        accountIDToNameLookup = new HashMap<>();
        allAccounts = new ArrayList<>();
        allAccountNames = new ArrayList<>();

        allPeriodNames = new ArrayList<String>(
                Arrays.asList( getResources().getStringArray(R.array.time_periods)) );
        currentlySelectedPeriod = allPeriodNames.get(0);



        // Set up recycler views
        transactionsRecyclerView = (RecyclerView) view.findViewById(R.id.transactionsTransactionsRecycler);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));



        // Retrieve all accounts from the DB
        db.getUserAccounts(new Database.DBGetAccountsInterface() {

            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {

                allAccounts.clear();
                allAccountNames.clear();
                int i = 0;

                // Add accounts to all accounts, all names, and all selected names initially
                allAccounts = accounts;
                for (Account a: accounts) {
                    allAccountNames.add(a.getAccountName());
                    selectedAccountNames.add(a.getAccountName());
                    accountIDToNameLookup.put(accountIDs.get(i++), a.getAccountName());
                }

                setupSelectAccountsDialog(allAccountNames);

                // Populate
                populateTransactions();
            }
        });


        // Set touch handler for accounts filter spinner, prompting dialog to choose many
        Spinner accountsSpinner = ((Spinner) view.findViewById(R.id.transactionsAccountsSpinner));
        accountsSpinner.setClickable(false);
        accountsSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (dialog != null) { dialog.show(); }
                return false;
            }
        });

        ((Spinner) view.findViewById(R.id.transactionsPeriodSpinner))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentlySelectedPeriod = allPeriodNames.get(i);
                populateTransactions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });



    // Configure floating action button listener
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.transactionsAddButton);
        addButton.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        parentDelegate.didSelectAddTransaction();
                    }
                }
        );

        return view;
    }


    // MARK: Interface implementation
    @Override
    public void onItemClick(View view, int position) {

    }



    // MARK: DB Interaction
    private void setupSelectAccountsDialog(final List<String> accountNames) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Accounts to Include");

        // Use the account names as items to select in the dialog
        CharSequence items[] = accountNames.toArray(new CharSequence[accountNames.size()]);
        boolean[] isInitiallyChecked = new boolean[accountNames.size()];
        Arrays.fill(isInitiallyChecked, true);

        builder.setMultiChoiceItems(items, isInitiallyChecked,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {

                        if (isChecked) {
                            selectedAccountNames.add(accountNames.get(indexSelected));

                        } else if (selectedAccountNames.contains(accountNames.get(indexSelected))) {
                            selectedAccountNames.remove(accountNames.get(indexSelected));
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // User confirmed new selection of accounts, repopulate transactions
                        populateTransactions();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {}
                });

        dialog = builder.create();
    }



    // Populate the transactions in the fragment dynamically
    private void populateTransactions() {

        Date nowDate = new Date();

        DateRange.DatePair dateRange = DateRange.getDatesForRange(currentlySelectedPeriod);
        long oldDate = dateRange.getOld();
        long newDate = dateRange.getNew();

        final TransactionsFragment thisRef = this;

        // Get the transactions that happened between the two dates established
        db.getTransactionsBetweenDates(oldDate, newDate, new Database.DBGetTransactionsInterface() {
            @Override
            public void didGet(List<Transaction> transactions, List<String> transactionIDs, Exception e) {

                // Populate the transaction list

                ArrayList<Transaction> filteredTransactions = new ArrayList<>();
                for (Transaction t: transactions) {

                    // Get the account name, given the account id version of the name stored in trans
                    String transAccountName = accountIDToNameLookup.get(t.getAccount());
                    if (selectedAccountNames.contains(transAccountName)) {
                        filteredTransactions.add(t);
                    }
                }


                transAdapter = new TransactionsTransactionsRecyclerAdapter(
                        getContext(), filteredTransactions);
                transAdapter.setClickListener(thisRef);

                // Set adapters to recycler views
                transactionsRecyclerView.setAdapter(transAdapter);

                // TODO: Show sum somewhere?
            }
        });
    }
}