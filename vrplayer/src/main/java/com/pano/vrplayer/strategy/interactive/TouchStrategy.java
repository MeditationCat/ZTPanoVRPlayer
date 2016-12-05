package com.pano.vrplayer.strategy.interactive;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.pano.vrplayer.VR360Director;

/**
 * Created by taipp on 9/7/2016.
 */
public class TouchStrategy extends AbsInteractiveStrategy {

    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;

    private static final float sDamping = 0.2f;

    private static final String TAG = "TouchStrategy";

    public TouchStrategy(InteractiveModeManager.Params params) {
        super(params);
    }

    @Override
    public void onResume(Context context) {}

    @Override
    public void onPause(Context context) {}

    @Override
    public boolean handleDrag(int distanceX, int distanceY) {
        for (VR360Director director : getDirectorList()){
            director.setDeltaX(director.getDeltaX() - distanceX / sDensity * sDamping);
            director.setDeltaY(director.getDeltaY() - distanceY / sDensity * sDamping);
        }
        return false;
    }

    @Override
    public void on(Activity activity) {
        for (VR360Director director : getDirectorList()){
            director.reset();
        }
    }

    @Override
    public void off(Activity activity) {}

    @Override
    public boolean isSupport(Activity activity) {
        return true;
    }
}
