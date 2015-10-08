package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;

public class PlayerInfoActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Theme
        MainStaticVars.setLayoutAccordingToPrefs(this, false);

        setContentView(R.layout.activity_player_info);

        this.toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(this.toolbar);

        //Set Theme again i guess
        toolbar.setBackgroundResource(MainStaticVars.getActionBarColor(this));

        this.coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_coordinator_layout);

        this.viewPager = (ViewPager) findViewById(R.id.activity_viewpager);
        setupViewPager(this.viewPager);

        this.tabLayout = (TabLayout) findViewById(R.id.activity_tablayout);
        this.tabLayout.setupWithViewPager(this.viewPager);
        this.tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        this.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        this.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                Fragment currentFrag = adapter.getItem(viewPager.getCurrentItem());
                if (currentFrag instanceof BaseFragmentCompat) {
                    BaseFragmentCompat main = (BaseFragmentCompat) currentFrag;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new PlayerInfoActivityFragment(), "Player Stats");

        viewPager.setAdapter(adapter);
    }

}
