package com.itachi1706.hypixelstatistics;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.util.HistoryHandling.ExceptionHistory;
import com.itachi1706.hypixelstatistics.util.ListViewAdapters.ExceptionListAdapter;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;
import com.itachi1706.hypixelstatistics.util.NotifyUserUtil;
import com.itachi1706.hypixelstatistics.util.Objects.ExceptionObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ViewAllExceptions extends AppCompatActivity {

    ListView items;
    TextView count;
    ExceptionListAdapter adapter;
    ArrayList<ExceptionObject> storage;
    ExceptionHistory hist;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set Theme
        setTheme(MainStaticVars.getTheme(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.getWindow().setStatusBarColor(getResources().getColor(MainStaticVars.getStatusBarColor(this)));

        setContentView(R.layout.activity_view_all_exceptions);

        count = (TextView) findViewById(R.id.tvExceptList);
        items = (ListView) findViewById(R.id.lvException);

        this.hist = new ExceptionHistory(this.getApplicationContext());
        this.activity = this;

        HashMap<String, ExceptionObject> obj = hist.getAllExceptions();
        this.storage = new ArrayList<>();
        for (Map.Entry<String, ExceptionObject> o : obj.entrySet()){
            this.storage.add(o.getValue());
        }
        this.adapter = new ExceptionListAdapter(this, R.layout.listview_exception_item, this.storage);
        items.setAdapter(adapter);
        updateCount();

        items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ExceptionObject o = (ExceptionObject) items.getItemAtPosition(position);
                new AlertDialog.Builder(ViewAllExceptions.this).setTitle(o.getTitle())
                        .setMessage(o.getDescription()).setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (hist.deleteException(o.getFile())) {
                            //Create a snackbar
                            NotifyUserUtil.showShortDismissSnackbar(getWindow().getCurrentFocus(), "Exception File deleted");
                            storage.remove(o);
                            adapter.updateAdapter(storage);
                            adapter.notifyDataSetChanged();
                            updateCount();
                        } else {
                            NotifyUserUtil.createShortToast(activity, "Unable to delete exception file");
                        }
                    }
                }).setPositiveButton("Close", null).show();
            }
        });
    }

    private void updateCount(){
        count.setText(Html.fromHtml(MinecraftColorCodes.parseColors("§b" + storage.size() + "§r app crash exceptions")));
    }

    @Override
    public void onResume(){
        super.onResume();

        HashMap<String, ExceptionObject> obj = hist.getAllExceptions();
        this.storage.clear();
        for (Map.Entry<String, ExceptionObject> o : obj.entrySet()){
            this.storage.add(o.getValue());
        }
        this.adapter.updateAdapter(this.storage);
        this.adapter.notifyDataSetChanged();
        updateCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_view_all_exceptions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
