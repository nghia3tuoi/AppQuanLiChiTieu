package com.uit.quanlychitieu.ui.category;

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
import com.uit.quanlychitieu.R;
import com.uit.quanlychitieu.ui.category.expense_manager.CategoryExpenseFragment;
import com.uit.quanlychitieu.ui.category.income_manager.CategoryIncomeFragment;

public class CategoryFragment extends Fragment {

    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private TabLayout tabLayout;

    private CategoryViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(CategoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_category, container, false);

        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        viewPager = root.findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout = root.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.expense_catogory);
        tabLayout.getTabAt(1).setIcon(R.drawable.income_category);

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
                fragment = new CategoryExpenseFragment();
            } else if (position == 1) {
                fragment = new CategoryIncomeFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LOẠI CHI";
                case 1:
                    return "LOẠI THU";
            }
            return null;
        }
    }
}