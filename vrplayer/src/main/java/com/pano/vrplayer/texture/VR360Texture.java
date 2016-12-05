package com.pano.vrplayer.texture;

import com.pano.vrplayer.VR360Program;

/**
 * Created by taipp on 9/7/2016.
 *
 * copied from surfaceTexture
 * Created by nitro888 on 15. 4. 5..
 * https://github.com/Nitro888/NitroAction360
 */
public abstract class VR360Texture {
    private static final int TEXTURE_EMPTY = 0;
    private static final String TAG = "VR360Texture";
    private int mTextureId = TEXTURE_EMPTY;
    public VR360Texture() {
    }

    // may called from multi thread
    public void create() {
        int glTexture = createTextureId();

        if (glTexture != TEXTURE_EMPTY){
            mTextureId = glTexture;
        }
    }

    abstract public boolean isReady();

    abstract public void destroy();

    abstract public void release();

    public int getCurrentTextureId(){
        return mTextureId;
    }

    final protected boolean isEmpty(int textureId){
        return textureId == TEXTURE_EMPTY;
    }

    abstract protected int createTextureId();

    abstract public boolean texture(VR360Program program);
}
