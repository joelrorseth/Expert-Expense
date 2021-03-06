package com.rorsethj.expertexpense;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
        void didAuthorizeLogin();
        void didAuthorizeNewAccount();
        void didRejectLoginAttempt();
    }


    private AuthInterface authDelegate;
    private FirebaseAuth authManager;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;

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

        usernameEditText = (EditText) view.findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        progressBar = (ProgressBar) view.findViewById(R.id.authLoadingProgressBar);

        // Add click listeners to buttons
        Button loginButton = (Button) view.findViewById(R.id.loginButton);
        Button signupButton = (Button) view.findViewById(R.id.signupButton);

        loginButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                signInExistingUser(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                createNewUser(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        return view;
    }


    // MARK: com.rorsethj.expertexpense.Database Auth
    // Attempt to create a new user in the database
    private void signInExistingUser(String email, String password) {

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        authManager.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("User signed in successfully");
                            authDelegate.didAuthorizeLogin();

                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInExistingUserWithEmail:failure", task.getException());
                            authDelegate.didRejectLoginAttempt();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    // Attempt to create a new user in the database
    private void createNewUser(String email, String password) {

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        authManager.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            System.out.println("User created successfully");
                            authDelegate.didAuthorizeNewAccount();


                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            authDelegate.didRejectLoginAttempt();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}