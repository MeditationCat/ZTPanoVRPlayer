package com.pano.vrplayer.plugins;

import android.content.Context;

/**
 * Created by taipp on 9/7/2016.
 */
public abstract class VRAbsLinePipe {
    abstract public void takeOver(int totalWidth, int totalHeight, int size);
    abstract public void commit(int totalWidth, int totalHeight, int size);
    abstract protected void init(Context context);

    private boolean mIsInit;

    // VRPosition position = VRPosition.sOriginalPosition;

    public final void setup(Context context){
        if (!mIsInit){
            init(context);
            mIsInit = true;
        }
    }


}
