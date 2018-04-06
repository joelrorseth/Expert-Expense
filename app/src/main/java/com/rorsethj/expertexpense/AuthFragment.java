package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;


public class AuthFragment extends Fragment {

    // Allow parent activity to implement functionality and manage this fragment
    public interface AuthInterface {
        public void didAuthorizeLogin();
        public void didRejectLoginAttempt();
    }


    private AuthInterface authDelegate;
    private FirebaseAuth authManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authManager = FirebaseAuth.getInstance();
    }

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

                // Determine if user with this email exists
            }
        });

        return view;
    }


    // MARK: Database Auth
    // Attempt to create a new user in the database
    private void signInExistingUser(String email, String password) {

        authManager.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        authDelegate.didAuthorizeLogin();

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = authManager.getCurrentUser();

                        // TODO: Set current user

                    } else {

                        authDelegate.didRejectLoginAttempt();

                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                }
            });
    }


    // Attempt to create a new user in the database
    private void createNewUser(String email, String password) {

        authManager.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        authDelegate.didAuthorizeLogin();

                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = authManager.getCurrentUser();

                        // TODO: Set current user

                    } else {

                        authDelegate.didRejectLoginAttempt();

                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                }
            });
    }
}