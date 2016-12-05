package com.pano.vrplayer.strategy.projection;

import android.content.Context;

import com.pano.vrplayer.VR360DirectorFactory;
import com.pano.vrplayer.model.VRMainPluginBuilder;
import com.pano.vrplayer.plugins.VRAbsPlugin;
import com.pano.vrplayer.strategy.IModeStrategy;

/**
 * Created by taipp on 9/7/2016.
 */
public abstract class AbsProjectionStrategy implements IModeStrategy, IProjectionMode {

    @Override
    public void onResume(Context context) {

    }

    @Override
    public void onPause(Context context) {

    }

    protected VR360DirectorFactory hijackDirectorFactory(){ return null; }

    abstract VRAbsPlugin buildMainPlugin(VRMainPluginBuilder builder);
}
