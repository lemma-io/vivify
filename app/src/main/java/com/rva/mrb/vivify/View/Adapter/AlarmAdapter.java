package com.rva.mrb.vivify.View.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private Disposable disposable;

    // lets the Alarm activity know when an alarm is pressed
    public OnAlarmToggleListener alarmToggleListener;
    @Inject AlarmsPresenter alarmsPresenter;

    public AlarmAdapter(Context context, RealmResults<Alarm> realmResults,
                        OnAlarmToggleListener listener, boolean automaticUpdate,
                        boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
        this.alarmToggleListener = listener;
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
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG, "Success!");
                Log.d(TAG, "Opening Detail activity on id: " + alarm.getId());
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("NewAlarm", false);
                intent.putExtra("AlarmArtist", alarm.getArtistName());
                intent.putExtra("Alarm", Parcels.wrap(alarm));
                view.getContext().startActivity(intent);
                disposable.dispose();
                Log.d(TAG, "disposable is " + disposable.isDisposed());
            }
        });
        viewHolder.isSet.setOnClickListener(new Switch.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Toggle alarm id: " + alarm.getId());
                AlarmScheduler.enableAlarmById(v.getContext(), alarm.getId());
                if (alarm.isEnabled()) {
                    viewHolder.alarmTimer.setText(getTimeUntil(alarm.getTime()));
                } else {
                    viewHolder.alarmTimer.setText("");
                }
                alarmToggleListener.onAlarmToggle();
            }
        });

        if (alarm.isValid()) {
            disposable = Flowable.interval(1000L, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(along -> {
                        if (alarm.isEnabled())
                            viewHolder.alarmTimer.setText(getTimeUntil(alarm.getTime()));
                    });
        }
    }

    private String getTimeUntil(Date time) {
        long millis = time.getTime() - System.currentTimeMillis();
        String timeStr = TimeUnit.MILLISECONDS.toHours(millis) + "hrs " +
                (TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))) +"mins";
        return timeStr;
    }

    public class ViewHolder extends RealmViewHolder {

        @BindView(R.id.alarm_tv) TextView timeTv;
        @BindView(R.id.card_alarms) CardView cardView;
        @BindView(R.id.alarm_nametv) TextView nameTv;
        @BindView(R.id.alarm_is_set) Switch isSet;
        @BindView(R.id.alarm_media_info) TextView mediaInfoTv;
        @BindView(R.id.alarm_bg) ImageView alarmBg;
        @BindView(R.id.alarm_timer) TextView alarmTimer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnAlarmToggleListener {
        void onAlarmToggle();
    }
}

