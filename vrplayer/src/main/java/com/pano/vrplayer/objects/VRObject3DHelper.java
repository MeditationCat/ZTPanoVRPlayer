package com.pano.vrplayer.objects;

import android.content.Context;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRObject3DHelper {

    public interface LoadComplete{
        void onComplete(VRAbsObject3D object3D);
    }

    public static void loadObj(final Context context, final VRAbsObject3D object3D){
        loadObj(context, object3D, null);
    }

    public static void loadObj(final Context context, final VRAbsObject3D object3D, final LoadComplete loadComplete){
        new Thread(new Runnable() {
            @Override
            public void run() {
                object3D.executeLoad(context);
                if (loadComplete != null)
                    loadComplete.onComplete(object3D);
            }
        }).start();
    }
}
