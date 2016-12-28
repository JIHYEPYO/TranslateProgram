package com.example.pyojihye.translateprogram.Activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;
import com.example.pyojihye.translateprogram.Movement.MyFragmentPagerAdapter;
import com.example.pyojihye.translateprogram.R;

public class ViewerOptionActivity extends AppCompatActivity {
    private final String TAG="ViewerOptionActivity";

    public MyFragmentPagerAdapter myFragmentPagerAdapter;
    private static ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"onCreate()");

        setTitle("Viewer Option");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer_mode_option);

        pager=(ViewPager)findViewById(R.id.pager);
        pager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabStrip =(PagerSlidingTabStrip)findViewById(R.id.tabs);
        tabStrip.setViewPager(pager);
    }

    public static void setPager(int index){
        pager.setCurrentItem(index);
    }
}