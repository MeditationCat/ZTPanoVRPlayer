package com.pano.vrplayer.strategy.projection;

import android.app.Activity;
import android.graphics.RectF;

import com.pano.vrplayer.model.VRMainPluginBuilder;
import com.pano.vrplayer.model.VRPosition;
import com.pano.vrplayer.objects.VRAbsObject3D;
import com.pano.vrplayer.objects.VRDome3D;
import com.pano.vrplayer.objects.VRObject3DHelper;
import com.pano.vrplayer.plugins.VRAbsPlugin;
import com.pano.vrplayer.plugins.VRPanoramaPlugin;

/**
 * Created by taipp on 9/7/2016.
 */
public class DomeProjection extends AbsProjectionStrategy {

    VRAbsObject3D object3D;

    private float mDegree;

    private boolean mIsUpper;

    private RectF mTextureSize;

    public DomeProjection(RectF textureSize, float degree, boolean isUpper) {
        this.mTextureSize = textureSize;
        this.mDegree = degree;
        this.mIsUpper = isUpper;
    }

    @Override
    public void on(Activity activity) {
        object3D = new VRDome3D(mTextureSize, mDegree, mIsUpper);
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
    public VRAbsPlugin buildMainPlugin(VRMainPluginBuilder builder) {
        return new VRPanoramaPlugin(builder);
    }
}
