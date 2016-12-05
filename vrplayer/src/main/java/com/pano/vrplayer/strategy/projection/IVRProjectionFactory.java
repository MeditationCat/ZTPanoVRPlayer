package com.pano.vrplayer.strategy.projection;

/**
 * Created by taipp on 9/7/2016.
 */
public interface IVRProjectionFactory {
    AbsProjectionStrategy createStrategy(int mode);
}
