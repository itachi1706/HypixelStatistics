package com.itachi1706.hypixelstatistics.RevampedDesign.Fragments.PlayerInfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.FriendsObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.Friends.GenerateFriendsList;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.Friends.RetriveFriendsName;
import com.itachi1706.hypixelstatistics.RevampedDesign.Fragments.BaseFragmentCompat;
import com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters.FriendsRecyclerAdapter;
import com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters.StringRecyclerAdapter;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import net.hypixel.api.reply.FriendsReply;
import net.hypixel.api.reply.PlayerReply;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FriendsStatisticsFragment extends BaseFragmentCompat {

    public FriendsStatisticsFragment() {
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_player_info_recycler;
    }

    //Fragment Elements
    private RecyclerView recyclerView;
    private ProgressDialog processDialog;

    private Context context;


    static String[] noStatistics = {"To start, press the Search icon!"};
    private StringRecyclerAdapter noStatAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(getFragmentLayout(), container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.player_info_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        noStatAdapter = new StringRecyclerAdapter(noStatistics);
        context = getContext();

        processPlayerJson(null);
        return v;
    }

    public void resetFragment(){
        friendsList = null;
        friendOwner = "";
        friendsListAdapter = null;
        friendsListSize = 0;
        uuidValue = "";
        recyclerView.setAdapter(noStatAdapter);
    }

    private String resetUUIDCheck = "";

    private void shouldResetFragment(String json){
        if (json == null || json.equals("")) return;

        Gson gson = new Gson();
        PlayerReply reply = gson.fromJson(json, PlayerReply.class);
        String uuid = reply.getPlayer().get("uuid").getAsString();
        if (uuid.equals(resetUUIDCheck)) return;
        resetFragment();
        resetUUIDCheck = uuid;
    }

    @Override
    public void processPlayerJson(String json){
        Log.i("HypixelStatistics", "Switched to FriendsStatisticsFragment");
        shouldResetFragment(json);
        if (!(friendsListAdapter == null || friendOwner == null || friendsList == null || friendsList.size() == 0)) {
            recyclerView.setAdapter(friendsListAdapter);
            return;
        }
        if (json == null || json.equals("")) {
            recyclerView.setAdapter(noStatAdapter);
            return;
        }
        Gson gson = new Gson();
        PlayerReply reply = gson.fromJson(json, PlayerReply.class);
        process(reply);
    }

    @Override
    public void processPlayerObject(PlayerReply object){
        recyclerView.setAdapter(noStatAdapter);
        process(object);
    }

    // PROCESS RESULT METHODS (GRABBLED FROM ASYNC TASK)

    private void process(PlayerReply reply){
        recyclerView.setVisibility(View.VISIBLE);
        String uuid = reply.getPlayer().get("uuid").getAsString();

        String playername;
        if (!MinecraftColorCodes.checkDisplayName(reply)) {
            playername = reply.getPlayer().get("playername").getAsString();
        } else {
            playername = MinecraftColorCodes.parseHypixelRanks(reply);
        }
        friendOwner = playername;

        retrieveFriendsList(uuid);
    }

    private void retrieveFriendsList(String searchQuery){
        processDialog = new ProgressDialog(getActivity());
        processDialog.setTitle("Retrieving Friends List");
        processDialog.setMessage(Html.fromHtml("Retrieving Friends List for " + friendOwner));
        processDialog.setCancelable(false);
        processDialog.show();

        new GenerateFriendsList(getActivity(), recyclerView, new FriendHandler(this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, searchQuery);
        uuidValue = searchQuery;
    }

    static class FriendHandler extends Handler{
        WeakReference<FriendsStatisticsFragment> mFragment;

        FriendHandler(FriendsStatisticsFragment fragment){
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg){
            FriendsStatisticsFragment fragment = mFragment.get();

            super.handleMessage(msg);

            switch (msg.what){
                case 1112: //Retrieve Friends List
                    String friendJson = (String) msg.getData().get("friendJson");
                    if (fragment.processDialog != null && fragment.processDialog.isShowing()) fragment.processDialog.dismiss();
                    fragment.generateFriendObjectForFurtherProcessing(friendJson);
                    break;
                case 1113:
                    String friendJsona = (String) msg.getData().get("friendJson");
                    FriendsObject obj = (FriendsObject) msg.obj;
                    fragment.processFriendsNamesFromHandler(friendJsona, obj);
                    break;
            }
        }
    }

    private String uuidValue;
    private List<FriendsObject> friendsList;
    private int friendsListSize;
    private String friendOwner;
    private FriendsRecyclerAdapter friendsListAdapter;
    private Snackbar snackBar;
    private int friendListCount;

    /**
     * This is the class that receives the json from the handler code 1112 and further processes it
     * This process will be done in {@link #processFriendsReplyObject(FriendsReply)}
     * @param json The json string from the handler
     */
    private void generateFriendObjectForFurtherProcessing(String json){
        MainStaticVars.friends_last_online_data.clear();
        MainStaticVars.friends_session_data.clear();
        Gson gson = new Gson();
        FriendsReply reply = gson.fromJson(json, FriendsReply.class);
        processFriendsReplyObject(reply);
    }

    /**
     * This is the class that receives the json from the handler code 1113 and processes it
     * This further processing will be done in {@link #processFriendsName(PlayerReply, FriendsObject)}
     * @param json The json string from the 1113 handler
     * @param object FriendsObject for this data
     */
    private void processFriendsNamesFromHandler(String json, FriendsObject object){
        Gson gson = new Gson();
        PlayerReply reply = gson.fromJson(json, PlayerReply.class);
        processFriendsName(reply, object);
    }

    private void processFriendsName(PlayerReply reply, FriendsObject playerName){
        //Succeeded
        if (!MinecraftColorCodes.checkDisplayName(reply)) {
            playerName.set_mcName(reply.getPlayer().get("playername").getAsString());
            playerName.set_mcNameWithRank(reply.getPlayer().get("playername").getAsString());
        } else {
            playerName.set_mcName(reply.getPlayer().get("displayname").getAsString());
            playerName.set_mcNameWithRank(MinecraftColorCodes.parseHypixelRanks(reply));
        }
        playerName.set_done(true);
        if (!CharHistory.checkHistory(reply, context)) {
            CharHistory.addHistory(reply, PreferenceManager.getDefaultSharedPreferences(context));
            Log.d("Player", "Added history for player " + reply.getPlayer().get("playername").getAsString());
        }
        addToAdapter(playerName);
        checkIfComplete();
    }

    private void processFriendsReplyObject(FriendsReply reply){
        //Update info
        ArrayList<FriendsObject> friendsListTemp = new ArrayList<>();
        int processedSize = reply.getRecords().size();
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
        }
        friendsListSize = processedSize;
        friendsList = new ArrayList<>();
        friendsList.clear();
        friendsListAdapter = new FriendsRecyclerAdapter(friendsList, getActivity());
        friendsListAdapter.updateFriendsOwner(friendOwner);
        recyclerView.setAdapter(friendsListAdapter);
        friendListCount = 0;
        Log.d("FriendsStatistics", "List of items in tmp: " + friendsListTemp.size() + ", in actual: " + friendsList.size());

        //Check history
        for (FriendsObject f : friendsListTemp) {
            HistoryArrayObject histCheckName = checkHistory(f.getFriendUUID());
            if (histCheckName == null){
                Log.d("Player", "Have to go and query playername for " + f.getFriendUUID());
                new RetriveFriendsName(getActivity(), new FriendHandler(this)).execute(f);
            } else {
                f.set_mcNameWithRank(MinecraftColorCodes.parseHistoryHypixelRanks(histCheckName));
                f.set_mcName(histCheckName.getDisplayname());
                f.set_done(true);
                Log.d("Player", "Found player " + f.get_mcName());
                //Update process
                addToAdapter(f);
            }
            checkIfComplete();
        }
    }

    private void addToAdapter(FriendsObject f){
        if (friendsList == null) return;
        friendListCount++;
        friendsList.add(f);
        friendsListAdapter.addNewFriend(f);
    }

    private void checkIfComplete(){
        if (friendsList == null) return;
        if (friendListCount > friendsListSize) friendListCount = friendsListSize;
        if (snackBar == null && getView() != null) snackBar = Snackbar.make(getView(), "Update", Snackbar.LENGTH_LONG);

        if (snackBar != null){
            snackBar = snackBar.setText("Updating Friends: " + friendListCount + "/" + friendsListSize);
            snackBar.show();
        }


        if (friendsList.size() >= friendsListSize){
            //Complete. Hide progress data
            Log.d("FriendsStatistics", "Friends List Processed. Checking and updating stuff");
            friendsListAdapter.updateAdapterIfDifferent(friendsList);
            if (snackBar != null && snackBar.isShown()) snackBar.dismiss();
        }
    }

    private HistoryArrayObject checkHistory(String uuid){
        String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(context));
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
                        CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(context), histCheck);
                        Log.d("HISTORY", "History Expired");
                        break;
                    } else {
                        return histCheckName;
                    }
                }
            }
        }
        return null;
    }
}
