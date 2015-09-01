package com.itachi1706.hypixelstatistics.ListViewAdapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.AsyncAPI.Friends.GetFriendsHead;
import com.itachi1706.hypixelstatistics.AsyncAPI.Players.GetLastOnlineInfoFriends;
import com.itachi1706.hypixelstatistics.AsyncAPI.Session.GetSessionInfoFriends;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.HeadHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.Objects.FriendsObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kenneth on 20/12/2014, 6:42 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class FriendsListAdapter extends ArrayAdapter<FriendsObject> {

    private ArrayList<FriendsObject> items;

    public FriendsListAdapter(Context context, int textViewResourceId, ArrayList<FriendsObject> objects){
        super(context, textViewResourceId, objects);
        this.items = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_guild_desc, parent, false);
        }

        FriendsObject i = items.get(position);


        TextView playerName = (TextView) v.findViewById(R.id.tvPlayerName);
        TextView session = (TextView) v.findViewById(R.id.tvPlayerStatus);
        TextView joined = (TextView) v.findViewById(R.id.tvPlayerJoined);
        TextView lastOnline = (TextView) v.findViewById(R.id.tvTimeStatus);
        ImageView head = (ImageView) v.findViewById(R.id.ivHead);
        ProgressBar prog = (ProgressBar) v.findViewById(R.id.pbPlayerHeadProg);

        if (i.is_done()) {
            if (playerName != null) {
                playerName.setText(Html.fromHtml(i.get_mcNameWithRank()));
            }
            if (session != null) {
                //Check if its running
                if (MainStaticVars.friends_session_data.containsKey(i.getFriendUUID())){
                    session.setText(Html.fromHtml(MinecraftColorCodes.parseColors(MainStaticVars.friends_session_data.get(i.getFriendUUID()))));
                } else {
                    session.setText("Getting Session Information...");
                    new GetSessionInfoFriends(session).execute(i.getFriendUUID());
                }
            }
            if (joined != null) {
                String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(i.getDate()));
                joined.setText("Friends From: " + timeStamp);
            }
            if (head != null) {
                prog.setVisibility(View.VISIBLE);
                //Check if head exists
                if (HeadHistory.checkIfHeadExists(getContext(), i.get_mcName())) {
                    head.setImageDrawable(HeadHistory.getHead(getContext(), i.get_mcName()));
                    prog.setVisibility(View.GONE);
                    Log.d("HEAD RETRIEVAL", "Retrieved " + i.get_mcName() + "'s Head from device");
                } else {
                    new GetFriendsHead(getContext(), head, prog).execute(i);
                }
            }
            if (lastOnline != null){
                if (MainStaticVars.friends_last_online_data.containsKey(i.getFriendUUID())){
                    lastOnline.setText(Html.fromHtml(MinecraftColorCodes.parseColors(MainStaticVars.friends_last_online_data.get(i.getFriendUUID()))));
                } else {
                    lastOnline.setText("Getting Last Online Information...");
                    new GetLastOnlineInfoFriends(lastOnline).execute(i.getFriendUUID());
                }
            }
        }

        return v;
    }

    public void updateAdapter(ArrayList<FriendsObject> newArrayData){
        this.items = newArrayData;
    }
}
