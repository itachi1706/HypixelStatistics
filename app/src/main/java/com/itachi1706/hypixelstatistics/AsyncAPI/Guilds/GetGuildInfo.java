package com.itachi1706.hypixelstatistics.AsyncAPI.Guilds;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.Objects.HistoryArrayObject;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.CharHistory;
import com.itachi1706.hypixelstatistics.ListViewAdapters.GuildMemberAdapter;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;
import com.itachi1706.hypixelstatistics.Objects.GuildMemberDesc;
import com.itachi1706.hypixelstatistics.Objects.HistoryObject;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.ListViewAdapters.ResultDescListAdapter;
import com.itachi1706.hypixelstatistics.Objects.ResultDescription;

import net.hypixel.api.reply.GuildReply;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Kenneth on 20/12/2014, 4:53 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.AsyncAPI
 */
public class GetGuildInfo extends AsyncTask<String, Void, String> {

    Exception except = null;
    Activity mContext;
    String guildID;
    ListView _generalInfo, _memberInfo;

    ArrayList<ResultDescription> guildInfo;
    public static ArrayList<GuildMemberDesc> guildMembers;

    public GetGuildInfo(Activity activity, ListView generalInfo, ListView memberInfo){
        mContext = activity;
        _generalInfo = generalInfo;
        _memberInfo = memberInfo;
    }

