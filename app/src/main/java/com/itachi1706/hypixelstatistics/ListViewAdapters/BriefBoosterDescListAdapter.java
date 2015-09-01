package com.itachi1706.hypixelstatistics.ListViewAdapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.Objects.BoosterDescription;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kenneth on 13/11/2014, 9:44 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class BriefBoosterDescListAdapter extends ArrayAdapter<BoosterDescription> {

    private ArrayList<BoosterDescription> items;

    public BriefBoosterDescListAdapter(Context context, int textViewResourceId, ArrayList<BoosterDescription> objects){
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
        TextView time = (TextView) v.findViewById(R.id.tvTimeStatus);

        //To Hide
        TextView location = (TextView) v.findViewById(R.id.tvLocation);
        TextView boostVal = (TextView) v.findViewById(R.id.tvBoosterValue);
        ImageView head = (ImageView) v.findViewById(R.id.ivHead);
        ProgressBar prog = (ProgressBar) v.findViewById(R.id.pbPlayerHeadProg);

        if (i.is_done()) {
            if (playerName != null) {
                playerName.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§6" + i.get_mcName() + "§r")));
            }
            if (time != null) {
                if (i.get_boostRate() > 1)
                    time.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§a" + i.get_boostRate() + "§r boosters activated")));
                else
                    time.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§a" + i.get_boostRate() + "§r booster activated")));
            }
            if (status != null){
                status.setText(Html.fromHtml(MinecraftColorCodes.parseColors("Time Remaining: " + createTimeLeftString(i.get_timeRemaining()))));
            }
        }

        //Hide unnecessary stuff
        if (location != null)
            location.setVisibility(View.GONE);
        if (boostVal != null)
            boostVal.setVisibility(View.GONE);
        if (head != null)
            head.setVisibility(View.GONE);
        if (prog != null)
            prog.setVisibility(View.GONE);

        return v;
    }

    private String createTimeLeftString(int timeRemaining){
        long days, hours, minutes, seconds;

        days = TimeUnit.SECONDS.toDays(timeRemaining);
        hours = TimeUnit.SECONDS.toHours(timeRemaining) - (days * 24);
        minutes = TimeUnit.SECONDS.toMinutes(timeRemaining) - (TimeUnit.SECONDS.toHours(timeRemaining)* 60);
        seconds = TimeUnit.SECONDS.toSeconds(timeRemaining) - (TimeUnit.SECONDS.toMinutes(timeRemaining) * 60);

        //Craft the time statement
        StringBuilder timeString = new StringBuilder();
        timeString.append("(§b");
        if (days != 0) {
            if (days == 1)
                timeString.append(days).append(" day ");
            else
                timeString.append(days).append(" days ");
        }
        if (hours != 0) {
            if (hours == 1)
                timeString.append(hours).append(" hour ");
            else
                timeString.append(hours).append(" hours ");
        }
        if (minutes != 0) {
            if (minutes == 1)
                timeString.append(minutes).append(" minute ");
            else
                timeString.append(minutes).append(" minutes ");
        }
        if (seconds != 0) {
            if (seconds == 1)
                timeString.append(seconds).append(" second ");
            else
                timeString.append(seconds).append(" seconds ");
        }
        timeString.append("§r)");
        return timeString.toString();
    }
}
