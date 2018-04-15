package com.rorsethj.expertexpense;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements OverviewFragment.OverviewInterface,
        TransactionsFragment.TransactionsInterface {

    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Start app on the Overview fragment
        OverviewFragment frag = new OverviewFragment();
        frag.parentDelegate = MainActivity.this;
        replaceCurrentFragment(frag);

        final MainActivity thisRef = this;

        // Assign a navigation item selected listener to the NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        // Select menu item and leave selected
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        // Switch to the selected / desired fragment
                        switch (menuItem.getItemId()) {

                            case R.id.nav_overview:
                                OverviewFragment frag = new OverviewFragment();
                                frag.parentDelegate = thisRef;
                                replaceCurrentFragment(frag);
                                break;

                            case R.id.nav_accounts:
                                AccountsFragment accFrag = new AccountsFragment();
                                accFrag.parentDelegate = thisRef;
                                replaceCurrentFragment(accFrag);
                                break;

                            case R.id.nav_transactions:
                                TransactionsFragment transFrag = new TransactionsFragment();
                                transFrag.parentDelegate = thisRef;
                                replaceCurrentFragment(transFrag);
                                break;

                            case R.id.nav_reports:
                                replaceCurrentFragment(new ReportsFragment());
                                break;

                        }

                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // MARK: OverviewFragment interface implementation
    @Override
    public void didSelectCustomizationIcon() {
        pushFragment(new OverviewCustomizationFragment());
    }

    @Override
    public void didSelectAddAccountIcon(boolean isEdit, Account existingAccount, String accountID) {
        AddNewAccountFragment f = new AddNewAccountFragment();

        // If this is an edit, setup the fragment for the edit process instead
        if (isEdit) {
            f.populateAccountBeingEdited(accountID, existingAccount);
        }

        pushFragment(f);
    }

    @Override
    public void didSelectAddTransactionIcon(boolean isEdit, Transaction existingTrans, String transID) {
        AddNewTransactionFragment f = new AddNewTransactionFragment();

        // If this is an edit, setup the fragment for the edit process instead
        if (isEdit) {
            f.populateTransactionBeingEdited(transID, existingTrans);
        }

        pushFragment(f);
    }

    @Override
    public void didSelectAddBillIcon() {
        pushFragment(new AddNewBillFragment());
    }

    @Override
    public void didRequestTransactionsFrag() {
        TransactionsFragment transFrag = new TransactionsFragment();
        transFrag.parentDelegate = this;
        replaceCurrentFragment(transFrag);

    }


    @Override
    public void didSelectAddTransaction() {
        pushFragment(new AddNewTransactionFragment());
    }



    // MARK: Utility
    // Perform a FragmentTransaction to replace current hosted fragment with new one
    private void pushFragment(Fragment destFragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, destFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void replaceCurrentFragment(Fragment destFragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, destFragment);
        transaction.commit();
    }
}
