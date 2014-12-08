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
import android.text.Html;
import android.widget.EditText;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.AsyncAPI.GetKeyInfoVerification;

import java.util.UUID;

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

            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            final Preference api_key = findPreference("api_key");
            final Preference staff_rank = findPreference("staff_rnk");
            final Preference player_IGN = findPreference("staff_p");
            updateKeyString(sp, api_key, prefs);
            updateApiKeyOwnerInfo(sp, staff_rank, player_IGN);

            final Preference finalAPIInfo = prefs;
            api_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //Alert Dialog asking to enter new api key
                    final EditText newKey = new EditText(getActivity());
                    newKey.setSingleLine(true);
                    newKey.setHint("Enter API Key (Including dashes)");
                    new AlertDialog.Builder(getActivity()).setTitle("Enter Personal API Key")
                            .setMessage("This allows you to insert your own API Key to reduce the" +
                                    "occurrence of API throttling. \n" +
                                    "If you are a staff member, inserting your personal key" +
                                    " will grant you access to more information \n\n" +
                                    "To get your API Key, Launch Minecraft, join mc.hypixel.net and do /api in-game.\n\n" +
                                    "Enter the entire API String given to you (including dashes) in the textbox below.")
                            .setView(newKey)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String newKeyString = newKey.getText().toString();
                                    if (newKeyString.equals("")) {
                                        Toast.makeText(getActivity(), "INVALID API KEY", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    //Check if its a UUID
                                    UUID uid;
                                    try {
                                        uid = UUID.fromString(newKeyString);
                                    } catch (IllegalArgumentException e) {
                                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    api_key.setSummary("Verifying Key...");
                                    new GetKeyInfoVerification(getActivity(), sp, api_key, finalAPIInfo, staff_rank, player_IGN).execute(uid);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .setNeutralButton("Reset Key", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sp.edit().remove("api-key").apply();
                                    updateKeyString(sp, api_key, finalAPIInfo);
                                    updateApiKeyOwnerInfo(sp, staff_rank, player_IGN);
                                    Toast.makeText(getActivity(), "API Key has been reset to default!", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                    return true;
                }
            });

            Preference histAct = findPreference("view_hist_activity");
            histAct.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), HistPrefActivity.class));
                    return true;
                }
            });
        }

        public void updateKeyString(SharedPreferences sp, Preference apikey, Preference apikeyinfo){
            String keyString = sp.getString("api-key", "Default Key");
            apikey.setSummary(keyString);
            if (keyString.equals("Default Key"))
                apikeyinfo.setEnabled(false);
            else {
                apikeyinfo.setEnabled(true);
            }
        }

        public void updateApiKeyOwnerInfo(SharedPreferences sp, Preference staff, Preference name){
            String keyString = sp.getString("api-key", "Default Key");
            if (keyString.equals("Default Key")){
                staff.setSummary("-");
                name.setSummary("-");
                staff.setEnabled(false);
                name.setEnabled(false);
            } else {
                staff.setSummary(sp.getString("rank", "Not Staff"));
                name.setSummary(Html.fromHtml(sp.getString("playerName", "Cannot find player")));
                staff.setEnabled(true);
                name.setEnabled(true);
            }
        }
    }
}
