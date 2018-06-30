package com.rva.mrb.vivify.View.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.View.Login.LoginActivity;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment())
                .commit();
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

            onSendFeedback();
            onLogOut();
        }

        private void onSendFeedback() {
            Preference feedback = findPreference("key_send_feedback");
            feedback.setOnPreferenceClickListener(preference -> {
                sendFeedback(getActivity());
                return true;
            });
        }

        private void onLogOut() {
            Preference logout = findPreference("logout");
            logout.setOnPreferenceClickListener(preference -> {
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
            });
        }

        public static void sendFeedback(Context context) {
            String body = null;
            try {
                body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                body = "\n\n========================================\n\n Device OS: Android \n Device OS version: " +
                        Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                        "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER +
                        "\n========================================\n";
            } catch (PackageManager.NameNotFoundException e) {
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ context.getString(R.string.app_email) });
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ "bdpham93@gmail.com" });
            intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app");
            intent.putExtra(Intent.EXTRA_TEXT, body);
            context.startActivity(Intent.createChooser(intent, "Email"));
        }
    }
}