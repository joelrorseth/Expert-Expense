package com.rorsethj.expertexpense;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReportChartFragment extends Fragment {

    public static final String WITHDRAWAL = "Withdrawal";
    public static final String DEPOSIT = "Deposit";
    public static final String DAILY = "Daily";
    public static final String MONTHLY = "Monthly";

    private Database db;
    public String chartType;

    public ReportChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_report_chart, container, false);

        db = Database.getCurrentUserDatabase();
        plotCorrectGraph(view);

        return view;
    }

    public void plotCorrectGraph(final View view) {


        ArrayList<String> labels = new ArrayList<>();
        float[] values = {};

        // Determine what to graph
        switch (chartType) {

            // TODO:
            case "Expense By Category":

                plotByCategory(view, WITHDRAWAL);
                break;

            case "Daily Expense":

                // Get expense last 7 days in bar chart
                plotTransactionTypeForIncrement(view, chartType, WITHDRAWAL, DAILY);
                break;

            case "Monthly Expense":
                // Show total expenses each month since january, show average over those months
                plotTransactionTypeForIncrement(view, chartType, WITHDRAWAL, MONTHLY);
                break;

            case "Income By Category":

                plotByCategory(view, DEPOSIT);
                break;

            case "Daily Income":

                // Bar chart shows total income for each of last 7 days
                plotTransactionTypeForIncrement(view, chartType, DEPOSIT, DAILY);
                break;

            case "Monthly Income":

                // Bar chart shows YTD each months income, display average also
                plotTransactionTypeForIncrement(view, chartType, DEPOSIT, MONTHLY);
                break;

            case "Income Vs Expense":
                // Show pie chart with income and expense labels, values are this months amount
                // for each. Should be income vs expense totals for the month

                plotByCategory(view, "vs");
                break;

            case "Daily Balance":
                // Show, for each day, a +/- standing in a line graph
                break;
        }
    }



    // MARK: Logic
    // Plot expense / income by category, by obtaining transactions of that type between 2 dates
    private void plotByCategory(final View view, final String type) {

        // Get transactions from last 30 days
        Calendar oldCal = Calendar.getInstance();
        oldCal.setTime(new Date());
        oldCal.add(Calendar.DATE, -30);

        long oldDate = oldCal.getTime().getTime();
        long newDate = (new Date()).getTime();

        db.getTransactionsBetweenDates(oldDate, newDate, new Database.DBGetTransactionsInterface() {
            @Override
            public void didGet(List<Transaction> transactions, Exception e) {

                if (type.equals("vs")) {
                    plotIncomeVsExpense(view, transactions);
                } else {
                    plotTransactionWithType(view, transactions, type);
                }
            }
        });
    }


    // Determine variables and filter transactions to plot all of a given type
    private void plotTransactionWithType(final View view, List<Transaction> transactions, String type) {

        Map<String, Double> expenseByCategory = new HashMap<>();

        // Make a map of categories to total expense amount for that category
        for (Transaction t: transactions) {

            // Expenses are Transactions with type "Withdrawal"
            // Income are Transactions with type "Deposit"
            if (t.getType().equals(type)) {

                String category = t.getCategory();
                Double amount = t.getAmount();

                // Add to other totals from that category if multiple
                if (expenseByCategory.containsKey(category)) {
                    expenseByCategory.put(category, expenseByCategory.get(category) + amount);

                } else {
                    expenseByCategory.put(category, amount);
                }
            }
        }

        List<String> labels = new ArrayList<>();
        float values[] = new float[expenseByCategory.size()];
        int i = 0;

        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            labels.add(entry.getKey());
            values[i++] = entry.getValue().floatValue();
        }

        plotPieChart(view, chartType, labels, values);
    }

    private void plotIncomeVsExpense(final View view, List<Transaction> transactions) {

        Map<String, Double> expenseByCategory = new HashMap<>();

        // Make a map of categories to total expense amount for that category
        for (Transaction t: transactions) {

            String type = t.getType();
            Double amount = t.getAmount();

            // Add to other totals from that category if multiple
            if (expenseByCategory.containsKey(type)) {
                expenseByCategory.put(type, expenseByCategory.get(type) + amount);

            } else {
                expenseByCategory.put(type, amount);
            }
        }

        List<String> labels = new ArrayList<>();
        float values[] = new float[expenseByCategory.size()];
        int i = 0;

        for (Map.Entry<String, Double> entry : expenseByCategory.entrySet()) {
            labels.add(entry.getKey());
            values[i++] = entry.getValue().floatValue();
        }

        plotPieChart(view, chartType, labels, values);
    }






    // Plot expenses / incomes for daily / monthly basis
    private void plotTransactionTypeForIncrement(final View view, final String title,
                                                 final String type, String increment) {


        // Create two placeholder objects for two dates
        Calendar oldCal = Calendar.getInstance();
        Calendar tempCal = Calendar.getInstance();
        Date newDateObj = new Date();
        oldCal.setTime(newDateObj);
        oldCal.setTime(newDateObj);

        final String incrementMapKey;
        final ArrayList<String> increments = new ArrayList<>();


        if (increment.equals(MONTHLY)) {

            oldCal.add(Calendar.DATE, -170);    // TODO: Temp ~6 months
            tempCal.add(Calendar.DATE, -170);    // TODO: Temp ~6 months
            incrementMapKey = "MMM"; // eg. "Jun"

            //Calendar tempDate = oldCal;
            String newDateMonth = (String) DateFormat.format(incrementMapKey,   newDateObj);

            // Populate ArrayList of increments between start and end date eg. [Nov,Dec,Jan,Feb,Mar]
            while (true) {

                String tempDateCurrentMonth = (String) DateFormat.format(incrementMapKey, tempCal);
                increments.add(tempDateCurrentMonth);

                if (tempDateCurrentMonth.equals(newDateMonth)) { break; }
                else { tempCal.add(Calendar.MONTH, 1); }   // Increment to next month
            }


        } else {

            oldCal.add(Calendar.DATE, -7);      // TODO: Temo ~7 days
            tempCal.add(Calendar.DATE, -7);      // TODO: Temo ~7 days
            incrementMapKey = "dd";  // "20"

            //Calendar tempDate = oldCal;
            String newDateDay = (String) DateFormat.format(incrementMapKey,   newDateObj);

            // Populate ArrayList of increments between start and end date eg. [29,30,31,01,02,03]
            while (true) {

                String tempDateCurrentDay = (String) DateFormat.format(incrementMapKey, tempCal);
                increments.add(tempDateCurrentDay);

                if (tempDateCurrentDay.equals(newDateDay)) { break; }
                else { tempCal.add(Calendar.DATE, 1); }   // Increment to next month
            }
        }


        long oldDate = oldCal.getTime().getTime();
        long newDate = (newDateObj).getTime();


        db.getTransactionsBetweenDates(oldDate, newDate, new Database.DBGetTransactionsInterface() {
            @Override
            public void didGet(List<Transaction> transactions, Exception e) {


                // Create mapping of incremental categories to the amounts accumulated in those increments
                Map<String, Double> amountByCategory = new HashMap<>();
                for (Transaction t: transactions) {

                    // Filter out types that are not the specified type, eg. filter expenses
                    if (!t.getType().equals(type)) { continue; }

                    // Get corresponding increment depending, eg. get Jun if monthly, or get 27 if daily
                    Double amount = t.getAmount();
                    Date date = new Date(t.getDate());
                    String increment = (String) DateFormat.format(incrementMapKey, date);


                    // Add to other totals from that category if multiple
                    if (amountByCategory.containsKey(increment)) {
                        amountByCategory.put(increment, amountByCategory.get(increment) + amount);

                    } else {
                        amountByCategory.put(increment, amount);
                    }
                }

                float values[] = new float[increments.size()];
                int i = 0;


                // Manually populate values, inserting 0.0 for missing, we already know labels
                for (String increment: increments) {

                    if (amountByCategory.containsKey(increment)) {
                        values[i++] = amountByCategory.get(increment).floatValue();

                    } else {
                        values[i++] = 0.0f;
                    }
                }


                plotBarChart(view, title, increments, values);
            }
        });
    }









    // MARK: Chart rendering
    // Plot a Pie Chart using data
    private void plotPieChart(View view, String title, List<String> labels, float[] values) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        // Create Entry for each label,value pair
        for (int i = 0; i < labels.size(); ++i) {
            entries.add(new PieEntry(values[i], labels.get(i)));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, title);
        PieData pieData = new PieData(pieDataSet);

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create the chart view
        PieChart chart = new PieChart(getContext());
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setHoleRadius(50);
        chart.setTransparentCircleRadius(50);

        // Animate the chart appearing
        chart.setData(pieData);
        chart.animateY(1000);

        // Create layout parameters to force the chart to be full screen
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        chart.setLayoutParams(lp);

        // Dynamically load in the chart
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.reportChartLinearLayout);
        layout.addView(chart);
    }


    // Plot a bar chart
    private void plotBarChart(View view, String title, List<String> labels, float[] values) {

        List<BarEntry> entries = new ArrayList<>();

        // Create Entry for each label,value pair
        for (int i = 0; i < labels.size(); ++i) {
            System.out.println(labels.get(i) + ": " + values[i]);
            entries.add(new BarEntry(i, values[i]));
        }

        BarDataSet dataset = new BarDataSet(entries, title);
        BarData data = new BarData(dataset);

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create the chart view
        BarChart chart = new BarChart(getContext());
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.setData(data);
        chart.animateY(1000);

        // Create layout parameters to force the chart to be full screen
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        chart.setLayoutParams(lp);

        // Dynamically load in the chart
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.reportChartLinearLayout);
        layout.addView(chart);
    }
}
