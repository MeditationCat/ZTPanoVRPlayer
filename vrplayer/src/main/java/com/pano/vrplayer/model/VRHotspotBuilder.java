package com.pano.vrplayer.model;

import android.util.SparseArray;

import com.pano.vrplayer.VRLibrary;
import com.pano.vrplayer.texture.VR360BitmapTexture;
import com.pano.vrplayer.texture.VR360Texture;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRHotspotBuilder {

    public float width = 2;

    public float height = 2;

    public String title;

    public VRLibrary.ITouchPickListener clickListener;

    public VRPosition position;

    public SparseArray<VR360Texture> textures = new SparseArray<>(6);

    public int[] statusList;

    public int[] checkedStatusList;

    public static VRHotspotBuilder create(){
        return new VRHotspotBuilder();
    }

    private VRHotspotBuilder status(int normal, int focused, int pressed){
        statusList = new int[]{normal, focused, pressed};
        return this;
    }

    public VRHotspotBuilder status(int normal, int focused){
        return status(normal, focused, focused);
    }

    public VRHotspotBuilder status(int normal){
        return status(normal,normal);
    }

    private VRHotspotBuilder checkedStatus(int normal, int focused, int pressed){
        checkedStatusList = new int[]{normal, focused, pressed};
        return this;
    }

    public VRHotspotBuilder checkedStatus(int normal, int focused){
        return checkedStatus(normal, focused, focused);
    }

    public VRHotspotBuilder checkedStatus(int normal){
        return checkedStatus(normal, normal);
    }

    public VRHotspotBuilder title(String title){
        this.title = title;
        return this;
    }

    public VRHotspotBuilder size(float width, float height){
        this.width = width;
        this.height = height;
        return this;
    }

    public VRHotspotBuilder provider(VRLibrary.IBitmapProvider provider){
        provider(0,provider);
        return this;
    }

    public VRHotspotBuilder provider(int key, VRLibrary.IBitmapProvider provider){
        textures.append(key,new VR360BitmapTexture(provider));
        return this;
    }

    public VRHotspotBuilder position(VRPosition position) {
        this.position = position;
        return this;
    }

    public VRHotspotBuilder listenClick(VRLibrary.ITouchPickListener listener){
        this.clickListener = listener;
        return this;
    }
}
