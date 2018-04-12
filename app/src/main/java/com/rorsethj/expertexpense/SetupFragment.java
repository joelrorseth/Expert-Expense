package com.rorsethj.expertexpense;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SetupFragment extends Fragment implements
        GeneralSetupFragment.GeneralSetupInterface, AccountSetupFragment.AccountSetupInterface {

    // Allow parent activity to implement functionality and manage this fragment
    public interface SetupInterface {
        void didConfirmDone(String currency, String language, String cashAmount, String bankAmount);
    }

    private String selectedCurrency = "CAD";
    private String selectedLanguage = "English";
    private String cashAccountAmount = null;
    private String bankAccountAmount = null;

    private GeneralSetupFragment generalSetupFrag;
    private AccountSetupFragment accountSetupFrag;

    private SetupInterface setupDelegate;

    public void setSetupDelegate(SetupInterface conformer) {
        setupDelegate = conformer;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // Inflate XML resource for this fragment
        View view = inflater.inflate(R.layout.fragment_setup, container, false);

        // Establish the fragments and this as delegate, ahead of time
        generalSetupFrag = new GeneralSetupFragment();
        generalSetupFrag.setupDelegate = this;

        accountSetupFrag = new AccountSetupFragment();
        accountSetupFrag.setupDelegate = this;

        ViewPager pager = (ViewPager) view.findViewById(R.id.setupViewPager);
        PagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.setupTabLayout);
        tabLayout.setupWithViewPager(pager, true);

        Button button = (Button) view.findViewById(R.id.setupDoneButton);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Pass the selected fields being maintained in this fragment back to the delegate
                setupDelegate.didConfirmDone(selectedCurrency, selectedLanguage,
                        cashAccountAmount, bankAccountAmount);
            }
        });


        return view;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                // Put correct fragment in swipe pager container
                case 0: return generalSetupFrag;
                case 1: return accountSetupFrag;
                default:
                    return generalSetupFrag;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    // MARK: Implementation for delegate methods

    @Override
    public void didUpdateCurrency(String currency) {
        this.selectedCurrency = currency;
    }

    @Override
    public void didUpdateLanguage(String language) {
        this.selectedLanguage = language;
    }

    @Override
    public void didChangeCashAmount(String amount) {
        this.cashAccountAmount = amount;
    }

    @Override
    public void didChangeBankAmount(String amount) {
        this.bankAccountAmount = amount;
    }
}