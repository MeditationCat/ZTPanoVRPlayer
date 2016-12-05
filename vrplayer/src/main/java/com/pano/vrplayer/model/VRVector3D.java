package com.pano.vrplayer.model;

import android.opengl.Matrix;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRVector3D {

    private float[] values;

    public VRVector3D() {
        values = new float[4];
        values[3] = 1.0f;
    }

    public VRVector3D setX(float x) {
        values[0] = x;
        return this;
    }

    public VRVector3D setY(float y) {
        values[1] = y;
        return this;
    }

    public VRVector3D setZ(float z) {
        values[2] = z;
        return this;
    }

    public float getX(){
        return values[0];
    }

    public float getY(){
        return values[1];
    }

    public float getZ(){
        return values[2];
    }

    public void multiplyMV(float[] mat){
        Matrix.multiplyMV(values, 0, mat, 0, values, 0);
    }

    @Override
    public String toString() {
        return "VRVector3D{" +
                "x=" + getX() +
                ", y=" + getY() +
                ", z=" + getZ() +
                '}';
    }
}
