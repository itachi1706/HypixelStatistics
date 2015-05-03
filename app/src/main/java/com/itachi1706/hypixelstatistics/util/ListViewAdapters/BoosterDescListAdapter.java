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

import com.itachi1706.hypixelstatistics.AsyncAPI.Boosters.BoosterGetPlayerHead;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.HeadHistory;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.Objects.FriendsObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kenneth on 13/11/2014, 9:44 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class BoosterDescListAdapter extends ArrayAdapter<BoosterDescription> {

    private ArrayList<BoosterDescription> items;

    public BoosterDescListAdapter(Context context, int textViewResourceId, ArrayList<BoosterDescription> objects){
        super(context, textViewResourceId, objects);
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_booster_desc, parent, false);
        }

        BoosterDescription i = items.get(position);


        TextView playerName = (TextView) v.findViewById(R.id.tvPlayerName);
        TextView status = (TextView) v.findViewById(R.id.tvPlayerStatus);
        TextView location = (TextView) v.findViewById(R.id.tvLocation);
        TextView time = (TextView) v.findViewById(R.id.tvTimeStatus);
        TextView boostVal = (TextView) v.findViewById(R.id.tvBoosterValue);
        ImageView head = (ImageView) v.findViewById(R.id.ivHead);
        ProgressBar prog = (ProgressBar) v.findViewById(R.id.pbPlayerHeadProg);

        if (i.is_done()) {
            if (playerName != null) {
                playerName.setText(Html.fromHtml(i.get_mcNameWithRank()));
            }
            if (status != null) {
                //Check if its running
                if (i.checkIfBoosterActive()){
                    //Its active. Get Time Remaining
                    int timeDurationWork = i.get_timeRemaining();
                    String timeDuration = String.format("%d min, %d sec remaining", TimeUnit.SECONDS.toMinutes(timeDurationWork), timeDurationWork - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(timeDurationWork)));
                    status.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§aACTIVE§r (" + timeDuration + ")")));
                } else {
                    status.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§cINACTIVE - QUEUED§r")));
                }
            }
            if (location != null) {
                try {
                    location.setText(i.get_gameType().getName());
                } catch (NullPointerException e){
                    Log.e("INVALID GAMETYPE", "Invalid New Gametype. Must add new game modes");
                    location.setText("INVALID GAME MODE. Inform Dev");
                }
            }
            if (time != null) {
                String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(i.get_date()));
                time.setText("Used On: " + timeStamp);
            }
            if (head != null) {
                prog.setVisibility(View.VISIBLE);
                //Check if head exists
                if (HeadHistory.checkIfHeadExists(getContext(), i.get_mcName())) {
                    head.setImageDrawable(HeadHistory.getHead(getContext(), i.get_mcName()));
                    prog.setVisibility(View.GONE);
                    Log.d("HEAD RETRIEVAL", "Retrieved " + i.get_mcName() + "'s Head from device");
                } else {
                    new BoosterGetPlayerHead(getContext(), head, prog).execute(i);
                }
                //head.setImageDrawable(i.getMcHead());
            }
            if (boostVal != null){
                int boostTime = i.get_originalTime();
                String timeDuration = "Error";
                if (boostTime > 7200 && boostTime%3600 != 0)
                    timeDuration = String.format("%d hrs %d min", TimeUnit.SECONDS.toHours(boostTime), TimeUnit.SECONDS.toMinutes(boostTime) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(boostTime)));
                else if (boostTime%3600 == 0 && boostTime != 3600)
                    timeDuration = String.format("%d hrs", TimeUnit.SECONDS.toHours(boostTime));
                else if (boostTime > 3600 && boostTime < 7200)
                    timeDuration = String.format("%d hr %d min", TimeUnit.SECONDS.toHours(boostTime), TimeUnit.SECONDS.toMinutes(boostTime) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(boostTime)));
                else if (boostTime == 3600)
                    timeDuration = String.format("%d hr", TimeUnit.SECONDS.toHours(boostTime));
                else if (boostTime < 3600 && boostTime > 60)
                    timeDuration = String.format("%d mins", TimeUnit.SECONDS.toMinutes(boostTime));
                else if (boostTime == 60)
                    timeDuration = String.format("%d min", TimeUnit.SECONDS.toMinutes(boostTime));
                else if (boostTime < 60 && boostTime > 1)
                    timeDuration = String.format("%d secs", boostTime);
                else if (boostTime == 1)
                    timeDuration = String.format("%d sec", boostTime);
                boostVal.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§6" + i.get_boostRate() + "x§r Coins (" + timeDuration + ")")));
            }
        }

        return v;
    }

    public void updateAdapter(ArrayList<BoosterDescription> newArrayData){
        this.items = newArrayData;
    }
}
