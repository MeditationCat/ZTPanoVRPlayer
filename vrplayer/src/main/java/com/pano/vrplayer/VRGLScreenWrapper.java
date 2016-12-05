package com.pano.vrplayer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.google.android.apps.muzei.render.GLTextureView;

/**
 * Created by taipp on 9/7/2016.
 */
public abstract class VRGLScreenWrapper {
    abstract public View getView();
    abstract public void setRenderer(GLSurfaceView.Renderer renderer);
    abstract public void init(Context context);
    abstract public void onResume();
    abstract public void onPause();

    public static VRGLScreenWrapper wrap(GLSurfaceView glSurfaceView){
        return new VRGLSurfaceViewImpl(glSurfaceView);
    }

    public static VRGLScreenWrapper wrap(GLTextureView glTextureView){
        return new VRGLTextureViewImpl(glTextureView);
    }

    private static class VRGLTextureViewImpl extends VRGLScreenWrapper {

        GLTextureView glTextureView;

        public VRGLTextureViewImpl(GLTextureView glTextureView) {
            this.glTextureView = glTextureView;
        }

        @Override
        public View getView() {
            return glTextureView;
        }

        @Override
        public void setRenderer(GLSurfaceView.Renderer renderer) {
            glTextureView.setRenderer(renderer);
        }

        @Override
        public void init(Context context) {
            glTextureView.setEGLContextClientVersion(2);
            glTextureView.setPreserveEGLContextOnPause(true);
        }

        @Override
        public void onResume() {
            glTextureView.onResume();
        }

        @Override
        public void onPause() {
            glTextureView.onPause();
        }
    }

    private static class VRGLSurfaceViewImpl extends VRGLScreenWrapper {

        GLSurfaceView glSurfaceView;

        private VRGLSurfaceViewImpl(GLSurfaceView glSurfaceView) {
            this.glSurfaceView = glSurfaceView;
        }

        @Override
        public View getView() {
            return glSurfaceView;
        }

        @Override
        public void setRenderer(GLSurfaceView.Renderer renderer) {
            glSurfaceView.setRenderer(renderer);
        }

        @Override
        public void init(Context context) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setPreserveEGLContextOnPause(true);
        }

        @Override
        public void onResume() {
            glSurfaceView.onResume();
        }

        @Override
        public void onPause() {
            glSurfaceView.onPause();
        }
    }
}
