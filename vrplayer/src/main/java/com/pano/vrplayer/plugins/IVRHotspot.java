package com.pano.vrplayer.plugins;

import com.pano.vrplayer.model.VRRay;

/**
 * Created by taipp on 9/7/2016.
 */
public interface IVRHotspot {
    boolean hit(VRRay ray);
    void onEyeHitIn(long timestamp);
    void onEyeHitOut();
    void onTouchHit(VRRay ray);
    String getTitle();
    void useTexture(int key);
}
