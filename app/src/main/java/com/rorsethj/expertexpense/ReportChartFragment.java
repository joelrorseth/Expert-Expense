package com.rorsethj.expertexpense;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ReportChartFragment extends Fragment {

    public static final String WITHDRAWAL = "Withdrawal";
    public static final String DEPOSIT = "Deposit";
    public static final String DAILY = "Daily";
    public static final String MONTHLY = "Monthly";
    public static final String DATE_FORMAT_DAILY = "dd/MM/yyyy";
    public static final String DATE_FORMAT_MONTHLY = "MM/yyyy";
    public static final String DATE_FORMAT_DAILY_SHORT = "dd";
    public static final String DATE_FORMAT_MONTHLY_SHORT = "MMM";

    private Spinner accountsSpinner;
    private Spinner periodsSpinner;

    private PieChart pieChart = null;
    private BarChart barChart = null;
    private LineChart lineChart = null;

    private List<String> allPeriodNames;
    private String currentlySelectedPeriod;
    private Set<String> selectedAccountNames;
    private Map<String, String> accountIDToNameLookup;
    private List<String> allAccountNames;
    private List<Account> allAccounts;

    private Database db;
    private AlertDialog dialog;

    private boolean hasErroneousSpinnerClickOccured = false;
    private boolean isFirstChart = true;

    private List<Transaction> allTransactions = new ArrayList<>();

    public String chartType;



    public ReportChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_report_chart, container, false);
        final View viewRef = view;

        // Find spinners
        accountsSpinner = view.findViewById(R.id.reportAccountsSpinner);
        periodsSpinner = view.findViewById(R.id.reportPeriodSpinner);

        // Initialize lists etc, initialize currently SELECTED PERIOD
        db = Database.getCurrentUserDatabase();
        selectedAccountNames = new HashSet<>();
        accountIDToNameLookup = new HashMap<>();
        allAccounts = new ArrayList<>();
        allAccountNames = new ArrayList<>();
        allPeriodNames = new ArrayList<String>(
                Arrays.asList( getResources().getStringArray(R.array.time_periods)) );
        currentlySelectedPeriod = allPeriodNames.get(0);


        // Retrieve all accounts from the DB, initialize currently SELECTED ACCOUNTS
        db.getUserAccounts(new Database.DBGetAccountsInterface() {
            @Override
            public void didGet(List<Account> accounts, List<String> accountIDs, Exception e) {

                allAccounts.clear();
                allAccountNames.clear();
                int i = 0;

                // Add accounts to all accounts, all names, and all selected names initially
                allAccounts = accounts;
                for (Account a: accounts) {
                    allAccountNames.add(a.getAccountName());
                    selectedAccountNames.add(a.getAccountName());
                    accountIDToNameLookup.put(accountIDs.get(i++), a.getAccountName());
                }

                // Initialize the alert dialog for choosing accounts, now we have info to PLOT
                setupSelectAccountsDialog(viewRef, allAccountNames);
                plotCorrectGraph(viewRef);
            }
        });


        // Set touch handler for accounts filter spinner, prompting dialog to choose many
        Spinner accountsSpinner = ((Spinner) view.findViewById(R.id.reportAccountsSpinner));
        accountsSpinner.setClickable(false);
        accountsSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (dialog != null) { dialog.show(); }
                return false;
            }
        });


        ((Spinner) view.findViewById(R.id.reportPeriodSpinner))
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        currentlySelectedPeriod = allPeriodNames.get(i);

                        // NOTE: Known bug is occurring where this onItemSelected is fired once for
                        // each spinner BEFORE the view is loaded. Account for this here
                        if (hasErroneousSpinnerClickOccured) {

                            // REDRAW the chart when NEW PERIOD is selected
                            plotCorrectGraph(viewRef);

                        } else { hasErroneousSpinnerClickOccured = true; }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });


        db = Database.getCurrentUserDatabase();
        return view;
    }

    // Initialize the Dialog to allow Account filter selection
    private void setupSelectAccountsDialog(final View view, final List<String> accountNames) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
        builder.setTitle("Select Accounts to Include");

        // Use the account names as items to select in the dialog
        CharSequence items[] = accountNames.toArray(new CharSequence[accountNames.size()]);
        boolean[] isInitiallyChecked = new boolean[accountNames.size()];
        Arrays.fill(isInitiallyChecked, true);

        builder.setMultiChoiceItems(items, isInitiallyChecked,
                new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {

                        // Manage which names are selected/not, store in variables
                        if (isChecked) {
                            selectedAccountNames.add(accountNames.get(indexSelected));

                        } else if (selectedAccountNames.contains(accountNames.get(indexSelected))) {
                            selectedAccountNames.remove(accountNames.get(indexSelected));
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int id) {
                        // REDRAW chart when NEW ACCOUNT(S) are selected
                        plotCorrectGraph(view);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int id) {}
                });

        dialog = builder.create();
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            int c = getResources().getColor(R.color.colorPrimaryDark, null);
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(c);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(c);
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(c);
            }
        });
    }




    // MARK: Charting Dispatch
    // MAIN entry point to determine plotting procedure for the indicated CHART_TYPE
    // This determines which type of chart to draw, who to call to draw that
    public void plotCorrectGraph(final View view) {

        // Determine what to graph
        switch (chartType) {

            case "Expense By Category":

                plotByCategory(view, WITHDRAWAL);
                break;

            case "Daily Expense":

                // Get expense last 7 days in bar chart
                if (isFirstChart) { periodsSpinner.setSelection(4); isFirstChart = false; }
                plotTransactionTypeForIncrement(view, chartType, WITHDRAWAL, DAILY);
                break;

            case "Monthly Expense":
                // Show total expenses each month since january, show average over those months
                if (isFirstChart) { periodsSpinner.setSelection(8); isFirstChart = false; }
                plotTransactionTypeForIncrement(view, chartType, WITHDRAWAL, MONTHLY);
                break;

            case "Income By Category":

                plotByCategory(view, DEPOSIT);
                break;

            case "Daily Income":

                // Bar chart shows total income for each of last 7 days
                if (isFirstChart) { periodsSpinner.setSelection(4); isFirstChart = false; }
                plotTransactionTypeForIncrement(view, chartType, DEPOSIT, DAILY);
                break;

            case "Monthly Income":

                // Bar chart shows YTD each months income, display average also
                if (isFirstChart) { periodsSpinner.setSelection(8); isFirstChart = false; }
                plotTransactionTypeForIncrement(view, chartType, DEPOSIT, MONTHLY);
                break;

            case "Income Vs Expense":
                // Show pie chart with income and expense labels, values are this months amount
                // for each. Should be income vs expense totals for the month

                plotByCategory(view, "vs");
                break;

            case "Daily Balance":

                // Show, for each day, a +/- standing in a line graph
                // TODO
                plotTransactionTypeForIncrement(view, chartType, WITHDRAWAL, DAILY);
                break;
        }
    }



    // MARK: Logic of What to Plot
    // Entry point for pie chart logic
    // Plot expense / income by category, by obtaining transactions of that type between 2 dates
    private void plotByCategory(final View view, final String category) {

        // Pull currently selected period
        DateRange.DatePair range = DateRange.getDatesForRange(currentlySelectedPeriod);
        long oldDate = range.getOld();
        long newDate = range.getNew();

        db.getTransactionsBetweenDates(oldDate, newDate, new Database.DBGetTransactionsInterface() {
            @Override
            public void didGet(List<Transaction> transactions, List<String> transactionIDs, Exception e) {

                // Pie charts might plot Income vs Expense for example, so call separate method
                if (category.equals("vs")) { plotIncomeVsExpense(view, transactions); }
                else { plotTransactionWithType(view, transactions, category); }
            }
        });
    }


    // Entry point for bar chart logic
    // Plot expenses / incomes for daily / monthly basis
    private void plotTransactionTypeForIncrement(final View view, final String title,
                                                 final String type, String chartIncrement) {

        DateRange.DatePair range = DateRange.getDatesForRange(currentlySelectedPeriod);
        long oldDate = range.getOld();
        long newDate = range.getNew();

        // Increments gathers ALL x-axis values eg. [Nov, Dec, Jan, Feb] or ["29","30","31","01"]
        final ArrayList<String> increments = interpolateValuesForIncrement(chartIncrement);
        final String INCREMENT_KEY = chartIncrement.equals(MONTHLY) ?
            DATE_FORMAT_MONTHLY_SHORT : DATE_FORMAT_DAILY_SHORT;


        db.getTransactionsBetweenDates(oldDate, newDate, new Database.DBGetTransactionsInterface() {
            @Override
            public void didGet(List<Transaction> transactions, List<String> transactionIDs, Exception e) {


                // Create mapping of incremental categories to the amounts accumulated in those increments
                Map<String, Double> amountByCategory = new HashMap<>();
                for (Transaction t: transactions) {

                    // Filter out any transactions which do not belong to currently selected accounts
                    String nameOfCurrentAccount = accountIDToNameLookup.get(t.getAccount());
                    if (!selectedAccountNames.contains(nameOfCurrentAccount)) { continue; }


                    // Filter out types that are not the specified type, eg. filter expenses
                    if (!t.getType().equals(type)) { continue; }


                    // Get corresponding increment depending, eg. get Jun if monthly, or get 27 if daily
                    Double amount = t.getAmount();
                    Date date = new Date(t.getDate());
                    String increment = (String) DateFormat.format(INCREMENT_KEY, date);


                    // Add to other totals from that category if multiple
                    if (amountByCategory.containsKey(increment)) {
                        amountByCategory.put(increment, amountByCategory.get(increment) + amount);

                    } else {
                        amountByCategory.put(increment, amount);
                    }
                }

                float values[] = new float[increments.size()];
                int i = 0;


                // Bar graphs require interpolation to make sure days without values get 0.0 etc
                // Manually populate values, inserting 0.0 for missing, we already know labels
                for (String increment: increments) {

                    if (amountByCategory.containsKey(increment)) {
                        values[i++] = amountByCategory.get(increment).floatValue();

                    } else { values[i++] = 0.0f; }
                }

                plotBarChart(view, title, increments, values);
            }
        });
    }


