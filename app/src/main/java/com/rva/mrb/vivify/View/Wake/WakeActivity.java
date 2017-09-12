package com.rva.mrb.vivify.View.Wake;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rva.mrb.vivify.AlarmApplication;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.BaseActivity;
import com.rva.mrb.vivify.BuildConfig;
import com.rva.mrb.vivify.Model.Data.AccessToken;
import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Data.MediaType;
import com.rva.mrb.vivify.Model.Data.Track;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.Model.Service.NotificationService;
import com.rva.mrb.vivify.Model.Service.PlayerService;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.Spotify.NodeService;
import com.rva.mrb.vivify.Spotify.SpotifyService;
import com.rva.mrb.vivify.View.Adapter.WakeRecyclerViewAdapter;
import com.rva.mrb.vivify.View.Adapter.WakeTouchAdapter;
import com.spotify.sdk.android.player.*;
import com.spotify.sdk.android.player.Error;
import com.rva.mrb.vivify.Spotify.AudioTrackController;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WakeActivity extends BaseActivity implements ConnectionStateCallback, SpotifyPlayer.NotificationCallback, WakeView {

    private static final String TAG = WakeActivity.class.getSimpleName();
//    @BindView(R.id.dismiss_tv) TextView dismissTv;
//    @BindView(R.id.snooze_tv) TextView snoozeTv;
//    @BindView(R.id.myseek) SeekBar seekBar;
    @BindView(R.id.trackImageView) ImageView trackIV;
//    @BindView(R.id.next_song) ImageView fastForward;
//    @BindView(R.id.wake_media_info) TextView mediaInfo;
    @BindView(R.id.wake_time) TextView clock;
    @BindView(R.id.wake_recyclerview) RecyclerView recyclerView;
    @Inject WakePresenter wakePresenter;
    @Inject NodeService nodeService;
    @Inject SpotifyService spotifyService;

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
    private boolean vibrate;
    private Vibrator vibrator;
    private Alarm alarm;
    private String playlistID;
    private Ringtone r;
    private AudioManager am;
    private Context mContext;
    private SharedPreferences sharedPref;
    private AudioManager.OnAudioFocusChangeListener amFocusListener;
    private AudioTrackController audioTrackController;
    private Player.OperationCallback operationCallback;
    private Disposable disposable;
    //private Disposable initPlayer;
    private Disposable shuffleDis;
    private Disposable musicDisposable;
    private Disposable queue;
    private boolean destroyed;
    private NotificationService mNotificationService;
    private PlayerService playerService;
    private List<Track> shuffledTracks;
    private WakeRecyclerViewAdapter wakeAdapterRecyclerViewAdapter;
    private WakeTouchAdapter.WakeTouchListener touchListener;
    private WakeRecyclerViewAdapter.NextMediaListener nextMediaListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        destroyed = false;
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
        mContext = getApplicationContext();
        //Get trackId and image URL from Bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            alarmId = (String) extras.get("alarmId");
            alarm = wakePresenter.getAlarmById(alarmId);
            shuffle = alarm.isShuffle();
            trackId = alarm.getTrackId();
            trackImage = alarm.getTrackImage();
            snoozed = alarm.isSnoozed();
            vibrate = alarm.isVibrate();
            //Log.d("WakeActivity", "shuffleString: "+shuffleString);
            //shuffle = Boolean.parseBoolean(shuffleString);
            Log.d("WakeActivity", "trackId: "+trackId);
            Log.d("WakeActivity", "shuffle: "+shuffle);
            Log.d("WakeActivity", "snoozed: "+snoozed);
            Log.d("PlayAlbum", "Alarm created");
//            alarm = RealmService.getAlarmById(alarmId);
            playlistID = alarm.getArtistName();
//            mediaInfo.setText(alarm.getArtistName()+": " + alarm.getTrackName());

            //Use Glide to load image URL
            trackIV.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(this)
                    .load(trackImage)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .into(trackIV);

            Log.d("trackImage", "Traack Image Url: " + trackImage);

            attachAdaptersToRecyclerview();
        }


        playerService = new PlayerService(mContext, spotifyService, alarm);
        //Retrieve access token from spotify
        refreshToken();
        am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_ALARM);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        handleRingVolume();
        audioTrackController = new AudioTrackController();

        //Set the seekbar that dissmisses/snoozes alarm
