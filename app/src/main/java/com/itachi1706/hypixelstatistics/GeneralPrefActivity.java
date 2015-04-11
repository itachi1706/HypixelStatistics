package com.itachi1706.hypixelstatistics;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.itachi1706.hypixelstatistics.AsyncAPI.AppUpdateCheck;
import com.itachi1706.hypixelstatistics.AsyncAPI.KeyCheck.GetKeyInfoVerification;
import com.itachi1706.hypixelstatistics.util.MainStaticVars;
import com.itachi1706.hypixelstatistics.util.MinecraftColorCodes;

import java.util.ArrayList;
import java.util.Collections;
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
    @SuppressWarnings("ConstantConditions")
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

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

            Preference updaterPref = findPreference("launch_updater");
            updaterPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AppUpdateCheck(getActivity(), sp).execute();
                    return false;
                }
            });

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

            final Preference api_key = findPreference("api_key");
            final Preference staff_rank = findPreference("staff_rnk");
            final Preference player_IGN = findPreference("staff_p");
            final Preference player_UUID = findPreference("staff_uuid");
            updateKeyString(sp, api_key, prefs, getActivity());
            updateApiKeyOwnerInfo(sp, staff_rank, player_IGN, player_UUID);

            player_UUID.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Player UUID", player_UUID.getSummary());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(),"Text copied to clipboard", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            final Preference finalAPIInfo = prefs;
            api_key.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //Alert Dialog asking to enter new api key
                    final EditText newKey = new EditText(getActivity());
                    newKey.setSingleLine(true);
                    newKey.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                    newKey.setHint("Enter API Key (Including dashes)");
                    String dialogPrompt = getString(R.string.pref_set_api_key_dialog_init_prompt);
                    new AlertDialog.Builder(getActivity()).setTitle("Enter Personal API Key")
                            .setMessage(Html.fromHtml(MinecraftColorCodes.parseColors(dialogPrompt.replace("\n","<br />"))))
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
                                            new GetKeyInfoVerification(getActivity(), sp, api_key, finalAPIInfo, staff_rank, player_IGN, player_UUID).execute(uid);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .setNeutralButton("Reset Key", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            sp.edit().remove("api-key").apply();
                                            sp.edit().remove("rank").apply();
                                            sp.edit().remove("playerName").apply();
                                            sp.edit().remove("own").apply();
                                            updateKeyString(sp, api_key, finalAPIInfo, getActivity());
                                            updateApiKeyOwnerInfo(sp, staff_rank, player_IGN, player_UUID);
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

            Preference devInfoPref = findPreference("vDevInfo");
            devInfoPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), DebugSettings.class);
                    startActivity(intent);
                    return true;
                }
            });

            Preference changelogPref = findPreference("android_changelog");
            changelogPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String changelog = sp.getString("version-changelog", "l");
                    if (changelog.equals("l")){
                        //Not available
                        new AlertDialog.Builder(getActivity()).setTitle("No Changelog")
                                .setMessage("No changelog was found. Please check if you can connect to the server")
                                .setPositiveButton(android.R.string.ok, null).show();
                    } else {
                        String[] changelogArr = changelog.split("\n");
                        ArrayList<String> changelogArrList = new ArrayList<>();
                        Collections.addAll(changelogArrList, changelogArr);
                        String body = MainStaticVars.getChangelogStringFromArrayList(changelogArrList);
                        new AlertDialog.Builder(getActivity()).setTitle("Changelog")
                                .setMessage(Html.fromHtml(body)).setPositiveButton("Close", null).show();
                    }
                    return true;
                }
            });

            Preference oldVersionPref = findPreference("get_old_app");
            oldVersionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getResources().getString(R.string.link_legacy)));
                    startActivity(i);
                    return false;
                }
            });

            Preference latestVersionPref = findPreference("get_latest_app");
            latestVersionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(getResources().getString(R.string.link_updates)));
                    startActivity(i);
                    return false;
                }
            });

            Preference useDetailedPref = findPreference("detailed_active_boosters");
            useDetailedPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity().getApplicationContext(), "Preference will take effect after a reboot " +
                            "or refresh of active boosters", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        public void updateKeyString(SharedPreferences sp, Preference apikey, Preference apikeyinfo, Context mCon){
            String keyString = sp.getString("api-key", "Default Key");
            apikey.setSummary(keyString);
            MainStaticVars.updateAPIKey(mCon);
            if (keyString.equals("Default Key")) {
                apikeyinfo.setEnabled(false);
                apikey.setTitle("API Key (Click to enter new key)");
            } else {
                apikeyinfo.setEnabled(true);
                apikey.setTitle("API Key (Click to change key)");
            }
        }

        public void updateApiKeyOwnerInfo(SharedPreferences sp, Preference staff, Preference name, Preference uuid){
            String keyString = sp.getString("api-key", "Default Key");
            if (keyString.equals("Default Key")){
                staff.setSummary("-");
                name.setSummary("-");
                uuid.setSummary("-");
                staff.setEnabled(false);
                name.setEnabled(false);
                uuid.setEnabled(false);
            } else {
                staff.setSummary(sp.getString("rank", "Not Staff"));
                name.setSummary(Html.fromHtml(sp.getString("playerName", "Cannot find player")));
                uuid.setSummary(sp.getString("own-uuid", "Invalid Player UUID. Please relaunch App"));
                staff.setEnabled(true);
                name.setEnabled(true);
                uuid.setEnabled(true);
            }
        }
    }
}
