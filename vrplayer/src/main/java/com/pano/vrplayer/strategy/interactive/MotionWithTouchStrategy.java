package com.pano.vrplayer.strategy.interactive;

import android.content.res.Resources;

import com.pano.vrplayer.VR360Director;

/**
 * Created by taipp on 9/7/2016.
 */
public class MotionWithTouchStrategy extends MotionStrategy {

    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;

    private static final float sDamping = 0.2f;

    public MotionWithTouchStrategy(InteractiveModeManager.Params params) {
        super(params);
    }

    @Override
    public boolean handleDrag(int distanceX, int distanceY) {
        for (VR360Director director : getDirectorList()){
            director.setDeltaX(director.getDeltaX() - distanceX / sDensity * sDamping);
            // director.setDeltaY(director.getDeltaY() - distanceY / sDensity * sDamping);
        }
        return false;
    }
}