    @Override
    protected String doInBackground(String... guildId){
        guildID = guildId[0];
        String url = MainStaticVars.API_BASE_URL + "guild?key=" + MainStaticVars.apikey + "&id=" + guildID;
        String tmp = "";
        Log.d("guild Info URL", url);
        try {
            URL urlConn = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlConn.openConnection();
            conn.setConnectTimeout(MainStaticVars.HTTP_QUERY_TIMEOUT);
            conn.setReadTimeout(MainStaticVars.HTTP_QUERY_TIMEOUT);
            InputStream in = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null)
            {
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

    @SuppressLint("SimpleDateFormat")
    protected void onPostExecute(String json) {
        MainStaticVars.guild_member_session_data.clear();
        MainStaticVars.guild_last_online_data.clear();
        if (except != null) {
            //Theres an exception
            if (except instanceof SocketTimeoutException)
                NotifyUserUtil.createShortToast(mContext, "Connection Timed Out. Try again later");
            else
                NotifyUserUtil.createShortToast(mContext, "An error occured. (" + except.getLocalizedMessage() + ")");
            return;
        }
        Log.d("GUILD INFO JSON STRING", json);
        Gson gson = new Gson();
        if (!MainStaticVars.checkIfYouGotJsonString(json)){
            if (json.contains("524") && json.contains("timeout") && json.contains("CloudFlare"))
                NotifyUserUtil.createShortToast(mContext, "A CloudFlare timeout has occurred. Please wait a while before trying again");
            else
                NotifyUserUtil.createShortToast(mContext, "An error occured. (Invalid JSON String) Please Try Again");
            return;
        }
        GuildReply reply = gson.fromJson(json, GuildReply.class);
        Log.d("GUILD INFO JSON OBJECT", reply.toString());
        if (reply.isThrottle()) {
            //Throttled (API Exceeded Limit)
            NotifyUserUtil.createShortToast(mContext, "The Hypixel Public API only allows 60 queries per minute. Please try again later");
            return;
        }
        if (!reply.isSuccess()) {
            //Not Successful
            NotifyUserUtil.createShortToast(mContext, "Unsuccessful Query!\n Reason: " + reply.getCause());
            return;
        }
        if (reply.getGuild() == null){
            NotifyUserUtil.createShortToast(mContext, "No guild found with ID: " + guildID);
            return;
        }

        //Success

        //General Info
        guildInfo = new ArrayList<>();
        guildMembers = new ArrayList<>();
        MainStaticVars.guildList.clear();
        guildInfo.add(new ResultDescription("Guild Name", reply.getGuild().get("name").getAsString()));
        guildInfo.add(new ResultDescription("Guild ID", guildID));
        if (reply.getGuild().has("created"))
            guildInfo.add(new ResultDescription("Created On", new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(reply.getGuild().get("created").getAsLong()))));
        if (reply.getGuild().has("joinable")) {
            if (reply.getGuild().get("joinable").getAsBoolean())
                guildInfo.add(new ResultDescription("Join Request", MinecraftColorCodes.parseColors("§aEnabled§r")));
            else
                guildInfo.add(new ResultDescription("Join Request", MinecraftColorCodes.parseColors("§cDisabled§r")));
        }
        if (reply.getGuild().has("publiclyListed")) {
            if (reply.getGuild().get("publiclyListed").getAsBoolean())
                guildInfo.add(new ResultDescription("Guild Status", MinecraftColorCodes.parseColors("§aEnabled§r")));
            else
                guildInfo.add(new ResultDescription("Guild Status", MinecraftColorCodes.parseColors("§cDisabled§r")));
        }

        //Coins
        if (reply.getGuild().has("coins"))
            guildInfo.add(new ResultDescription("Current Guild Coins", reply.getGuild().get("coins").getAsString()));
        if (reply.getGuild().has("coinsEver"))
            guildInfo.add(new ResultDescription("Guild Coins Earned Ever", reply.getGuild().get("coinsEver").getAsString()));

        //Upgrades
        if (reply.getGuild().has("memberSizeLevel"))
            guildInfo.add(new ResultDescription("Guild Member Upgrade Level", reply.getGuild().get("memberSizeLevel").getAsString()));
        if (reply.getGuild().has("bankSizeLevel"))
            guildInfo.add(new ResultDescription("Guild Banking Upgrade Level", reply.getGuild().get("bankSizeLevel").getAsString()));
        if (reply.getGuild().has("canMotd")) {
            if (reply.getGuild().get("canMotd").getAsBoolean())
                guildInfo.add(new ResultDescription("Guild MOTD", MinecraftColorCodes.parseColors("§aPurchased§r")));
            else
                guildInfo.add(new ResultDescription("Guild MOTD", MinecraftColorCodes.parseColors("§cNot Purchased§r")));
        } else {
            guildInfo.add(new ResultDescription("Guild MOTD", MinecraftColorCodes.parseColors("§cNot Purchased§r")));
        }
        if (reply.getGuild().has("canParty")) {
            if (reply.getGuild().get("canParty").getAsBoolean())
                guildInfo.add(new ResultDescription("Guild Party", MinecraftColorCodes.parseColors("§aPurchased§r")));
            else
                guildInfo.add(new ResultDescription("Guild Party", MinecraftColorCodes.parseColors("§cNot Purchased§r")));
        } else {
            guildInfo.add(new ResultDescription("Guild Party", MinecraftColorCodes.parseColors("§cNot Purchased§r")));
        }
        if (reply.getGuild().has("canTag")) {
            if (reply.getGuild().get("canTag").getAsBoolean())
                guildInfo.add(new ResultDescription("Guild [TAG]", MinecraftColorCodes.parseColors("§aPurchased§r")));
            else
                guildInfo.add(new ResultDescription("Guild [TAG]", MinecraftColorCodes.parseColors("§cNot Purchased§r")));
        } else {
            guildInfo.add(new ResultDescription("Guild [TAG]", MinecraftColorCodes.parseColors("§cNot Purchased§r")));
        }

        //Guild Coins
        StringBuilder builder = new StringBuilder();
        boolean hasKey = false;
        for (Map.Entry<String, JsonElement> entry : reply.getGuild().entrySet()) {
            if (entry.getKey().startsWith("dailyCoins")){
                //Split String and parse
                hasKey = true;
                String[] splitEntryKey = entry.getKey().split("-");
                String date = splitEntryKey[1] + "/" + splitEntryKey[2] + "/" + splitEntryKey[3];
                builder.append(date).append(": §b").append(entry.getValue()).append("§r <br />");
            }
        }
        if (hasKey){
            guildInfo.add(new ResultDescription("Daily Coins", "Click here to view daily coins activity", true, builder.toString()));
        }

        ResultDescListAdapter adapter = new ResultDescListAdapter(mContext, R.layout.listview_result_desc, guildInfo);
        _generalInfo.setAdapter(adapter);


        //Members
        JsonArray memberArray = reply.getGuild().getAsJsonArray("members");
        for (JsonElement e : memberArray) {
            JsonObject obj = e.getAsJsonObject();
            GuildMemberDesc member;
            if (obj.has("name"))
                 member = new GuildMemberDesc(obj.get("uuid").getAsString(), obj.get("name").getAsString(), obj.get("rank").getAsString(), obj.get("joined").getAsLong());
            else
                member = new GuildMemberDesc(obj.get("uuid").getAsString(), obj.get("rank").getAsString(), obj.get("joined").getAsLong());
            boolean hasKey1 = false;
            Log.d("Guild Member", "Analysing " + member.get_uuid() + " guild contributions");
            Log.d("Guild Member String", obj.toString());
            //Member Coins
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                if (entry.getKey().startsWith("dailyCoins")){
                    //Split String and parse
                    hasKey1 = true;
                    String[] splitEntryKey = entry.getKey().split("-");
                    String date = splitEntryKey[1] + "/" + splitEntryKey[2] + "/" + splitEntryKey[3];
                    builder.append(date).append(": §b").append(entry.getValue()).append("§r <br />");
                }
            }
            if (hasKey1){
                member.set_dailyCoins(builder.toString());
                Log.d("Guild Member D. Coins", member.get_uuid() + " has daily coins contributions");
            }
            guildMembers.add(member);
        }

        //Parse History to check if head
        for (GuildMemberDesc desc : guildMembers){
            String hist = CharHistory.getListOfHistory(PreferenceManager.getDefaultSharedPreferences(mContext));
            boolean hasHist = false;
            if (hist != null) {
                HistoryObject check = gson.fromJson(hist, HistoryObject.class);
                List<HistoryArrayObject> histCheck = CharHistory.convertHistoryArrayToList(check.getHistory());
                for (HistoryArrayObject histCheckName : histCheck) {
                    if (histCheckName.getUuid().equals(desc.get_uuid())) {
                        //Check if history expired
                        if (CharHistory.checkHistoryExpired(histCheckName)){
                            //Expired, reobtain
                            histCheck.remove(histCheckName);
                            CharHistory.updateJSONString(PreferenceManager.getDefaultSharedPreferences(mContext), histCheck);
                            Log.d("HISTORY", "History Expired");
                            break;
                        } else {
                            desc.set_mcNameWithRank(MinecraftColorCodes.parseHistoryHypixelRanks(histCheckName));
                            desc.set_mcName(histCheckName.getDisplayname());
                            desc.set_done(true);
                            MainStaticVars.guildList.add(desc);
                            hasHist = true;
                            Log.d("Player", "Found player " + desc.get_mcName());
                            break;
                        }
                    }
                }
            }
            if (!hasHist)
                new GuildGetMemberName(mContext, _memberInfo).execute(desc);
            checkIfComplete();
        }
    }

    private void checkIfComplete(){
        boolean done = true;
        for (GuildMemberDesc desc : MainStaticVars.guildList){
            if (!desc.is_done()) {
                done = false;
                break;
            }
        }
        if (done){
            GuildMemberAdapter adapter = new GuildMemberAdapter(mContext, R.layout.listview_guild_desc, MainStaticVars.guildList);
            _memberInfo.setAdapter(adapter);
        }
    }
}
