package com.pano.vrplayer.common;

import android.graphics.PointF;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.view.Surface;

import com.pano.vrplayer.VR360Director;
import com.pano.vrplayer.model.VRRay;
import com.pano.vrplayer.model.VRVector3D;
import com.zhitech.zhilunvrsdk.Utils.Utils;

import java.util.Locale;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRUtil {

    private static final String TAG = "VRUtil";
    private static float[] mTmp = new float[16];

    public static void sensorRotationVector2Matrix(SensorEvent event, int rotation, float[] output) {
        float[] values = event.values;
        Utils.dLog(TAG, String.format(Locale.US, "sensorName:%s, accuracy:%d, timestamp:%d, " +
                "%n values[%d]{%f, %f, %f, %f, %f}, rotation:%d",
                event.sensor.getName(),
                event.accuracy,
                event.timestamp,
                values.length,
                values[0], values[1], values[2], values[3], values[4],
                rotation));
        switch (rotation){
            case Surface.ROTATION_0:
            case Surface.ROTATION_180: /* Notice: not supported for ROTATION_180! */
                SensorManager.getRotationMatrixFromVector(output, values);
                break;
            case Surface.ROTATION_90:
                SensorManager.getRotationMatrixFromVector(mTmp, values);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, output);
                break;
            case Surface.ROTATION_270:
                SensorManager.getRotationMatrixFromVector(mTmp, values);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, output);
                break;
        }
        Matrix.rotateM(output, 0, 90.0F, 1.0F, 0.0F, 0.0F);
    }
    // test 01
    public static void sensorRotation2Matrix(float[] gravity, float[] geomagnetic, int rotation, float[] output) {
        switch (rotation){
            case Surface.ROTATION_0:
            case Surface.ROTATION_180: /* Notice: not supported for ROTATION_180! */
                SensorManager.getRotationMatrix(output, null, gravity, geomagnetic);
                break;
            case Surface.ROTATION_90:
                SensorManager.getRotationMatrix(mTmp, null, gravity, geomagnetic);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, output);
                break;
            case Surface.ROTATION_270:
                SensorManager.getRotationMatrix(mTmp, null, gravity, geomagnetic);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, output);
                break;
        }
        Matrix.rotateM(output, 0, 90.0F, 1.0F, 0.0F, 0.0F);
    }

    // test 02
    public static void sensorRotationVector2Matrix(float[] rotationVector, int rotation, float[] output) {
        switch (rotation){
            case Surface.ROTATION_0:
            case Surface.ROTATION_180: /* Notice: not supported for ROTATION_180! */
                SensorManager.getRotationMatrixFromVector(output, rotationVector);
                break;
            case Surface.ROTATION_90:
                SensorManager.getRotationMatrixFromVector(mTmp, rotationVector);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, output);
                break;
            case Surface.ROTATION_270:
                SensorManager.getRotationMatrixFromVector(mTmp, rotationVector);
                SensorManager.remapCoordinateSystem(mTmp, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, output);
                break;
        }
        Matrix.rotateM(output, 0, 90.0F, 1.0F, 0.0F, 0.0F);
    }
    // test 03
    public static void sensorRotationVector2Matrix(float[] gravity, float[] geomagnetic, byte rotation, float[] output) {
        int axisX = 0;//SensorManager.AXIS_X;
        int axisY = 0;//SensorManager.AXIS_Y;

        //byte switchCase = 0x00;
        //switchCase = 0x20; //0x15;

        switch (rotation) {
            // X MINUS_X
            case 0x00:
                axisX =SensorManager.AXIS_X;
                axisY =SensorManager.AXIS_Y;
                break;
            case 0x01:
                axisX =SensorManager.AXIS_X;
                axisY =SensorManager.AXIS_MINUS_Y;
                break;
            case 0x02:
                axisX =SensorManager.AXIS_X;
                axisY =SensorManager.AXIS_Z;
                break;
            case 0x03:
                axisX =SensorManager.AXIS_X;
                axisY =SensorManager.AXIS_MINUS_Z;
                break;
            case 0x04:
                axisX =SensorManager.AXIS_MINUS_X;
                axisY =SensorManager.AXIS_Y;
                break;
            case 0x05:
                axisX =SensorManager.AXIS_MINUS_X;
                axisY =SensorManager.AXIS_MINUS_Y;
                break;
            case 0x06:
                axisX =SensorManager.AXIS_MINUS_X;
                axisY =SensorManager.AXIS_Z;
                break;
            case 0x07:
                axisX =SensorManager.AXIS_MINUS_X;
                axisY =SensorManager.AXIS_MINUS_Z;
                break;
            // Y MINUS_Y
            case 0x10:
                axisX =SensorManager.AXIS_Y;
                axisY =SensorManager.AXIS_X;
                break;
            case 0x11:
                axisX =SensorManager.AXIS_Y;
                axisY =SensorManager.AXIS_MINUS_X;
                break;
            case 0x12:
                axisX =SensorManager.AXIS_Y;
                axisY =SensorManager.AXIS_Z;
                break;
            case 0x13:
                axisX =SensorManager.AXIS_Y;
                axisY =SensorManager.AXIS_MINUS_Z;
                break;
            case 0x14:
                axisX =SensorManager.AXIS_MINUS_Y;
                axisY =SensorManager.AXIS_X;
                break;
            case 0x15:
                axisX =SensorManager.AXIS_MINUS_Y;
                axisY =SensorManager.AXIS_MINUS_X;
                break;
            case 0x16:
                axisX =SensorManager.AXIS_MINUS_Y;
                axisY =SensorManager.AXIS_Z;
                break;
            case 0x17:
                axisX =SensorManager.AXIS_MINUS_Y;
                axisY =SensorManager.AXIS_MINUS_Z;
                break;
            // Z MINUS_Z
            case 0x20:
                axisX =SensorManager.AXIS_Z;
                axisY =SensorManager.AXIS_X;
                break;
            case 0x21:
                axisX =SensorManager.AXIS_Z;
                axisY =SensorManager.AXIS_MINUS_X;
                break;
            case 0x22:
                axisX =SensorManager.AXIS_Z;
                axisY =SensorManager.AXIS_Y;
                break;
            case 0x23:
                axisX =SensorManager.AXIS_Z;
                axisY =SensorManager.AXIS_MINUS_Y;
                break;
            case 0x24:
                axisX =SensorManager.AXIS_MINUS_Z;
                axisY =SensorManager.AXIS_X;
                break;
            case 0x25:
                axisX =SensorManager.AXIS_MINUS_Z;
                axisY =SensorManager.AXIS_MINUS_X;
                break;
            case 0x26:
                axisX =SensorManager.AXIS_MINUS_Z;
                axisY =SensorManager.AXIS_Y;
                break;
            case 0x27:
                axisX =SensorManager.AXIS_MINUS_Z;
                axisY =SensorManager.AXIS_MINUS_Y;
                break;
        }
        SensorManager.getRotationMatrix(mTmp, null, gravity, geomagnetic);
        SensorManager.remapCoordinateSystem(mTmp, axisX, axisY, output);
        Matrix.rotateM(output, 0, 90.0F, 1.0F, 0.0F, 0.0F);
    }
    public static void notNull(Object object, String error){
        if (object == null) throw new RuntimeException(error);
    }

    public static void barrelDistortion(double paramA, double paramB, double paramC, PointF src){

        double paramD = 1.0 - paramA - paramB - paramC; // describes the linear scaling of the image

        float d = 1.0f;

        // center of dst image
        double centerX = 0f;
        double centerY = 0f;

        if (src.x == centerX && src.y == centerY){
            return;
        }

        // cartesian coordinates of the destination point (relative to the centre of the image)
        double deltaX = (src.x - centerX) / d;
        double deltaY = (src.y - centerY) / d;

        // distance or radius of dst image
        double dstR = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // distance or radius of src image (with formula)
        double srcR = (paramA * dstR * dstR * dstR + paramB * dstR * dstR + paramC * dstR + paramD) * dstR;

        // comparing old and new distance to get factor
        double factor = Math.abs(dstR / srcR);

        // coordinates in source image
        float xResult = (float) (centerX + (deltaX * factor * d));
        float yResult = (float) (centerY + (deltaY * factor * d));

        src.set(xResult,yResult);
    }

    public static VRVector3D vec3Sub(VRVector3D v1, VRVector3D v2){
        return new VRVector3D().setX(v1.getX() - v2.getX()).setY(v1.getY() - v2.getY()).setZ(v1.getZ() - v2.getZ());
    }

    public static VRVector3D vec3Cross(VRVector3D v1, VRVector3D v2){
        return new VRVector3D().setX(v1.getY() * v2.getZ() - v2.getY() * v1.getZ())
                .setY(v1.getZ() * v2.getX() - v2.getZ() * v1.getX())
                .setZ(v1.getX() * v2.getY() - v2.getX() * v1.getY());
    }

    public static float vec3Dot(VRVector3D v1, VRVector3D v2){
        return v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ();
    }

    public static VRRay point2Ray(float x, float y, VR360Director director){
        VRVector3D v = new VRVector3D();
        float[] projection = director.getProjectionMatrix();
        v.setX(- ( ( ( 2.0f * x ) / director.getViewportWidth() ) - 1 ) / projection[0]);
        v.setY(( ( ( 2.0f * y ) / director.getViewportHeight() ) - 1 ) / projection[5]);
        v.setZ(1.0f);

        float[] view = director.getViewMatrix();
        float[] temp = new float[16];
        boolean success = Matrix.invertM(temp,0,view,0);
        if (success){
            VRVector3D vPickRayDir = new VRVector3D();
            VRVector3D vPickRayOrig = new VRVector3D();

            vPickRayDir.setX(v.getX() * temp[0] + v.getY() * temp[4] + v.getZ() * temp[8]);
            vPickRayDir.setY(v.getX() * temp[1] + v.getY() * temp[5] + v.getZ() * temp[9]);
            vPickRayDir.setZ(v.getX() * temp[2] + v.getY() * temp[6] + v.getZ() * temp[10]);
            vPickRayOrig.setX(temp[12]);
            vPickRayOrig.setY(temp[13]);
            vPickRayOrig.setZ(temp[14]);
            return new VRRay(vPickRayOrig,vPickRayDir);
        } else {
            return null;
        }
    }

    public static boolean intersectTriangle(VRRay ray, VRVector3D v0, VRVector3D v1, VRVector3D v2){
        // Find vectors for two edges sharing vert0
        VRVector3D edge1 = vec3Sub(v1 , v0);
        VRVector3D edge2 = vec3Sub(v2 , v0);

        // Begin calculating determinant - also used to calculate U parameter
        VRVector3D pvec;
        pvec = vec3Cross( ray.getDir(), edge2 );

        // If determinant is near zero, ray lies in plane of triangle
        float det = vec3Dot( edge1, pvec );

        VRVector3D tvec;
        if( det > 0 ) {
            tvec = vec3Sub(ray.getOrig() , v0);
        } else {
            tvec = vec3Sub(v0 , ray.getOrig());
            det = -det;
        }

        if( det < 0.0001f )
            return false;

        // Calculate U parameter and test bounds
        float u = vec3Dot( tvec, pvec );
        if( u < 0.0f || u > det ){
            return false;
        }

        // Prepare to test V parameter
        VRVector3D qvec;
        qvec = vec3Cross(tvec, edge1);

        // Calculate V parameter and test bounds
        float v = vec3Dot(ray.getDir(), qvec);
        if( v < 0.0f || u + v > det ){
            return false;
        }

        // Calculate t, scale parameters, ray intersects triangle
        float t = vec3Dot(edge2, qvec);
        float fInvDet = 1.0f / det;
        t *= fInvDet;
        u *= fInvDet;
        v *= fInvDet;

        return true;
    }

}