//        setSeekBar();
        handleVibrator();
        mNotificationService = new NotificationService(mContext);
        mNotificationService.cancelNotification();

        //Allow activity to wake up device
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void attachAdaptersToRecyclerview() {
        nextMediaListener = () -> onNextSongClick();
        wakeAdapterRecyclerViewAdapter = new WakeRecyclerViewAdapter(alarm, nextMediaListener);
        touchListener = new WakeTouchAdapter.WakeTouchListener() {
            @Override
            public void onAlarmDismissed() {
                onDismiss();
            }

            @Override
            public void onAlarmSnoozed() {
                onSnooze();
            }
        };
        ItemTouchHelper.Callback callback = new WakeTouchAdapter(touchListener);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(wakeAdapterRecyclerViewAdapter);
    }

    @Override
    protected void closeRealm() {

    }

    public void handleVibrator() {
        Log.d("Vibration", Boolean.toString(vibrate));
        if(vibrate){
            Log.d("Vibration", "handling vibrate");
            vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            // Start without a delay
            // Vibrate for 100 milliseconds
            // Sleep for 1000 milliseconds
            long[] pattern = {0, 300, 800};

            // The '0' here means to repeat indefinitely
            // '0' is actually the index at which the pattern keeps repeating from (the start)
            // To repeat the pattern from any other point, you could increase the index, e.g. '1'
            vibrator.vibrate(pattern, 0);
        }
    }

    public void handleRingVolume() {
        int fadein = Integer.parseInt(sharedPref.getString("fadein_key", "30"));
        Log.d("wake max volume: ", Integer.toString(am.getStreamMaxVolume(AudioManager.STREAM_ALARM)));
        double remainder = (fadein % 6)/6.0;
        int remain = ((int) (remainder*1000));

        Log.d("remain: ", Integer.toString(remain));
        if(fadein != 0) {
            am.setStreamVolume(AudioManager.STREAM_ALARM, 1, 0);
            disposable = Flowable.interval((fadein / 6) * 1000 + (remain), TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(along -> {
                        am.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_RAISE, 0);
                        Log.d("current volume ", Integer.toString(am.getStreamVolume(AudioManager.STREAM_ALARM)));
                        if (am.getStreamVolume(AudioManager.STREAM_ALARM) == am.getStreamMaxVolume(AudioManager.STREAM_ALARM)) {
                            disposable.dispose();
                        }
                    });
        }
        else {
            am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
        }

    }
    /*
    This method is called when the user dismisses the alarm. It cancels the alarm and pauses the
    player.
     */
    public void onDismiss() {
        if (mPlayer != null)
            mPlayer.pause(operationCallback);

        if (disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
        if (vibrate){
            vibrator.cancel();
        }

        if(snoozed){
            if (alarmId != null) {
                Log.d("Dismiss", "alarm ID: " + alarmId);
                AlarmScheduler.disableAlarmById(getApplicationContext(), alarmId);
            }
            AlarmScheduler.cancelSnoozedAlarm(getApplicationContext());
        }
        else {
            AlarmScheduler.cancelNextAlarm(getApplicationContext());
            if (alarmId != null) {
                Log.d("Dismiss", "alarm ID: " + alarmId);
                AlarmScheduler.disableAlarmById(getApplicationContext(), alarmId);
            }
        }
        finish();
    }

    /*
    This method is called when the user snoozes the alarm. It pauses the player and reschedules the
    alarm.
     */
    public void onSnooze() {
        if (mPlayer != null)
            mPlayer.pause(operationCallback);
        int snoozeMins = Integer.parseInt(sharedPref.getString("snooze_key", "5"));
        int snoozeTime = snoozeMins * 60000;
        Log.d("snooze", "Snooze time in mins: " + snoozeMins);
        Log.d("snooze", "Snooze time in millis: " + snoozeTime);
        if (alarmId != null) {
            Log.d("Snooze", "alarm ID: " + alarmId);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MILLISECOND, snoozeTime);
            AlarmScheduler.setSnoozedById(getApplicationContext(), alarmId, cal.getTime());
        }
        snoozed = true;
        AlarmScheduler.snoozeNextAlarm(getApplicationContext());
        if (disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
        if (vibrate){
            vibrator.cancel();
        }
        finish();
    }

    /*
    This method sets the seekbar touchListener and allows user to snooze or dismiss the alarm.
     */
//    public void setSeekBar() {
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (seekBar.getProgress() > 85) {
//                    dismissTv.setTextSize(30);
//                    dismissTv.setTypeface(null, Typeface.BOLD);
//                    dismissRingtone();
//                } else if (seekBar.getProgress() < 15) {
//                    snoozeTv.setTextSize(30);
//                    snoozeTv.setTypeface(null, Typeface.BOLD);
//                    dismissRingtone();
//                } else {
//                    dismissTv.setTextSize(20);
//                    snoozeTv.setTextSize(20);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (seekBar.getProgress() > 85) {
//                    onDismiss();
//                } else if (seekBar.getProgress() < 15) {
//                    onSnooze();
//                } else {
//                    seekBar.setProgress(50);
//                }
//            }
//        });
//    }

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
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(sharedPreferences.getLong("expires", -1));
        Date expires = cal.getTime();
        if(expires.before(Calendar.getInstance().getTime())) {
            nodeService.refreshToken(refreshToken).enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    AccessToken results = response.body();
                    applicationModule.setAccessToken(results.getAccessToken());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, results.getExpiresIn());
                    editor.putLong("expires", calendar.getTimeInMillis());
                    editor.commit();
//                Disposable d = Observable.just("hello").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(result -> {
//                    Log.d("WakeActivity", "getting tracks");
//                    playerService.getTracks();
//                    shuffledTracks = playerService.returnTracks();
//
//                });
                    if (shuffle) {
                        shuffleDis = playerService.getTracks().subscribe(() -> {
                            shuffledTracks = playerService.returnTracks();
                            //initPlayer = initCustomPlayer().subscribe();
                            initCustomPlayer();
                        });
                    } else {
                        //initPlayer = initCustomPlayer().subscribe();
                        initCustomPlayer();
                    }
//                spotifyService.getAlbumTracks(trackId)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(res -> {
//                            shuffledTracks = res.getItems();
//                            Collections.shuffle(shuffledTracks);
//                            Log.d("PlayerService", "getting response");
//                            initCustomPlayer();
//                        });

                    // playerService.getTracks();


//                initSpotifyPlayer();

                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.d("Node", "error: " + t.getMessage());
                }
            });
        }
        else{
            String accessToken = sharedPreferences.getString("access_token", null);
            applicationModule.setAccessToken(accessToken);
            if (shuffle) {
                shuffleDis = playerService.getTracks().subscribe(() -> {
                    shuffledTracks = playerService.returnTracks();
                    //initPlayer = initCustomPlayer().subscribe();
                    initCustomPlayer();
                });
            } else {
                //initPlayer = initCustomPlayer().subscribe();
                initCustomPlayer();
            }
        }
    }

    public void initCustomPlayer() {
//        return Completable.create(new CompletableOnSubscribe() {
//            @Override
//            public void subscribe(CompletableEmitter e) throws Exception {
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
                            Log.d("WakeActivity", "logged in: " + spotifyPlayer.isLoggedIn());
                            mPlayer = spotifyPlayer;
                            if(spotifyPlayer.isLoggedIn()){
                                musicDisposable = playMusic().subscribe();
                            }
                            else {
                                mPlayer.addConnectionStateCallback(WakeActivity.this);
                                mPlayer.addNotificationCallback(WakeActivity.this);
                                //Log.d("spotifyPlayer", AudioManager.getActivePlaybackConfigurations());
                            }

                            Log.d("spotifyPlayer", "initialized  custom player");
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize custom player: " + throwable.getMessage());
                    }
                });
