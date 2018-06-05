package com.rorsethj.expertexpense;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class UpcomingBillsRecyclerAdapter
        extends RecyclerView.Adapter<UpcomingBillsRecyclerAdapter.ViewHolder> {

    private List<Bill> myBills = Collections.emptyList();

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

            myImageView = itemView.findViewById(R.id.upcomingBillsItemIcon);
            titleTextView = itemView.findViewById(R.id.upcomingBillsItemTitle);
            descTextView = itemView.findViewById(R.id.upcomingBillsItemDesc);
            amountTextView = itemView.findViewById(R.id.upcomingBillsItemAmount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }
    }



    // Custom constructor
    public UpcomingBillsRecyclerAdapter(Context context, List<Bill> bills) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.myBills = bills;
    }


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.upcoming_bills_recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Bill bill = myBills.get(position);
        Date date = new Date( bill.getDueDate() );

        holder.titleTextView.setText(bill.getPayee());

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        holder.descTextView.setText(format.format(date));

        holder.amountTextView.setText(

                String.format(
                        context.getResources().getString(R.string.money_amount),
                        bill.getAmount()
                )
        );
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return myBills.size();
    }

    // Get item at pos
    public Bill getItem(int id) {
        return myBills.get(id);
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
