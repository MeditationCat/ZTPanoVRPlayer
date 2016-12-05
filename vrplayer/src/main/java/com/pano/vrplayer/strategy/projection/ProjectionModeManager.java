package com.pano.vrplayer.strategy.projection;

import android.app.Activity;
import android.graphics.RectF;

import com.pano.vrplayer.VR360Director;
import com.pano.vrplayer.VR360DirectorFactory;
import com.pano.vrplayer.VRLibrary;
import com.pano.vrplayer.model.VRMainPluginBuilder;
import com.pano.vrplayer.model.VRPosition;
import com.pano.vrplayer.objects.VRAbsObject3D;
import com.pano.vrplayer.plugins.VRAbsPlugin;
import com.pano.vrplayer.strategy.ModeManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by taipp on 9/7/2016.
 */
public class ProjectionModeManager extends ModeManager<AbsProjectionStrategy> implements IProjectionMode {

    public static int[] sModes = {VRLibrary.PROJECTION_MODE_SPHERE, VRLibrary.PROJECTION_MODE_DOME180, VRLibrary.PROJECTION_MODE_DOME230};

    public static class Params{
        public RectF textureSize;
        public VR360DirectorFactory directorFactory;
        public VRMainPluginBuilder mainPluginBuilder;
        public IVRProjectionFactory projectionFactory;
    }

    private List<VR360Director> mDirectors = new CopyOnWriteArrayList<>();

    private RectF mTextureSize;

    private VR360DirectorFactory mCustomDirectorFactory;

    private VRAbsPlugin mMainPlugin;

    private VRMainPluginBuilder mMainPluginBuilder;

    private IVRProjectionFactory mProjectionFactory;

    public ProjectionModeManager(int mode, Params projectionManagerParams) {
        super(mode);
        this.mTextureSize = projectionManagerParams.textureSize;
        this.mCustomDirectorFactory = projectionManagerParams.directorFactory;
        this.mProjectionFactory = projectionManagerParams.projectionFactory;
        this.mMainPluginBuilder = projectionManagerParams.mainPluginBuilder;
        this.mMainPluginBuilder.setProjectionModeManager(this);
    }

    public VRAbsPlugin getMainPlugin() {
        if (mMainPlugin == null){
            mMainPlugin = getStrategy().buildMainPlugin(mMainPluginBuilder);
        }
        return mMainPlugin;
    }

    @Override
    public void switchMode(Activity activity, int mode) {
        super.switchMode(activity, mode);
    }

    @Override
    public void on(Activity activity) {
        super.on(activity);

        // destroy prev main plugin
        if( mMainPlugin != null){
            mMainPlugin.destroy();
            mMainPlugin = null;
        }

        mDirectors.clear();

        VR360DirectorFactory factory = getStrategy().hijackDirectorFactory();
        factory = factory == null ? mCustomDirectorFactory : factory;

        for (int i = 0; i < VRLibrary.sMultiScreenSize; i++){
            mDirectors.add(factory.createDirector(i));
        }
    }

    @Override
    protected AbsProjectionStrategy createStrategy(int mode) {
        if (mProjectionFactory != null){
            AbsProjectionStrategy strategy = mProjectionFactory.createStrategy(mode);
            if (strategy != null) return strategy;
        }
        
        switch (mode){
            case VRLibrary.PROJECTION_MODE_DOME180:
                return new DomeProjection(this.mTextureSize,180f,false);
            case VRLibrary.PROJECTION_MODE_DOME230:
                return new DomeProjection(this.mTextureSize,230f,false);
            case VRLibrary.PROJECTION_MODE_DOME180_UPPER:
                return new DomeProjection(this.mTextureSize,180f,true);
            case VRLibrary.PROJECTION_MODE_DOME230_UPPER:
                return new DomeProjection(this.mTextureSize,230f,true);
            case VRLibrary.PROJECTION_MODE_STEREO_SPHERE:
                return new StereoSphereProjection();
            case VRLibrary.PROJECTION_MODE_PLANE_FIT:
            case VRLibrary.PROJECTION_MODE_PLANE_CROP:
            case VRLibrary.PROJECTION_MODE_PLANE_FULL:
                return PlaneProjection.create(mode,this.mTextureSize);
            case VRLibrary.PROJECTION_MODE_MULTI_FISH_EYE_HORIZONTAL:
                return new MultiFishEyeProjection(1f,true);
            case VRLibrary.PROJECTION_MODE_MULTI_FISH_EYE_VERTICAL:
                return new MultiFishEyeProjection(1f,false);
            case VRLibrary.PROJECTION_MODE_SPHERE:
            default:
                return new SphereProjection();
        }
    }

    @Override
    protected int[] getModes() {
        return sModes;
    }

    @Override
    public VRPosition getModelPosition() {
        return getStrategy().getModelPosition();
    }

    @Override
    public VRAbsObject3D getObject3D() {
        return getStrategy().getObject3D();
    }

    public List<VR360Director> getDirectors() {
        return mDirectors;
    }
}
