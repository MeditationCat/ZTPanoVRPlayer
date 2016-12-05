package com.pano.vrplayer.strategy.interactive;

import android.content.Context;

/**
 * Created by taipp on 9/7/2016.
 */
public interface IInteractiveMode {

    void onResume(Context context);

    void onPause(Context context);

    boolean handleDrag(int distanceX, int distanceY);
}
