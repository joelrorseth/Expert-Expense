package com.rorsethj.expertexpense;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class OverviewCustomizationRecyclerAdapter
        extends RecyclerView.Adapter<OverviewCustomizationRecyclerAdapter.ViewHolder> {

    private List<String> mySettings = Collections.emptyList();
    private SharedPreferences prefs;

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;


    // Class to store recycled views
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleTextView;
        public Switch enabledSwitch;

        public ViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.overviewCustomizationItemTitle);
            enabledSwitch = itemView.findViewById(R.id.overviewCustomizationItemSwitch);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // Custom constructor
    public OverviewCustomizationRecyclerAdapter(Context context, List<String> settings) {
        this.mInflater = LayoutInflater.from(context);
        this.mySettings = settings;
        this.context = context;

        // Get preferences at first load
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.overview_customization_recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Set title of the preference at this position
        final String currentSetting = mySettings.get(position);
        holder.titleTextView.setText(currentSetting);

        // Set the switch to be enabled / disabled
        // IMPORTANT: The preferences keys are simply the name of settings displayed in the menu
        holder.enabledSwitch.setChecked(prefs.getBoolean(currentSetting, true));

        holder.enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Write the new changed boolean (switch on?) of the switch to this pref
                PreferenceManager.getDefaultSharedPreferences(context)
                        .edit().putBoolean(currentSetting,isChecked).apply();
            }
        });
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mySettings.size();
    }

    // Get item at pos
    public String getItem(int id) {
        return mySettings.get(id);
    }

    // Handle item clicks
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // Provide interface for parent activity to respond to clicks
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
