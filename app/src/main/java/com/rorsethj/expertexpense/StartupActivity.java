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

public class StartupActivity extends AppCompatActivity implements AuthFragment.AuthInterface {


    @Override
    public void didAuthorizeLogin() {

        // TODO: Conditionally switch to setup only if new user, else go to MainActivity
        // Switch to Setup fragment
        switchToFragment(new SetupFragment());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        AuthFragment authFrag = new AuthFragment();
        authFrag.setAuthDelegate(this);
        switchToFragment(authFrag);
    }


    // Perform a FragmentTransaction to replace current hosted fragment with new one
    private void switchToFragment(Fragment destFragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerSetup, destFragment);
        transaction.commit();
    }
}
