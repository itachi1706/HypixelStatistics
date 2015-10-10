package com.itachi1706.hypixelstatistics.RevampedDesign;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.AsyncAPI.Friends.GetFriendsName;
import com.itachi1706.hypixelstatistics.ListViewAdapters.FriendsListAdapter;
import com.itachi1706.hypixelstatistics.Objects.FriendsObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.Friends.GenerateFriendsList;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.Friends.RetriveFriendOwner;
import com.itachi1706.hypixelstatistics.RevampedDesign.AsyncTask.PlayerInfo.PlayerInfoQueryHead;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoBase;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoHeader;
import com.itachi1706.hypixelstatistics.RevampedDesign.Objects.PlayerInfoStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.DonatorStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GameStatisticsHandler;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.GeneralStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.OngoingAchievementStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.ParkourStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.QuestStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StaffOrYtStatistics;
import com.itachi1706.hypixelstatistics.RevampedDesign.PlayerStatistics.StatisticsHelper;
import com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters.PlayerInfoExpandableRecyclerAdapter;
import com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters.StringRecyclerAdapter;
import com.itachi1706.hypixelstatistics.util.GeneratePlaceholderDrawables;
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

        recyclerView.setAdapter(noStatAdapter);
        return v;
    }

    @Override
    public void processPlayerJson(String json){
        Log.i("HypixelStatistics", "Switched to FriendsStatisticsFragment");
        if (json == null || json.equals("")) { recyclerView.setAdapter(noStatAdapter); return; }
        Gson gson = new Gson();
        PlayerReply reply = gson.fromJson(json, PlayerReply.class);
        process(reply);
    }

    @Override
    public void processPlayerObject(PlayerReply object){
        process(object);
    }

    // PROCESS RESULT METHODS (GRABBLED FROM ASYNC TASK)

    private void process(PlayerReply reply){
        recyclerView.setVisibility(View.VISIBLE);
        String uuid = reply.getPlayer().get("uuid").getAsString();

        retrieveFriendsList(uuid);
    }

    private void retrieveFriendsList(String searchQuery){
        new GenerateFriendsList(getActivity(), recyclerView, new FriendHandler(this)).execute(searchQuery);
        uuidValue = searchQuery;
    }

    //TODO: Handler to pull friends list back (through MainStaticVars)
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
                case 1111: //Success
                    String friendOwner = (String) msg.getData().get("friendOwner");
                    //TODO: Handle assigning friend owner
                    break;
                case 1112: //Fail Display Error Message
                    String friendJson = (String) msg.getData().get("friendJson");
                    //TODO: Handle what after retriving of friends list
                    //TODO: Do the check
                    //TODO: Uncomment to continue
                    //fragment.generateFriendObjectForFurtherProcessing(friendJson);
                    break;

            }
        }
    }

    private String uuidValue;

    //TODO: Uncomment when continuing
    /*

    //TODO: It is to throw the friends list here for further processing
    private void generateFriendObjectForFurtherProcessing(String json){
        Gson gson = new Gson();
        FriendsReply reply = gson.fromJson(json, FriendsReply.class);
        processFriendsReplyObject(reply);
    }

    private void processFriendsReplyObject(FriendsReply reply){
        //Get Name of player whose's friends list is being shown
        String user = checkHistory(uuidValue);
        if (user.equals("err")){
            //Get Name
            new RetriveFriendOwner(getActivity(), reply.getRecords().size(), new FriendHandler(this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uuidValue);
        } else {
            MainStaticVars.friendOwner = user;  //TODO: Convert to local
        }

        //Update info
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
        }
        MainStaticVars.friendsListSize = processedSize;
        MainStaticVars.friendsList.clear();
        //Check history
        for (FriendsObject f : friendsListTemp) {
            String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()));
            boolean hasHist = false;
            MainStaticVars.friendsListAdapter = new FriendsListAdapter(mActivity, R.layout.listview_guild_desc, MainStaticVars.friendsList);
            playerRecyclerView.setAdapter(MainStaticVars.friendsListAdapter);
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
            //playerRecyclerView.setAdapter(adapter);
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
    }*/
}
