package com.rorsethj.expertexpense;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ReusableReportListAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public ReusableReportListAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new ReportsExpenseFragment();
        } else if (position == 1){
            return new ReportsIncomeFragment();
        } else if (position == 2){
            return new ReportsCashFlowFragment();
        } else {
            return new ReportsBalanceFragment();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 4;
    }

    // Determine title for each tab
    @Override
    public CharSequence getPageTitle(int position) {

        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.reports_tab_expense);
            case 1:
                return mContext.getString(R.string.reports_tab_income);
            case 2:
                return mContext.getString(R.string.reports_tab_cashflow);
            case 3:
                return mContext.getString(R.string.reports_tab_balance);
            default:
                return null;
        }
    }

}

