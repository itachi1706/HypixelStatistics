package com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.AsyncAPI.Boosters.BoosterGetPlayerHead;
import com.itachi1706.hypixelstatistics.ExpandedPlayerInfoActivity;
import com.itachi1706.hypixelstatistics.Objects.BoosterDescription;
import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.GeneratePlaceholderDrawables;
import com.itachi1706.hypixelstatistics.util.HistoryHandling.HeadHistory;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kenneth on 10/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.RevampedDesign.RecyclerViewAdapters
 */
public class BoosterRecyclerAdapter extends RecyclerView.Adapter<BoosterRecyclerAdapter.BoosterViewHolder> implements Filterable {

    private ArrayList<BoosterDescription> items;
    private Activity activity;

    public BoosterRecyclerAdapter(ArrayList<BoosterDescription> boosterObjects, Activity activity){
        Log.d("BoosterRecyclerAdapter", "Init Booster Object: " + boosterObjects.size());
        this.items = boosterObjects;
        this.activity = activity;
    }

    public void updateAdapterIfDifferent(ArrayList<BoosterDescription> newItems){
        if (newItems.size() != items.size()){
            updateAdater(newItems);
            return;
        }

        for (int i = 0; i < items.size(); i++){
            if (!items.get(i).equals(newItems.get(i))){
                updateAdater(newItems);
                break;
            }
        }
    }

    private void updateAdater(ArrayList<BoosterDescription> updatedItems){
        this.items = updatedItems;
        notifyDataSetChanged();
    }

    @Override
    public BoosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View friendView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_booster_item, parent, false);
        return new BoosterViewHolder(friendView);
    }

    @Override
    public void onBindViewHolder(BoosterViewHolder holder, int position) {
        BoosterDescription object = items.get(position);

        if (object.is_done()) {
            holder.playerName.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§6" + object.get_mcName() + "§r")));

            //Check if its running
            if (object.checkIfBoosterActive()){
                //Its active. Get Time Remaining
                int timeDurationWork = object.get_timeRemaining();
                String timeDuration = String.format("%d min, %d sec remaining", TimeUnit.SECONDS.toMinutes(timeDurationWork), timeDurationWork - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(timeDurationWork)));
                holder.status.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§aACTIVE§r (" + timeDuration + ")")));
            } else {
                holder.status.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§cINACTIVE - QUEUED§r")));
            }

            try {
                holder.location.setText(object.get_gameType().getName());
            } catch (NullPointerException e){
                Log.e("INVALID GAMETYPE", "Invalid New Gametype. Must add new game modes");
                holder.location.setText("INVALID GAME MODE. Inform Dev");
            }

            String timeStamp = new SimpleDateFormat(MainStaticVars.DATE_FORMAT, Locale.US).format(new Date(object.get_date()));
            holder.time.setText("Used On: " + timeStamp);

            holder.prog.setVisibility(View.VISIBLE);
            //Set the placeholder drawable first
            holder.head.setImageDrawable(GeneratePlaceholderDrawables.generateFromMcNameWithInitialsConversion(object.get_mcName()));
            //Check if head exists
            if (HeadHistory.checkIfHeadExists(activity, object.get_mcName())) {
                holder.head.setImageDrawable(HeadHistory.getHead(activity, object.get_mcName()));
                holder.prog.setVisibility(View.GONE);
                Log.d("HEAD RETRIEVAL", "Retrieved " + object.get_mcName() + "'s Head from device");
            } else {
                new BoosterGetPlayerHead(activity, holder.head, holder.prog).execute(object);
            }
            //head.setImageDrawable(object.getMcHead());

            int boostTime = object.get_originalTime();
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
            holder.boostVal.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§6" + object.get_boostRate() + "x§r Coins (" + timeDuration + ")")));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String filteredStringForBooster = "";

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<BoosterDescription> filteredBoosters = new ArrayList<>();
                if (filteredStringForBooster == null || filteredStringForBooster.length() == 0 || constraint == ""){
                    filteredBoosters = items;
                } else {
                    Log.d("BOOSTER-FILTER", "Filter String used: " + constraint);
                    ArrayList<BoosterDescription> tmp = items;
                    Log.d("BOOSTER-FILTER", "Filter Size: " + tmp.size());
                    String[] constraints;
                    if (constraint.toString().contains("%SPLIT%"))
                        constraints = constraint.toString().split("%SPLIT%");
                    else {
                        constraints = new String[1];
                        constraints[0] = constraint.toString();
                    }
                    for (BoosterDescription d : tmp) {
                        for (String con : constraints) {
                            Log.d("BOOSTER-FILTER-F", "Filtering: " + d.get_gameType().getName() + " with " + con);
                            if (d.get_gameType().getName().equalsIgnoreCase(con)) {
                                Log.d("BOOSTER-FILTER-F", "Found match for " + d.get_gameType().getName());
                                filteredBoosters.add(d);
                                break;
                            }
                        }
                    }
                }
                results.count = filteredBoosters.size();
                results.values = filteredBoosters;
                Log.e("BOOSTER-FILTER", "Size of filtered items: " + results.count);
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0){
                    Log.e("BOOSTER-FILTER", "Results of filter is NULL!!! Display all anyway");
                } else {
                    ArrayList<BoosterDescription> itemss = (ArrayList<BoosterDescription>) results.values;
                    items.clear();
                    for (BoosterDescription d : itemss) {
                        items.add(d);
                    }
                    notifyDataSetChanged();
                }
            }
        };
    }

    public String getFilteredStringForBooster() {
        return filteredStringForBooster;
    }

    public void setFilteredStringForBooster(String filteredStringForBooster) {
        this.filteredStringForBooster = filteredStringForBooster;
    }

    public class BoosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView playerName, status, time, location, boostVal;
        protected ImageView head;
        protected ProgressBar prog;

        public BoosterViewHolder(View v){
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
            int position = this.getLayoutPosition();
            final BoosterDescription item = items.get(position);
            
            Intent intentE = new Intent(activity, ExpandedPlayerInfoActivity.class);
            intentE.putExtra("player", item.get_mcName());
            activity.startActivity(intentE);
        }
    }

}
