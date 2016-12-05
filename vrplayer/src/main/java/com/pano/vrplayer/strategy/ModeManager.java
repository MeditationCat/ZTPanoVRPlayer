package com.pano.vrplayer.strategy;

import android.app.Activity;
import android.content.Context;

import com.pano.vrplayer.VRLibrary;

import java.util.Arrays;

/**
 * Created by taipp on 9/7/2016.
 */
public abstract class ModeManager<T extends IModeStrategy> {
    private int mMode;
    private T mStrategy;
    private boolean mIsResumed;
    private VRLibrary.INotSupportCallback mCallback;

    public ModeManager(int mode) {
        this.mMode = mode;
    }

    /**
     * must call after new instance
     * @param activity activity
     */
    public void prepare(Activity activity, VRLibrary.INotSupportCallback callback){
        mCallback = callback;
        initMode(activity,mMode);
    }

    abstract protected T createStrategy(int mode);

    abstract protected int[] getModes();

    private void initMode(Activity activity, int mode){
        if (mStrategy != null){
            off(activity);
        }
        mStrategy = createStrategy(mode);
        if (!mStrategy.isSupport(activity)){
            if (mCallback != null) mCallback.onNotSupport(mode);
        } else {
            on(activity);
        }
    }

    public void switchMode(Activity activity){
        int[] modes = getModes();
        int mode = getMode();
        int index = Arrays.binarySearch(modes, mode);
        int nextIndex = (index + 1) %  modes.length;
        int nextMode = modes[nextIndex];

        switchMode(activity,nextMode);
    }

    public void switchMode(Activity activity, int mode){
        if (mode == getMode()) return;
        mMode = mode;
        initMode(activity,mMode);
    }

    public void on(Activity activity) {
        if (mStrategy.isSupport(activity))
            mStrategy.on(activity);
    }

    public void off(Activity activity) {
        if (mStrategy.isSupport(activity))
            mStrategy.off(activity);
    }

    protected T getStrategy() {
        return mStrategy;
    }

    public int getMode() {
        return mMode;
    }

    public void onResume(Context context) {
        mIsResumed = true;
        if (getStrategy().isSupport((Activity)context)){
            getStrategy().onResume(context);
        }
    }

    public void onPause(Context context) {
        mIsResumed = false;
        if (getStrategy().isSupport((Activity)context)){
            getStrategy().onPause(context);
        }
    }

    public boolean isResumed() {
        return mIsResumed;
    }
}
