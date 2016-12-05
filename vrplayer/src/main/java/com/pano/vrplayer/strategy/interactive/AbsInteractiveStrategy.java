package com.pano.vrplayer.strategy.interactive;

import com.pano.vrplayer.VR360Director;
import com.pano.vrplayer.strategy.IModeStrategy;

import java.util.List;

/**
 * Created by taipp on 9/7/2016.
 */
public abstract class AbsInteractiveStrategy implements IModeStrategy, IInteractiveMode {

    private InteractiveModeManager.Params params;

    public AbsInteractiveStrategy(InteractiveModeManager.Params params) {
        this.params = params;
    }

    public InteractiveModeManager.Params getParams() {
        return params;
    }

    protected List<VR360Director> getDirectorList() {
        return params.projectionModeManager.getDirectors();
    }
}
