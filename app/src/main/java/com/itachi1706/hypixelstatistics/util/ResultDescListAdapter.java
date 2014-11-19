package com.itachi1706.hypixelstatistics.util;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.R;

import java.util.ArrayList;

/**
 * Created by Kenneth on 13/11/2014, 9:44 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class ResultDescListAdapter extends ArrayAdapter<ResultDescription> {

    private ArrayList<ResultDescription> items;

    public ResultDescListAdapter(Context context, int textViewResourceId, ArrayList<ResultDescription> objects){
        super(context, textViewResourceId, objects);
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_result_desc, parent, false);
        }

        ResultDescription i = items.get(position);

        if (!i.is_hasDescription()) {
            ListView.LayoutParams p = (ListView.LayoutParams) v.getLayoutParams();
            p.height = 100;
            v.setLayoutParams(p);
        } else {
            if (i.get_result().length() < 40 ){
                ListView.LayoutParams p = (ListView.LayoutParams) v.getLayoutParams();
                p.height = 150;
                v.setLayoutParams(p);
            } else {
                ListView.LayoutParams p = (ListView.LayoutParams) v.getLayoutParams();
                p.height = 180;
                v.setLayoutParams(p);
            }
        }


        TextView title = (TextView) v.findViewById(R.id.tvTitle);
        TextView desc = (TextView) v.findViewById(R.id.tvResult);

        if (desc != null){
            if (i.is_hasDescription()){
                desc.setText(Html.fromHtml(i.get_result()));
                desc.setTextSize(14);
            } else {
                if (i.is_subTitle()){
                    desc.setText(Html.fromHtml(i.get_title()));
                    desc.setTextSize(18);
                } else {
                    desc.setText(Html.fromHtml(i.get_title()));
                    desc.setTextSize(22);
                }
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

        return v;
    }
}
