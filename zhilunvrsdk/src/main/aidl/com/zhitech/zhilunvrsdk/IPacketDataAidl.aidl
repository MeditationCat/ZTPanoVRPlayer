// IPacketDataAidl.aidl
package com.zhitech.zhilunvrsdk;

import com.zhitech.zhilunvrsdk.Utils.SensorPacketDataObject;

// Declare any non-default types here with import statements

interface IPacketDataAidl {
    void OnSensorDataChanged(in SensorPacketDataObject object);
    void OnTouchPadActonEvent(in int[] values);
}
