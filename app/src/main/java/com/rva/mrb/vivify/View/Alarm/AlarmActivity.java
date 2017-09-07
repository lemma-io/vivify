package com.rva.mrb.vivify.View.Alarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import com.rva.mrb.vivify.AlarmApplication;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.BaseActivity;
import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.NotificationService;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.View.Adapter.AlarmAdapter;
import com.rva.mrb.vivify.View.Detail.DetailActivity;
import com.rva.mrb.vivify.View.Login.LoginActivity;
import com.rva.mrb.vivify.View.Settings.SettingsActivity;


import org.parceler.Parcels;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;

public class AlarmActivity extends BaseActivity implements AlarmsView{

    public static final String TAG = AlarmActivity.class.getSimpleName();
    public static final int DETAIL_ACTIVITY_REQUEST = 45;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_notification) TextView alarmNotification;
    @BindView(R.id.recyclerview) RealmRecyclerView mRecyclerView;
    @Inject AlarmsPresenter alarmPresenter;
    private NotificationService mNotificationService;
    private AlarmAdapter mAdapter;
    private AlarmAdapter.OnAlarmToggleListener listener;
    private AlarmAdapter.AlarmClickListener clickListener;
    private Context context;

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

        context = getApplicationContext();
        mNotificationService = new NotificationService(context);
        alarmPresenter.checkMissedAlarms(context, mNotificationService);
        //init default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // set our own toolbar and disable the default app name title
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // find the next scheduled alarm
        updateAlarmNotification();

        // custom listener listening alarm toggles
        listener = () -> updateAlarmNotification();

        clickListener = (pos, alarm, sharedImageView) -> startDetailActivity(pos, alarm, sharedImageView);

        // create a new container to list all alarms
        // and set to auto update from realm results
        mAdapter = new AlarmAdapter(getApplicationContext(),
                alarmPresenter.getAllAlarms(), listener, clickListener, true, true);
        mRecyclerView.setAdapter(mAdapter);

        updateAlarmNotification();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.alarm_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startDetailActivity(int pos, Alarm alarm, ImageView sharedImageView){
        Log.d(TAG, "Opening Detail activity on id: " + alarm.getId());
        Intent intent = new Intent(context, DetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("NewAlarm", false);
        intent.putExtra("AlarmArtist", alarm.getArtistName());
        intent.putExtra("Alarm", Parcels.wrap(alarm));
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, sharedImageView, "detail");

        startActivity(intent, options.toBundle());
    }

    /**
     * This method checks if the user is logged in. If logged in, this method will start the
     * AlarmActivity
     */
    public void checkLoginStatus(){
        Log.d(TAG, "Checking login status");
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        alarmPresenter.checkMissedAlarms(context, mNotificationService);
        mAdapter.notifyDataSetChanged();
        supportStartPostponedEnterTransition();
        checkLoginStatus();
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
        alarmPresenter.checkMissedAlarms(context, mNotificationService);
        mAdapter.notifyDataSetChanged();
        supportStartPostponedEnterTransition();
        checkLoginStatus();
        updateAlarmNotification();
    }

    public void showAlarms(final RealmResults<Alarm> alarms) {
    }

    @Override
    public void showAddNewAlarmView() {
        startActivityForResult(new Intent(this, DetailActivity.class), DETAIL_ACTIVITY_REQUEST);
    }

    /**
     * Onclick method that open alarm details activity for a new alarm.
     */
    @OnClick(R.id.new_alarm_fab)
    public void onAddNewAlarmClick(){
        alarmPresenter.onAddNewAlarm();
    }

    public void updateAlarmNotification() {
        Alarm nextAlarm = alarmPresenter.getNextAlarmTime();

        if (nextAlarm!=null){
//            if(nextAlarm.isSnoozed()) {
//                alarmNotification.setText(nextAlarm.getSnoozedAt() + "");
//            }
//            else {
//                alarmNotification.setText(nextAlarm.getTime() + "");
//            }
            mNotificationService.setNotification(
                    alarmPresenter.prettyDateFormat(nextAlarm.getTime())+"", nextAlarm.isSnoozed());
        }
        else {
            mNotificationService.cancelNotification();
//            alarmNotification.setText("No Alarms Set");
        }
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
        if (requestCode == DETAIL_ACTIVITY_REQUEST) {
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

