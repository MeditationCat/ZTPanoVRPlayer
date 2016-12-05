package com.pano.vrplayer.plugins;

import android.content.Context;

import com.pano.vrplayer.VR360Director;
import com.pano.vrplayer.model.VRPosition;

/**
 * Created by taipp on 9/7/2016.
 */
public abstract class VRAbsPlugin {

    private boolean mIsInit;

    VRPosition position = VRPosition.sOriginalPosition;

    public final void setup(Context context){
        if (!mIsInit){
            init(context);
            mIsInit = true;
        }
    }

    abstract protected void init(Context context);

    abstract public void beforeRenderer(int totalWidth, int totalHeight);

    abstract public void renderer(int index, int itemWidth, int itemHeight, VR360Director director);

    abstract public void destroy();

    protected VRPosition getModelPosition(){
        return position;
    }

    public void setModelPosition(VRPosition position) {
        this.position = position;
    }

    abstract protected boolean removable();

}
