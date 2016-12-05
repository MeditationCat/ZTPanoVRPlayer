package com.pano.vrplayer;

import android.content.Context;
import android.view.MotionEvent;

import com.pano.vrplayer.common.VRHandler;
import com.pano.vrplayer.common.VRUtil;
import com.pano.vrplayer.model.VRRay;
import com.pano.vrplayer.plugins.IVRHotspot;
import com.pano.vrplayer.plugins.VRAbsPlugin;
import com.pano.vrplayer.plugins.VRPluginManager;
import com.pano.vrplayer.strategy.display.DisplayModeManager;
import com.pano.vrplayer.strategy.projection.ProjectionModeManager;

import java.util.List;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRPickerManager {

    private static final String TAG = "VRPickerManager";

    private static final int HIT_FROM_EYE = 1;

    private static final int HIT_FROM_TOUCH = 2;

    private boolean mEyePickEnable;

    private DisplayModeManager mDisplayModeManager;

    private ProjectionModeManager mProjectionModeManager;

    private VRPluginManager mPluginManager;

    private VRLibrary.IEyePickListener mEyePickChangedListener;

    private VRLibrary.ITouchPickListener mTouchPickListener;

    private EyePickPoster mEyePickPoster = new EyePickPoster();

    private TouchPickPoster mTouchPickPoster = new TouchPickPoster();

    private VRLibrary.IGestureListener mTouchPicker = new VRLibrary.IGestureListener() {
        @Override
        public void onClick(MotionEvent e) {
            rayPickAsTouch(e);
        }
    };

    private VRAbsPlugin mEyePicker = new VRAbsPlugin() {
        @Override
        protected void init(Context context) {

        }

        @Override
        public void beforeRenderer(int totalWidth, int totalHeight) {

        }

        @Override
        public void renderer(int index, int width, int height, VR360Director director) {
            if (index == 0 && isEyePickEnable()){
                rayPickAsEye(width >> 1, height >> 1, director);
            }
        }

        @Override
        public void destroy() {

        }

        @Override
        protected boolean removable() {
            return false;
        }
    };

    private VRPickerManager(Builder params) {
        this.mDisplayModeManager = params.displayModeManager;
        this.mProjectionModeManager = params.projectionModeManager;
        this.mPluginManager = params.pluginManager;
    }

    public boolean isEyePickEnable() {
        return mEyePickEnable;
    }

    public void setEyePickEnable(boolean eyePickEnable) {
        this.mEyePickEnable = eyePickEnable;
    }

    private void rayPickAsTouch(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        int size = mDisplayModeManager.getVisibleSize();
        if (size == 0){
            return;
        }

        int itemWidth = mProjectionModeManager.getDirectors().get(0).getViewportWidth();

        int index = (int) (x / itemWidth);
        if (index >= size){
            return;
        }
        VRRay ray = VRUtil.point2Ray(x - itemWidth * index, y, mProjectionModeManager.getDirectors().get(index));

        IVRHotspot hotspot = pick(ray, HIT_FROM_TOUCH);

        if (ray != null && mTouchPickListener != null){
            mTouchPickListener.onHotspotHit(hotspot, ray);
        }
    }

    private void rayPickAsEye(float x, float y, VR360Director director) {
        VRRay ray = VRUtil.point2Ray(x, y, director);
        pick(ray, HIT_FROM_EYE);
    }

    private IVRHotspot pick(VRRay ray, int hitType){
        if (ray == null) return null;
        return hitTest(ray, hitType);
    }

    private IVRHotspot hitTest(VRRay ray, int hitType) {
        List<VRAbsPlugin> plugins = mPluginManager.getPlugins();
        IVRHotspot hitHotspot = null;
        boolean hasHit = false;
        for (VRAbsPlugin plugin : plugins) {
            if (plugin instanceof IVRHotspot) {
                IVRHotspot hotspot = (IVRHotspot) plugin;
                hasHit = hotspot.hit(ray);
                if (hasHit){
                    hitHotspot = hotspot;
                    break;
                }
            }
        }

        switch (hitType) {
            case HIT_FROM_TOUCH:
                // only post the hotspot which is hit.
                if (hasHit){
                    mTouchPickPoster.setRay(ray);
                    mTouchPickPoster.setHit(hitHotspot);
                    VRHandler.sharedHandler().post(mTouchPickPoster);
                }
                break;
            case HIT_FROM_EYE:
                mEyePickPoster.setHit(hitHotspot);
                VRHandler.sharedHandler().postDelayed(mEyePickPoster, 100);
                break;
        }

        return hitHotspot;
    }

    public VRLibrary.IGestureListener getTouchPicker() {
        return mTouchPicker;
    }

    public VRAbsPlugin getEyePicker() {
        return mEyePicker;
    }

    public static Builder with() {
        return new Builder();
    }

    public void setEyePickChangedListener(VRLibrary.IEyePickListener eyePickChangedListener) {
        this.mEyePickChangedListener = eyePickChangedListener;
    }

    public void setTouchPickListener(VRLibrary.ITouchPickListener touchPickListener) {
        this.mTouchPickListener = touchPickListener;
    }

    private class EyePickPoster implements Runnable {

        private IVRHotspot hit;

        private long timestamp;

        @Override
        public void run() {
            VRHandler.sharedHandler().removeCallbacks(this);

            if (mEyePickChangedListener != null){
                mEyePickChangedListener.onHotspotHit(hit, timestamp);
            }
        }

        public void setHit(IVRHotspot hit) {
            if (this.hit != hit){
                timestamp = System.currentTimeMillis();

                if (this.hit != null){
                    this.hit.onEyeHitOut();
                }
            }

            this.hit = hit;

            if (this.hit != null){
                this.hit.onEyeHitIn(timestamp);
            }
        }
    }

    private static class TouchPickPoster implements Runnable {

        private IVRHotspot hit;

        private VRRay ray;

        @Override
        public void run() {
            if (hit != null){
                hit.onTouchHit(ray);
            }
        }

        public void setRay(VRRay ray) {
            this.ray = ray;
        }

        public void setHit(IVRHotspot hit) {
            this.hit = hit;
        }
    }

    public static class Builder{
        private DisplayModeManager displayModeManager;
        private ProjectionModeManager projectionModeManager;
        private VRPluginManager pluginManager;

        private Builder() {
        }

        public VRPickerManager build(){
            return new VRPickerManager(this);
        }

        public Builder setPluginManager(VRPluginManager pluginManager) {
            this.pluginManager = pluginManager;
            return this;
        }

        public Builder setDisplayModeManager(DisplayModeManager displayModeManager) {
            this.displayModeManager = displayModeManager;
            return this;
        }

        public Builder setProjectionModeManager(ProjectionModeManager projectionModeManager) {
            this.projectionModeManager = projectionModeManager;
            return this;
        }
    }

    public void resetEyePick(){
        if (mEyePickPoster != null){
            mEyePickPoster.setHit(null);
        }
    }
}
