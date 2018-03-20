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


public class SetupFragment extends Fragment {

    // Allow parent activity to implement functionality and manage this fragment
    public interface SetupInterface {
        public void didConfirmDone();
    }

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


        ViewPager pager = (ViewPager) view.findViewById(R.id.setupViewPager);
        PagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.setupTabLayout);
        tabLayout.setupWithViewPager(pager, true);

        Button button = (Button) view.findViewById(R.id.setupDoneButton);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                setupDelegate.didConfirmDone();
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

                case 0: return new GeneralSetupFragment();
                case 1: return new AccountSetupFragment();
                default:
                    return new GeneralSetupFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}