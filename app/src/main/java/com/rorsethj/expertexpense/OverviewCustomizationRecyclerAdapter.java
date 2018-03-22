package com.rorsethj.expertexpense;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;


public class OverviewCustomizationRecyclerAdapter
        extends RecyclerView.Adapter<OverviewCustomizationRecyclerAdapter.ViewHolder> {

    // TODO: Replace with actual preferences?
    private List<String> mySettings = Collections.emptyList();

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;



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

        // TODO: Insert logic to read from shared preferences for the switch
        String setting = mySettings.get(position);
        // switch ...

        // TODO
        holder.titleTextView.setText(setting);
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
