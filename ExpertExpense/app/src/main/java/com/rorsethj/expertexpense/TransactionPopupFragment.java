package com.rorsethj.expertexpense;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

public class TransactionPopupFragment extends DialogFragment {


    public interface TransactionPopupInterface {
        void trDidSelectEdit();
        void trDidSelectDelete();
        void trDidSelectCopy();
        void trDidSelectMove();
        void trDidSelectNewStatus();
        void trDidSelectSetProj();
    }

    public TransactionPopupInterface parentDelegate;

    private final int popupButtonIDs[] = {
            R.id.tranPopupEdit,
            R.id.tranPopupDelete,
            R.id.tranPopupCopy,
            R.id.tranPopupMove,
            R.id.tranPopupNewStatus,
            R.id.tranPopupSetProj };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.popup_transaction, null);

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

            case R.id.tranPopupEdit:

                parentDelegate.trDidSelectEdit();
                break;

            case R.id.tranPopupDelete:

                parentDelegate.trDidSelectDelete();
                break;

            case R.id.tranPopupCopy:

                parentDelegate.trDidSelectCopy();
                break;

            case R.id.tranPopupMove:

                parentDelegate.trDidSelectMove();
                break;

            case R.id.tranPopupNewStatus:

                parentDelegate.trDidSelectNewStatus();
                break;

            case R.id.tranPopupSetProj:

                parentDelegate.trDidSelectSetProj();
                break;
        }
    }

}