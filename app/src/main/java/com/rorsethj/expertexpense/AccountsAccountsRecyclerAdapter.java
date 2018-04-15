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


public class AccountsAccountsRecyclerAdapter
        extends RecyclerView.Adapter<AccountsAccountsRecyclerAdapter.ViewHolder> {

    private List<Account> myAccounts = Collections.emptyList();

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

            myImageView = itemView.findViewById(R.id.accountsAccountsItemIcon);
            titleTextView = itemView.findViewById(R.id.accountsAccountsItemTitle);
            descTextView = itemView.findViewById(R.id.accountsAccountsItemDesc);
            amountTextView = itemView.findViewById(R.id.accountsAccountsItemAmount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }



    // Custom constructor
    public AccountsAccountsRecyclerAdapter(Context context, List<Account> accounts) {
        this.mInflater = LayoutInflater.from(context);
        this.myAccounts = accounts;
        this.context = context;
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.accounts_accounts_recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Account account = myAccounts.get(position);

        holder.titleTextView.setText(account.getAccountName());
        holder.descTextView.setText(account.getCurrency());

        // Determine color of amount text based on possibility of negative balance
        if (account.getBalance() < 0.0) {

            // Set color to red
            holder.amountTextView.setTextColor(
                    context.getResources().getColor(R.color.colorErrorRed)
            );

            // Add minus sign when displaying amount
            holder.amountTextView.setText(
                    String.format(
                            context.getResources().getString(R.string.money_amount_negative),
                            account.getBalance()
                    )
            );

        } else {

            // Set green
            holder.amountTextView.setTextColor(
                    context.getResources().getColor(R.color.colorTextBlack)
            );

            holder.amountTextView.setText(

                    String.format(
                            context.getResources().getString(R.string.money_amount),
                            account.getBalance()
                    )
            );
        }
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return myAccounts.size();
    }

    // Get item at pos
    public Account getItem(int id) {
        return myAccounts.get(id);
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