//            }
//        });

    }

    public Completable playMusic(){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                if(!destroyed) {
                    Log.d("PlayAlbum", "Alarm Type: " + alarm.getMediaType());
                    if (shuffle) {
                        if (alarm.getMediaType() == MediaType.DEFAULT_TYPE) {
                            playDefaultRingtone();
                        } else {
                            mPlayer.playUri(operationCallback, shuffledTracks.get(0).getUri(), 0, 0);
                            shuffledTracks.remove(0);
                            queue = Flowable.interval(250, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(along -> {
                                        if (shuffledTracks.size() > 0) {
                                            Log.d("wakeActivity", shuffledTracks.get(0).getUri());
                                            mPlayer.queue(operationCallback, shuffledTracks.get(0).getUri());
                                            shuffledTracks.remove(0);
                                        }
                                    });
                        }
                    } else {
                        switch (alarm.getMediaType()) {
                            case MediaType.DEFAULT_TYPE:
                                playDefaultRingtone();
                                break;
                            case MediaType.TRACK_TYPE:
                                mPlayer.playUri(operationCallback, "spotify:track:" + trackId, 0, 0);
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

                    }

                    mPlayer.setShuffle(operationCallback, shuffle);
                    mPlayer.setRepeat(operationCallback, true);
                }
                else{
                    Spotify.destroyPlayer(this);
                }
            }
        });

    }

//    @OnClick(R.id.next_song)
    public void onNextSongClick(){
        mPlayer.skipToNext(operationCallback);
    }

    /**
     * This method plays music once SpotifyPlayer has been initialized
     */
    @Override
    public void onLoggedIn() {
            musicDisposable = playMusic().subscribe();
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
        Log.d("WakeActivity", "onDestroy");
        destroyed = true;
//        if(initPlayer != null && !initPlayer.isDisposed()) {
//            Log.d("WakeActivity", "disposing initplayer");
//            initPlayer.dispose();
//        }
        if(shuffleDis != null && !shuffleDis.isDisposed()) {
            shuffleDis.dispose();
        }
        if(musicDisposable != null && !musicDisposable.isDisposed()) {
            musicDisposable.dispose();
        }
        if(queue != null && !queue.isDisposed()) {
            queue.dispose();
        }
        Spotify.destroyPlayer(this);
        if (vibrate){
            vibrator.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent event) {

    }


    @Override
    public void onPlaybackError(com.spotify.sdk.android.player.Error error) {

    }
}
