package com.rorsethj.expertexpense;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;


public class RecentTransactionsRecyclerAdapter
        extends RecyclerView.Adapter<RecentTransactionsRecyclerAdapter.ViewHolder> {

    // TODO: Replace with <Transaction> class?
    private List<String> myTransactions = Collections.emptyList();

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;



    // Class to store recycled views
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView myImageView;
        public TextView titleTextView;
        public TextView descTextView;
        public TextView amountTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            myImageView = itemView.findViewById(R.id.recentTransactionItemIcon);
            titleTextView = itemView.findViewById(R.id.recentTransactionItemTitle);
            descTextView = itemView.findViewById(R.id.recentTransactionItemDesc);
            amountTextView = itemView.findViewById(R.id.recentTransactionItemAmount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }



    // Custom constructor
    public RecentTransactionsRecyclerAdapter(Context context, List<String> transactions) {
        this.mInflater = LayoutInflater.from(context);
        this.myTransactions = transactions;
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recent_transactions_recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String trans = myTransactions.get(position);
        holder.myImageView.setBackgroundColor(200);

        // TODO
        holder.titleTextView.setText(trans);
        holder.descTextView.setText("Placeholder desc");
        holder.amountTextView.setText("-$12.34");
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return myTransactions.size();
    }

    // Get item at pos
    public String getItem(int id) {
        return myTransactions.get(id);
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
