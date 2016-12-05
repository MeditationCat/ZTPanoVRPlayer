package com.pano.vrplayer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.pano.vrplayer.common.Fps;
import com.pano.vrplayer.plugins.VRAbsLinePipe;
import com.pano.vrplayer.plugins.VRAbsPlugin;
import com.pano.vrplayer.plugins.VRBarrelDistortionLinePipe;
import com.pano.vrplayer.plugins.VRPluginManager;
import com.pano.vrplayer.strategy.display.DisplayModeManager;
import com.pano.vrplayer.strategy.projection.ProjectionModeManager;

import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.pano.vrplayer.common.GLUtil.glCheck;

/**
 * Created by taipp on 9/7/2016.
 *
 * @see Builder
 * @see #with(Context)
 */
public class VR360Renderer implements GLSurfaceView.Renderer {

	private static final String TAG = "VR360Renderer";
	private DisplayModeManager mDisplayModeManager;
	private ProjectionModeManager mProjectionModeManager;
	private VRPluginManager mPluginManager;
	private VRAbsLinePipe mMainLinePipe;
	private Fps mFps = new Fps();
	private int mWidth;
	private int mHeight;

	// private VRBarrelDistortionPlugin mBarrelDistortionPlugin;

	// final
	private final Context mContext;

	private VR360Renderer(Builder params){
		mContext = params.context;
		mDisplayModeManager = params.displayModeManager;
		mProjectionModeManager = params.projectionModeManager;
		mPluginManager = params.pluginManager;

		mMainLinePipe = new VRBarrelDistortionLinePipe(mDisplayModeManager);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config){
		// set the background clear color to black.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		// enable depth testing
		// GLES20.glEnable(GLES20.GL_DEPTH_TEST);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height){
		this.mWidth = width;
		this.mHeight = height;
	}

	@Override
	public void onDrawFrame(GL10 glUnused){

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		glCheck("VR360Renderer onDrawFrame 1");

		int size = mDisplayModeManager.getVisibleSize();

		int width = (int) (this.mWidth * 1.0f / size);
		int height = mHeight;

		// take over
		mMainLinePipe.setup(mContext);
		mMainLinePipe.takeOver(mWidth,mHeight,size);

		List<VR360Director> directors = mProjectionModeManager.getDirectors();

		// main plugin
		VRAbsPlugin mainPlugin = mProjectionModeManager.getMainPlugin();
		if (mainPlugin != null){
			mainPlugin.setup(mContext);
			mainPlugin.beforeRenderer(this.mWidth, this.mHeight);
		}

		for (VRAbsPlugin plugin : mPluginManager.getPlugins()) {
			plugin.setup(mContext);
			plugin.beforeRenderer(this.mWidth, this.mHeight);
		}

		for (int i = 0; i < size; i++){
			if (i >= directors.size()) break;

			VR360Director director = directors.get(i);
			GLES20.glViewport(width * i, 0, width, height);
			GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
			GLES20.glScissor(width * i, 0, width, height);

			if (mainPlugin != null){
				mainPlugin.renderer(i, width, height, director);
			}

			for (VRAbsPlugin plugin : mPluginManager.getPlugins()) {
				plugin.renderer(i, width, height, director);
			}

			GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
		}

		mMainLinePipe.commit(mWidth,mHeight,size);
		// mFps.step();
	}

	public static Builder with(Context context) {
		Builder builder = new Builder();
		builder.context = context;
		return builder;
	}

	public static class Builder{
		private Context context;
		private DisplayModeManager displayModeManager;
		private ProjectionModeManager projectionModeManager;
		public VRPluginManager pluginManager;

		private Builder() {
		}

		public VR360Renderer build(){
			return new VR360Renderer(this);
		}

		public Builder setPluginManager(VRPluginManager pluginManager) {
			this.pluginManager = pluginManager;
			return this;
		}

		public Builder setDisplayModeManager(DisplayModeManager displayModeManager) {
			this.displayModeManager = displayModeManager;
			return this;
		}

		public Builder setProjectionModeManager(ProjectionModeManager projectionModeManager) {
			this.projectionModeManager = projectionModeManager;
			return this;
		}
	}
}
