package com.rva.mrb.vivify.View.Alarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import com.rva.mrb.vivify.AlarmApplication;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.BaseActivity;
import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.View.Adapter.AlarmAdapter;
import com.rva.mrb.vivify.View.Detail.DetailActivity;
import com.rva.mrb.vivify.View.Login.LoginActivity;
import com.rva.mrb.vivify.View.Settings.SettingsActivity;


import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;

public class AlarmActivity extends BaseActivity implements AlarmsView {

    public static final String TAG = AlarmActivity.class.getSimpleName();
    public static final int DetailRequest = 45;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_notification) TextView alarmNotification;
    @BindView(R.id.recyclerview) RealmRecyclerView mRecyclerView;
    @Inject AlarmsPresenter alarmPresenter;

    private AlarmAdapter mAdapter;
    private AlarmAdapter.OnAlarmToggleListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check if user is logged in
        checkLoginStatus();

        setContentView(R.layout.activity_main);
        //Inject dagger and butterknife dependencies
        AlarmComponent alarmComponent = DaggerAlarmComponent.builder()
                        .applicationModule(new ApplicationModule((AlarmApplication)getApplication()))
                        .alarmModule(new AlarmModule(this))
                        .applicationComponent(((AlarmApplication) getApplication()).getComponent())
                        .build();
        alarmComponent.inject(this);
        ButterKnife.bind(this);

        //init default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // set our own toolbar and disable the default app name title
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // find the next scheduled alarm
        updateAlarmNotification();

        // custom listener listening alarm toggles
        listener = new AlarmAdapter.OnAlarmToggleListener() {
            @Override
            public void onAlarmToggle() {
                updateAlarmNotification();
            }
        };

//        Log.d(TAG, "All Alarms:" + alarmPresenter.getAllAlarms().first().toString());

        // create a new container to list all alarms
        // and set to auto update from realm results
        mAdapter = new AlarmAdapter(getApplicationContext(),
                alarmPresenter.getAllAlarms(), listener, true, true);
        mRecyclerView.setAdapter(mAdapter);


        updateAlarmNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.alarm_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * This method checks if the user is logged in. If logged in, this method will start the
     * AlarmActivity
     */
    public void checkLoginStatus(){
        Log.d("Login", "Checking login status");
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        updateAlarmNotification();
    }

    @Override
    public void onStart() {
        super.onStart();
        alarmPresenter.setView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.d(TAG, "Next wake time is " + alarmPresenter.getNextAlarmTime());
        mAdapter.notifyDataSetChanged();
        updateAlarmNotification();
    }

    public void showAlarms(final RealmResults<Alarm> alarms) {
    }

    @Override
    public void showAddNewAlarmView() {
//        startActivity(new Intent(this, DetailActivity.class));
        startActivityForResult(new Intent(this, DetailActivity.class), DetailRequest);
    }

    /**
     * Onclick method that open alarm details activity for a new alarm.
     */
    @OnClick(R.id.new_alarm_fab)
    public void onAddNewAlarmClick(){
        alarmPresenter.onAddNewAlarm();
    }

    public void updateAlarmNotification() {
        alarmNotification.setText(alarmPresenter.getNextAlarmTime());
    }

    public void closeRealm(){
        alarmPresenter.closeRealm();
    }

    public void onStop() {
        super.onStop();
        alarmPresenter.clearView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == DetailRequest) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Result Successful");
                if (data.getBooleanExtra("enabled", true)) {
                    Log.d(TAG, "New alarm is toggled");
                    updateAlarmNotification();
                }
            }
        }
    }
}