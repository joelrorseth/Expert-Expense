package com.rorsethj.expertexpense;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddNewTransactionFragment extends Fragment {

    public interface AddTransactionCallback {
        void didAddTransaction(boolean success, Exception e);
    }

    private EditText payeeText;
    private EditText amountText;
    private EditText notesText;
    private EditText dateText;
    private Spinner accountSpinner;
    private Spinner typeSpinner;
    private Spinner categorySpinner;
    private Spinner statusSpinner;

    private final Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormatter;

    private List<String> currentUserAccountIDs;
    private List<Account> currentUserAccounts;

    private boolean isBeingEdited = false;
    private String currentTransactionIDBeingEdited;
    private Transaction currentTransactionBeingEdited;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_transaction, container, false);

        dateFormatter =  new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);


        payeeText = view.findViewById(R.id.new_transaction_payee_text);
        amountText = view.findViewById(R.id.new_transaction_amount_text);
        notesText = view.findViewById(R.id.new_transaction_notes_text);
        dateText = view.findViewById(R.id.new_transaction_date_text);

        accountSpinner = view.findViewById(R.id.new_transaction_account_spinner);
        typeSpinner = view.findViewById(R.id.new_transaction_type_spinner);
        categorySpinner = view.findViewById(R.id.new_transaction_category_spinner);
        statusSpinner = view.findViewById(R.id.new_transaction_status_spinner);



        // EDIT mode ONLY
        // Potentially fill blank EditText objects if transaction is being edited
        if (isBeingEdited) {

            // Set the EditText text attributes
            payeeText.setText(currentTransactionBeingEdited.getPayee());
            notesText.setText(currentTransactionBeingEdited.getNotes());
            amountText.setText(currentTransactionBeingEdited.getAmount() + "");

            // Extract date from trans, show in EditText but also update current Calendar object
            Date dateOfEditedTrans = new Date(currentTransactionBeingEdited.getDate());
            myCalendar.setTime(dateOfEditedTrans);
            dateText.setText(
                    dateFormatter.format(dateOfEditedTrans)
            );

            // Determine index in spinner of this transaction's type
            String types[] = getResources().getStringArray(R.array.transaction_types_array);
            for (int i = 0; i < types.length; ++i) {
                if (types[i].equals(currentTransactionBeingEdited.getType()))
                    typeSpinner.setSelection(i);
            }

            // " " for category
            String categories[] = getResources().getStringArray(R.array.bill_transaction_category_array);
            for (int i = 0; i < categories.length; ++i) {
                if (categories[i].equals(currentTransactionBeingEdited.getCategory()))
                    categorySpinner.setSelection(i);
            }

            // For status
            String statuses[] = getResources().getStringArray(R.array.transaction_status_array);
            for (int i = 0; i < statuses.length; ++i) {
                if (statuses[i].equals(currentTransactionBeingEdited.getStatus()))
                    statusSpinner.setSelection(i);
            }

            // IMPORTANT
            // NOTE: Postpone filling in account until the accounts are loaded from DB
        }


        // Get account names for Spinner
        Database db = Database.getCurrentUserDatabase();
        db.getUserAccounts(new Database.DBGetAccountsInterface() {

            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {

                currentUserAccountIDs = accountIDs;
                currentUserAccounts = accounts;

                // If an error occurred loading accounts, print exception
                if (e != null) {

                    Toast.makeText(getContext(), e.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Use List of account names for user-friendly display
                ArrayList<String> accountNames = new ArrayList<>();
                for (Account account: accounts) {
                    accountNames.add(account.getAccountName());
                }

                // Establish adapter for account names
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, accountNames);

                // Display the account names in the Spinner
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                accountSpinner.setAdapter(adapter);


                // If this account is being edited, determine account that is associated with the
                // transaction, find it, display it in the spinner by default
                if (isBeingEdited) {
                    int i = 0, j = 0;
                    String accountNameOfCurrentlyEdited = "";

                    // Determine human name of account associated with currently edited trans
                    for (String id: accountIDs) {
                        if (id.equals( currentTransactionBeingEdited.getAccount() )) {
                            accountNameOfCurrentlyEdited = accounts.get(i).getAccountName();
                        }
                        i++;
                    }

                    // Determine position in spinner array of that name, set it
                    for (String name: accountNames) {
                        if (name.equals( accountNameOfCurrentlyEdited )) {
                            accountSpinner.setSelection(j);
                        }
                        j++;
                    }
                }
            }
        });



        // Establish a DatePickerDialog that pops when the dueDateText is selected
        final DatePickerDialog.OnDateSetListener dateListener =
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        // Set the date of the Calendar object
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Update EditText that the user sees regularly
                        dateText.setText(
                                dateFormatter.format(myCalendar.getTime())
                        );
                    }

                };

        dateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(getContext(), dateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });




        // Attach listeners
        view.findViewById(R.id.new_transaction_cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getFragmentManager().popBackStack();
                    }
                }
        );

        view.findViewById(R.id.new_transaction_save_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // TODO: Validate fields

                        int selectedAccountIndex = accountSpinner.getSelectedItemPosition();

                        Transaction newTransaction = new Transaction(
                                currentUserAccountIDs.get(selectedAccountIndex),
                                payeeText.getText().toString(),
                                typeSpinner.getSelectedItem().toString(),
                                categorySpinner.getSelectedItem().toString(),
                                notesText.getText().toString(),
                                statusSpinner.getSelectedItem().toString(),
                                myCalendar.getTime().getTime(), // Time in ms since epoch
                                Double.parseDouble(amountText.getText().toString())
                        );


                        if (isBeingEdited) {

                            // Update transaction being edited
                            Database db = Database.getCurrentUserDatabase();
                            db.updateExistingTransaction(currentTransactionIDBeingEdited, newTransaction,
                                    new AddTransactionCallback() {

                                @Override
                                public void didAddTransaction(boolean success, Exception e) {

                                    if (!success && e != null) {
                                        Toast.makeText(getContext(), e.getLocalizedMessage(),
                                                Toast.LENGTH_SHORT).show();

                                    } else {

                                        // Show success message and go back
                                        Toast.makeText(getContext(), "Transaction has been updated",
                                                Toast.LENGTH_SHORT).show();
                                        getFragmentManager().popBackStack();
                                    }
                                }
                            });

                        } else {


                            // Save the new transaction in database
                            Database db = Database.getCurrentUserDatabase();
                            db.saveNewTransaction(newTransaction, new AddTransactionCallback() {

                                @Override
                                public void didAddTransaction(boolean success, Exception e) {

                                    if (!success && e != null) {
                                        Toast.makeText(getContext(), e.getLocalizedMessage(),
                                                Toast.LENGTH_SHORT).show();

                                    } else {

                                        // Show success message and go back
                                        Toast.makeText(getContext(), "New transaction has been added",
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


    // Call this method to populate this form with a transaction already made
    // This should be called when instead a transaction is being edited
    public void populateTransactionBeingEdited(String id, Transaction transactionBeingEdited) {

        currentTransactionIDBeingEdited = id;
        currentTransactionBeingEdited = transactionBeingEdited;
        isBeingEdited = true;
    }
}