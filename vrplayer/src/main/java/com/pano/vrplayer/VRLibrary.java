package com.pano.vrplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.google.android.apps.muzei.render.GLTextureView;
import com.pano.vrplayer.common.GLUtil;
import com.pano.vrplayer.common.VRHandler;
import com.pano.vrplayer.model.BarrelDistortionConfig;
import com.pano.vrplayer.model.VRMainPluginBuilder;
import com.pano.vrplayer.model.VRRay;
import com.pano.vrplayer.plugins.IVRHotspot;
import com.pano.vrplayer.plugins.VRAbsPlugin;
import com.pano.vrplayer.plugins.VRPluginManager;
import com.pano.vrplayer.strategy.display.DisplayModeManager;
import com.pano.vrplayer.strategy.interactive.InteractiveModeManager;
import com.pano.vrplayer.strategy.projection.IVRProjectionFactory;
import com.pano.vrplayer.strategy.projection.ProjectionModeManager;
import com.pano.vrplayer.texture.VR360BitmapTexture;
import com.pano.vrplayer.texture.VR360Texture;
import com.pano.vrplayer.texture.VR360VideoTexture;

import java.util.Iterator;
import java.util.List;

import static com.pano.vrplayer.common.VRUtil.notNull;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRLibrary {

    private static final String TAG = "VRLibrary";
    public static final int sMultiScreenSize = 2;

    // interactive mode
    public static final int INTERACTIVE_MODE_MOTION = 1;
    public static final int INTERACTIVE_MODE_TOUCH = 2;
    public static final int INTERACTIVE_MODE_MOTION_WITH_TOUCH = 3;

    // display mode
    public static final int DISPLAY_MODE_NORMAL = 101;
    public static final int DISPLAY_MODE_GLASS = 102;

    // projection mode
    public static final int PROJECTION_MODE_SPHERE = 201;
    public static final int PROJECTION_MODE_DOME180 = 202;
    public static final int PROJECTION_MODE_DOME230 = 203;
    public static final int PROJECTION_MODE_DOME180_UPPER = 204;
    public static final int PROJECTION_MODE_DOME230_UPPER = 205;
    public static final int PROJECTION_MODE_STEREO_SPHERE = 206;
    public static final int PROJECTION_MODE_PLANE_FIT = 207;
    public static final int PROJECTION_MODE_PLANE_CROP = 208;
    public static final int PROJECTION_MODE_PLANE_FULL = 209;
    public static final int PROJECTION_MODE_MULTI_FISH_EYE_HORIZONTAL = 210;
    public static final int PROJECTION_MODE_MULTI_FISH_EYE_VERTICAL = 211;

    // private int mDisplayMode = DISPLAY_MODE_NORMAL;
    private RectF mTextureSize = new RectF(0,0,1024,1024);
    private InteractiveModeManager mInteractiveModeManager;
    private DisplayModeManager mDisplayModeManager;
    private ProjectionModeManager mProjectionModeManager;
    private VRPluginManager mPluginManager;
    private VRPickerManager mPickerManager;
    private VRGLScreenWrapper mScreenWrapper;
    private VRTouchHelper mTouchHelper;
    private VR360Texture mTexture;

    private VRLibrary(Builder builder) {

        // init main handler
        VRHandler.init();

        // init mode manager
        initModeManager(builder);

        // init plugin manager
        initPluginManager(builder);

        // init glSurfaceViews
        initOpenGL(builder.activity, builder.screenWrapper);

        mTexture = builder.texture;
        mTouchHelper = new VRTouchHelper(builder.activity);
        mTouchHelper.addClickListener(builder.gestureListener);
        mTouchHelper.setPinchEnabled(builder.pinchEnabled);
        mTouchHelper.setAdvanceGestureListener(new IAdvanceGestureListener() {
            @Override
            public void onDrag(float distanceX, float distanceY) {
                mInteractiveModeManager.handleDrag((int) distanceX,(int) distanceY);
            }

            @Override
            public void onPinch(float scale) {
                List<VR360Director> directors = mProjectionModeManager.getDirectors();
                for (VR360Director director : directors){
                    director.updateProjectionNearScale(scale);
                }
            }

        });

        mScreenWrapper.getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mTouchHelper.handleTouchEvent(event);
            }
        });

        // init picker manager
        initPickerManager(builder);
    }

    private void initModeManager(Builder builder) {

        // init ProjectionModeManager
        ProjectionModeManager.Params projectionManagerParams = new ProjectionModeManager.Params();
        projectionManagerParams.textureSize = mTextureSize;
        projectionManagerParams.directorFactory = builder.directorFactory;
        projectionManagerParams.projectionFactory = builder.projectionFactory;
        projectionManagerParams.mainPluginBuilder = new VRMainPluginBuilder()
                .setContentType(builder.contentType)
                .setTexture(builder.texture);

        mProjectionModeManager = new ProjectionModeManager(builder.projectionMode, projectionManagerParams);
        mProjectionModeManager.prepare(builder.activity, builder.notSupportCallback);

        // init DisplayModeManager
        mDisplayModeManager = new DisplayModeManager(builder.displayMode);
        mDisplayModeManager.setBarrelDistortionConfig(builder.barrelDistortionConfig);
        mDisplayModeManager.setAntiDistortionEnabled(builder.barrelDistortionConfig.isDefaultEnabled());
        mDisplayModeManager.prepare(builder.activity, builder.notSupportCallback);

        // init InteractiveModeManager
        InteractiveModeManager.Params interactiveManagerParams = new InteractiveModeManager.Params();
        interactiveManagerParams.projectionModeManager = mProjectionModeManager;
        interactiveManagerParams.mMotionDelay = builder.motionDelay;
        interactiveManagerParams.mSensorListener = builder.sensorListener;
        mInteractiveModeManager = new InteractiveModeManager(builder.interactiveMode,interactiveManagerParams);
        mInteractiveModeManager.prepare(builder.activity, builder.notSupportCallback);
    }

    private void initPluginManager(Builder builder) {
        mPluginManager = new VRPluginManager();
        /*
        VRMainPluginBuilder mainPluginBuilder = new VRMainPluginBuilder()
                .setContentType(builder.contentType)
                .setTexture(builder.texture)
                .setProjectionModeManager(mProjectionModeManager);
        VRAbsPlugin mainPlugin = new VRMultiFishEyePlugin(mainPluginBuilder);

        mPluginManager.add(mainPlugin);
        */
    }

    private void initPickerManager(Builder builder) {
        mPickerManager = VRPickerManager.with()
                .setPluginManager(mPluginManager)
                .setDisplayModeManager(mDisplayModeManager)
                .setProjectionModeManager(mProjectionModeManager)
                .build();
        setEyePickEnable(builder.eyePickEnabled);
        mPickerManager.setEyePickChangedListener(builder.eyePickChangedListener);
        mPickerManager.setTouchPickListener(builder.touchPickChangedListener);

        // listener
        mTouchHelper.addClickListener(mPickerManager.getTouchPicker());
        mPluginManager.add(mPickerManager.getEyePicker());
    }

    private void initOpenGL(Context context, VRGLScreenWrapper screenWrapper) {
        if (GLUtil.supportsEs2(context)) {
            screenWrapper.init(context);
            // Request an OpenGL ES 2.0 compatible context.

            VR360Renderer renderer = VR360Renderer.with(context)
                    .setPluginManager(mPluginManager)
                    .setProjectionModeManager(mProjectionModeManager)
                    .setDisplayModeManager(mDisplayModeManager)
                    .build();

            // Set the renderer to our demo renderer, defined below.
            screenWrapper.setRenderer(renderer);
            this.mScreenWrapper = screenWrapper;
        } else {
            this.mScreenWrapper.getView().setVisibility(View.GONE);
            Toast.makeText(context, "OpenGLES2 not supported.", Toast.LENGTH_SHORT).show();
        }
    }

    public void switchInteractiveMode(Activity activity) {
        mInteractiveModeManager.switchMode(activity);
    }

    /**
     * Switch Interactive Mode
     *
     * @param activity activity
     * @param mode mode
     *
     * {@link #INTERACTIVE_MODE_MOTION}
     * {@link #INTERACTIVE_MODE_TOUCH}
     * {@link #INTERACTIVE_MODE_MOTION_WITH_TOUCH}
     */
    public void switchInteractiveMode(Activity activity, int mode){
        mInteractiveModeManager.switchMode(activity,mode);
    }

    public void switchDisplayMode(Activity activity){
        mDisplayModeManager.switchMode(activity);
    }

    /**
     * Switch Display Mode
     *
     * @param activity activity
     * @param mode mode
     *
     * {@link #DISPLAY_MODE_GLASS}
     * {@link #DISPLAY_MODE_NORMAL}
     */
    public void switchDisplayMode(Activity activity, int mode){
        mDisplayModeManager.switchMode(activity,mode);
    }

    /**
     * Switch Projection Mode
     *
     * @param activity activity
     * @param mode mode
     *
     * {@link #PROJECTION_MODE_SPHERE}
     * {@link #PROJECTION_MODE_DOME180}
     * and so on.
     */
    public void switchProjectionMode(Activity activity, int mode) {
        mProjectionModeManager.switchMode(activity, mode);
    }

    public void resetTouch(){
        List<VR360Director> directors = mProjectionModeManager.getDirectors();
        for (VR360Director director : directors){
            director.reset();
        }
    }

    public void resetPinch(){
        mTouchHelper.reset();
    }

    public void resetEyePick(){
        mPickerManager.resetEyePick();
    }

    public void setAntiDistortionEnabled(boolean enabled){
        mDisplayModeManager.setAntiDistortionEnabled(enabled);
    }

    public boolean isAntiDistortionEnabled(){
        return mDisplayModeManager.isAntiDistortionEnabled();
    }

    public boolean isEyePickEnable() {
        return mPickerManager.isEyePickEnable();
    }

    public void setEyePickEnable(boolean eyePickEnable) {
        mPickerManager.setEyePickEnable(eyePickEnable);
    }

    public void setEyePickChangedListener(IEyePickListener listener){
        mPickerManager.setEyePickChangedListener(listener);
    }

    public void setTouchPickListener(ITouchPickListener listener){
        mPickerManager.setTouchPickListener(listener);
    }

    public int getScreenSize(){
        return mDisplayModeManager.getVisibleSize();
    }

    public void addPlugin(VRAbsPlugin plugin){
        mPluginManager.add(plugin);
    }

    public void removePlugin(VRAbsPlugin plugin){
        mPluginManager.remove(plugin);
    }

    public void removePlugins(){
        mPluginManager.removeAll();
    }

    public void onTextureResize(float width, float height){
        mTextureSize.set(0,0,width,height);
    }

    public void onResume(Context context){
        mInteractiveModeManager.onResume(context);
        if (mScreenWrapper != null){
            mScreenWrapper.onResume();
        }
    }

    public void onPause(Context context){
        mInteractiveModeManager.onPause(context);
        if (mScreenWrapper != null){
            mScreenWrapper.onPause();
        }
    }

    public void onDestroy(){
        Iterator<VRAbsPlugin> iterator = mPluginManager.getPlugins().iterator();
        while (iterator.hasNext()){
            VRAbsPlugin plugin = iterator.next();
            plugin.destroy();
        }

        VRAbsPlugin mainPlugin = mProjectionModeManager.getMainPlugin();
        if (mainPlugin != null){
            mainPlugin.destroy();
        }

        if (mTexture != null){
            mTexture.destroy();
            mTexture.release();
            mTexture = null;
        }

    }

    /**
     * handle touch touch to rotate the model
     * @deprecated deprecated since 2.0
     *
     * @param event
     * @return true if handled.
     */
    public boolean handleTouchEvent(MotionEvent event) {
        Log.e(TAG,"please remove the handleTouchEvent in activity!");
        return false;
    }

    public int getInteractiveMode() {
        return mInteractiveModeManager.getMode();
    }

    public int getDisplayMode(){
        return mDisplayModeManager.getMode();
    }

    public int getProjectionMode(){
        return mProjectionModeManager.getMode();
    }

    public interface IOnSurfaceReadyCallback {
        void onSurfaceReady(Surface surface);
    }

    public interface IBitmapProvider {
        void onProvideBitmap(VR360BitmapTexture.Callback callback);
    }

    public interface INotSupportCallback{
        void onNotSupport(int mode);
    }

    public interface IGestureListener {
        void onClick(MotionEvent e);
    }

    interface IAdvanceGestureListener {
        void onDrag(float distanceX, float distanceY);
        void onPinch(float scale);
    }

    public interface IEyePickListener {
        void onHotspotHit(IVRHotspot hitHotspot, long hitTimestamp);
    }

    public interface ITouchPickListener {
        void onHotspotHit(IVRHotspot hitHotspot, VRRay ray);
    }

    public static Builder with(Activity activity){
        return new Builder(activity);
    }

    /**
     *
     */
    public static class Builder {
        private int displayMode = DISPLAY_MODE_NORMAL;
        private int interactiveMode = INTERACTIVE_MODE_MOTION;
        private int projectionMode = PROJECTION_MODE_SPHERE;
        private Activity activity;
        private int contentType = ContentType.DEFAULT;
        private VR360Texture texture;
        private INotSupportCallback notSupportCallback;
        private IGestureListener gestureListener;
        private boolean pinchEnabled; // default false.
        private boolean eyePickEnabled = true; // default true.
        private BarrelDistortionConfig barrelDistortionConfig;
        private IEyePickListener eyePickChangedListener;
        private ITouchPickListener touchPickChangedListener;
        private VR360DirectorFactory directorFactory;
        private int motionDelay = SensorManager.SENSOR_DELAY_GAME;
        private SensorEventListener sensorListener;
        private VRGLScreenWrapper screenWrapper;
        private IVRProjectionFactory projectionFactory;

        private Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder displayMode(int displayMode){
            this.displayMode = displayMode;
            return this;
        }

        public Builder interactiveMode(int interactiveMode){
            this.interactiveMode = interactiveMode;
            return this;
        }

        public Builder projectionMode(int projectionMode){
            this.projectionMode = projectionMode;
            return this;
        }

        public Builder ifNotSupport(INotSupportCallback callback){
            this.notSupportCallback = callback;
            return this;
        }

        public Builder asVideo(IOnSurfaceReadyCallback callback){
            texture = new VR360VideoTexture(callback);
            contentType = ContentType.VIDEO;
            return this;
        }

        public Builder asBitmap(IBitmapProvider bitmapProvider){
            notNull(bitmapProvider, "bitmap Provider can't be null!");
            texture = new VR360BitmapTexture(bitmapProvider);
            contentType = ContentType.BITMAP;
            return this;
        }

        /**
         * gesture listener, e.g.
         * onClick
         * @deprecated please use {@link #listenGesture(IGestureListener)}
         *
         * @param listener listener
         * @return builder
         */
        @Deprecated
        public Builder gesture(IGestureListener listener) {
            gestureListener = listener;
            return this;
        }

        /**
         * enable or disable the pinch gesture
         *
         * @param enabled default is false
         * @return builder
         */
        public Builder pinchEnabled(boolean enabled) {
            this.pinchEnabled = enabled;
            return this;
        }

        /**
         * enable or disable the eye picking.
         *
         * @param enabled default is false
         * @return builder
         */
        public Builder eyePickEanbled(boolean enabled) {
            this.eyePickEnabled = enabled;
            return this;
        }

        /**
         * gesture listener, e.g.
         * onClick
         *
         * @param listener listener
         * @return builder
         */
        public Builder listenGesture(IGestureListener listener) {
            gestureListener = listener;
            return this;
        }

        /**
         * IPickListener listener
         *
         * @param listener listener
         * @return builder
         */
        public Builder listenEyePick(IEyePickListener listener){
            this.eyePickChangedListener = listener;
            return this;
        }

        /**
         * IPickListener listener
         *
         * @param listener listener
         * @return builder
         */
        public Builder listenTouchPick(ITouchPickListener listener){
            this.touchPickChangedListener = listener;
            return this;
        }

        /**
         * sensor delay in motion mode.
         *
         * {@link android.hardware.SensorManager#SENSOR_DELAY_FASTEST}
         * {@link android.hardware.SensorManager#SENSOR_DELAY_GAME}
         * {@link android.hardware.SensorManager#SENSOR_DELAY_NORMAL}
         * {@link android.hardware.SensorManager#SENSOR_DELAY_UI}
         *
         * @param motionDelay default is {@link android.hardware.SensorManager#SENSOR_DELAY_GAME}
         * @return builder
         */
        public Builder motionDelay(int motionDelay){
            this.motionDelay = motionDelay;
            return this;
        }

        public Builder sensorCallback(SensorEventListener callback){
            this.sensorListener = callback;
            return this;
        }

        public Builder directorFactory(VR360DirectorFactory directorFactory){
            this.directorFactory = directorFactory;
            return this;
        }

        public Builder projectionFactory(IVRProjectionFactory projectionFactory){
            this.projectionFactory = projectionFactory;
            return this;
        }

        public Builder barrelDistortionConfig(BarrelDistortionConfig config){
            this.barrelDistortionConfig = config;
            return this;
        }

        /**
         * build it!
         *
         * @param glViewId will find the GLSurfaceView by glViewId in the giving {@link #activity}
         *                 or find the GLTextureView by glViewId
         * @return vr lib
         */
        public VRLibrary build(int glViewId){
            View view = activity.findViewById(glViewId);
            if (view instanceof GLSurfaceView){
                return build((GLSurfaceView) view);
            } else if(view instanceof GLTextureView){
                return build((GLTextureView) view);
            } else {
                throw new RuntimeException("Please ensure the glViewId is instance of GLSurfaceView or GLTextureView");
            }
        }

        public VRLibrary build(GLSurfaceView glSurfaceView){
            return build(VRGLScreenWrapper.wrap(glSurfaceView));
        }

        public VRLibrary build(GLTextureView glTextureView){
            return build(VRGLScreenWrapper.wrap(glTextureView));
        }

        private VRLibrary build(VRGLScreenWrapper screenWrapper){
            notNull(texture,"You must call video/bitmap function before build");
            if (this.directorFactory == null) directorFactory = new VR360DirectorFactory.DefaultImpl();
            if (this.barrelDistortionConfig == null) barrelDistortionConfig = new BarrelDistortionConfig();
            this.screenWrapper = screenWrapper;
            return new VRLibrary(this);
        }
    }

    public interface ContentType{
        int VIDEO = 0;
        int BITMAP = 1;
        int DEFAULT = VIDEO;
    }
}
