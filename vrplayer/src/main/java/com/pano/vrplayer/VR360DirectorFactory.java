package com.pano.vrplayer;

import android.opengl.Matrix;

/**
 * Created by taipp on 9/7/2016.
 */
public abstract class VR360DirectorFactory {
    abstract public VR360Director createDirector(int index);

    public static class DefaultImpl extends VR360DirectorFactory {
        @Override
        public VR360Director createDirector(int index) {
            switch (index){
                // case 1:   return VR360Director.builder().setEyeX(-2.0f).setLookX(-2.0f).build();
                default:  return VR360Director.builder().build();
            }
        }
    }

    public static class OrthogonalImpl extends VR360DirectorFactory {

        @Override
        public VR360Director createDirector(int index) {
            switch (index){
                default:  return new OrthogonalDirector(new VR360Director.Builder());
            }
        }
    }


    private static class OrthogonalDirector extends VR360Director {

        private OrthogonalDirector(Builder builder) {
            super(builder);
        }

        @Override
        public void setDeltaX(float mDeltaX) {
            // nop
        }

        @Override
        public void setDeltaY(float mDeltaY) {
            // nop
        }

        @Override
        public void updateSensorMatrix(float[] sensorMatrix) {
            // nop
        }

        @Override
        protected void updateProjection(){
            final float left = - 1f;
            final float right = 1f;
            final float bottom = - 1f;
            final float top = 1f;
            final float far = 500;
            Matrix.orthoM(getProjectionMatrix(), 0, left, right, bottom, top, getNear(), far);
        }
    }

}
