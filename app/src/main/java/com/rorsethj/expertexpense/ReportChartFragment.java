package com.rorsethj.expertexpense;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;


public class ReportChartFragment extends Fragment {

    public String chartType;

    public ReportChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_report_chart, container, false);

        plotCorrectGraph(view);

        return view;
    }

    public void plotCorrectGraph(View view) {


        ArrayList<String> labels = new ArrayList<>();
        float[] values = {};

        // Determine what to graph
        switch (chartType) {

            // TODO:
            case "Expense By Category":
                // Get all expenses, labels are categories, values are expense amounts per
                // Show total expenses over all categories
                break;
            case "Daily Expense":
                // Get expense last 7 days in bar chart
                break;
            case "Monthly Expense":
                // Show total expenses each month since january, show average over those months
                break;
            case "Income By Category":
                // Get all incomes, split up labels as categories, values are incomes per
                // Show total income over all categories
                break;
            case "Daily Income":
                // Bar chart shows total income for each of last 7 days
                break;
            case "Monthly Income":
                // Bar chart shows YTD each months income, display average also
                break;
            case "Income Vs Expense":
                // Show pie chart with income and expense labels, values are this months amount
                // for each. Should be income vs expense totals for the month
                break;
            case "Daily Balance":
                // Show, for each day, a +/- standing in a line graph
                break;
        }

        plotPieChart(view, chartType, labels, values);
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

        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // Create the chart view
        PieChart chart = new PieChart(getContext());
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setHoleRadius(40);
        chart.setTransparentCircleRadius(40);

        // Animate the chart appearing
        chart.setData(pieData);
        chart.animateY(2000);

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
