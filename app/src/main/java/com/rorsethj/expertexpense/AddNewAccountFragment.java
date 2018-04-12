package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class AddNewAccountFragment extends Fragment {

    public interface AddAccountCallback {
        void didAddAccount(boolean success, Exception e);
    }

    private EditText nameText;
    private EditText descrText;
    private EditText balanceText;
    private Spinner currencySpinner;
    private String icon = "ICON_NAME";  // TODO

    private boolean isBeingEdited = false;
    private String currentAccountIDBeingEdited;
    private Account currentAccountBeingEdited;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_account, container, false);

        nameText = view.findViewById(R.id.new_account_name_text);
        descrText = view.findViewById(R.id.new_account_description_text);
        balanceText = view.findViewById(R.id.new_account_balance_text);
        currencySpinner = view.findViewById(R.id.new_account_currency_spinner);


        // EDIT mode ONLY
        // Potentially fill blank EditText objects if account is being edited
        if (isBeingEdited) {

            // Set the EditText text attributes
            nameText.setText(currentAccountBeingEdited.getAccountName());
            descrText.setText(currentAccountBeingEdited.getDescription());
            balanceText.setText(currentAccountBeingEdited.getBalance() + "");

            String currencies[] = getResources().getStringArray(R.array.currency_array);
            for (int i = 0; i < currencies.length; ++i) {
                if (currencies[i].equals(currentAccountBeingEdited.getCurrency()))
                    currencySpinner.setSelection(i);
            }
        }


        // Attach listeners to SAVE and CANCEL
        view.findViewById(R.id.new_account_cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getFragmentManager().popBackStack();
                    }
                }
        );

        view.findViewById(R.id.new_account_save_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // TODO: Validate fields

                        // Create Account object
                        Account newAccount = new Account(
                                nameText.getText().toString(),
                                descrText.getText().toString(),
                                currencySpinner.getSelectedItem().toString(),
                                icon,
                                Double.parseDouble(balanceText.getText().toString())
                        );

                        Database db = Database.getCurrentUserDatabase();

                        if (isBeingEdited) {

                            // Update the account being edited (using ID) with account made here
                            db.updateExistingAccount(currentAccountIDBeingEdited, newAccount,
                                    new AddAccountCallback() {
                                @Override
                                public void didAddAccount(boolean success, Exception e) {

                                    if (!success && e != null) {
                                        Toast.makeText(getContext(), e.getLocalizedMessage(),
                                                Toast.LENGTH_SHORT).show();

                                    } else {

                                        // Show success message and go back
                                        Toast.makeText(getContext(), "Account has been updated",
                                                Toast.LENGTH_SHORT).show();
                                        getFragmentManager().popBackStack();
                                    }
                                }
                            });

                        } else {
                            // Attempt to save new Account in database
                            db.saveNewAccount(newAccount, new AddAccountCallback() {
                                @Override
                                public void didAddAccount(boolean success, Exception e) {

                                    if (!success && e != null) {
                                        Toast.makeText(getContext(), e.getLocalizedMessage(),
                                                Toast.LENGTH_SHORT).show();

                                    } else {

                                        // Show success message and go back
                                        Toast.makeText(getContext(), "New account has been added",
                                                Toast.LENGTH_SHORT).show();
                                        getFragmentManager().popBackStack();
                                    }
                                }
                            });
                        }
                    }
                }
        );

        return view;
    }


    // Call this method to populate this form with an account already made
    // This should be called when instead an account is being edited
    public void populateAccountBeingEdited(String id, Account accountBeingEdited) {

        currentAccountIDBeingEdited = id;
        currentAccountBeingEdited = accountBeingEdited;
        isBeingEdited = true;
    }
}