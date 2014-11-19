package com.itachi1706.hypixelstatistics;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itachi1706.hypixelstatistics.util.HistoryObject;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class GeneralPrefActivity extends ActionBarActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            //Debug Info Get
            String version = "NULL", packName = "NULL";
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                version = pInfo.versionName;
                packName = pInfo.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            Preference verPref = findPreference("view_app_version");
            verPref.setSummary(version);
            Preference pNamePref = findPreference("view_app_name");
            pNamePref.setSummary(packName);

            Preference prefs = findPreference("view_sdk_version");
            prefs.setSummary(android.os.Build.VERSION.RELEASE);

            prefs = findPreference("view_api_info");
            prefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), KeyInfoActivity.class));
                    return true;
                }
            });

            Preference hist = findPreference("view_hist");
            hist.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    String jsonHist = prefs.getString("history", null);
                    if (jsonHist != null) {
                        StringBuilder builder = new StringBuilder();
                        Gson gson = new Gson();
                        HistoryObject obj = gson.fromJson(jsonHist, HistoryObject.class);
                        JsonArray arr = obj.getHistory();
                        for (JsonElement e : arr){
                            JsonObject o = e.getAsJsonObject();
                            builder.append(o.get("displayname").getAsString());
                            builder.append(" (").append(o.get("playername").getAsString()).append(")\n");
                        }
                        new AlertDialog.Builder(getActivity()).setTitle("History").setMessage(builder.toString()).show();
                        return true;
                    }
                    new AlertDialog.Builder(getActivity()).setTitle("History").setMessage("No History Found").show();
                    return true;
                }
            });

            Preference dhist = findPreference("view_hist_detailed");
            dhist.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    String jsonHist = prefs.getString("history", null);
                    if (jsonHist != null) {
                        new AlertDialog.Builder(getActivity()).setTitle("History").setMessage(jsonHist).show();
                        return true;
                    }
                    new AlertDialog.Builder(getActivity()).setTitle("History").setMessage("No History Found").show();
                    return true;
                }
            });

            Preference histClr = findPreference("view_hist_clr");
            histClr.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    prefs.edit().putString("history", null).apply();
                    new AlertDialog.Builder(getActivity()).setTitle("History").setMessage("History Cleared!")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    return true;
                }
            });

        }
    }
}
