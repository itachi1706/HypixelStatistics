package com.itachi1706.hypixelstatistics.AsyncAPI.Friends;

import android.app.Activity;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.util.ListViewAdapters.FriendsListAdapter;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.Objects.FriendsObject;
import com.itachi1706.hypixelstatistics.util.Objects.HistoryObject;

import net.hypixel.api.reply.FriendsReply;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by Kenneth on 11/4/2015
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.AsyncAPI.Friends
 */
public class GetFriendsListUUID extends AsyncTask<String, Void, String> {

    private String uuidValue;
    private Exception except;
    private Activity mActivity;
    private ListView playerListView;
    private TextView tvResult;

    public GetFriendsListUUID(Activity mActivity, ListView playerListView, TextView tvResult){
        this.mActivity = mActivity;
        this.playerListView = playerListView;
        this.tvResult = tvResult;
    }

    @Override
    protected String doInBackground(String... uuid) {
        String url = MainStaticVars.API_BASE_URL + "friends?key=" + MainStaticVars.apikey + "&uuid=" + uuid[0];
        String tmp = "";
        uuidValue = uuid[0];
        Log.i("FRIENDS-UUID", "Getting Friends List Data for " + uuid[0]);
        //Get Statistics
        try {
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, MainStaticVars.HTTP_QUERY_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, MainStaticVars.HTTP_QUERY_TIMEOUT);
            HttpClient client = new DefaultHttpClient(httpParams);
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            InputStream in = response.getEntity().getContent();
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
            if (except instanceof ConnectTimeoutException) {
                Toast.makeText(mActivity.getApplicationContext(), "Connection Timed Out. Try again later", Toast.LENGTH_SHORT).show();
            } else if (except instanceof SocketTimeoutException) {
                Toast.makeText(mActivity.getApplicationContext(), "Socket Connection Timed Out. Try again later", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mActivity.getApplicationContext(), except.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        Gson gson = new Gson();
        if (!MainStaticVars.checkIfYouGotJsonString(json)) {
            if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                Toast.makeText(mActivity.getApplicationContext(), "CloudFlare timeout 524 occurred", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(mActivity.getApplicationContext(), "An error occurred (Invalid JSON String)", Toast.LENGTH_SHORT).show();
            return;
        }
        FriendsReply reply = gson.fromJson(json, FriendsReply.class);
        if (reply.isThrottle()) {
            Toast.makeText(mActivity.getApplicationContext(), "Query Throttled (Limit reached)", Toast.LENGTH_SHORT).show();
            return;
        } else if (!reply.isSuccess()) {
            Toast.makeText(mActivity.getApplicationContext(), "Invalid UUID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (reply.getRecords().size() == 0) {
            tvResult.setText(Html.fromHtml(MinecraftColorCodes.parseColors("Friends: §b0§r")));
            String[] noFriendsSadFace = {"No Friends Found :("};
            ArrayAdapter<String> noFriendsAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, noFriendsSadFace);
            playerListView.setAdapter(noFriendsAdapter);
            return;
        }
        MainStaticVars.friends_session_data.clear();
        //Process Friends Requests
        tvResult.setText(Html.fromHtml(MinecraftColorCodes.parseColors("Friends: §b" + reply.getRecords().size() + "§r")));
        ArrayList<FriendsObject> friendsListTemp = new ArrayList<>();
        for (JsonElement e : reply.getRecords()) {
            JsonObject obj = e.getAsJsonObject();
            long friendsFromDate = obj.get("started").getAsLong();
            String senderUUID = obj.get("uuidSender").getAsString();
            String receiverUUID = obj.get("uuidReceiver").getAsString();
            FriendsObject friends;
            if (senderUUID.equals(uuidValue)) {
                friends = new FriendsObject(friendsFromDate, receiverUUID);
            } else {
                friends = new FriendsObject(friendsFromDate, senderUUID);
            }
            friendsListTemp.add(friends);

            //Check history
            for (FriendsObject f : friendsListTemp) {
                String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()));
                boolean hasHist = false;
                if (hist != null) {
                    HistoryObject check = gson.fromJson(hist, HistoryObject.class);
                    JsonArray histCheck = check.getHistory();
                    for (JsonElement el : histCheck) {
                        JsonObject histCheckName = el.getAsJsonObject();
                        if (histCheckName.get("uuid").getAsString().equals(f.getFriendUUID())) {
                            //Check if history expired
                            if (CharHistory.checkHistoryExpired(histCheckName)) {
                                //Expired, reobtain
                                histCheck.remove(histCheckName);
                                CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext()), histCheck);
                                Log.d("HISTORY", "History Expired");
                                break;
                            } else {
                                f.set_mcNameWithRank(MinecraftColorCodes.parseHistoryHypixelRanks(histCheckName));
                                f.set_mcName(histCheckName.get("displayname").getAsString());
                                f.set_done(true);
                                MainStaticVars.friendsList.add(f);
                                hasHist = true;
                                Log.d("Player", "Found player " + f.get_mcName());
                                break;
                            }
                        }
                    }
                    if (!hasHist)
                        new GetFriendsName(mActivity, playerListView).execute(f);
                    checkIfComplete();
                }
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
            FriendsListAdapter adapter = new FriendsListAdapter(mActivity, R.layout.listview_guild_desc, MainStaticVars.friendsList);
            playerListView.setAdapter(adapter);
        }
    }
}
