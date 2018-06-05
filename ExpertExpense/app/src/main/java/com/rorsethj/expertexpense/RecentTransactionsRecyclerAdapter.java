package com.rorsethj.expertexpense;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Date;
import java.util.List;


public class RecentTransactionsRecyclerAdapter
        extends RecyclerView.Adapter<RecentTransactionsRecyclerAdapter.ViewHolder> {

    // TODO: Replace with <Transaction> class?
    private List<Transaction> myTransactions = Collections.emptyList();

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;


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
    public RecentTransactionsRecyclerAdapter(Context context, List<Transaction> transactions) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
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

        Transaction trans = myTransactions.get(position);
        holder.titleTextView.setText(trans.getPayee());

        // Conditionally show high spending alerts when warranted
        boolean highSpendingAlertsEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources().getString(
                        R.string.prefs_high_spending), true);


        // Account for high spending notifications
        if (trans.getAmount() > 500.0 && trans.getType().equals("Withdrawal")
                &&  highSpendingAlertsEnabled) {

            holder.descTextView.setText(context.getResources().getText(R.string.high_spending));
            holder.descTextView.setTextColor(context.getResources().getColor(R.color.colorErrorRed, null));
        } else {
            holder.descTextView.setText(trans.getCategory());
        }


        // Determine color of amount text based on type
        if (trans.getType().equals("Withdrawal")) {

            // Set color to red
            holder.amountTextView.setTextColor(
                    context.getResources().getColor(R.color.colorErrorRed)
            );

            // Add minus sign when displaying amount
            holder.amountTextView.setText(

                    String.format(
                            context.getResources().getString(R.string.money_amount_negative),
                            trans.getAmount()
                    )
            );

        } else {

            // Set green
            holder.amountTextView.setTextColor(
                    context.getResources().getColor(R.color.colorValidGreen)
            );

            holder.amountTextView.setText(

                    String.format(
                            context.getResources().getString(R.string.money_amount),
                            trans.getAmount()
                    )
            );
        }
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return myTransactions.size();
    }

    // Get item at pos
    public Transaction getItem(int id) {
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
