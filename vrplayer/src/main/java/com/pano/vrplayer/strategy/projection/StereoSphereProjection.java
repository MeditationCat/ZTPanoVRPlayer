package com.pano.vrplayer.strategy.projection;

import android.app.Activity;

import com.pano.vrplayer.VR360Director;
import com.pano.vrplayer.VR360DirectorFactory;
import com.pano.vrplayer.model.VRMainPluginBuilder;
import com.pano.vrplayer.model.VRPosition;
import com.pano.vrplayer.objects.VRAbsObject3D;
import com.pano.vrplayer.objects.VRObject3DHelper;
import com.pano.vrplayer.objects.VRStereoSphere3D;
import com.pano.vrplayer.plugins.VRAbsPlugin;
import com.pano.vrplayer.plugins.VRPanoramaPlugin;

/**
 * Created by taipp on 9/7/2016.
 */
public class StereoSphereProjection extends AbsProjectionStrategy {

    private static class FixedDirectorFactory extends VR360DirectorFactory {
        @Override
        public VR360Director createDirector(int index) {
            return VR360Director.builder().build();
        }
    }

    private VRAbsObject3D object3D;

    @Override
    public void on(Activity activity) {
        object3D = new VRStereoSphere3D();
        VRObject3DHelper.loadObj(activity, object3D);
    }

    @Override
    public void off(Activity activity) {

    }

    @Override
    public boolean isSupport(Activity activity) {
        return true;
    }

    @Override
    public VRAbsObject3D getObject3D() {
        return object3D;
    }

    @Override
    public VRPosition getModelPosition() {
        return VRPosition.sOriginalPosition;
    }

    @Override
    protected VR360DirectorFactory hijackDirectorFactory() {
        return new FixedDirectorFactory();
    }

    @Override
    public VRAbsPlugin buildMainPlugin(VRMainPluginBuilder builder) {
        return new VRPanoramaPlugin(builder);
    }
}
