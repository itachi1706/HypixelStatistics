package com.itachi1706.hypixelstatistics;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.AsyncAPI.Friends.GetFriendsListPlayer;
import com.itachi1706.hypixelstatistics.AsyncAPI.Friends.GetFriendsListUUID;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.Objects.FriendsObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class FriendListActivity extends AppCompatActivity {

    TextView friendsCount, progressInfo;
    ProgressBar loadingStatus;
    ListView friendListView;
    private boolean isSearchActive = false;
    private Menu activityMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Theme
        setTheme(MainStaticVars.getTheme(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.getWindow().setStatusBarColor(getResources().getColor(MainStaticVars.getStatusBarColor(this)));

        setContentView(R.layout.activity_friend_list);

        MainStaticVars.updateTimeout(this);
        friendsCount = (TextView) findViewById(R.id.friendCount);
        friendListView = (ListView) findViewById(R.id.lvFriendList);
        loadingStatus = (ProgressBar) findViewById(R.id.pbFriendsList);
        progressInfo = (TextView) findViewById(R.id.tvProgressInfo);

        if (this.getIntent().hasExtra("playeruuid")){
            //Automatically search this UUID
            retrieveFriendsList(this.getIntent().getStringExtra("playeruuid"));
        }

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = friendListView.getItemAtPosition(position);
                if (obj instanceof FriendsObject){
                    final FriendsObject friend = (FriendsObject) obj;
                    String message;
                    if (friend.isSendFromOwner()){
                        message = "Friend Request sent by " + MainStaticVars.friendOwner + "<br />";
                    } else {
                        message = "Sent Friend Request to " + MainStaticVars.friendOwner + "<br />";
                    }
                    String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(friend.getDate()));
                    message += "Friends From: " + timeStamp;
                    new AlertDialog.Builder(FriendListActivity.this).setTitle(Html.fromHtml(friend.get_mcNameWithRank()))
                        .setMessage(Html.fromHtml(message))
                        .setPositiveButton("View Player Info", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intentE = new Intent(FriendListActivity.this, ExpandedPlayerInfoActivity.class);
                                intentE.putExtra("player", friend.get_mcName());
                                startActivity(intentE);
                            }
                        }).setNegativeButton("Close", null).show();

                }
            }
        });

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
            retrieveFriendsList(searchQuery);
        }
    }

    private void retrieveFriendsList(String searchQuery){
        loadingStatus.setVisibility(View.VISIBLE);
        progressInfo.setVisibility(View.VISIBLE);
        progressInfo.setText("Retriving Friends List... Querying API...");
        Toast.makeText(this.getApplicationContext(), "Retrieving Friend List of " + searchQuery, Toast.LENGTH_SHORT).show();
        if (searchQuery.length() == 32)
            new GetFriendsListUUID(this, friendListView, friendsCount, loadingStatus, progressInfo).execute(searchQuery);
        else
            new GetFriendsListPlayer(this, friendListView, friendsCount, loadingStatus, progressInfo).execute(searchQuery);
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

        //Not triggered by anywhere else
        if (!this.getIntent().hasExtra("playeruuid"))
            friendsCount.setText("To start, press the search icon!");
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
            startActivity(new Intent(FriendListActivity.this, GeneralPrefActivity.class));
            return true;
        } else if (id == R.id.search){
            isSearchActive = true;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}