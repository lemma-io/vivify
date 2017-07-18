package com.rva.mrb.vivify.View.Wake;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.Spotify.NodeService;
import com.spotify.sdk.android.player.*;
import com.spotify.sdk.android.player.Error;
import com.rva.mrb.vivify.Spotify.AudioTrackController;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WakeActivity extends BaseActivity implements ConnectionStateCallback, SpotifyPlayer.NotificationCallback {

    private static final String TAG = WakeActivity.class.getSimpleName();
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
    private boolean shuffle;
    private Alarm alarm;
    private String playlistID;
    private Ringtone r;
    private AudioManager am;
    private Context mContext;
    private AudioManager.OnAudioFocusChangeListener amFocusListener;
    private AudioTrackController audioTrackController;
    private Player.OperationCallback operationCallback;

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

        operationCallback = new Player.OperationCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Error error) {
                Log.d("WakeActivity", "OperationCallback error: " + error);
            }
        };

        //Retrieve access token from spotify
        refreshToken();
        mContext = getApplicationContext();
        am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_ALARM);

        audioTrackController = new AudioTrackController();
        //Get trackId and image URL from Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            alarmId = (String) extras.get("alarmId");
            alarm = wakePresenter.getAlarmById(alarmId);
            shuffle = alarm.isShuffle();
            trackId = alarm.getTrackId();
            trackImage = alarm.getTrackImage();
            snoozed = alarm.isSnoozed();
            //Log.d("WakeActivity", "shuffleString: "+shuffleString);
            //shuffle = Boolean.parseBoolean(shuffleString);
            Log.d("WakeActivity", "trackId: "+trackId);
            Log.d("WakeActivity", "shuffle: "+shuffle);
            Log.d("WakeActivity", "snoozed: "+snoozed);
            Log.d("PlayAlbum", "Alarm created");
//            alarm = RealmService.getAlarmById(alarmId);
            playlistID = alarm.getArtistName();

            //Use Glide to load image URL
            Glide.with(this)
                    .load(trackImage)
                    .centerCrop()
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

        mPlayer.pause(operationCallback);
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
        mPlayer.pause(operationCallback);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        int snoozeMins = Integer.parseInt(sharedPref.getString("snooze_key", "5"));
        int snoozeTime = snoozeMins * 60000;
        Log.d("snooze", "Snooze time in mins: " + snoozeMins);
        Log.d("snooze", "Snooze time in millis: " + snoozeTime);
        if (alarmId != null) {
            Log.d("Snooze", "alarm ID: " + alarmId);
            AlarmScheduler.setSnoozedById(getApplicationContext(), alarmId);
        }
        snoozed = true;
        AlarmScheduler.snoozeNextAlarm(getApplicationContext(), alarmId, snoozeTime);
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
                    dismissRingtone();
                } else if (seekBar.getProgress() < 15) {
                    snoozeTv.setTextSize(30);
                    snoozeTv.setTypeface(null, Typeface.BOLD);
                    dismissRingtone();
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

    private void dismissRingtone() {
        if (r != null && r.isPlaying())
            r.stop();
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
//                initSpotifyPlayer();
                initCustomPlayer();
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.d("Node", "error: " + t.getMessage());
            }
        });
    }

    public void initCustomPlayer() {
        Log.d("Player", "Init custom player");
        playerConfig = new Config(mContext, applicationModule.getAccessToken(), CLIENT_ID);
        SpotifyPlayer.Builder builder = new SpotifyPlayer.Builder(playerConfig)
                .setAudioController(audioTrackController);
//        builder.build(new SpotifyPlayer.InitializationObserver() {
//                    @Override
//                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
//                        int result = am.requestAudioFocus(amFocusListener,
//                                AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN);
//                        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                            mPlayer = spotifyPlayer;
//                            mPlayer.addConnectionStateCallback(WakeActivity.this);
//                            mPlayer.addNotificationCallback(WakeActivity.this);
//                            //Log.d("spotifyPlayer", AudioManager.getActivePlaybackConfigurations());
//                            mPlayer.setRepeat(true);
//                            Log.d("spotifyPlayer", "initialized  custom player");
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
//                    }
//                });
        Spotify.getPlayer(builder, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                int result = am.requestAudioFocus(amFocusListener,
                        AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN);
                if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    mPlayer = spotifyPlayer;
                    mPlayer.addConnectionStateCallback(WakeActivity.this);
                    mPlayer.addNotificationCallback(WakeActivity.this);
                    //Log.d("spotifyPlayer", AudioManager.getActivePlaybackConfigurations());

                    Log.d("spotifyPlayer", "initialized  custom player");
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize custom player: " + throwable.getMessage());
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
                Log.e("MainActivity", "Could not initialize custom player: " + throwable.getMessage());
            }
        });
    }

    @OnClick(R.id.next_song)
    public void onNextSongClick(){
        mPlayer.skipToNext(operationCallback);
    }

    /**
     * This method plays music once SpotifyPlayer has been initialized
     */
    @Override
    public void onLoggedIn() {
        Log.d("PlayAlbum", "Alarm Type: " + alarm.getMediaType());
        switch (alarm.getMediaType()) {
            case MediaType.DEFAULT_TYPE:
                playDefaultRingtone();
                break;
            case MediaType.TRACK_TYPE:
                mPlayer.playUri(operationCallback,"spotify:track:" + trackId, 0, 0);
                break;
            case MediaType.ALBUM_TYPE:
                Log.d("PlayAlbum", "spotify:album:" + trackId);
                mPlayer.playUri(operationCallback, "spotify:album:" + trackId, 0, 0);
                Log.d("WakeActivity", "setting shuffle: " + shuffle);
                break;
            case MediaType.PLAYLIST_TYPE:
                Log.d("PlayAlbum", "spotify:user:" + playlistID + ":playlist:" + trackId);
                mPlayer.playUri(operationCallback, "spotify:user:" + playlistID + ":playlist:" + trackId, 0, 0);
                break;
        }
        mPlayer.setShuffle(operationCallback, shuffle);
        mPlayer.setRepeat(operationCallback, true);

    }

    public void playDefaultRingtone() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String ringtonePref = preferences.getString("default_ringtone_key",
                "DEFAULT_RINGTONE_URI");
        Uri notification = Uri.parse(ringtonePref);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audio = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            r.setAudioAttributes(audio);
        } else
            r.setStreamType(AudioManager.STREAM_ALARM);
        r.play();
    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Error error) {

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
