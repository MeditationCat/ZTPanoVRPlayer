package com.pano.vrplayer.strategy.interactive;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

import com.pano.vrplayer.VR360Director;
import com.pano.vrplayer.common.VRUtil;
import com.zhitech.zhilunvrsdk.ExtSensorEventListener;
import com.zhitech.zhilunvrsdk.Utils.ExtSensorEvent;
import com.zhitech.zhilunvrsdk.Utils.Utils;
import com.zhitech.zhilunvrsdk.ZhilunVrServiceSDK;

/**
 * Created by taipp on 9/7/2016.
 */
public class MotionStrategy extends AbsInteractiveStrategy implements SensorEventListener, ExtSensorEventListener {

    private static final String TAG = "MotionStrategy";

    private int mDeviceRotation;

    private float[] mSensorMatrix = new float[16];

    private boolean mRegistered = false;

    private Boolean mIsSupport = false;

    private ZhilunVrServiceSDK mVrServiceSDK = null;

    public MotionStrategy(InteractiveModeManager.Params params) {
        super(params);
        Utils.dLog(TAG);
    }

    private Handler handler = new Handler();

    @Override
    public void onResume(final Context context) {
        Utils.dLog(TAG);
        mVrServiceSDK.registerListener(MotionStrategy.this);
        if (mVrServiceSDK.sensorIsPrepared()) {
            mVrServiceSDK.registerListener(MotionStrategy.this);
            unregisterSensor(context);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mVrServiceSDK.sensorIsPrepared()) {
                        mVrServiceSDK.registerListener(MotionStrategy.this);
                        return;
                    }
                    registerSensor(context);
                }
            }, 1500);
        }
    }

    @Override
    public void onPause(Context context) {
        Utils.dLog(TAG);
        if (mVrServiceSDK.sensorIsPrepared()) {
            mVrServiceSDK.unregisterListener();
        }
        unregisterSensor(context);
    }

    @Override
    public boolean handleDrag(int distanceX, int distanceY) {
        return false;
    }

    @Override
    public void on(Activity activity) {
        Utils.dLog(TAG);
        mVrServiceSDK = new ZhilunVrServiceSDK(activity.getApplicationContext(), 0x2d29, 0x1001);
        mVrServiceSDK.onStart();
        mDeviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        for (VR360Director director : getDirectorList()){
            director.reset();
        }
    }

    @Override
    public void off(Activity activity) {
        Utils.dLog(TAG);
        if (mVrServiceSDK.sensorIsPrepared()) {
            mVrServiceSDK.onStop();
        }
        unregisterSensor(activity);
    }

    @Override
    public boolean isSupport(Activity activity) {
        Utils.dLog(TAG);
        if (!mIsSupport){
            SensorManager mSensorManager = (SensorManager) activity
                    .getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mIsSupport = (sensor != null);
        }
        return mIsSupport;
    }

    protected void registerSensor(Context context){
        if (mRegistered) return;
        Utils.dLog(TAG);
        SensorManager mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (sensor == null){
            Utils.dLog(TAG,"TYPE_ROTATION_VECTOR sensor not support!");
            return;
        }
        mSensorManager.registerListener(this, sensor, getParams().mMotionDelay);
        mRegistered = true;
    }

    protected void unregisterSensor(Context context){
        if (!mRegistered) return;
        Utils.dLog(TAG);
        SensorManager mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);
        mRegistered = false;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Utils.dLog(TAG, "SensorEvent:" + event.toString());
        if (mVrServiceSDK.sensorIsRunning()) {
            return;
        }
        if (event.accuracy != 0){
            if (getParams().mSensorListener != null){
                getParams().mSensorListener.onSensorChanged(event);
            }
            int type = event.sensor.getType();
            switch (type){
                case Sensor.TYPE_ROTATION_VECTOR:
                    VRUtil.sensorRotationVector2Matrix(event, mDeviceRotation, mSensorMatrix);
                    for (VR360Director director : getDirectorList()){
                        director.updateSensorMatrix(mSensorMatrix);
                        // if (mDisplayMode == DISPLAY_MODE_NORMAL) break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onSensorChanged(ExtSensorEvent event) {
        Utils.dLog(TAG, "ExtSensorEvent:" + event.toString());
        int type = event.getType();
        float[] values = event.values;
        switch (type){
            case Utils.TYPE_ACCELEROMETER:
            case Utils.TYPE_MAGNETIC_FIELD:
            case Utils.TYPE_GYROSCOPE:
            case Utils.TYPE_ROTATION_VECTOR:
                break;
        }
        /*Utils.dLog(TAG, String.format(Locale.US, "ExtSensorEvent name:%s\t values:{%f, %f, %f}\ttimestamp:%d",
                event.getName(),
                values[0], values[1], values[2],
                event.timestamp));*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (getParams().mSensorListener != null){
            getParams().mSensorListener.onAccuracyChanged(sensor,accuracy);
        }
    }
}
