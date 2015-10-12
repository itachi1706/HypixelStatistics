package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.itachi1706.hypixelstatistics.GeneralPrefActivity;
import com.itachi1706.hypixelstatistics.GuildActivity;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.PlayerInfo.PlayerInfoQuery;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.PlayerInfo.PlayerInfoQueryHead;
import com.itachi1706.hypixelstatistics.util.GeneratePlaceholderDrawables;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import net.hypixel.api.reply.PlayerReply;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PlayerInfoActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    CoordinatorLayout coordinatorLayout;

    private SharedPreferences sp;

    private boolean isSearchActive = false;
    private Menu activityMenu;
    private CursorAdapter searchCursor;

    private boolean usingUUID = false;

    private ProgressDialog checkProgress;

    private String playerJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Theme
        MainStaticVars.setLayoutAccordingToPrefs(this, false);
        setContentView(R.layout.activity_player_info);
        this.toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(this.toolbar);
        //Set Theme for toolbar
        toolbar.setBackgroundResource(MainStaticVars.getActionBarColor(this));

        //Initialize
        this.sp = PreferenceManager.getDefaultSharedPreferences(this);
        this.coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_coordinator_layout);
        this.viewPager = (ViewPager) findViewById(R.id.activity_viewpager);
        setupViewPager(viewPager);
        this.tabLayout = (TabLayout) findViewById(R.id.activity_tablayout);
        this.tabLayout.setupWithViewPager(viewPager);
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
                    main.processPlayerJson(playerJsonString);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Do the player info processing stuff that was in fragment originally
        playerJsonString = "";

        if (this.getIntent().hasExtra("player")){
            String intentPlayer = this.getIntent().getStringExtra("player");
            handleNewMethod(intentPlayer);
        } else if (this.getIntent().hasExtra("playerUuid")){
            String intentPlayerUid = this.getIntent().getStringExtra("playerUuid");
            usingUUID = true;
            handleNewMethod(intentPlayerUid);
        }

        handleIntent(getIntent());
        initSearchableAdapter();
    }

    @Override
    protected void onNewIntent(Intent intent){
        handleIntent(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("Player Info", "Resuming App");
        MainStaticVars.updateTimeout(this);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new GeneralInfoFragment(), "Session");  //TODO: WIP. Future General Information
        adapter.addFrag(new GeneralStatisticsFragment(), "Player Stats");
        adapter.addFrag(new FriendsStatisticsFragment(), "Friends");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_info, menu);
        activityMenu = menu;

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSuggestionsAdapter(searchCursor);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchCursor.getItem(position);
                String suggestion = cursor.getString(1);
                searchView.setQuery(suggestion, true); // submit query now
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                generateSuggestions(newText);
                return true;
            }
        });

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
        } else if (id == R.id.view_debug) {
            new AlertDialog.Builder(this).setTitle("JSON Information")
                    .setMessage(playerJsonString)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return true;
        } else if (id == R.id.view_known_alias){
            String message;
            if (!playerJsonString.equals("")){
                Gson gson = new Gson();
                PlayerReply reply = gson.fromJson(playerJsonString, PlayerReply.class);
                if (reply.getPlayer().has("knownAliases")){
                    JsonArray arr = reply.getPlayer().getAsJsonArray("knownAliases");
                    StringBuilder listOfAliases = new StringBuilder();
                    for (JsonElement e : arr){
                        listOfAliases.append(e.getAsString()).append("\n");
                    }
                    message = "\n" + listOfAliases.toString();
                } else {
                    message = "\nPlayer has no known aliases";
                }
            } else
                message = "\nPlease query for a player to view his aliases";
            AlertDialog.Builder debugAlert = new AlertDialog.Builder(this);
            debugAlert.setTitle("Known Aliases");
            debugAlert.setMessage(message);
            debugAlert.setPositiveButton(android.R.string.ok, null);
            debugAlert.show();
            return true;
        } else if (id == R.id.view_guild){
            if (playerJsonString.length() > 200) {
                Gson gson = new Gson();
                PlayerReply reply = gson.fromJson(playerJsonString, PlayerReply.class);
                if (reply.getPlayer().has("displayname")) {
                    Intent intent = new Intent(this, GuildActivity.class);
                    intent.putExtra("playername", reply.getPlayer().get("displayname").getAsString());
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(this).setTitle("Player not found")
                            .setMessage("Please search a player to view guild")
                            .setPositiveButton(android.R.string.ok, null).show();
                }
            } else {
                new AlertDialog.Builder(this).setTitle("Player not found")
                        .setMessage("Please search a player to view guild")
                        .setPositiveButton(android.R.string.ok, null).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void generateSuggestions(String searchTerm){
        final MatrixCursor c = new MatrixCursor(new String[]{ "_id", "name" });
        String[] history = getHistory();
        for (int i = 0; i < history.length; i++){
            if (history[i].toLowerCase().startsWith(searchTerm.toLowerCase()))
                c.addRow(new Object[] {i, history[i]});
        }

        searchCursor.changeCursor(c);
    }

    private void initSearchableAdapter(){
        final String[] from = {"name"};
        final int[] to = {android.R.id.text1};
        //noinspection deprecation
        searchCursor = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to);
    }

    private String[] getHistory(){
        String hist = CharHistory.getListOfHistory(sp);
        ArrayList<String> tmp = new ArrayList<>();
        if (hist != null) {
            Gson gson = new Gson();
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            List<HistoryArrayObject> histCheck = CharHistory.convertHistoryArrayToList(check.getHistory());
            for (HistoryArrayObject histCheckName : histCheck) {
                tmp.add(histCheckName.getDisplayname());
            }
        }

        String[] results = new String[tmp.size()];
        for (int i = 0; i < results.length; i++){
            results[i] = tmp.get(i);
        }
        return results;
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

        if (searchQuery.equals("")){
            NotifyUserUtil.showShortDismissSnackbar(findViewById(android.R.id.content), "Please enter a search term!");
            return;
        }

        handleNewMethod(searchQuery);
    }

    private void handleNewMethod(final String query){
        if (!usingUUID) { //Not confirmed UUID, go and check
            if (query.length() == 32) usingUUID = true; //Might be UUID
        }
        if (checkProgress != null && checkProgress.isShowing()) checkProgress.dismiss();
        checkProgress = null;
        checkProgress = new ProgressDialog(this);
        checkProgress.setCancelable(false);
        checkProgress.setIndeterminate(true);
        checkProgress.setTitle("Querying Server...");
        checkProgress.setMessage("Getting Player Statistics from the Hypixel API");
        checkProgress.show();

        new PlayerInfoQuery(this, usingUUID, new PlayerInfoHandler(this)).execute(query);
        usingUUID = false;
    }

    private void updateActionBar(String title, String subtitle){
        ActionBar ab = getSupportActionBar();
        if (ab == null) return;
        ab.setLogo(null);
        ab.setTitle(Html.fromHtml(title));
        ab.setSubtitle(Html.fromHtml(subtitle));
    }

    static class PlayerInfoHandler extends Handler {
        WeakReference<PlayerInfoActivity> mActivity;

        PlayerInfoHandler(PlayerInfoActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            PlayerInfoActivity activity = mActivity.get();

            super.handleMessage(msg);

            switch (msg.what){
                case 1000: //Success
                    String json = (String) msg.getData().get("playerJson");
                    activity.playerJsonString = json;
                    Gson gson = new Gson();
                    PlayerReply reply = gson.fromJson(json, PlayerReply.class);

                    //Succeeded, Process Action Bar stuff
                    if (MinecraftColorCodes.checkDisplayName(reply)) {
                        new PlayerInfoQueryHead(activity, activity.getSupportActionBar()).execute(reply.getPlayer().get("displayname").getAsString());
                    }
                    activity.updateActionBar("&nbsp;" + MinecraftColorCodes.parseHypixelRanks(reply), "&nbsp;&nbsp;<b>" + MinecraftColorCodes.getPlayerServerRankFormatted(reply) + "</b>");
                    activity.toolbar.setLogo(GeneratePlaceholderDrawables.generateFromMcNameWithInitialsConversion(MinecraftColorCodes.checkDisplayName(reply) ?
                            reply.getPlayer().get("displayname").getAsString() : reply.getPlayer().get("playername").getAsString()));

                    if (activity.checkProgress.isShowing()) activity.checkProgress.dismiss();
                    ViewPagerAdapter adapter = (ViewPagerAdapter) activity.viewPager.getAdapter();
                    Fragment currentFragment = adapter.getItem(activity.viewPager.getCurrentItem());
                    if (currentFragment instanceof BaseFragmentCompat){
                        BaseFragmentCompat fragmentCompat = (BaseFragmentCompat) currentFragment;
                        fragmentCompat.processPlayerObject(reply);
                    }
                    break;
                case 1001: //Fail Display Error Message
                    if (activity.checkProgress.isShowing()) activity.checkProgress.dismiss();
                    String errorMessage = (String) msg.getData().get("errorMsg");
                    activity.updateActionBar(errorMessage, "<b>ERROR</b>");
                    break;

            }
        }
    }

}
