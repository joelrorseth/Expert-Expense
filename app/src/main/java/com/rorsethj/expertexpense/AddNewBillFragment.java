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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class AddNewBillFragment extends Fragment implements CalcDialog.CalcDialogCallback {

    public interface AddBillCallback {
        void didAddBill(boolean success, Exception e);
    }

    private EditText payeeText;
    private EditText amountText;
    private EditText notesText;
    private EditText dueDateText;
    private Spinner typeSpinner;
    private Spinner currencySpinner;
    private Spinner categorySpinner;

    private BigDecimal value;
    private final Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormatter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_bill, container, false);

        payeeText = view.findViewById(R.id.new_bill_payee_text);
        amountText = view.findViewById(R.id.new_bill_amount_text);
        notesText = view.findViewById(R.id.new_bill_notes_text);
        dueDateText = view.findViewById(R.id.new_bill_due_date_text);

        typeSpinner = view.findViewById(R.id.new_bill_type_spinner);
        currencySpinner = view.findViewById(R.id.new_bill_currency_spinner);
        categorySpinner = view.findViewById(R.id.new_bill_category_spinner);


        dateFormatter =  new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);


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
                dueDateText.setText(
                        dateFormatter.format(myCalendar.getTime())
                );
            }

        };

        dueDateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(getContext(), dateListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        final CalcDialog calcDialog = new CalcDialog();
        final AddNewBillFragment thisRef = this;

        // Set click listener to amount EditText, present custom calculator popup dialog
        view.findViewById(R.id.new_bill_amount_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Set the value, pass along this fragment to be delegate for updates
                calcDialog.setValue(value)
                        .setSignCanBeChanged(false, 1)
                        .setTargetFragment(thisRef, 0);
                calcDialog.show(getFragmentManager(), "calc_dialog");
            }
        });



        // Attach listeners
        view.findViewById(R.id.new_bill_cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getFragmentManager().popBackStack();
                    }
                }
        );

        view.findViewById(R.id.new_bill_save_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // TODO: Validate fields

                        Bill newBill = new Bill(
                                payeeText.getText().toString(),
                                typeSpinner.getSelectedItem().toString(),
                                currencySpinner.getSelectedItem().toString(),
                                categorySpinner.getSelectedItem().toString(),
                                notesText.getText().toString(),
                                myCalendar.getTime().getTime(),     // Time in ms since epoch
                                Double.parseDouble(amountText.getText().toString())
                        );

                        Database db = Database.getCurrentUserDatabase();

                        // Attempt to save new Account in database
                        db.saveNewBill(newBill, new AddNewBillFragment.AddBillCallback() {
                            @Override
                            public void didAddBill(boolean success, Exception e) {

                                if (!success && e != null) {
                                    Toast.makeText(getContext(), e.getLocalizedMessage(),
                                            Toast.LENGTH_SHORT).show();

                                } else {

                                    // Show success message and go back
                                    Toast.makeText(getContext(), "New bill has been added",
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

    // Interface
    @Override
    public void onValueEntered(BigDecimal value) {
        // The calculator dialog returned a value
        amountText.setText(value.toPlainString());
    }
}