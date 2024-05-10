package com.econok.economykanban.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    private boolean swipeEnabled;

    public CustomViewPager(@NonNull Context context) {
        super(context);
        swipeEnabled = true;
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        swipeEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return swipeEnabled && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return swipeEnabled && super.onInterceptTouchEvent(ev);
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        this.swipeEnabled = swipeEnabled;
    }
}
