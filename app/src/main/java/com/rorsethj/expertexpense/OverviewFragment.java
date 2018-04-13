package com.rorsethj.expertexpense;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import uk.co.markormesher.android_fab.FloatingActionButton;
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter;
import uk.co.markormesher.android_fab.SpeedDialMenuItem;


public class OverviewFragment extends Fragment
        implements MyAccountsRecyclerAdapter.ItemClickListener,
        RecentTransactionsRecyclerAdapter.ItemClickListener,
        UpcomingBillsRecyclerAdapter.ItemClickListener,
        AccountPopupFragment.AccountPopupInterface,
        TransactionPopupFragment.TransactionPopupInterface {


    // Define interface to containing Activity to respond to events taking place here
    public interface OverviewInterface {
        void didSelectCustomizationIcon();
        void didSelectAddAccountIcon(boolean isEdit, Account existingAccount, String accountID);
        void didSelectAddTransactionIcon(boolean isEdit, Transaction existingTrans, String transID);
        void didSelectAddBillIcon();
        void didRequestTransactionsFrag();
    }

    OverviewInterface parentDelegate;
    Database db;

    private TextView newBalanceTextView;
    private RecyclerView accountsRecyclerView;
    private RecyclerView transRecyclerView;
    private RecyclerView billsRecyclerView;

    private AccountPopupFragment accPopupFragment;
    private TransactionPopupFragment tranPopupFragment;

    private MyAccountsRecyclerAdapter myAccountsAdapter;
    private RecentTransactionsRecyclerAdapter recentTransactionsAdapter;
    private UpcomingBillsRecyclerAdapter upcomingBillsAdapter;

    private List<Account> currentAccounts = new ArrayList<>();
    private List<String> currentAccountIDs = new ArrayList<>();
    private List<Transaction> currentTransactions = new ArrayList<>();
    private List<String> currentTransactionIDs = new ArrayList<>();
    private int currentlySelectedAccountIndex = -1;
    private int currentlySelectedTransIndex = -1;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        db = Database.getCurrentUserDatabase();

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        // Conditionally load in elements into Overview screen based on preferences
        conditionallyLoadAllContent(view);

        // Configure Popup dialogs ahead of time
        accPopupFragment = new AccountPopupFragment();
        accPopupFragment.parentDelegate = this;

        tranPopupFragment = new TransactionPopupFragment();
        tranPopupFragment.parentDelegate = this;


        // Configure floating action button listener
        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.overviewAddButton);
        addButton.setButtonIconResource(R.drawable.ic_add);
        addButton.setButtonBackgroundColour(getResources().getColor(R.color.colorPrimaryDark, null));
        addButton.setSpeedDialMenuAdapter(new SpeedDialMenuAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @NotNull
            @Override
            public SpeedDialMenuItem getMenuItem(Context context, int i) {

                if (i == 0) {
                    return new SpeedDialMenuItem(context, R.drawable.ic_accounts, "Add Income");
                } else {
                    return new SpeedDialMenuItem(context, R.drawable.ic_money_sign, "Add Expense");
                }
            }

            @Override
            public boolean onMenuItemClick(int position) {

                // Ask parent delegate to begin a new transaction
                parentDelegate.didSelectAddTransactionIcon(false, null, null);
                return super.onMenuItemClick(position);
            }
        });

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

                refreshDBContent();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // MARK: Popup Logic and interface implementations
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


        String tag = view.getTag().toString();


        // Determine which View was clicked -- Several subviews implement onItemClick
        if (tag.equals(getResources().getString(R.string.tag_overview_accounts_view))) {

            // Update index corresponding to which Account was clicked, show popup
            currentlySelectedAccountIndex = position;
            accPopupFragment.show(ft, "dialog");

        } else if (tag.equals(getResources().getString(R.string.tag_overview_transactions_view))) {

            // Update index for selected Transaction, show popup
            currentlySelectedTransIndex = position;
            tranPopupFragment.show(ft, "dialog");

        } else if (tag.equals(getResources().getString(R.string.tag_overview_bills_view))) {

            // TODO: Transition directly to edit bill screen
        }
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

        String accountIDToBeDeleted = currentAccountIDs.get(currentlySelectedAccountIndex);
        db.deleteAccount(accountIDToBeDeleted, new Database.DBDeletionInterface() {
            @Override
            public void didSuccessfullyDelete(boolean success) {

                if (success) {
                    Toast.makeText(getContext(), "Account has been deleted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "An error occurred while deleting the account",
                            Toast.LENGTH_SHORT).show();
                }

                refreshDBContent();
            }
        });
    }

    @Override
    public void acDidSelectEditAccount() {

        // Get selected account
        Account accountToBeEdited = currentAccounts.get(currentlySelectedAccountIndex);
        String accountIDToBeEdited = currentAccountIDs.get(currentlySelectedAccountIndex);

        // Tell parent to show Add Account screen, but set up for editing
        accPopupFragment.dismiss();
        parentDelegate.didSelectAddAccountIcon(true, accountToBeEdited, accountIDToBeEdited);
    }

    @Override
    public void acDidSelectHide() {
        accPopupFragment.dismiss();
    }

    @Override
    public void acDidSelectTransfer() {
        accPopupFragment.dismiss();
    }



    // MARK: TransactionPopupFragment interface
    @Override
    public void trDidSelectEdit() {

        // Get selected account
        Transaction transToBeEdited = currentTransactions.get(currentlySelectedTransIndex);
        String transIDToBeEdited = currentTransactionIDs.get(currentlySelectedTransIndex);

        // Tell parent to show Add Account screen, but set up for editing
        tranPopupFragment.dismiss();
        parentDelegate.didSelectAddTransactionIcon(true, transToBeEdited, transIDToBeEdited);
    }

    @Override
    public void trDidSelectDelete() {
        tranPopupFragment.dismiss();

        String transIDToBeDeleted = currentTransactionIDs.get(currentlySelectedTransIndex);
        db.deleteTransaction(transIDToBeDeleted, new Database.DBDeletionInterface() {
            @Override
            public void didSuccessfullyDelete(boolean success) {

                if (success) {
                    Toast.makeText(getContext(), "Transaction has been deleted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "An error occurred while deleting the transaction",
                            Toast.LENGTH_SHORT).show();
                }

                refreshDBContent();
            }
        });
    }

    @Override
    public void trDidSelectCopy() {
        trDidSelectEdit();
    }

    @Override
    public void trDidSelectMove() {
        trDidSelectEdit();
    }

    @Override
    public void trDidSelectNewStatus() {
        trDidSelectEdit();
    }

    @Override
    public void trDidSelectSetProj() {
        trDidSelectEdit();
    }


    // MARK: Module loading
    // Conditionally display the following layout based on user preferences
    private void conditionallyLoadAllContent(View view) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Resources resources = getResources();

        // Conditionally load in elements into Overview screen based on preferences
        if (prefs.getBoolean(resources.getString(R.string.prefs_accounts_overview), true)) {
            displayAccounts(view);
        }

        if (prefs.getBoolean(resources.getString(R.string.prefs_latest_transactions), true)) {
            displayTransactions(view);
        }

        if (prefs.getBoolean(resources.getString(R.string.prefs_upcoming_bills), true)) {
            displayUpcomingBills(view);
        }

        if (prefs.getBoolean(resources.getString(R.string.prefs_high_spending), true)) {
            displayHighSpending(view);
        }

        if (prefs.getBoolean(resources.getString(R.string.prefs_expense_category), true)) {
            displayExpenseByCategory(view);
        }

        if (prefs.getBoolean(resources.getString(R.string.prefs_income_expense), true)) {
            displayIncomeVsExpense(view);
        }
    }

    private void displayAccounts(View view) {

        // Unhide the Accounts view
        view.findViewById(R.id.overviewAccountsLayout).setVisibility(LinearLayout.VISIBLE);
        newBalanceTextView = view.findViewById(R.id.newBalanceTextView);

        // Add references and set listeners to buttons inside this fragment
        view.findViewById(R.id.overviewMyAccountsAddButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        parentDelegate.didSelectAddAccountIcon(false, null, null);
                    }
                }
        );

        // Set up recycler views
        accountsRecyclerView = (RecyclerView) view.findViewById(R.id.myAccountsRecycler);
        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false));

        loadAccountsFromDB();

    }

    private void displayTransactions(View view) {

        // Unhide
        view.findViewById(R.id.overviewTransactionsLayout).setVisibility(LinearLayout.VISIBLE);

        view.findViewById(R.id.overviewTransactionsAddButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        parentDelegate.didSelectAddTransactionIcon(false, null, null);
                    }
                }
        );

        transRecyclerView = (RecyclerView) view.findViewById(R.id.recentTransactionsRecycler);
        transRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));

        loadTransactionsFromDB();
    }

    private void displayUpcomingBills(View view) {

        // Unhide
        view.findViewById(R.id.overviewBillsLayout).setVisibility(LinearLayout.VISIBLE);

        view.findViewById(R.id.overviewBillsAddButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        parentDelegate.didSelectAddBillIcon();
                    }
                }
        );

        billsRecyclerView = (RecyclerView) view.findViewById(R.id.upcomingBillsRecycler);
        billsRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false));

        loadBillsFromDB();
    }

    private void displayHighSpending(View view) {

    }

    private  void displayExpenseByCategory(View view) {

        // Unhide
        view.findViewById(R.id.overviewExpenseCatLayout).setVisibility(LinearLayout.VISIBLE);
    }

    private void displayIncomeVsExpense(View view) {

        // Unhide
        view.findViewById(R.id.overviewIncVsExpLayout).setVisibility(LinearLayout.VISIBLE);
    }


    // MARK: DB Content update only
    // Refresh the accounts, transactions, bills etc from the database and update views
    private void loadAccountsFromDB() {

        final OverviewFragment thisRef = this;

        // Asynchronously get accounts from DB, update UI when downloaded
        db.getUserAccounts(new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {

                currentAccounts = accounts;
                currentAccountIDs = accountIDs;

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
    }

    private void loadTransactionsFromDB() {

        final OverviewFragment thisRef = this;
        DateRange.DatePair dateRange = DateRange.getDatesForRange("Last 30 Days");
        long oldDate = dateRange.getOld();
        long newDate = dateRange.getNew();


        // Load RECENT transactions
        db.getTransactionsBetweenDates(oldDate, newDate, new Database.DBGetTransactionsInterface() {
            @Override
            public void didGet(List<Transaction> transactions, List<String> transactionIDs, Exception e) {

                currentTransactions = transactions;
                currentTransactionIDs = transactionIDs;

                recentTransactionsAdapter = new RecentTransactionsRecyclerAdapter(getContext(), transactions);
                recentTransactionsAdapter.setClickListener(thisRef);
                transRecyclerView.setAdapter(recentTransactionsAdapter);
            }
        });
    }

    private void loadBillsFromDB() {

        final OverviewFragment thisRef = this;

        // Load bills
        db.getBillsAfterDate((new Date()).getTime(), new Database.DBGetBillsInterface() {
            @Override
            public void didGet(List<Bill> bills, Exception e) {

                upcomingBillsAdapter = new UpcomingBillsRecyclerAdapter(getContext(), bills);
                upcomingBillsAdapter.setClickListener(thisRef);
                billsRecyclerView.setAdapter(upcomingBillsAdapter);
            }
        });
    }

    private void refreshDBContent() {
        loadAccountsFromDB();
        loadTransactionsFromDB();
        loadBillsFromDB();
    }
}