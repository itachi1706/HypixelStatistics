package com.itachi1706.hypixelstatistics.util;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.R;

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

        if (i.is_done()) {
            if (playerName != null) {
                playerName.setText(i.get_mcName());
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
                location.setText(i.get_gameType().getName());
            }
            if (time != null) {
                String timeStamp = new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(i.get_date()));
                if (i.checkIfBoosterActive()) {
                    time.setText("Activated On: " + timeStamp);
                } else {
                    time.setText("Activates On: " + timeStamp);
                }
            }
            if (head != null) {
                head.setImageDrawable(i.getMcHead());
            }
            if (boostVal != null){
                boostVal.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§6" + i.get_boostRate() + "x§r Coins")));
            }
        }

        return v;
    }
}
