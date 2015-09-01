package com.itachi1706.hypixelstatistics.ListViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itachi1706.hypixelstatistics.R;
import com.itachi1706.hypixelstatistics.Objects.ExceptionComparator;
import com.itachi1706.hypixelstatistics.Objects.ExceptionObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by Kenneth on 13/11/2014, 9:44 PM
 * for Hypixel Statistics in package com.itachi1706.hypixelstatistics.util
 */
public class ExceptionListAdapter extends ArrayAdapter<ExceptionObject> {

    private ArrayList<ExceptionObject> items;

    public ExceptionListAdapter(Context context, int textViewResourceId, ArrayList<ExceptionObject> objects){
        super(context, textViewResourceId, objects);
        this.items = objects;
        sortList();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.listview_exception_item, parent, false);
        }

        ExceptionObject i = items.get(position);

        TextView title = (TextView) v.findViewById(R.id.tvTitle);
        TextView timeStamp = (TextView) v.findViewById(R.id.tvTimestamp);

        if (timeStamp != null){
            timeStamp.setText(new SimpleDateFormat("dd-MMM-yyyy hh:mm a zz").format(new Date(i.getTimeStampInMillis())));
        }
        if (title != null){
            title.setText(i.getTitle());
        }

        return v;
    }

    @Override
    public int getCount(){
        return items != null? items.size() : 0;
    }

    public void updateAdapter(ArrayList<ExceptionObject> newArrayData){
        this.items = newArrayData;
        sortList();
    }

    private void sortList(){
        Collections.sort(this.items, new ExceptionComparator());
    }
}
