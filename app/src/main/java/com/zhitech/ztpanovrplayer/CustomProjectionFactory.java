package com.zhitech.ztpanovrplayer;

import com.pano.vrplayer.strategy.projection.AbsProjectionStrategy;
import com.pano.vrplayer.strategy.projection.IVRProjectionFactory;
import com.pano.vrplayer.strategy.projection.MultiFishEyeProjection;

/**
 * Created by taipp on 9/7/2016.
 */
public class CustomProjectionFactory implements IVRProjectionFactory {

    public static final int CUSTOM_PROJECTION_FISH_EYE_RADIUS_VERTICAL = 9611;

    @Override
    public AbsProjectionStrategy createStrategy(int mode) {
        switch (mode){
            case CUSTOM_PROJECTION_FISH_EYE_RADIUS_VERTICAL:
                return new MultiFishEyeProjection(0.745f,false);
            default:return null;
        }
    }
}
