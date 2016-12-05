package com.pano.vrplayer.plugins;

import android.content.Context;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.util.SparseArray;

import com.pano.vrplayer.VR360Director;
import com.pano.vrplayer.VR360Program;
import com.pano.vrplayer.VRLibrary;
import com.pano.vrplayer.common.VRUtil;
import com.pano.vrplayer.model.VRHotspotBuilder;
import com.pano.vrplayer.model.VRPosition;
import com.pano.vrplayer.model.VRRay;
import com.pano.vrplayer.model.VRVector3D;
import com.pano.vrplayer.objects.VRAbsObject3D;
import com.pano.vrplayer.objects.VRObject3DHelper;
import com.pano.vrplayer.objects.VRPlane;
import com.pano.vrplayer.texture.VR360Texture;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import static com.pano.vrplayer.common.GLUtil.glCheck;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRHotspotPlugin extends VRAbsPlugin implements IVRHotspot {

    private static final String TAG = "MDSimplePlugin";

    private VRLibrary.ITouchPickListener clickListener;

    private VRAbsObject3D object3D;

    private VR360Program program;

    private SparseArray<VR360Texture> textures;

    private RectF size;

    private String title;

    private int mCurrentTextureKey = 0;

    public VRHotspotPlugin(VRHotspotBuilder builder) {
        textures = builder.textures;
        size = new RectF(0, 0, builder.width, builder.height);
        clickListener = builder.clickListener;
        setTitle(builder.title);
        setModelPosition(builder.position == null ? VRPosition.sOriginalPosition : builder.position);
    }

    @Override
    public void init(Context context) {

        program = new VR360Program(VRLibrary.ContentType.BITMAP);
        program.build(context);

        for (int i = 0; i < textures.size(); i++) {
            textures.valueAt(i).create();
        }

        object3D = new VRPlane(size);
        VRObject3DHelper.loadObj(context,object3D);

    }

    @Override
    public void beforeRenderer(int totalWidth, int totalHeight) {

    }

    @Override
    public void renderer(int index, int width, int height, VR360Director director) {

        VR360Texture texture = textures.get(mCurrentTextureKey);
        if (texture == null) return;

        texture.texture(program);
        if (texture.isReady()){
            // Update Projection
            director.updateViewport(width, height);

            // Set our per-vertex lighting program.
            program.use();
            glCheck("MDSimplePlugin mProgram use");

            object3D.uploadVerticesBufferIfNeed(program, index);

            object3D.uploadTexCoordinateBufferIfNeed(program, index);

            // Pass in the combined matrix.
            director.shot(program, getModelPosition());

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


            object3D.draw();
            GLES20.glDisable(GLES20.GL_BLEND);
        }

    }

    @Override
    public void destroy() {

    }

    @Override
    protected boolean removable() {
        return true;
    }

    @Override
    public boolean hit(VRRay ray) {
        if (object3D == null || object3D.getVerticesBuffer(0) == null){
            return false;
        }

        VRPosition position = getModelPosition();
        float[] model = position.getMatrix();

        List<VRVector3D> points = new LinkedList<>();

        FloatBuffer buffer = object3D.getVerticesBuffer(0);
        int numPoints = buffer.capacity() / 3;

        for (int i = 0; i < numPoints; i++){
            VRVector3D v = new VRVector3D();
            v.setX(buffer.get(i * 3)).setY(buffer.get(i * 3 + 1)).setZ(buffer.get(i * 3 + 2));
            v.multiplyMV(model);
            points.add(v);
        }

        boolean hit = false;
        if (points.size() == 4){
            hit = VRUtil.intersectTriangle(ray, points.get(0), points.get(1), points.get(2));
            hit |= VRUtil.intersectTriangle(ray,points.get(1), points.get(2), points.get(3));
        }

        // Log.d(TAG,"Ray:" + ray);
        // Log.e(TAG,"MDSimplePlugin hit:" + hit);

        return hit;
    }

    @Override
    public void onEyeHitIn(long timestamp) {

    }

    @Override
    public void onEyeHitOut() {

    }

    @Override
    public void onTouchHit(VRRay ray) {
        if (clickListener != null){
            clickListener.onHotspotHit(this, ray);
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void useTexture(int key) {
        mCurrentTextureKey = key;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
