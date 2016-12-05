package com.pano.vrplayer.strategy.projection;

import com.pano.vrplayer.model.VRMainPluginBuilder;
import com.pano.vrplayer.plugins.VRAbsPlugin;
import com.pano.vrplayer.plugins.VRMultiFishEyePlugin;

/**
 * Created by taipp on 9/7/2016.
 */
public class MultiFishEyeProjection extends SphereProjection {

    private float radius;
    private boolean isHorizontal;

    public MultiFishEyeProjection(float radius, boolean isHorizontal) {
        this.radius = radius;
        this.isHorizontal = isHorizontal;
    }

    @Override
    public VRAbsPlugin buildMainPlugin(VRMainPluginBuilder builder) {
        return new VRMultiFishEyePlugin(builder, radius, isHorizontal);
    }
}
