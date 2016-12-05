package com.zhitech.zhilunvrsdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.zhitech.zhilunvrsdk.Utils.ExtSensorEvent;
import com.zhitech.zhilunvrsdk.Utils.SensorPacketDataObject;
import com.zhitech.zhilunvrsdk.Utils.Utils;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by taipp on 7/8/2016.
 */
public class ZhilunVrServiceSDK {

    private static final String TAG = "ZhilunVrServiceSDK";

    private IZhilunVrAidl iZhilunVrAidl = null;

    private int mVendorId = 0x2d29;

    private int mProductId = 0x1001;

    private Context mContext;

    private ExtSensorEventListener mExtSensorEventListener = null;

    private IPacketDataAidl.Stub iPacketDataAidlStub = new IPacketDataAidl.Stub() {
        @Override
        public void OnSensorDataChanged(SensorPacketDataObject object) throws RemoteException {

            if (new String(object.getPacketHeader()).equals("M5")) {
                long timestamp = object.getPacketDataTimestamp();
                float[] acc = object.getAccValues();
                float[] mag = object.getMagValues();
                float[] gyro = object.getGyroValues();

                // call onSensorChanged interface
                if (mExtSensorEventListener != null) {
                    // acc
                    ExtSensorEvent accEvent = new ExtSensorEvent(
                            Utils.TYPE_ACCELEROMETER,
                            Utils.STRING_TYPE_ACCELEROMETER,
                            acc,
                            acc.length,
                            timestamp);
                    mExtSensorEventListener.onSensorChanged(accEvent);
                    // mag
                    ExtSensorEvent magEvent = new ExtSensorEvent(
                            Utils.TYPE_MAGNETIC_FIELD,
                            Utils.STRING_TYPE_MAGNETIC_FIELD,
                            mag,
                            mag.length,
                            timestamp);
                    mExtSensorEventListener.onSensorChanged(magEvent);
                    // gyro
                    ExtSensorEvent gyroEvent = new ExtSensorEvent(
                            Utils.TYPE_GYROSCOPE,
                            Utils.STRING_TYPE_GYROSCOPE,
                            gyro,
                            gyro.length,
                            timestamp);
                    mExtSensorEventListener.onSensorChanged(gyroEvent);
                }
            }
        }

        @Override
        public void OnTouchPadActonEvent(int[] values) throws RemoteException {
            //
        }
    };

    private ICommandResultAidl.Stub iCommandResultAidlStub = new ICommandResultAidl.Stub() {
        @Override
        public void OnCommandResultChanged(int cmd, byte[] data, int length) throws RemoteException {
            Utils.dLog(TAG, String.format("cmd = %#04x", cmd));
            switch (cmd) {
                case Utils.CMD_CHECK_VERSION + 1:
                case Utils.CMD_STOP_SENSOR_DATA + 1:
                    break;
                default:
                    break;
            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Utils.dLog(TAG);
            iZhilunVrAidl = IZhilunVrAidl.Stub.asInterface(iBinder);
            if (iZhilunVrAidl != null) {
                try {
                    iZhilunVrAidl.RemoteRegisterOnPacketDataListener(iPacketDataAidlStub);
                    iZhilunVrAidl.RemoteRegisterOnCommandResultListener(iCommandResultAidlStub);
                    iZhilunVrAidl.RemoteSetDeviceFilter(mVendorId, mProductId);
                    iZhilunVrAidl.RemoteConnectToDevice();
                    iZhilunVrAidl.RemoteGetSensorState();
                    SendCommand(Utils.CMD_CHECK_VERSION);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Utils.dLog(TAG);
            if (iZhilunVrAidl != null) {
                try {
                    iZhilunVrAidl.RemoteUnregisterOnPacketDataListener(iPacketDataAidlStub);
                    iZhilunVrAidl.RemoteUnregisterOnCommandResultListener(iCommandResultAidlStub);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            iZhilunVrAidl = null;
        }
    };

    public ZhilunVrServiceSDK(Context mContext, int mVendorId, int mProductId) {
        Utils.dLog(TAG);
        this.mVendorId = mVendorId;
        this.mProductId = mProductId;
        this.mContext = mContext;
        //start service
        //this.mContext.startService(new Intent(this.mContext, ZhilunVrService.class));
    }

    public ZhilunVrServiceSDK(Context mContext) {
        Utils.dLog(TAG);
        this.mContext = mContext;
        onStart();
    }

    public void onStart() {
        //bind remote service
        Intent binderIntent = new Intent(this.mContext, ZhilunVrService.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("create_flag", true);
        binderIntent.putExtras(bundle);
        this.mContext.bindService(binderIntent, connection, BIND_AUTO_CREATE);
    }

    public void registerListener(ExtSensorEventListener listener) {
        Utils.dLog(TAG);
        SendCommand(Utils.CMD_RECV_SENSOR_DATA);
        this.mExtSensorEventListener = listener;
    }

    public void unregisterListener() {
        Utils.dLog(TAG);
        SendCommand(Utils.CMD_STOP_SENSOR_DATA);
        this.mExtSensorEventListener = null;
    }

    public void onStop() {
        Utils.dLog(TAG);
        SendCommand(Utils.CMD_STOP_SENSOR_DATA);
        this.mContext.unbindService(connection);
        //stopService(new Intent(this, ZhilunVrService.class));
    }

    public boolean sensorIsPrepared() {
        int state = getSensorState();
        Utils.dLog(TAG, "state = " + state);
        switch (state) {
            case Utils.SENSOR_STATE_CONNECTED:
            case Utils.SENSOR_STATE_IDLE:
            case Utils.SENSOR_STATE_RUNNING:
            case Utils.SENSOR_STATE_STOPPED:
                return true;
            default:
                break;
        }
        return false;
    }

    public boolean sensorIsRunning() {
        int state = getSensorState();
        Utils.dLog(TAG, "state = " + state);
        switch (state) {
            case Utils.SENSOR_STATE_RUNNING:
                return true;
            default:
                break;
        }
        return false;
    }

    public int getSensorState() {
        int state = Utils.SENSOR_STATE_NONE;
        if (iZhilunVrAidl != null) {
            try {
                state = iZhilunVrAidl.RemoteGetSensorState();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return state;
    }

    public void SetBulkTransferTimeout(int timeout) {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteSetBulkTransferTimeout(timeout);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void SetReceiveDataGapTime(int gaptime) {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteSetReceiveDataGapTime(gaptime);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void SetMainActivityClassName(String className) {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteSetMainActivityClassName(className);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public String GetDeviceInfo() {
        if (iZhilunVrAidl != null) {
            try {
                return iZhilunVrAidl.RemoteGetDeviceInfo();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //send command to device
    public int SendCommand(int cmd) {
        int retValue = -1;
        if (iZhilunVrAidl != null) {
            try {
                retValue = iZhilunVrAidl.RemoteSendCommand(cmd);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return retValue;
    }

    public int SendCommandWithData(int cmd, byte[] data) {
        int retValue = -1;
        if (iZhilunVrAidl != null) {
            try {
                retValue = iZhilunVrAidl.RemoteSendCommandWithData(cmd, data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return retValue;
    }
    //daemo thread control
    public boolean CheckServiceStatus() {
        boolean flag = false;
        if (iZhilunVrAidl != null) {
            try {
                flag = iZhilunVrAidl.RemoteCheckServiceListenerThreadIsRunnig();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public void RestartService() {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteRestartServiceListenerThread();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
