package com.rva.mrb.vivify.View.Wake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.rva.mrb.vivify.AlarmApplication;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.BaseActivity;
import com.rva.mrb.vivify.BuildConfig;
import com.rva.mrb.vivify.Model.Data.AccessToken;
import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Data.MediaType;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.Model.Service.RealmService;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.Spotify.NodeService;
import com.spotify.sdk.android.player.*;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WakeActivity extends BaseActivity implements ConnectionStateCallback, SpotifyPlayer.NotificationCallback {

    @BindView(R.id.dismiss_tv) TextView dismissTv;
    @BindView(R.id.snooze_tv) TextView snoozeTv;
    @BindView(R.id.myseek) SeekBar seekBar;
    @BindView(R.id.trackImageView) ImageView trackIV;
    @BindView(R.id.next_song) ImageButton fastForward;
    @Inject
    WakePresenter wakePresenter;
    @Inject
    NodeService nodeService;

    // Spotify
    private static final String CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID;
    private static final int REQUEST_CODE = 5123;
    private static final String REDIRECT_URI = "vivify://callback";
    private Player mPlayer;
    private Config playerConfig;
    private ApplicationModule applicationModule = new ApplicationModule((AlarmApplication) getApplication());
    private String trackId;
    private String trackImage;
    private String alarmId;
    private boolean snoozed;
    private Alarm alarm;
    private String playlistID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Dagger and Butterknife dependenncy injecetions
        setContentView(R.layout.activity_wake);
        WakeComponent wakeComponent = DaggerWakeComponent.builder()
                .applicationModule(applicationModule)
                .wakeModule(new WakeModule(this))
                .applicationComponent(((AlarmApplication) getApplication()).getComponent())
                .build();
        wakeComponent.inject(this);
        ButterKnife.bind(this);

        //Retrieve access token from spotify
        refreshToken();

        //Get trackId and image URL from Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            trackId = (String) extras.get("trackId");
            trackImage = (String) extras.get("trackImage");
            alarmId = (String) extras.get("alarmId");
            snoozed = Boolean.parseBoolean(extras.getString("snoozed", "false"));
            Log.d("WakeActivity", "snoozed: "+snoozed);
            Log.d("PlayAlbum", "Alarm created");
            alarm = RealmService.getAlarmById(alarmId);
            playlistID = alarm.getArtistName();

            //Use Glide to load image URL
            Glide.with(this)
                    .load(trackImage)
                    .into(trackIV);
            trackIV.setScaleType(ImageView.ScaleType.FIT_XY);
            Log.d("trackImage", "Traack Image Url: " + trackImage);
        }
        //Set the seekbar that dissmisses/snoozes alarm
        setSeekBar();

        //Allow activity to wake up device
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }


//    @Override
//    protected void onResume() {}

    @Override
    protected void closeRealm() {

    }

    /*
    This method is called when the user dismisses the alarm. It cancels the alarm and pauses the
    player.
     */
    public void onDismiss() {
        if(snoozed){
            AlarmScheduler.cancelSnoozedAlarm(getApplicationContext());
        }
        else {
            AlarmScheduler.cancelNextAlarm(getApplicationContext());
        }

        mPlayer.pause();
        if (alarmId != null) {
            Log.d("Dismiss", "alarm ID: " + alarmId);
            AlarmScheduler.disableAlarmById(getApplicationContext(), alarmId);
        }
        finish();
    }

    /*
    This method is called when the user snoozes the alarm. It pauses the player and reschedules the
    alarm.
     */
    public void onSnooze() {
        mPlayer.pause();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int snoozeMins = Integer.parseInt(sharedPref.getString("snooze_key", "5"));
        int snoozeTime = snoozeMins * 60000;
        Log.d("snooze", "Snooze time in mins: "+ snoozeMins);
        Log.d("snooze", "Snooze time in millis: "+ snoozeTime);
        if (alarmId != null) {
            Log.d("Snooze", "alarm ID: " + alarmId);
            AlarmScheduler.setSnoozedById(getApplicationContext(), alarmId);
        }
        snoozed = true;
        AlarmScheduler.snoozeNextAlarm(getApplicationContext(), trackId, trackImage, alarmId, snoozed, snoozeTime);
        finish();
    }

    /*
    This method sets the seekbar listener and allows user to snooze or dismiss the alarm.
     */
    public void setSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() > 85) {
                    dismissTv.setTextSize(30);
                    dismissTv.setTypeface(null, Typeface.BOLD);
                } else if (seekBar.getProgress() < 15) {
                    snoozeTv.setTextSize(30);
                    snoozeTv.setTypeface(null, Typeface.BOLD);
                } else {
                    dismissTv.setTextSize(20);
                    snoozeTv.setTextSize(20);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 85) {
                    onDismiss();
                } else if (seekBar.getProgress() < 15) {
                    onSnooze();
                } else {
                    seekBar.setProgress(50);
                }
            }
        });
    }

    /**
     * This method makes a call to the backend server and obtains a fresh access token
     */
    private void refreshToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("refresh_token", null);
        Log.d("Node", "sharedpref refresh token: " + refreshToken);
        nodeService.refreshToken(refreshToken).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                AccessToken results = response.body();
                applicationModule.setAccessToken(results.getAccessToken());

                initSpotifyPlayer();

            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.d("Node", "error: " + t.getMessage());
            }
        });
    }

    /**
     * This method initializes SpotifyPlayer after a fresh access token has been obtained.
     */
    public void initSpotifyPlayer() {
        playerConfig = new Config(this, applicationModule.getAccessToken(), CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                mPlayer = spotifyPlayer;
                mPlayer.addConnectionStateCallback(WakeActivity.this);
                mPlayer.addNotificationCallback(WakeActivity.this);
                Log.d("spotifyPlayer", "initialized player");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    @OnClick(R.id.next_song)
    public void onNextSongClick(){
        mPlayer.skipToNext();
    }

    /**
     * This method plays music once SpotifyPlayer has been initialized
     */
    @Override
    public void onLoggedIn() {
        Log.d("PlayAlbum", "Alarm Type: " + alarm.getMediaType());
        switch (alarm.getMediaType()) {
            case MediaType.TRACK_TYPE:
                mPlayer.playUri("spotify:track:" + trackId, 0, 0);
                mPlayer.setRepeat(true);
                break;
            case MediaType.ALBUM_TYPE:
                Log.d("PlayAlbum", "spotify:album:" + trackId);
                mPlayer.playUri("spotify:album:" + trackId, 0, 0);
                mPlayer.setRepeat(true);
                break;
            case MediaType.PLAYLIST_TYPE:
                Log.d("PlayAlbum", "spotify:user:" + playlistID +":playlist:"+ trackId);
                mPlayer.playUri("spotify:user:"+playlistID+":playlist:"+trackId, 0, 0);
                mPlayer.setRepeat(true);
                break;
        }

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(int i) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent event) {

    }


    @Override
    public void onPlaybackError(com.spotify.sdk.android.player.Error error) {

    }
}
