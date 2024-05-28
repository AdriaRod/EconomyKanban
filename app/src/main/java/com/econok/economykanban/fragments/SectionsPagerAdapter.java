package com.econok.economykanban.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private final String currencySymbol;

    public SectionsPagerAdapter(Context context, FragmentManager fm, String currencySymbol) {
        super(fm);
        mContext = context;
        this.currencySymbol = currencySymbol;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TransactionsFragment transactionsFragment = new TransactionsFragment();
                Bundle args = new Bundle();
                args.putString("MonedaFromCentral", currencySymbol);
                transactionsFragment.setArguments(args);
                return transactionsFragment;
            case 1:
                CategoriesFragment categoriesFragment = new CategoriesFragment();
                Bundle categoriesArgs = new Bundle();
                categoriesArgs.putString("MonedaFromCentral", currencySymbol);
                categoriesFragment.setArguments(categoriesArgs);
                return categoriesFragment;
            case 2:
                return new GraphicsFragment();
            case 3:
                return new SettingsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        // Show 4 total pages.
        return 4;
    }
}
