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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_transaction, container, false);

        payeeText = view.findViewById(R.id.new_transaction_payee_text);
        amountText = view.findViewById(R.id.new_transaction_amount_text);
        notesText = view.findViewById(R.id.new_transaction_notes_text);
        dateText = view.findViewById(R.id.new_transaction_date_text);

        accountSpinner = view.findViewById(R.id.new_transaction_account_spinner);
        typeSpinner = view.findViewById(R.id.new_transaction_type_spinner);
        categorySpinner = view.findViewById(R.id.new_transaction_category_spinner);
        statusSpinner = view.findViewById(R.id.new_transaction_status_spinner);


        dateFormatter =  new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);



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
                                dateText.getText().toString(),
                                statusSpinner.getSelectedItem().toString(),
                                Double.parseDouble(amountText.getText().toString())
                        );


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
        );

        return view;
    }
}