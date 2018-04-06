package com.rorsethj.expertexpense;

import android.content.Intent;
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
import android.widget.Toast;

public class StartupActivity extends AppCompatActivity
        implements AuthFragment.AuthInterface, SetupFragment.SetupInterface {


    // MARK: Interface implementations
    @Override
    public void didAuthorizeLogin() {

        // TODO: Conditionally switch to setup only if new user, else go to MainActivity

        // Switch to Setup fragment, set this activity as its delegate
        SetupFragment setupFrag = new SetupFragment();
        setupFrag.setSetupDelegate(this);
        switchToFragment(setupFrag);
    }

    @Override
    public void didRejectLoginAttempt() {

        Toast.makeText(this, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didConfirmDone() {

        // TODO: Potentially add validation to setup fields, prob okay to leave blank

        // Go to MainActivity
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
    }


    // MARK: View lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Start by displaying Auth fragment, setting its delegate to be this class
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
