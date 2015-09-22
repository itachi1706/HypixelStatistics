package com.itachi1706.hypixelstatistics.AsyncAPI.Friends;

import android.app.Activity;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.ListViewAdapters.FriendsListAdapter;
import com.itachi1706.hypixelstatistics.Objects.FriendsObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;

import net.hypixel.api.reply.FriendsReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 11/4/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.AsyncAPI.Friends
 */
public class GetFriendsListPlayer extends AsyncTask<String, Void, String> {

    private String playerName;
    private Exception except;
    private Activity mActivity;
    private ListView playerListView;
    private TextView tvResult;
    private boolean invalid = false;

    //Progress Information
    private ProgressBar progressCircle;
    private TextView progressInfo;

    public GetFriendsListPlayer(Activity mActivity, ListView playerListView, TextView tvResult, ProgressBar progress, TextView progressInfo){
        this.mActivity = mActivity;
        this.playerListView = playerListView;
        this.tvResult = tvResult;
        this.progressCircle = progress;
        this.progressInfo = progressInfo;
    }

    public GetFriendsListPlayer(Activity mActivity, ListView playerListView, TextView tvResult, ProgressBar progress, TextView progressInfo, boolean invalid){
        this.mActivity = mActivity;
        this.playerListView = playerListView;
        this.tvResult = tvResult;
        this.progressCircle = progress;
        this.progressInfo = progressInfo;
        this.invalid = invalid;
    }

    @Override
    protected String doInBackground(String... player) {
        String url = MainStaticVars.API_BASE_URL + "?type=friends&player=" + player[0];
        url = MainStaticVars.updateURLWithApiKeyIfExists(url);
        String tmp = "";
        playerName = player[0];
        Log.i("FRIENDS-UUID", "Getting Friends List Data for " + player[0]);
        //Get Statistics
        try {
            URL urlConn = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlConn.openConnection();
            conn.setConnectTimeout(MainStaticVars.HTTP_QUERY_TIMEOUT);
            conn.setReadTimeout(MainStaticVars.HTTP_QUERY_TIMEOUT);
            InputStream in = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }
            in.close();
            tmp = str.toString();


        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            except = e;
        }

