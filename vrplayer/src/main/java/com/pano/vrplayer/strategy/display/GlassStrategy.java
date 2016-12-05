package com.pano.vrplayer.strategy.display;

import android.app.Activity;

/**
 * Created by taipp on 9/7/2016.
 */
public class GlassStrategy extends AbsDisplayStrategy {

    public GlassStrategy() {}

    @Override
    public void on(Activity activity) {}

    @Override
    public void off(Activity activity) {}

    @Override
    public boolean isSupport(Activity activity) {
        return true;
    }

    @Override
    public int getVisibleSize() {
        return 2;
    }
}
