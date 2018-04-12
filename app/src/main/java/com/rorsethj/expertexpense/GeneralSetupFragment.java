package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class GeneralSetupFragment extends Fragment {

    public interface GeneralSetupInterface {
        void didUpdateCurrency(String currency);
        void didUpdateLanguage(String language);
    }

    public GeneralSetupInterface setupDelegate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_general_setup, container, false);


        Spinner defaultCurrencySpinner = (Spinner) view.findViewById(R.id.defaultCurrencySpinner);
        Spinner languageSpinner = (Spinner) view.findViewById(R.id.languageSpinner);

        // Establish adapters for the spinner objects
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.currency_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.language_array, android.R.layout.simple_spinner_item);

        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Delegate is informed when new selection from spinner occurs
        defaultCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setupDelegate.didUpdateCurrency( getResources().getStringArray(R.array.currency_array)[position] );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });


        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setupDelegate.didUpdateLanguage( getResources().getStringArray(R.array.language_array)[position] );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });



        // Set adapters to the spinners
        defaultCurrencySpinner.setAdapter(currencyAdapter);
        languageSpinner.setAdapter(languageAdapter);

        return view;
    }
}