package com.pano.vrplayer.strategy.projection;


import com.pano.vrplayer.model.VRPosition;
import com.pano.vrplayer.objects.VRAbsObject3D;

/**
 * Created by taipp on 9/7/2016.
 */
public interface IProjectionMode {
    VRAbsObject3D getObject3D();
    VRPosition getModelPosition();
}
