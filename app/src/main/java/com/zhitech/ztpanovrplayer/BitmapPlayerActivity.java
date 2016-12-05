package com.zhitech.ztpanovrplayer;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.pano.vrplayer.VRLibrary;
import com.pano.vrplayer.model.BarrelDistortionConfig;
import com.pano.vrplayer.model.VRRay;
import com.pano.vrplayer.plugins.IVRHotspot;
import com.pano.vrplayer.texture.VR360BitmapTexture;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * Created by taipp on 9/7/2016.
 */
public class BitmapPlayerActivity extends PanoVRPlayerActivity {

    private static final String TAG = "BitmapPlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cancelBusy();
    }

    private Target mTarget;// keep the reference for picasso.

    private void loadImage(Uri uri, final VR360BitmapTexture.Callback callback){
        mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // notify if size changed
                getVRLibrary().onTextureResize(bitmap.getWidth(),bitmap.getHeight());

                // texture
                callback.texture(bitmap);
                cancelBusy();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(getApplicationContext()).load(uri).resize(3072,2048).centerInside().memoryPolicy(NO_CACHE, NO_STORE).into(mTarget);
    }

    @Override
    protected VRLibrary createVRLibrary() {
        return VRLibrary.with(this)
                .displayMode(VRLibrary.DISPLAY_MODE_GLASS)
                .interactiveMode(VRLibrary.INTERACTIVE_MODE_MOTION)
                .asBitmap(new VRLibrary.IBitmapProvider() {
                    @Override
                    public void onProvideBitmap(final VR360BitmapTexture.Callback callback) {
                        loadImage(getUri(),callback);
                    }
                })
                .listenTouchPick(new VRLibrary.ITouchPickListener() {
                    @Override
                    public void onHotspotHit(IVRHotspot hitHotspot, VRRay ray) {
                        Log.d(TAG,"Ray:" + ray + ", hitHotspot:" + hitHotspot);
                    }
                })
                .pinchEnabled(true)
                .projectionFactory(new CustomProjectionFactory())
                .barrelDistortionConfig(new BarrelDistortionConfig().setDefaultEnabled(true).setScale(0.95f))
                .build(R.id.gl_view);
    }
}
