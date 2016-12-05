package com.pano.vrplayer.texture;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.pano.vrplayer.VR360Program;
import com.pano.vrplayer.VRLibrary;
import com.pano.vrplayer.common.VRHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.pano.vrplayer.common.GLUtil.glCheck;
import static com.pano.vrplayer.common.VRUtil.notNull;

/**
 * Created by taipp on 9/7/2016.
 */
public class VR360BitmapTexture extends VR360Texture {

    private static final String TAG = "VR360BitmapTexture";
    private VRLibrary.IBitmapProvider mBitmapProvider;
    private Map<String,AsyncCallback> mCallbackList = new HashMap<>();
    private boolean mIsReady;

    public VR360BitmapTexture(VRLibrary.IBitmapProvider bitmapProvider) {
        this.mBitmapProvider = bitmapProvider;
    }

    @Override
    protected int createTextureId() {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        final int textureId = textureHandle[0];

        final AsyncCallback callback = new AsyncCallback();

        // save to thread local
        mCallbackList.put(Thread.currentThread().toString(),callback);

        // call the provider
        // to load the bitmap.
        VRHandler.sharedHandler().post(new Runnable() {
            @Override
            public void run() {
                mBitmapProvider.onProvideBitmap(callback);
            }
        });

        return textureId;
    }

    @Override
    public boolean texture(VR360Program program) {
        AsyncCallback asyncCallback = mCallbackList.get(Thread.currentThread().toString());
        int textureId = getCurrentTextureId();
        if (asyncCallback != null && asyncCallback.hasBitmap()){
            Bitmap bitmap = asyncCallback.getBitmap();
            textureInThread(textureId,program,bitmap);
            asyncCallback.releaseBitmap();
            mIsReady = true;
        }

        if (isReady() && textureId != 0){
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(program.getTextureUniformHandle(),0);
        }
        return true;
    }

    @Override
    public boolean isReady() {
        return mIsReady;
    }

    @Override
    public void destroy() {
        Collection<AsyncCallback> callbacks = mCallbackList.values();
        for (AsyncCallback callback:callbacks){
            callback.releaseBitmap();
        }
        mCallbackList.clear();
    }

    @Override
    public void release() {
    }

    private void textureInThread(int textureId, VR360Program program, Bitmap bitmap) {
        notNull(bitmap,"bitmap can't be null!");

        if (isEmpty(textureId)) return;

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        glCheck("VR360BitmapTexture glActiveTexture");

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        glCheck("VR360BitmapTexture glBindTexture");

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        glCheck("VR360BitmapTexture texImage2D");

        GLES20.glUniform1i(program.getTextureUniformHandle(),0);
        glCheck("VR360BitmapTexture textureInThread");
    }

    private static class AsyncCallback implements Callback{
        private Bitmap bitmap;

        @Override
        public void texture(Bitmap bitmap) {
            this.bitmap = bitmap.copy(bitmap.getConfig(),true);
        }

        public Bitmap getBitmap(){
            return bitmap;
        }

        public boolean hasBitmap(){
            return bitmap != null;
        }

        synchronized public void releaseBitmap(){
            if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
            bitmap = null;
        }
    }

    public interface Callback {
        void texture(Bitmap bitmap);
    }
}
