package com.itachi1706.hypixelstatistics;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.AsyncAPI.Friends.GetFriendsListUUID;


public class FriendListActivity extends ActionBarActivity {

    TextView friendsCount;
    ListView friendListView;
    private boolean isSearchActive = false;
    private Menu activityMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        friendsCount = (TextView) findViewById(R.id.friendCount);
        friendListView = (ListView) findViewById(R.id.lvFriendList);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        Log.d("FRIEND-INTENT", "Received an intent. Passing it through");
        //handleIntent(getIntent());
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            Log.d("FRIEND-INTENT", "Search intent found. Processing it now...");
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            Log.d("FRIEND-INTENT", "Search query in intent is: " + searchQuery);
            //Dismiss searchview if still active
            if (isSearchActive) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    activityMenu.findItem(R.id.search).collapseActionView();
                    isSearchActive = false;
                }
                //SearchView view = (SearchView) activityMenu.findItem(R.id.search).getActionView();
            }

            //Do stuff with query
            friendsCount.setText("You asked for: " + searchQuery);
            Toast.makeText(this.getApplicationContext(), "Retrieving Friend List of " + searchQuery, Toast.LENGTH_SHORT).show();
            new GetFriendsListUUID(this, friendListView, friendsCount).execute(searchQuery);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
        activityMenu = menu;

        //Associate searchable config with SearchView
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
            return true;
        } else if (id == R.id.search){
            isSearchActive = true;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}