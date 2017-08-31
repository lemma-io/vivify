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

/**
 * Created by rigo on 8/28/17.
 */

public class Wake extends RecyclerView.Adapter<Wake.ViewHolder>
    implements CardTouchHelper {

    private Alarm alarm;
    private MediaListener listener;

    public Wake(Alarm alarm, MediaListener listener){
        Log.d("Adapter", "new wakeadapter");
        this.alarm = alarm;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Wake.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View wakeView = inflater.inflate(R.layout.wake_view, parent, false);
        viewHolder = new ViewHolder(wakeView);
        Log.d("Wake", "CreateViewHolder");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("Bind", "BindViewHolder");
        holder.mediaName.setText(alarm.getTrackName());
        holder.mediaOwner.setText(alarm.getArtistName());
        holder.nextSong.setOnClickListener(view -> {
            listener.onNextSong();
        });
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

    public interface MediaListener {
        void onNextSong();
    }
}
