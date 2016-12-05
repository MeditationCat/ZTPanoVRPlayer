package com.pano.vrplayer.model;

import com.pano.vrplayer.VRLibrary;
import com.pano.vrplayer.strategy.projection.ProjectionModeManager;
import com.pano.vrplayer.texture.VR360Texture;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRMainPluginBuilder {
    private VR360Texture texture;
    private int contentType = VRLibrary.ContentType.DEFAULT;
    private ProjectionModeManager projectionModeManager;

    public VRMainPluginBuilder() {
    }

    public VR360Texture getTexture() {
        return texture;
    }

    public int getContentType() {
        return contentType;
    }

    public ProjectionModeManager getProjectionModeManager() {
        return projectionModeManager;
    }


    public VRMainPluginBuilder setContentType(int contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * set surface{@link VR360Texture} to this render
     * @param texture {@link VR360Texture} surface may used by multiple render{@link com.pano.vrplayer.VR360Renderer}
     * @return builder
     */
    public VRMainPluginBuilder setTexture(VR360Texture texture){
        this.texture = texture;
        return this;
    }

    public VRMainPluginBuilder setProjectionModeManager(ProjectionModeManager projectionModeManager) {
        this.projectionModeManager = projectionModeManager;
        return this;
    }
}
