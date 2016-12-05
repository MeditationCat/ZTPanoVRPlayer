package com.zhitech.zhilunvrsdk.Utils;

/**
 * Created by taipp on 11/28/2016.
 */

public class ExtSensorEvent {
    private String mName;
    private int mType;
    public final float[] values;
    public long timestamp;

    public ExtSensorEvent(int type, String name, float[]values, int valueSize, long timestamp) {
        this.mType = type;
        this.mName = name;
        this.values = new float[valueSize];
        System.arraycopy(values, 0, this.values, 0, valueSize);
        this.timestamp = timestamp;
    }

    public ExtSensorEvent(int valueSize) {
        values = new float[valueSize];
    }

    public int getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }
}
