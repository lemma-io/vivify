package com.rva.mrb.vivify.View.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.Model.Service.RealmService;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.View.Detail.DetailActivity;
import com.rva.mrb.vivify.View.Alarm.AlarmsPresenter;


import org.parceler.Parcels;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

public class AlarmAdapter extends
        RealmBasedRecyclerViewAdapter<Alarm, AlarmAdapter.ViewHolder> {

    public static final String TAG = AlarmAdapter.class.getSimpleName();

    // lets the Alarm activity know when an alarm is pressed
    public OnAlarmToggleListener alarmToggleListener;
    public AlarmClickListener alarmClickListener;
    @Inject AlarmsPresenter alarmsPresenter;

    public AlarmAdapter(Context context, RealmResults<Alarm> realmResults,
                        OnAlarmToggleListener listener, AlarmClickListener clickListener, boolean automaticUpdate,
                        boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
        this.alarmToggleListener = listener;
        this.alarmClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View v = inflater.inflate(R.layout.alarm_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindRealmViewHolder(
            final AlarmAdapter.ViewHolder viewHolder, final int position) {
        dispose(viewHolder);
        final Alarm alarm = realmResults.get(position);
        viewHolder.timeTv.setText(alarm.getmWakeTime());
        viewHolder.nameTv.setText(alarm.getAlarmLabel());
        viewHolder.isSet.setChecked(alarm.isEnabled());
        viewHolder.mediaInfoTv.setText(alarm.getTrackName() + ": " + alarm.getArtistName());
        viewHolder.mediaInfoTv.setSelected(true);
        Glide.with(viewHolder.itemView.getContext())
                .load(alarm.getTrackImage())
                .centerCrop()
                .into(viewHolder.alarmBg);
        viewHolder.alarmBg.setScaleType(ImageView.ScaleType.FIT_XY);

        viewHolder.cardView.setOnClickListener(view -> {
            alarmClickListener.onAlarmClick(position, alarm, viewHolder.alarmBg);
//            Log.d(TAG, "Opening Detail activity on id: " + alarm.getId());
//            Intent intent = new Intent(view.getContext(), DetailActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("NewAlarm", false);
//            intent.putExtra("AlarmArtist", alarm.getArtistName());
//            intent.putExtra("Alarm", Parcels.wrap(alarm));
//            ActivityOptionsCompat options = ActivityOptionsCompat.
//                    makeSceneTransitionAnimation(view, viewHolder.alarmBg, "detail");
//            view.getContext().startActivity(intent);
            viewHolder.disposable.dispose();
        });

        viewHolder.isSet.setOnClickListener(v -> {
            Log.d(TAG, "Toggle alarm id: " + alarm.getId());
            AlarmScheduler.enableAlarmById(v.getContext(), alarm.getId());

            alarmToggleListener.onAlarmToggle();
        });

        viewHolder.toggleLayout.setOnClickListener(v -> {
            viewHolder.isSet.toggle();
            Log.d(TAG, "Toggle alarm id: " + alarm.getId());
            AlarmScheduler.enableAlarmById(v.getContext(), alarm.getId());
            setAlarmTimer(alarm, viewHolder);
            alarmToggleListener.onAlarmToggle();
        });

        if (alarm.isValid()) {
            setAlarmTimer(alarm, viewHolder);
            viewHolder.disposable = Flowable.interval(1000L, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(along -> {
                        setAlarmTimer(alarm, viewHolder);
                    });
        }
    }

    public void setAlarmTimer(Alarm alarm, ViewHolder viewHolder) {
        if (alarm.isValid()) {
            if (alarm.isEnabled()) {
                if (alarm.isSnoozed()){
                    viewHolder.alarmTimer.setText(getTimeUntil(alarm.getSnoozedAt()));
                }
                else {
                    viewHolder.alarmTimer.setText(getTimeUntil(alarm.getTime()));
                }
            }
            else
                viewHolder.alarmTimer.setText("");
        }
    }

    public void dispose(ViewHolder viewHolder) {
        if(viewHolder.disposable != null) {
            Log.d("alarmadapter", "disposing");
            viewHolder.disposable.dispose();
            viewHolder.alarmTimer.setText("");
        }
    }
    private String getTimeUntil(Date time) {
        long millis = time.getTime() - System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toHours(millis) + "hrs " +
                (TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))) +"mins";
    }

    public class ViewHolder extends RealmViewHolder {

        @BindView(R.id.alarm_tv) TextView timeTv;
        @BindView(R.id.card_alarms) CardView cardView;
        @BindView(R.id.alarm_nametv) TextView nameTv;
        @BindView(R.id.alarm_is_set) Switch isSet;
        @BindView(R.id.alarm_media_info) TextView mediaInfoTv;
        @BindView(R.id.alarm_bg) ImageView alarmBg;
        @BindView(R.id.alarm_timer) TextView alarmTimer;
        @BindView(R.id.alarm_toggle_layout) LinearLayout toggleLayout;
        Disposable disposable;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAlarmToggleListener {
        void onAlarmToggle();
    }


    public interface AlarmClickListener {
        void onAlarmClick(int pos, Alarm alarm, ImageView sharedImageView);
    }
}

