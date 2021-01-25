package com.uit.quanlychitieu.ui.statistic;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.uit.quanlychitieu.Language;
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.ui.category.CategoryFragment;
import com.uit.quanlychitieu.ui.category.expense_manager.CategoryExpenseFragment;
import com.uit.quanlychitieu.ui.category.income_manager.CategoryIncomeFragment;
import com.uit.quanlychitieu.ui.login.LoginActivity;
import com.uit.quanlychitieu.ui.statistic.category_statistic.CategoryStatisticFragment;
import com.uit.quanlychitieu.ui.statistic.data_statistic.DataStatisticFragment;
import com.uit.quanlychitieu.ui.statistic.month_statistic.MonthStatisticFragment;
import com.uit.quanlychitieu.ui.statistic.week_statistic.WeekStatisticFragment;

public class StatisticFragment extends Fragment {

    private StatisticViewModel statisticViewModel;

    private ViewPager viewPager;
    private StatisticFragment.SectionsPagerAdapter sectionsPagerAdapter;
    private TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Language.setLanguage(getContext(), LoginActivity.LANGUAGE);
        statisticViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(StatisticViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistic, container, false);

        sectionsPagerAdapter = new StatisticFragment.SectionsPagerAdapter(getChildFragmentManager());
        viewPager = root.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout = root.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return root;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new DataStatisticFragment();
            } else if (position == 1) {
                fragment = new CategoryStatisticFragment();
            } else if (position == 2) {
                fragment = new WeekStatisticFragment();
            } else if (position == 3) {
                fragment = new MonthStatisticFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.statistics_data_title);
                case 1:
                    return getResources().getString(R.string.statistics_category_title);
                case 2:
                    return getResources().getString(R.string.statistics_week_title);
                case 3:
                    return getResources().getString(R.string.statistics_month_title);
            }
            return null;
        }
    }
}