//    private void plotDailyBalance(View view) {
//
//        // Pull currently selected period
//        DateRange.DatePair range = DateRange.getDatesForRange(currentlySelectedPeriod);
//        long oldDate = range.getOld();
//        long newDate = range.getNew();
//
//        db.getTransactionsBetweenDates(oldDate, newDate, new Database.DBGetTransactionsInterface() {
//            @Override
//            public void didGet(List<Transaction> transactions, List<String> transactionIDs, Exception e) {
//
//                formatForLineChart(view, transactions);
//                // Pie charts might plot Income vs Expense for example, so call separate method
//                if (category.equals("vs")) { plotIncomeVsExpense(view, transactions); }
//                else { plotTransactionWithType(view, transactions, category); }
//            }
//        });
//    }


    // MARK: Data formatting for plotting (only PieCharts right now)
    // Determine variables and filter transactions to plot all of a given type
    private void plotTransactionWithType(final View view, List<Transaction> transactions, String type) {

        Map<String, Double> expenseByCategory = new HashMap<>();

        // Make a map of categories to total expense amount for that category
        for (Transaction t: transactions) {

            // Filter out any transactions which do not belong to currently selected accounts
            String nameOfCurrentAccount = accountIDToNameLookup.get(t.getAccount());
            if (!selectedAccountNames.contains(nameOfCurrentAccount)) { continue; }

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

            // Filter out any transactions which do not belong to currently selected accounts
            String nameOfCurrentAccount = accountIDToNameLookup.get(t.getAccount());
            if (!selectedAccountNames.contains(nameOfCurrentAccount)) { continue; }


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




    // MARK: Chart rendering
    // Plot a Pie Chart using data
    private void plotPieChart(View view, String title, List<String> labels, float[] values) {


        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.reportChartLinearLayout);

        if (pieChart != null) {
            pieChart.clear();
            layout.removeView(pieChart);
        }

        ArrayList<PieEntry> entries = new ArrayList<>();

        // Create Entry for each label,value pair
        for (int i = 0; i < labels.size(); ++i) {
            entries.add(new PieEntry(values[i], labels.get(i)));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, title);
        PieData pieData = new PieData(pieDataSet);

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create the chart view
        pieChart = new PieChart(getContext());
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(50);
        pieChart.getDescription().setEnabled(false);
        pieChart.setTransparentCircleRadius(50);

        // Animate the chart appearing
        pieChart.setData(pieData);
        pieChart.animateY(1000);

        // Create layout parameters to force the chart to be full screen
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        pieChart.setLayoutParams(lp);

        // Dynamically load in the chart
        layout.addView(pieChart);
        pieData.setValueTextSize(36f);
    }


    // Plot a Bar Chart
    private void plotBarChart(View view, String title, List<String> labels, float[] values) {

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.reportChartLinearLayout);

        if (barChart != null) {
            barChart.clear();
            layout.removeView(barChart);
        }

        List<BarEntry> entries = new ArrayList<>();

        // Create Entry for each label,value pair
        for (int i = 0; i < labels.size(); ++i) {
            entries.add(new BarEntry(i, values[i]));
        }

        BarDataSet dataset = new BarDataSet(entries, title);
        BarData data = new BarData(dataset);

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        // Create the chart view
        barChart = new BarChart(getContext());
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);

        // Create layout parameters to force the chart to be full screen
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        barChart.setLayoutParams(lp);

        // Dynamically load in the chart
        layout.addView(barChart);
    }


    // Plot a Line Chart
    private void plotLineChart(View view, String title, List<String> labels, float[] values) {

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.reportChartLinearLayout);

        if (lineChart != null) {
            lineChart.clear();
            layout.removeView(lineChart);
        }

        List<Entry> entries = new ArrayList<>();

        // Create Entry for each label,value pair
        for (int i = 0; i < labels.size(); ++i) {
            entries.add(new Entry(i, values[i]));
        }

        LineDataSet dataset = new LineDataSet(entries, title);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        LineData data = new LineData(dataset);

        // Create the chart view
        lineChart = new LineChart(getContext());
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.setData(data);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateY(1000);

        // Create layout parameters to force the chart to be full screen
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        lineChart.setLayoutParams(lp);

        // Dynamically load in the chart
        layout.addView(lineChart);
    }




    // MARK: Utility
    // Return list of interpolated x-axis values such as [Nov, Dec, Jan, Feb]
    private ArrayList<String> interpolateValuesForIncrement(String chartIncrement) {

        // Get current period
        DateRange.DatePair range = DateRange.getDatesForRange(currentlySelectedPeriod);
        long oldDate = range.getOld();
        long newDate = range.getNew();

        // Increments gathers ALL x-axis values eg. [Nov, Dec, Jan, Feb] or ["29","30","31","01"]
        final ArrayList<String> increments = new ArrayList<>();
        int CAL_INCREMENT = -1;
        String INCREMENT_KEY = "";          // Longer key to make sure year is correct etc
        String INCREMENT_KEY_SHORT = "";  // Actual nice key for x-axis

        if (chartIncrement.equals(MONTHLY)) {
            CAL_INCREMENT = Calendar.MONTH;
            INCREMENT_KEY = DATE_FORMAT_MONTHLY;
            INCREMENT_KEY_SHORT = DATE_FORMAT_MONTHLY_SHORT;

        } else if (chartIncrement.equals(DAILY)) {
            CAL_INCREMENT = Calendar.DATE;
            INCREMENT_KEY = DATE_FORMAT_DAILY;
            INCREMENT_KEY_SHORT = DATE_FORMAT_DAILY_SHORT;
        }


        // Start temp calendar at "old date", we will increment until "new date"
        Calendar oldCal = Calendar.getInstance();
        Calendar tempCal = Calendar.getInstance();
        oldCal.setTimeInMillis(oldDate);
        tempCal.setTimeInMillis(oldDate);

        // Get string for "new date" for stopping point eg. May
        String newDateString = (String) DateFormat.format(INCREMENT_KEY, (new Date(newDate)).getTime());

        while (true) {

            // Store the next key for month/day (eg. Jan for monthly, "27" if daily)
            String tempDateString = (String) DateFormat.format(INCREMENT_KEY, tempCal);
            increments.add( (String) DateFormat.format(INCREMENT_KEY_SHORT, tempCal) );

            // If temp, the counter, has reach the new date, then we are finished
            // Otherwise, advance temp to next month/day and add that increment

            if (tempDateString.equals(newDateString)) { break; }
            else { tempCal.add(CAL_INCREMENT, 1); }
        }

        return increments;
    }
}
