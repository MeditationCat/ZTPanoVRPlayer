package com.pano.vrplayer.strategy;

import android.app.Activity;
import android.content.Context;

/**
 * Created by taipp on 9/7/2016.
 */
public interface IModeStrategy {

    void on(Activity activity);

    void off(Activity activity);
    
    boolean isSupport(Activity activity);

    void onResume(Context context);

    void onPause(Context context);

}
