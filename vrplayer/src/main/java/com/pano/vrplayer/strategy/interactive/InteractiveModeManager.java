package com.pano.vrplayer.strategy.interactive;

import android.app.Activity;
import android.hardware.SensorEventListener;

import com.pano.vrplayer.VRLibrary;
import com.pano.vrplayer.strategy.ModeManager;
import com.pano.vrplayer.strategy.projection.ProjectionModeManager;

/**
 * Created by taipp on 9/7/2016.
 */
public class InteractiveModeManager extends ModeManager<AbsInteractiveStrategy> implements IInteractiveMode {

    private static int[] sModes = {VRLibrary.INTERACTIVE_MODE_MOTION,
            VRLibrary.INTERACTIVE_MODE_TOUCH,
            VRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH,
    };

    public static class Params{
        public int mMotionDelay;
        public SensorEventListener mSensorListener;
        public ProjectionModeManager projectionModeManager;
    }

    private Params mParams;

    public InteractiveModeManager(int mode, Params params) {
        super(mode);
        mParams = params;
    }

    @Override
    protected int[] getModes() {
        return sModes;
    }

    @Override
    public void switchMode(Activity activity, int mode) {
        super.switchMode(activity, mode);
        if (isResumed()) onResume(activity);
    }

    @Override
    protected AbsInteractiveStrategy createStrategy(int mode) {
        switch (mode){
            case VRLibrary.INTERACTIVE_MODE_MOTION:
                return new MotionStrategy(mParams);
            case VRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH:
                return new MotionWithTouchStrategy(mParams);
            case VRLibrary.INTERACTIVE_MODE_TOUCH:
            default:
                return new TouchStrategy(mParams);
        }
    }

    /**
     * handle touch touch to rotate the model
     *
     * @param distanceX x
     * @param distanceY y
     * @return true if handled.
     */
    @Override
    public boolean handleDrag(int distanceX, int distanceY) {
        return getStrategy().handleDrag(distanceX,distanceY);
    }
}
