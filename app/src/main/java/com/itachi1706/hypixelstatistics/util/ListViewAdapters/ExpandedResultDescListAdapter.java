package com.itachi1706.hypixelstatistics.util.ListViewAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.util.Objects.ResultDescription;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/11/2014, 9:44 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class ExpandedResultDescListAdapter extends BaseExpandableListAdapter {

    private ArrayList<ResultDescription> items;
    public Activity activity;
    public LayoutInflater inflater;

    public ExpandedResultDescListAdapter(Activity act, ArrayList<ResultDescription> objects){
        activity = act;
        this.items = objects;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition){
        return items.get(groupPosition).get_childItems(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
        final ResultDescription i = (ResultDescription) getChild(groupPosition, childPosition);

        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.listview_result_desc_item, null);
        }

        //ResultDescription i = items.get(position);

        TextView title = (TextView) v.findViewById(R.id.tvExpTitle);
        TextView desc = (TextView) v.findViewById(R.id.tvExpResult);

        if (desc != null){
            if (i.is_hasDescription()){
                desc.setText(Html.fromHtml(i.get_result()));
                desc.setTextSize(14);
            } else {
                desc.setText(Html.fromHtml(i.get_title()));
                desc.setTextSize(22);
            }
        }
        if (title != null){
            if (i.is_hasDescription()) {
                title.setVisibility(View.VISIBLE);
                title.setText(Html.fromHtml(i.get_title()));
            } else {
                title.setVisibility(View.GONE);
                title.setText("");
            }
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i != null) {
                    if (i.get_alert() != null){
                        TextView tv = (TextView) new AlertDialog.Builder(activity)
                                .setTitle(i.get_title())
                                .setMessage(Html.fromHtml(i.get_alert()))
                                //.setView(tv)
                                .setPositiveButton(android.R.string.ok, null).show()
                                .findViewById(android.R.id.message);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                }
            }
        });

        return v;
    }

    @Override
    public int getChildrenCount(int groupPosition){
        return items.get(groupPosition).get_childItems().size();
    }

    @Override
    public ResultDescription getGroup(int groupPosition){
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount(){
        return items.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition){
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition){
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition){
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_result_desc_grp, null);
        }

        ResultDescription group = getGroup(groupPosition);
        CheckedTextView header = (CheckedTextView) convertView.findViewById(R.id.tvExpGrpTitle);

        header.setText(Html.fromHtml(group.get_title()));
        header.setChecked(isExpanded);

        return convertView;
    }

    @Override
    public boolean hasStableIds(){
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