        return tmp;
    }

    protected void onPostExecute(String json) {
        if (except != null) {
            if (except instanceof SocketTimeoutException) {
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Connection Timed Out. Try again later");
            } else {
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), except.getMessage());
            }
            return;
        }
        Gson gson = new Gson();
        if (!MainStaticVars.checkIfYouGotJsonString(json)) {
            if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "CloudFlare timeout 524 occurred");
            else
                NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "An error occurred (Invalid JSON String)");
            return;
        }
        FriendsReply reply = gson.fromJson(json, FriendsReply.class);
        if (reply.isThrottle()) {
            NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Query Throttled (Limit reached)");
            return;
        } else if (!reply.isSuccess()) {
            NotifyUserUtil.createShortToast(mActivity.getApplicationContext(), "Invalid UUID");
            return;
        }
        if (reply.getRecords().size() == 0) {
            if (playerName.length() == 32 && !invalid)
                new GetFriendsListUUID(mActivity, playerListView, tvResult, progressCircle, progressInfo, true).execute(playerName);
            else {
                tvResult.setText(Html.fromHtml(MinecraftColorCodes.parseColors("Friends: §b0§r")));
                String[] noFriendsSadFace = {"No Friends Found :("};
                ArrayAdapter<String> noFriendsAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, noFriendsSadFace);
                playerListView.setAdapter(noFriendsAdapter);

                //Dismiss progress bar and info
                progressInfo.setVisibility(View.INVISIBLE);
                progressCircle.setVisibility(View.INVISIBLE);
            }
            return;
        }
        MainStaticVars.friends_session_data.clear();
        MainStaticVars.friends_last_online_data.clear();

        String uuidValue;
        //Get Owner UUID
        JsonElement check1 = reply.getRecords().get(1);
        if (check1.getAsJsonObject().get("sender").getAsString().equals(playerName))
            uuidValue = check1.getAsJsonObject().get("uuidSender").getAsString();
        else
            uuidValue = check1.getAsJsonObject().get("uuidReceiver").getAsString();

        //Process Friends Requests
        tvResult.setText(Html.fromHtml(MinecraftColorCodes.parseColors("Friends: §b" + reply.getRecords().size() + "§r")));

        //Get Name of player whose's friends list is being shown
        String user = checkHistory(uuidValue);
        if (user.equals("err")){
            //Get Name
            new GetFriendsOwner(mActivity, tvResult, reply.getRecords().size()).execute(uuidValue);
        } else {
            //Set Name
            tvResult.setText(Html.fromHtml(MinecraftColorCodes.parseColors(user + "'s friends" +
                    "<br />Friends: §b" + reply.getRecords().size() + "§r")));
            MainStaticVars.friendOwner = user;
        }

        //Update info
        progressInfo.setText("Found " + reply.getRecords().size() + " friends. Processing now...");
        ArrayList<FriendsObject> friendsListTemp = new ArrayList<>();
        int processedProgress = 1, processedSize = reply.getRecords().size();
        for (JsonElement e : reply.getRecords()) {
            JsonObject obj = e.getAsJsonObject();
            long friendsFromDate = obj.get("started").getAsLong();
            String senderUUID = obj.get("uuidSender").getAsString();
            String receiverUUID = obj.get("uuidReceiver").getAsString();
            FriendsObject friends;
            if (senderUUID.equals(uuidValue)) {
                friends = new FriendsObject(friendsFromDate, receiverUUID, true);
            } else {
                friends = new FriendsObject(friendsFromDate, senderUUID, false);
            }
            friendsListTemp.add(friends);
            //Update progress
            progressInfo.setText("Processed " + processedProgress + "/" + processedSize);
        }
        progressInfo.setText("Processing Completed. Retrieving Friends Data now...");
        MainStaticVars.friendsListSize = processedSize;
        MainStaticVars.friendsList.clear();
        //Check history
        for (FriendsObject f : friendsListTemp) {
            String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()));
            boolean hasHist = false;
            MainStaticVars.friendsListAdapter = new FriendsListAdapter(mActivity, R.layout.listview_guild_desc, MainStaticVars.friendsList);
            playerListView.setAdapter(MainStaticVars.friendsListAdapter);
            if (hist != null) {
                HistoryObject check = gson.fromJson(hist, HistoryObject.class);
                List<HistoryArrayObject> histCheck = CharHistory.convertHistoryArrayToList(check.getHistory());
                for (HistoryArrayObject histCheckName : histCheck) {
                    if (histCheckName.getUuid().equals(f.getFriendUUID())) {
                        //Check if history expired
                        if (CharHistory.checkHistoryExpired(histCheckName)) {
                            //Expired, reobtain
                            histCheck.remove(histCheckName);
                            CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()), histCheck);
                            Log.d("HISTORY", "History Expired");
                            break;
                        } else {
                            f.set_mcNameWithRank(MinecraftColorCodes.parseHistoryHypixelRanks(histCheckName));
                            f.set_mcName(histCheckName.getDisplayname());
                            f.set_done(true);
                            MainStaticVars.friendsList.add(f);
                            hasHist = true;
                            Log.d("Player", "Found player " + f.get_mcName());
                            //Update process
                            progressInfo.setText("Retrieved " + MainStaticVars.friendsList.size() + "/" + MainStaticVars.friendsListSize + " friends");
                            break;
                        }
                    }
                }
                if (!hasHist)
                    new GetFriendsName(mActivity, progressCircle, progressInfo).execute(f);
                checkIfComplete();
            }
        }
    }

    private void checkIfComplete(){
        boolean done = true;
        for (FriendsObject o : MainStaticVars.friendsList){
            if (!o.is_done()){
                done = false;
                break;
            }
        }

        if (done){
            //FriendsListAdapter adapter = new FriendsListAdapter(mActivity, R.layout.listview_guild_desc, MainStaticVars.friendsList);
            //playerListView.setAdapter(adapter);
            MainStaticVars.friendsListAdapter.updateAdapter(MainStaticVars.friendsList);
            MainStaticVars.friendsListAdapter.notifyDataSetChanged();
        }

        if (MainStaticVars.friendsList.size() >= MainStaticVars.friendsListSize){
            //Complete. Hide progress data
            progressCircle.setVisibility(View.INVISIBLE);
            progressInfo.setVisibility(View.INVISIBLE);
        }
    }

    private String checkHistory(String uuid){
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()));
        Gson gson = new Gson();
        if (hist != null) {
            HistoryObject check = gson.fromJson(hist, HistoryObject.class);
            List<HistoryArrayObject> histCheck = CharHistory.convertHistoryArrayToList(check.getHistory());
            for (HistoryArrayObject histCheckName : histCheck) {
                if (histCheckName.getUuid().equals(uuid)) {
                    //Check if history expired
                    if (CharHistory.checkHistoryExpired(histCheckName)) {
                        //Expired, reobtain
                        histCheck.remove(histCheckName);
                        CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()), histCheck);
                        Log.d("HISTORY", "History Expired");
                        break;
                    } else {
                        return MinecraftColorCodes.parseHistoryHypixelRanks(histCheckName);
                    }
                }
            }
        }
        return "err";
    }
}
