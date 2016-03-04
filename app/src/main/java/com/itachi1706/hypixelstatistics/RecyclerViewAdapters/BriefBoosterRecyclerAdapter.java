package com.itachi1706.hypixelstatistics.RecyclerViewAdapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kenneth on 10/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.RecyclerViewAdapters
 */
public class BriefBoosterRecyclerAdapter extends RecyclerView.Adapter<BriefBoosterRecyclerAdapter.BriefBoosterViewHolder> {

    private List<BoosterDescription> items;

    public BriefBoosterRecyclerAdapter(List<BoosterDescription> boosterObjects){
        Log.d("B.BoosterR.Adapter", "Init Booster Object: " + boosterObjects.size());
        this.items = boosterObjects;
    }

    private void updateAdater(List<BoosterDescription> updatedItems){
        this.items = updatedItems;
        notifyDataSetChanged();
    }

    @Override
    public BriefBoosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View friendView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_booster_item, parent, false);
        return new BriefBoosterViewHolder(friendView);
    }

    @Override
    public void onBindViewHolder(BriefBoosterViewHolder holder, int position) {
        BoosterDescription object = items.get(position);

        if (object.is_done()) {
            holder.playerName.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§6" + object.get_mcName() + "§r")));
            if (object.get_boostRate() > 1)
                holder.time.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§a" + object.get_boostRate() + "§r boosters activated")));
            else
                holder.time.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§a" + object.get_boostRate() + "§r booster activated")));
            holder.status.setText(Html.fromHtml(MinecraftColorCodes.parseColors("Time Remaining: " + createTimeLeftString(object.get_timeRemaining()))));
        }

        // Hide non brief booster related fields
        holder.location.setVisibility(View.GONE);
        holder.boostVal.setVisibility(View.GONE);
        holder.head.setVisibility(View.GONE);
        holder.prog.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
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

    public class BriefBoosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView playerName, status, time, location, boostVal;
        protected ImageView head;
        protected ProgressBar prog;

        public BriefBoosterViewHolder(View v){
            super(v);
            playerName = (TextView) v.findViewById(R.id.tvPlayerName);
            status = (TextView) v.findViewById(R.id.tvPlayerStatus);
            time = (TextView) v.findViewById(R.id.tvTimeStatus);
            location = (TextView) v.findViewById(R.id.tvLocation);
            boostVal = (TextView) v.findViewById(R.id.tvBoosterValue);
            head = (ImageView) v.findViewById(R.id.ivHead);
            prog = (ProgressBar) v.findViewById(R.id.pbPlayerHeadProg);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }

}
