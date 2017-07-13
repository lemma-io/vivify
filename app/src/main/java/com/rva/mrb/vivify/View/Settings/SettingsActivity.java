package com.rva.mrb.vivify.View.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rva.mrb.vivify.R;

/**
 * Created by Bao on 9/28/16.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
        @Override
        protected void onCreate(final Bundle savedInstanceState)
        {
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

    public static class MyPreferenceFragment extends PreferenceFragment
        {
            @Override
            public void onCreate(final Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);
            }
        }
    }