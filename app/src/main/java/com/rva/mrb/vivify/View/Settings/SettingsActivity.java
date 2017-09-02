package com.rva.mrb.vivify.View.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.Model.Service.RealmService;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.View.Login.LoginActivity;

/**
 * Created by Bao on 9/28/16.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String ringtonePref = preferences.getString("default_ringtone_key",
                "DEFAULT_RINGTONE_URI");
        Log.d("Settings", "Ringtone:" + ringtonePref);
    }
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference logout = (Preference) findPreference("logout");
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("Preferences", preference.getKey());
                    Log.d("Preferences", "Logging out");
                    SharedPreferences sharedPref = getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("access_token", null);
                    editor.putString("refresh_token", null);
                    editor.putBoolean("isLoggedIn", false);
                    editor.commit();
                    AlarmScheduler.cancelSnoozedAlarm(getActivity().getApplicationContext());
                    AlarmScheduler.cancelNextAlarm(getActivity().getApplicationContext());
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    return true;
                }
            });
        }
    }
}