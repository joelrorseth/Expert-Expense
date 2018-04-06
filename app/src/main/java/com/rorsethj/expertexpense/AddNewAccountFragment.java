package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


public class AddNewAccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_account, container, false);

        // Attach listeners
        view.findViewById(R.id.new_account_cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {
                         getFragmentManager().popBackStack();
                    }
                }
        );

        view.findViewById(R.id.new_account_save_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override public void onClick(View view) {

                        // TODO:
                    }
                }
        );

        return view;
    }
}