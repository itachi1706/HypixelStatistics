package com.itachi1706.hypixelstatistics.util.ListViewAdapters;

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

import com.itachi1706.hypixelstatistics.AsyncAPI.Guilds.GuildGetPlayerHead;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.Objects.GuildMemberDesc;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.HeadHistory;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kenneth on 20/12/2014, 6:42 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class GuildMemberAdapter extends ArrayAdapter<GuildMemberDesc> {

    private ArrayList<GuildMemberDesc> items;

    public GuildMemberAdapter(Context context, int textViewResourceId, ArrayList<GuildMemberDesc> objects){
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

        GuildMemberDesc i = items.get(position);


        TextView playerName = (TextView) v.findViewById(R.id.tvPlayerName);
        TextView rank = (TextView) v.findViewById(R.id.tvPlayerStatus);
        TextView joined = (TextView) v.findViewById(R.id.tvTimeStatus);
        ImageView head = (ImageView) v.findViewById(R.id.ivHead);
        ProgressBar prog = (ProgressBar) v.findViewById(R.id.pbPlayerHeadProg);

        if (i.is_done()) {
            if (playerName != null) {
                playerName.setText(Html.fromHtml(i.get_mcNameWithRank()));
            }
            if (rank != null) {
                //Check if its running
                rank.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§6" + i.get_rank() + "§r")));
            }
            if (joined != null) {
                String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(i.get_joined()));
                joined.setText("Used On: " + timeStamp);
            }
            if (head != null) {
                prog.setVisibility(View.VISIBLE);
                //Check if head exists
                if (HeadHistory.checkIfHeadExists(getContext(), i.get_mcName())) {
                    head.setImageDrawable(HeadHistory.getHead(getContext(), i.get_mcName()));
                    prog.setVisibility(View.GONE);
                    Log.d("HEAD RETRIEVAL", "Retrieved " + i.get_mcName() + "'s Head from device");
                } else {
                    new GuildGetPlayerHead(getContext(), head, prog).execute(i);
                }
            }
        }

        return v;
    }
}
