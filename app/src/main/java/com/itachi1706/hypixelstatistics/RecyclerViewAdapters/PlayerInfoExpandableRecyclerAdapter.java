package com.itachi1706.hypixelstatistics.RecyclerViewAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.MiddleActivityBetweenSingleTopActivity;
import com.itachi1706.hypixelstatistics.Objects.PlayerInfoBase;
import com.itachi1706.hypixelstatistics.Objects.PlayerInfoHeader;
import com.itachi1706.hypixelstatistics.Objects.PlayerInfoStatistics;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Kenneth on 10/10/2015.
 * for HypixelStatistics in package com.itachi1706.hypixelstatistics.RecyclerViewAdapters
 */
public class PlayerInfoExpandableRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PlayerInfoBase> items;
    private Activity mActivity;

    private static final int BASE_HEADER = 0;
    private static final int BASE_STATS = 1;

    public PlayerInfoExpandableRecyclerAdapter(List<PlayerInfoBase> items, Activity activity){
        this.items = items;
        this.mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case BASE_HEADER:
                View headerView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_player_info_header, parent, false);
                return new HeaderViewHolder(headerView);
            case BASE_STATS:
                View statsView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_player_info_statistics, parent, false);
                return new StatisticsViewHolder(statsView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PlayerInfoBase base = items.get(position);
        if (base instanceof PlayerInfoHeader){
            PlayerInfoHeader header = (PlayerInfoHeader) base;
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.header.setText(Html.fromHtml(header.getTitle()));
        } else if (base instanceof PlayerInfoStatistics){
            PlayerInfoStatistics statistics = (PlayerInfoStatistics) base;
            StatisticsViewHolder statisticsViewHolder = (StatisticsViewHolder) holder;
            statisticsViewHolder.title.setText(Html.fromHtml(statistics.getTitle()));
            statisticsViewHolder.message.setText(Html.fromHtml(statistics.getMessage()));
        }
    }

    @Override
    public int getItemViewType(int position){
        if (position == items.size()){
            return BASE_HEADER;
        }
        PlayerInfoBase item = items.get(position);
        if (item instanceof PlayerInfoHeader) return BASE_HEADER;
        if (item instanceof PlayerInfoStatistics) return BASE_STATS;
        return BASE_HEADER;
    }

    @Override
    public int getItemCount() {return items.size();}

    public int getHeaderIndex(PlayerInfoHeader header){
        int index = -1;
        for (int i = 0; i < items.size(); i++){
            if (!(items.get(i) instanceof PlayerInfoHeader)) continue;
            if (items.get(i) == header) index = i;
        }
        return index;
    }

    public void expand(PlayerInfoHeader header){
        if (!header.hasChild()) return;
        List<PlayerInfoStatistics> child = header.getChild();

        int add = getHeaderIndex(header);
        if (add == -1) return;

        items.get(add).setIsExpanded(true);

        ListIterator iterator = child.listIterator(child.size());
        while (iterator.hasPrevious()){
            PlayerInfoStatistics statistics = (PlayerInfoStatistics) iterator.previous();
            items.add(add+1, statistics);
            notifyItemInserted(add + 1);
        }
    }

    public void retract(PlayerInfoHeader header){
        if (!header.hasChild()) return;
        List<PlayerInfoStatistics> child = header.getChild();

        int remove = getHeaderIndex(header);
        if (remove == -1) return;

        items.get(remove).setIsExpanded(false);

        for (Iterator<PlayerInfoBase> iterator = items.iterator(); iterator.hasNext();){
            PlayerInfoBase base = iterator.next();
            if (base instanceof PlayerInfoHeader) continue;

            PlayerInfoStatistics item = (PlayerInfoStatistics) base;
            for (PlayerInfoStatistics childItems : child){
                if (childItems == item){
                    iterator.remove();
                    notifyItemRemoved(remove + 1);
                    break;
                }
            }
        }
    }


    //VIEW HOLDERS

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected TextView header;

        public HeaderViewHolder(View v){
            super(v);
            header = (TextView) v.findViewById(R.id.header_title);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getLayoutPosition();
            PlayerInfoHeader item = (PlayerInfoHeader) items.get(position);

            Log.d("PlayerInfo RV", "Header: " + header.getText() + " isExpanded: " + item.isExpanded());

            if (item.isExpanded()){
                retract(item);
            } else {
                if (item.hasChild()){
                    expand(item);
                }
            }
        }
    }

    public class StatisticsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title, message;

        public StatisticsViewHolder(View v){
            super(v);
            title = (TextView) v.findViewById(R.id.pinfo_title);
            message = (TextView) v.findViewById(R.id.pinfo_message);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            int position = this.getLayoutPosition();
            PlayerInfoStatistics item = (PlayerInfoStatistics) items.get(position);

            if (!item.hasAction()) return;

            //Check for special actions
            if (item.getAction().startsWith("=+=senduuid=+=")){
                //Query for a player with the given UUID in an new activity
                String[] tmp = item.getAction().split(" ");
                String uuid_to_go_to = tmp[1];
                Intent uuidOfPlayer = new Intent(mActivity, MiddleActivityBetweenSingleTopActivity.class);
                uuidOfPlayer.putExtra("class", "PlayerInfo");
                uuidOfPlayer.putExtra("playerUuid", uuid_to_go_to);
                Log.d("INTENT-ACTIVITY_LAUNCH", "Sending intent to launch activity to search with UUID: " + uuid_to_go_to);
                mActivity.startActivity(uuidOfPlayer);
                return;
            }

            //Normal text view dialog
            TextView tv = (TextView) new AlertDialog.Builder(mActivity)
                    .setTitle(item.getTitle())
                    .setMessage(Html.fromHtml(item.getAction()))
                    .setPositiveButton(android.R.string.ok, null).show()
                    .findViewById(android.R.id.message);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
