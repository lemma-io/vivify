package com.rva.mrb.vivify.View.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WakeRecyclerViewAdapter extends RecyclerView.Adapter<WakeRecyclerViewAdapter.ViewHolder>
    implements WakeAlarmTouchHelper {

    private static String TAG = WakeRecyclerViewAdapter.class.getSimpleName();
    private Alarm alarm;
    private NextMediaListener nextMediaListener;
    private String trackName;
    private String artistName;

    public WakeRecyclerViewAdapter(Alarm alarm, NextMediaListener nextMediaListener){
        this.alarm = alarm;
        this.nextMediaListener = nextMediaListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WakeRecyclerViewAdapter.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View wakeView = inflater.inflate(R.layout.wake_view, parent, false);
        viewHolder = new ViewHolder(wakeView);
        Log.d(TAG, "CreateViewHolder");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "BindViewHolder");
        if (trackName != null) {
            holder.mediaName.setText(trackName);
        } else {
            holder.mediaName.setText(alarm.getTrackName());
        }

        if (artistName != null) {
            holder.mediaOwner.setText(artistName);
        } else {
            holder.mediaOwner.setText(alarm.getArtistName());
        }
        holder.nextSong.setOnClickListener(view -> nextMediaListener.onNextSong());
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onAlarmDismiss(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public void onAlarmSnooze(int position) {
        notifyItemRemoved(position);
    }

    public void updateMediaInfo(String track, String artist) {
        this.artistName = artist;
        this.trackName = track;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.wake_card) CardView cardView;
        @BindView(R.id.wake_media_name) TextView mediaName;
        @BindView(R.id.wake_media_owner) TextView mediaOwner;
        @BindView(R.id.next_song) ImageView nextSong;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface NextMediaListener {
        void onNextSong();
    }
}
