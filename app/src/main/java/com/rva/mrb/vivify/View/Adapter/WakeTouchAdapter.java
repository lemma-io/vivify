package com.rva.mrb.vivify.View.Adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.*;
import android.util.DisplayMetrics;
import android.view.View;

import com.rva.mrb.vivify.R;

public class WakeTouchAdapter extends ItemTouchHelper.Callback {

    public static final float ALPHA_FULL = 1.0f;
    public WakeTouchListener listener;


    public WakeTouchAdapter(WakeTouchListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(
        RecyclerView recyclerView,
        RecyclerView.ViewHolder viewHolder,
        RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.RIGHT) {
            listener.onAlarmDismissed();
        } else {
            listener.onAlarmSnoozed();
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
        float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Get RecyclerView item from the ViewHolder
            View itemView = viewHolder.itemView;

            Paint p = new Paint();
            p.setStyle(Paint.Style.FILL);
            // Set the text color
            p.setColor(Color.RED);
            p.setAntiAlias(true);
            Bitmap icon;

            if (dX > 0) {
            /* Set your color for positive displacement */

                icon = BitmapFactory.decodeResource(recyclerView.getContext().getResources(), R.drawable.arrow);
                p.setARGB(255, 0, 255, 0);
                // Draw Rect with varying right side, equal to displacement dX
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                        (float) itemView.getBottom(), p);

//                c.drawText("Dismiss", (float) itemView.getLeft() + convertDpToPx(recyclerView.getContext(), 16),
//                        (float) itemView.getTop() + ((float) itemView.getBottom()
//                                - (float) itemView.getTop()), p);
                c.drawBitmap(icon,
                        (float) itemView.getLeft() + convertDpToPx(recyclerView.getContext(), 16),
                        (float) itemView.getTop() + ((float) itemView.getBottom()
                                - (float) itemView.getTop() - icon.getHeight())/2,
                        p);
            } else {
            /* Set your color for negative displacement */
                icon = BitmapFactory.decodeResource(recyclerView.getContext().getResources(), R.drawable.alarm_clock);
                p.setARGB(255, 255, 0, 0);
                // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), p);

//                c.drawText("Snooze", (float) itemView.getLeft() + convertDpToPx(recyclerView.getContext(), 16),
//                        (float) itemView.getTop() + ((float) itemView.getBottom()
//                                - (float) itemView.getTop()), p);
                c.drawBitmap(icon,
                        (float) itemView.getRight() - convertDpToPx(recyclerView.getContext(), 16) - icon.getWidth(),
                        (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                        p);
            }

            final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    private int convertDpToPx(Context context, int dp){
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public interface WakeTouchListener {
        void onAlarmDismissed();
        void onAlarmSnoozed();
    }
}
