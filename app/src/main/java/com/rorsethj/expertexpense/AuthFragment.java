package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class AuthFragment extends Fragment {

    // Allow parent activity to implement functionality and manage this fragment
    public interface AuthInterface {
        public void didAuthorizeLogin();
    }

    private AuthInterface authDelegate;

    public void setAuthDelegate(AuthInterface conformer) {
        authDelegate = conformer;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_auth, container, false);

        // Add click listener to login
        Button button = (Button) view.findViewById(R.id.loginButton);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                authDelegate.didAuthorizeLogin();
            }
        });

        return view;
    }
}