package com.pano.vrplayer.plugins;

import android.content.Context;

import com.pano.vrplayer.VR360Director;
import com.pano.vrplayer.VR360Program;
import com.pano.vrplayer.model.VRMainPluginBuilder;
import com.pano.vrplayer.model.VRPosition;
import com.pano.vrplayer.objects.VRAbsObject3D;
import com.pano.vrplayer.strategy.projection.ProjectionModeManager;
import com.pano.vrplayer.texture.VR360Texture;

import static com.pano.vrplayer.common.GLUtil.glCheck;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRPanoramaPlugin extends VRAbsPlugin {

    private VR360Program mProgram;

    private VR360Texture mTexture;

    private ProjectionModeManager mProjectionModeManager;

    public VRPanoramaPlugin(VRMainPluginBuilder builder) {
        mTexture = builder.getTexture();
        mProgram = new VR360Program(builder.getContentType());
        mProjectionModeManager = builder.getProjectionModeManager();
    }

    @Override
    public void init(Context context) {
        mProgram.build(context);
        mTexture.create();
    }

    @Override
    public void beforeRenderer(int totalWidth, int totalHeight) {

    }

    @Override
    public void renderer(int index, int width, int height, VR360Director director) {

        VRAbsObject3D object3D = mProjectionModeManager.getObject3D();
        // check obj3d
        if (object3D == null) return;

        // Update Projection
        director.updateViewport(width, height);

        // Set our per-vertex lighting program.
        mProgram.use();
        glCheck("VRPanoramaPlugin mProgram use");

        mTexture.texture(mProgram);

        object3D.uploadVerticesBufferIfNeed(mProgram, index);

        object3D.uploadTexCoordinateBufferIfNeed(mProgram, index);

        // Pass in the combined matrix.
        director.shot(mProgram, getModelPosition());
        object3D.draw();

    }

    @Override
    public void destroy() {
        mTexture = null;
    }

    @Override
    protected VRPosition getModelPosition() {
        return mProjectionModeManager.getModelPosition();
    }

    @Override
    protected boolean removable() {
        return false;
    }

}
