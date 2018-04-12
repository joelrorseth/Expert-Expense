package com.rorsethj.expertexpense;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

public class AccountPopupFragment extends DialogFragment {


    public interface AccountPopupInterface {
        void acDidSelectShowTransactions();
        void acDidSelectEditAccount();
        void acDidSelectAddTransaction();
        void acDidSelectTransfer();
        void acDidSelectDeleteAccount();
        void acDidSelectHide();
    }

    public AccountPopupInterface parentDelegate;

    private final int popupButtonIDs[] = {
            R.id.accPopupTransactions,
            R.id.accPopupEditAcct,
            R.id.accPopupAddTrans,
            R.id.accPopupTransfer,
            R.id.accPopupDelete,
            R.id.accPopupHide };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.popup_account, null);

        // Set the dialog layout
        builder.setView(view);


        // Setup a listener for each popup button
        for (int id: popupButtonIDs) {
            final int _id = id;

            ((ImageButton) view.findViewById(id)).setOnClickListener(new View.OnClickListener() {

                // Pass of handling to handleClickFor to determine appropriate course of action
                @Override
                public void onClick(View view) {
                    handleClickFor(_id);
                }
            });
        }

        return builder.create();
    }


    // Handler for click events for a given button with ID
    private void handleClickFor(int buttonID) {

        switch (buttonID) {

            case R.id.accPopupTransactions:

                // Go to TransactionFragment, displaying transactions for this account only
                parentDelegate.acDidSelectShowTransactions();
                break;

            case R.id.accPopupEditAcct:

                // Go to edit account screen
                parentDelegate.acDidSelectEditAccount();
                break;

            case R.id.accPopupAddTrans:

                // Go to add transaction screen, with calculator prompted
                parentDelegate.acDidSelectAddTransaction();
                break;

            case R.id.accPopupTransfer:

                // Go to transfer creation screen
                parentDelegate.acDidSelectTransfer();
                break;

            case R.id.accPopupDelete:

                // Confirm the deletion, if confirmed, delete account from records
                parentDelegate.acDidSelectDeleteAccount();
                break;

            case R.id.accPopupHide:

                // Hide this account from views
                parentDelegate.acDidSelectHide();
                break;
        }
    }

}