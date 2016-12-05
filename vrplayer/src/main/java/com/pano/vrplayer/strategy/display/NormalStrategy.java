package com.pano.vrplayer.strategy.display;

import android.app.Activity;
import android.content.Context;

/**
 * Created by taipp on 9/7/2016.
 */
public class NormalStrategy extends AbsDisplayStrategy {

    @Override
    public void on(Activity activity) {}

    @Override
    public void off(Activity activity) {}

    @Override
    public boolean isSupport(Activity activity) {
        return true;
    }

    @Override
    public void onResume(Context context) {

    }

    @Override
    public void onPause(Context context) {

    }

    @Override
    public int getVisibleSize() {
        return 1;
    }
}
