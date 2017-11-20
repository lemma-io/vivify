package com.rva.mrb.vivify.View.Adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Service.AlarmScheduler;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.View.Alarm.AlarmsPresenter;


import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class AlarmAdapter extends
        RealmRecyclerViewAdapter<Alarm, AlarmAdapter.ViewHolder> {

    public static final String TAG = AlarmAdapter.class.getSimpleName();

    // lets the Alarm activity know when an alarm is pressed
    public OnAlarmToggleListener alarmToggleListener;
    public AlarmClickListener alarmClickListener;
    @Inject AlarmsPresenter alarmsPresenter;

    public AlarmAdapter(OrderedRealmCollection<Alarm> data,
                        AlarmClickListener clickListener,
                        OnAlarmToggleListener listener) {
        super(data, true);
        alarmToggleListener = listener;
        alarmClickListener = clickListener;
    }

    public void setAlarmTimer(Alarm alarm, ViewHolder viewHolder) {
        if (alarm.isValid()) {
            if (alarm.isEnabled()) {
                if (alarm.isSnoozed())
                    viewHolder.alarmTimer.setText(getTimeUntil(alarm.getSnoozedAt()));
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        dispose(viewHolder);

        final Alarm alarm = getItem(position);//realmResults.get(position);
        viewHolder.timeTv.setText(alarm.getmWakeTime());
        viewHolder.nameTv.setText(alarm.getAlarmLabel());
        viewHolder.isSet.setChecked(alarm.isEnabled());
        viewHolder.mediaInfoTv.setText((alarm.getArtistName() != null ?
                alarm.getTrackName() + " by " + alarm.getArtistName() : alarm.getTrackName()));
        viewHolder.mediaInfoTv.setSelected(true);

        viewHolder.alarmBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(viewHolder.itemView.getContext())
                .load(alarm.getTrackImage())
                .placeholder(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.placeholder4))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(viewHolder.alarmBg);

        viewHolder.cardView.setOnClickListener(view -> {
            alarmClickListener.onAlarmClick(position, alarm, viewHolder.alarmBg);

            viewHolder.disposable.dispose();
        });

        viewHolder.isSet.setOnClickListener(v -> {
            AlarmScheduler.enableAlarmById(v.getContext(), alarm.getId());

            alarmToggleListener.onAlarmToggle();
        });

        viewHolder.toggleLayout.setOnClickListener(v -> {
            viewHolder.isSet.toggle();
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

    public class ViewHolder extends RecyclerView.ViewHolder {

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

