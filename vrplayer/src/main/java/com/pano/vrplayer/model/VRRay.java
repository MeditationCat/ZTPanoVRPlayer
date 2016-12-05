package com.pano.vrplayer.model;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRRay {
    private VRVector3D mOrig;
    private VRVector3D mDir;

    public VRRay(VRVector3D mOrig, VRVector3D mDir) {
        this.mOrig = mOrig;
        this.mDir = mDir;
    }

    public VRVector3D getOrig() {
        return mOrig;
    }

    public void setOrig(VRVector3D mOrig) {
        this.mOrig = mOrig;
    }

    public VRVector3D getDir() {
        return mDir;
    }

    public void setDir(VRVector3D mDir) {
        this.mDir = mDir;
    }

    @Override
    public String toString() {
        return "VRRay{" +
                ", mDir=" + mDir +
                ", mOrig=" + mOrig +
                '}';
    }
}
