package com.rorsethj.expertexpense;

import java.util.Calendar;
import java.util.Date;


public class DateRange {

    public static class DatePair {
        final long a;
        final long b;

        DatePair(final long oldDate, final long newDate) { this.a = oldDate; this.b = newDate; }
        public long getOld() { return a; }
        public long getNew() { return b; }
    }


    public static DatePair getDatesForRange(String currentlySelectedPeriod) {

        Date nowDate = new Date();

        long oldDate = 0;
        long newDate = 0;

        // Determine the correct date range to filter transactions by
        switch (currentlySelectedPeriod) {

            case "Current Month":
                Calendar firstOfThisMonth = Calendar.getInstance();
                firstOfThisMonth.set(Calendar.DAY_OF_MONTH, 1);

                oldDate = firstOfThisMonth.getTime().getTime();
                newDate = nowDate.getTime();
                break;

            case "Month to Date":
                Calendar oneMonthAgo = Calendar.getInstance();
                oneMonthAgo.add(Calendar.MONTH, -1);

                oldDate = oneMonthAgo.getTime().getTime();
                newDate = nowDate.getTime();
                break;

            case "Last Month":
                Calendar firstOfLastMonth = Calendar.getInstance();
                firstOfLastMonth.add(Calendar.MONTH, -1);   // Go to last month
                firstOfLastMonth.set(Calendar.DATE, 1); // Go to first of that month

                oldDate = firstOfLastMonth.getTime().getTime();

                // Get maximum day of this month, write that to new date
                firstOfLastMonth.set(Calendar.DATE, firstOfLastMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
                newDate = firstOfLastMonth.getTime().getTime();
                break;

            case "Next Month":
                Calendar firstOfNextMonth = Calendar.getInstance();
                firstOfNextMonth.add(Calendar.MONTH, 1);   // Go to next month
                firstOfNextMonth.set(Calendar.DATE, 1); // Go to first of that month

                oldDate = firstOfNextMonth.getTime().getTime();

                // Get maximum day of this month, write that to new date
                firstOfNextMonth.set(Calendar.DATE, firstOfNextMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
                newDate = firstOfNextMonth.getTime().getTime();
                break;

            case "Last 7 Days":
                Calendar sevenAgo = Calendar.getInstance();
                sevenAgo.add(Calendar.DAY_OF_YEAR, -7);

                oldDate = sevenAgo.getTime().getTime();
                newDate = nowDate.getTime();
                break;

            case "Last 30 Days":
                Calendar thirtyAgo = Calendar.getInstance();
                thirtyAgo.add(Calendar.DAY_OF_YEAR, -30);

                oldDate = thirtyAgo.getTime().getTime();
                newDate = nowDate.getTime();
                break;

            case "Last 60 Days":
                Calendar sixtyAgo = Calendar.getInstance();
                sixtyAgo.add(Calendar.DAY_OF_YEAR, -60);

                oldDate = sixtyAgo.getTime().getTime();
                newDate = nowDate.getTime();
                break;

            case "Last 90 Days":
                Calendar ninetyAgo = Calendar.getInstance();
                ninetyAgo.add(Calendar.DAY_OF_YEAR, -90);

                oldDate = ninetyAgo.getTime().getTime();
                newDate = nowDate.getTime();
                break;

            case "Last 6 Months":
                Calendar sixMonthsAgo = Calendar.getInstance();
                sixMonthsAgo.add(Calendar.MONTH, -6);

                oldDate = sixMonthsAgo.getTime().getTime();
                newDate = nowDate.getTime();
                break;

            case "Year to Date":
                Calendar yearAgo = Calendar.getInstance();
                yearAgo.add(Calendar.YEAR, -1);

                oldDate = yearAgo.getTime().getTime();
                newDate = nowDate.getTime();
                break;

            case "Last Year":
                Calendar lastYearFirst = Calendar.getInstance();
                lastYearFirst.add(Calendar.YEAR, -1);
                lastYearFirst.set(Calendar.DAY_OF_YEAR, 1);

                oldDate = lastYearFirst.getTime().getTime();

                lastYearFirst.set(Calendar.MONTH, 11);
                lastYearFirst.set(Calendar.DAY_OF_MONTH, 31);
                newDate = lastYearFirst.getTime().getTime();
                break;

            case "All":
            default:

                oldDate = 0;
                newDate = nowDate.getTime();
        }


        return new DatePair(oldDate, newDate);
    }
}
