package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.itachi1706.hypixelstatistics.GeneralPrefActivity;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

public class PlayerInfoActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    CoordinatorLayout coordinatorLayout;

    private boolean isSearchActive = false;
    private Menu activityMenu;

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

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new PlayerInfoActivityFragment(), "Player Stats");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_info, menu);
        activityMenu = menu;

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, GeneralPrefActivity.class));
            return true;
        } else if (id == R.id.search){
            isSearchActive = true;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Searchable intent handling
    private void handleIntent(Intent intent){
        if (!Intent.ACTION_SEARCH.equals(intent.getAction())) return;
        Log.d("PLAYER-INFO-SEARCH", "Search intent found. Processing it now...");
        String searchQuery = intent.getStringExtra(SearchManager.QUERY);
        Log.d("PLAYER-INFO-SEARCH", "Search query in intent is: " + searchQuery);
        if (isSearchActive) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                activityMenu.findItem(R.id.search).collapseActionView();
                isSearchActive = false;
            }
        }

        //TODO: Handle query
        NotifyUserUtil.showShortDismissSnackbar(findViewById(android.R.id.content), "Search Coming Soon! Current Search Query is: " + searchQuery);
    }

}
