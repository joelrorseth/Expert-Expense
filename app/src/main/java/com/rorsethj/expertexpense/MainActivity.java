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
        implements OverviewFragment.OverviewInterface {

    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Start app on the Overview fragment
        OverviewFragment frag = new OverviewFragment();
        frag.parentDelegate = MainActivity.this;
        switchToFragment(frag);


        // Assign a navigation item selected listener to the NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
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
                                frag.parentDelegate = MainActivity.this;
                                switchToFragment(frag);
                                break;

                            case R.id.nav_accounts:
                                switchToFragment(new AccountsFragment());
                                break;

                            case R.id.nav_transactions:
                                switchToFragment(new TransactionsFragment());
                                break;

                            case R.id.nav_reports:
                                switchToFragment(new ReportsFragment());
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


    // OverviewFragment interface implementation
    @Override
    public void didSelectCustomizationIcon() {

        // Customization icon should push OverviewCustomizationFragment on top of OverviewFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, new OverviewCustomizationFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void didSelectAddAccountIcon() {
        switchToFragment(new AddNewAccountFragment());
    }

    @Override
    public void didSelectAddBillIcon() {
        switchToFragment(new AddNewBillFragment());
    }


    // Perform a FragmentTransaction to replace current hosted fragment with new one
    private void switchToFragment(Fragment destFragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, destFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
