package com.pano.vrplayer.texture;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.view.Surface;

import com.pano.vrplayer.VR360Program;
import com.pano.vrplayer.VRLibrary;

import javax.microedition.khronos.opengles.GL10;

import static com.pano.vrplayer.common.GLUtil.glCheck;

/**
 * Created by taipp on 9/7/2016.
 */
public class VR360VideoTexture extends VR360Texture {

    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private VRLibrary.IOnSurfaceReadyCallback mOnSurfaceReadyListener;

    public VR360VideoTexture(VRLibrary.IOnSurfaceReadyCallback onSurfaceReadyListener) {
        mOnSurfaceReadyListener = onSurfaceReadyListener;
    }

    @Override
    public void release() {
        mOnSurfaceReadyListener = null;
    }

    @Override
    public void create() {
        super.create();
        int glSurfaceTexture = getCurrentTextureId();
        if (isEmpty(glSurfaceTexture)) return;

        onCreateSurface(glSurfaceTexture);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void destroy() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }
        mSurfaceTexture = null;

        if (mSurface != null) {
            mSurface.release();
        }
        mSurface = null;
    }

    private void onCreateSurface(int glSurfaceTextureId) {
        if ( mSurfaceTexture == null ) {
            //attach the texture to a surface.
            //It's a clue class for rendering an android view to gl level
            mSurfaceTexture = new SurfaceTexture(glSurfaceTextureId);
            // mSurfaceTexture.setDefaultBufferSize(getWidth(), getHeight());
            mSurface = new Surface(mSurfaceTexture);
            if (mOnSurfaceReadyListener != null)
                mOnSurfaceReadyListener.onSurfaceReady(mSurface);
        }
    }

    @Override
    protected int createTextureId() {
        int[] textures = new int[1];

        // Generate the texture to where android view will be rendered
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(1, textures, 0);
        glCheck("Texture generate");

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        glCheck("Texture bind");

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return textures[0];
    }

    @Override
    public boolean texture(VR360Program program) {
        int glSurfaceTexture = getCurrentTextureId();
        if (isEmpty(glSurfaceTexture)) return false;
        if (mSurfaceTexture == null) return false;

        mSurfaceTexture.updateTexImage();
        return true;
    }
}
