package com.pano.vrplayer.common;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRHandler {

    private static Handler sMainHandler;

    public static void init(){
        if (sMainHandler == null){
            sMainHandler = new Handler(Looper.getMainLooper());
        }
    }

    public static Handler sharedHandler(){
        return sMainHandler;
    }
}
