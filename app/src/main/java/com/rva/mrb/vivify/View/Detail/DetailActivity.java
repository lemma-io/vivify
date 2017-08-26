package com.rva.mrb.vivify.View.Detail;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rva.mrb.vivify.AlarmApplication;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.BaseActivity;
import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Data.Album;
import com.rva.mrb.vivify.Model.Data.Artist;
import com.rva.mrb.vivify.Model.Data.MediaType;
import com.rva.mrb.vivify.Model.Data.Playlist;
import com.rva.mrb.vivify.Model.Data.Track;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.View.Adapter.AlarmAdapter;
import com.rva.mrb.vivify.View.Search.SearchActivity;


import org.parceler.Parcels;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends BaseActivity implements DetailView {

    private static final String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.edit_name) EditText editname;
    @BindView(R.id.edit_time) EditText mEditTime;
    @BindView(R.id.track_tv) TextView mTrackTv;
    @BindView(R.id.isSet) CheckBox mIsSet;
//    @BindView(R.id.standard_time) CheckBox mStandardTime;
    @BindView(R.id.shuffle) CheckBox mShuffle;
    @BindView(R.id.vibrate) CheckBox mVibrate;
//    @BindView(R.id.button_add) Button addbt;
//    @BindView(R.id.button_delete) Button deletebt;
//    @BindView(R.id.button_save) Button savebt;
    @BindView(R.id.sunday_check) CheckBox sundayCb;
    @BindView(R.id.monday_check) CheckBox mondayCb;
    @BindView(R.id.tuesday_check) CheckBox tuesdayCb;
    @BindView(R.id.wednesday_check) CheckBox wednesdayCb;
    @BindView(R.id.thursday_check) CheckBox thursdayCb;
    @BindView(R.id.friday_check) CheckBox fridayCb;
    @BindView(R.id.saturday_check) CheckBox saturdayCb;
    @BindView(R.id.alarm_detail_bg) ImageView alarmDetailBg;
    private String trackName;
    private String artistName;
    private String trackId;
    private String trackImage;
    private int mediaType;
    private boolean isNew;
    private Alarm alarm = new Alarm();
    final private int requestCode = 1;

    // used to create a binary representation of days an alarm is enabled
    private int repeatDays = 0;

    @Inject DetailPresenter detailPresenter;
    private AlarmAdapter.OnAlarmToggleListener alarmToggleListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        setContentView(R.layout.activity_add_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        //inject dagger and butterknife dependencies
        DetailComponent detailComponent = DaggerDetailComponent.builder()
                .applicationModule(new ApplicationModule((AlarmApplication) getApplication()))
                .detailModule(new DetailModule())
                .applicationComponent(((AlarmApplication) getApplication()).getComponent())
                .build();
        detailComponent.inject(this);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            isNew = true;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        isNewAlarm();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(isNew){
            getMenuInflater().inflate(R.menu.new_alarm_menu, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.detail_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.exit_alarm:
                onDeleteAlarm();
                return true;
            case R.id.save_alarm:
                onAddClick();
                return true;
            default:
                if(!isNew) {
                    onSaveAlarm();
                }
                supportFinishAfterTransition();
                return true;
//                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method programmatically shows add, save, and delete buttons
     */
    private void isNewAlarm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmDetailBg.setTransitionName("detail");
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
//            setVisibility();
            Log.d("DetailActivity", Boolean.toString(bundle.getBoolean("NewAlarm", true)));
            if (bundle.getBoolean("NewAlarm", true) == false) {
                alarm = Parcels.unwrap(getIntent().getParcelableExtra("Alarm"));
                setMediaBackgroundImage(alarm.getTrackImage());
                mEditTime.setText(alarm.getmWakeTime());
                mIsSet.setChecked(alarm.isEnabled());
//                mStandardTime.setChecked(alarm.is24hr());
                mShuffle.setChecked(alarm.isShuffle());
                mVibrate.setChecked(alarm.isVibrate());
                trackName = alarm.getTrackName();
                artistName = bundle.getString("AlarmArtist");
//                artistName = alarm.getArtistName();
                trackId = alarm.getTrackId();
                trackImage = alarm.getTrackImage();
                mediaType = alarm.getMediaType();
                setTrackTv();
                setRepeatCheckBoxes(alarm.getDecDaysOfWeek());
            }
        } else {
            // TODO fill with default settings for new alarm
            Log.d("DetailTime", detailPresenter.getCurrentTime());
            mEditTime.setText(detailPresenter.getCurrentTime());
            alarm.setTime(Calendar.getInstance().getTime());
        }
    }

    private void setMediaBackgroundImage(String trackImage) {
        Glide.with(getApplicationContext())
                .load(trackImage)
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(alarmDetailBg);
    }

//    private void setVisibility() {
//        addbt.setVisibility(View.GONE);
////        savebt.setVisibility(View.VISIBLE);
////        deletebt.setVisibility(View.VISIBLE);
//    }

    // TODO handle by presenter
    private void setRepeatCheckBoxes(int daysOfWeek) {
        if ((daysOfWeek & Alarm.SUNDAY) == Alarm.SUNDAY)
            sundayCb.setChecked(true);
        if ((daysOfWeek & Alarm.MONDAY) == Alarm.MONDAY)
            mondayCb.setChecked(true);
        if ((daysOfWeek & Alarm.TUESDAY) == Alarm.TUESDAY)
            tuesdayCb.setChecked(true);
        if ((daysOfWeek & Alarm.WEDNESDAY) == Alarm.WEDNESDAY)
            wednesdayCb.setChecked(true);
        if ((daysOfWeek & Alarm.THURSDAY) == Alarm.THURSDAY)
            thursdayCb.setChecked(true);
        if ((daysOfWeek & Alarm.FRIDAY) == Alarm.FRIDAY)
            fridayCb.setChecked(true);
        if ((daysOfWeek & Alarm.SATURDAY) == Alarm.SATURDAY)
            saturdayCb.setChecked(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        detailPresenter.setView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        detailPresenter.clearView();
    }

    @Override
    protected void closeRealm() {
        detailPresenter.closeRealm();
    }

    /**
     * This method passes the alarm object to detailPresenter to add the alarm to realm
     */
//    @OnClick(R.id.button_add)
    public void onAddClick() {
        setAlarm();
        detailPresenter.onAddClick(alarm, getApplicationContext());

        Intent returnIntent = new Intent();
        returnIntent.putExtra("enabled", true);
        Log.d(TAG, "Extra " + returnIntent.getBooleanExtra("enabled", true));
        setResult(Activity.RESULT_OK, returnIntent);
        supportFinishAfterTransition();
//        finish();
    }

    /**
     * This method passes the current alarm object to detailPresenter to be deleted
     */
//    @OnClick(R.id.button_delete)
    public void onDeleteAlarm() {
        detailPresenter.onDeleteAlarm(getApplicationContext(), alarm);
        finish();
    }

    /**
     * This method passes the current alarm object to detailPresenter to be saved to realm
     */
//    @OnClick(R.id.button_save)
    public void onSaveAlarm() {
        setAlarm();
        detailPresenter.onSaveAlarm(alarm, getApplicationContext());
        supportFinishAfterTransition();
//        finish();
    }

    @Override
    public void onBackPressed()
    {
        if(!isNew) {
            onSaveAlarm();
        }
        supportFinishAfterTransition();
//        super.onBackPressed();  // optional depending on your needs
    }

    @OnClick(R.id.edit_time)
    public void onPickTime() {
        TimePickerDialog timePickerDialog =
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                mEditTime.setText(detailPresenter.getTime(hour, minute));
                Log.d("timepicker", "hour: " + hour);
                alarm.setmWakeTime(mEditTime.getText().toString());
                Date date = detailPresenter.getDate(alarm, hour, minute);
                alarm.setTime(date);
                alarm.setTimeOfDay(detailPresenter.getTimeOfDay(date));
            }
                    // Set Alarm time as default if it exists
        }, detailPresenter.getHour(alarm), detailPresenter.getMinute(alarm), false);
        timePickerDialog.show();
    }

    /**
     * This method sets the repeating days
     */
    public void onSetRepeat() {

        repeatDays = 0;
        alarm.setDaysOfWeek("0");
        // using bit wise operations to keep track of days
        // binary represenation of an alarm repeating
        // on Sunday and Saturday is 1000001

        // Should all CB's be passed to presenter?
        if (sundayCb.isChecked())
            repeatDays = repeatDays | Alarm.SUNDAY;
        if (mondayCb.isChecked())
            repeatDays = repeatDays | Alarm.MONDAY;
        if (tuesdayCb.isChecked())
            repeatDays = repeatDays | Alarm.TUESDAY;
        if (wednesdayCb.isChecked())
            repeatDays = repeatDays | Alarm.WEDNESDAY;
        if (thursdayCb.isChecked())
            repeatDays = repeatDays | Alarm.THURSDAY;
        if (fridayCb.isChecked())
            repeatDays = repeatDays | Alarm.FRIDAY;
        if (saturdayCb.isChecked())
            repeatDays = repeatDays | Alarm.SATURDAY;

        alarm.setDaysOfWeek(Integer.toBinaryString(repeatDays));
        Log.d(TAG, "Repeat Days: " + Integer.toBinaryString(repeatDays));
    }

    /**
     * This method starts(for result) a new SearchActivity to search for spotify music
     */
    @OnClick(R.id.spotify_search)
    public void onSearchClick() {

        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, requestCode);
    }

    public void setAlarm() {
        onSetRepeat();
        alarm.setAlarmLabel(editname.getText().toString());
        alarm.setEnabled(mIsSet.isChecked());
        alarm.setmWakeTime(mEditTime.getText().toString());
        alarm.setShuffle(mShuffle.isChecked());
        alarm.setVibrate(mVibrate.isChecked());
        Log.d("Set", "Time: " + alarm.getTime());
        alarm.setDaysOfWeek(Integer.toBinaryString(repeatDays));
        alarm.setTrackId(trackId);
        alarm.setTrackImage(trackImage);
        alarm.setTrackName(trackName);
        Log.d("Set", "artist: " + artistName);
        alarm.setArtist(artistName);
        Log.d("Set", "get artist: " + alarm.getArtistName());
        alarm.setMediaType(mediaType);
    }

    /**
     * This method called when the user selects a song in SearchActivity. This method sets music info
     * to the alarm object
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                MediaType type = Parcels.unwrap(data.getParcelableExtra("track"));
                mediaType = type.getMediaType();
                // todo handle by presenter
                switch (type.getMediaType()) {
                    case MediaType.TRACK_TYPE:
                        Track track = type.getTrack();
                        Log.d("onActivityResult", track.getName());
                        trackName = track.getName();
                        artistName = track.getArtists().get(0).getName();
                        trackId = track.getId();
                        trackImage = track.getAlbum().getImages().get(1).getUrl();
                        break;
                    case MediaType.ALBUM_TYPE:
                        Album album = type.getAlbum();
                        trackName = album.getName();
                        artistName = album.getArtists().get(0).getName();
                        trackId = album.getId();
                        trackImage = album.getImages().get(0).getUrl();
                        break;
                    case MediaType.ARTIST_TYPE:
                        Artist artist = type.getArtist();
                        trackName = artist.getName();
                        artistName = artist.getName();
                        trackId = artist.getId();
                        trackImage = artist.getImages().get(0).getUrl();
                        break;
                    case MediaType.PLAYLIST_TYPE:
                        Playlist playlist = type.getPlaylist();
                        trackName = playlist.getName();
                        artistName = playlist.getOwner().getId();
                        trackId = playlist.getId();
                        trackImage = playlist.getImages().get(0).getUrl();
                        break;
                }
                setMediaBackgroundImage(trackImage);
                setTrackTv();
            }
        }
    }

    /**
     * This method displays the current spotify music assigned to the alarm object
     */
    public void setTrackTv() {
        mTrackTv.setText(trackName + " by " + artistName);
    }

}
