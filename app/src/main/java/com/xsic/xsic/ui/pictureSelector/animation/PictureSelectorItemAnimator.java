package com.xsic.xsic.ui.pictureSelector.animation;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.ArrayList;
import java.util.List;

public class PictureSelectorItemAnimator extends SimpleItemAnimator {
    List<View> mAnimatorViewList = new ArrayList<>();

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(0);
        holder.itemView.setScaleY(0);
        mAnimatorViewList.add(holder.itemView);
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {
        for (View view : mAnimatorViewList) {
            final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
            animation.scaleX(1).setDuration(getAddDuration()).start();
            animation.scaleY(1).setDuration(getAddDuration()).start();
        }
        mAnimatorViewList.clear();
    }

    @Override
    public void endAnimation(@NonNull RecyclerView.ViewHolder item) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
