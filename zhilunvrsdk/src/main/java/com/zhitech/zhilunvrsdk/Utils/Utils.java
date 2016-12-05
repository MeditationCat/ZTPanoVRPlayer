package com.zhitech.zhilunvrsdk.Utils;

import android.util.Log;

/**
 * Created by taipp on 7/8/2016.
 */
public class Utils {
    public static final String ACTION_KILL_SELF = "com.android.example.KILL_SELF";
    // cmd
    public final static int CMD_IAP_UPGRADE = 0xA0;
    public final static int CMD_G_CALIBRATE = 0x2B;
    public final static int CMD_CHECK_VERSION = 0xB1;
    public final static int CMD_RECV_SENSOR_DATA = 0x0B;
    public final static int CMD_STOP_SENSOR_DATA = 0x07;
    public final static int CMD_RECV_TP_EVENT = 0xB3;
    public final static int CMD_STOP_TP_EVENT = 0xB9;
    public final static int CMD_WRITE_SN = 0xBA;
    public final static int CMD_READ_SN = 0xBC;
    public final static int CMD_WRITE_BLE_MAC = 0xB5;
    public final static int CMD_READ_BLE_MAC = 0xB7;

    public final static boolean DEBUG = true; //
    // sensor state
    public final static int SENSOR_STATE_NONE = 0x00;
    public final static int SENSOR_STATE_CONNECTING = 0x01;
    public final static int SENSOR_STATE_CONNECTED = 0x02;
    public final static int SENSOR_STATE_IDLE = 0x04;
    public final static int SENSOR_STATE_RUNNING = 0x08;
    public final static int SENSOR_STATE_STOPPED = 0x10;
    public final static int SENSOR_STATE_DISCONNECTED = 0x20;
    // sensor type
    public static final int TYPE_ACCELEROMETER = 1;
    public static final String STRING_TYPE_ACCELEROMETER = "Accelerometer";

    public static final int TYPE_MAGNETIC_FIELD = 2;
    public static final String STRING_TYPE_MAGNETIC_FIELD = "Magnetic field";

    public static final int TYPE_GYROSCOPE = 4;
    public static final String STRING_TYPE_GYROSCOPE = "Gyroscope";

    public static final int TYPE_LIGHT = 5;
    public static final String STRING_TYPE_LIGHT = "Light";

    public static final int TYPE_PRESSURE = 6;
    public static final String STRING_TYPE_PRESSURE = "Pressure";


    public static final int TYPE_PROXIMITY = 8;
    public static final String STRING_TYPE_PROXIMITY = "Proximity";

    public static final int TYPE_GRAVITY = 9;
    public static final String STRING_TYPE_GRAVITY = "Gravity";

    public static final int TYPE_LINEAR_ACCELERATION = 10;
    public static final String STRING_TYPE_LINEAR_ACCELERATION =
            "Linear acceleration";

    public static final int TYPE_ROTATION_VECTOR = 11;
    public static final String STRING_TYPE_ROTATION_VECTOR = "Rotation vector";


    public static void dLog(String Tag) {
        if (DEBUG) {
            Log.d(Tag, Thread.currentThread().getStackTrace()[3].getMethodName());
        }
    }

    public static void dLog(String Tag, String log) {
        if (DEBUG) {
            Log.d(Tag, Thread.currentThread().getStackTrace()[3].getMethodName() + "->" + log);
        }
    }
}