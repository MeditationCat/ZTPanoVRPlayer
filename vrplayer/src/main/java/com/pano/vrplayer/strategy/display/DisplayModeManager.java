package com.pano.vrplayer.strategy.display;

import com.pano.vrplayer.VRLibrary;
import com.pano.vrplayer.model.BarrelDistortionConfig;
import com.pano.vrplayer.strategy.ModeManager;

/**
 * Created by taipp on 9/7/2016.
 */
public class DisplayModeManager extends ModeManager<AbsDisplayStrategy> implements IDisplayMode {

    public static int[] sModes = {VRLibrary.DISPLAY_MODE_NORMAL, VRLibrary.DISPLAY_MODE_GLASS};

    private boolean antiDistortionEnabled;
    private BarrelDistortionConfig barrelDistortionConfig;

    public DisplayModeManager(int mode) {
        super(mode);
    }

    @Override
    protected int[] getModes() {
        return sModes;
    }

    @Override
    protected AbsDisplayStrategy createStrategy(int mode) {
        switch (mode){
            case VRLibrary.DISPLAY_MODE_GLASS:
                return new GlassStrategy();
            case VRLibrary.DISPLAY_MODE_NORMAL:
            default:
                return new NormalStrategy();
        }
    }

    @Override
    public int getVisibleSize() {
        return getStrategy().getVisibleSize();
    }

    public void setAntiDistortionEnabled(boolean antiDistortionEnabled) {
        this.antiDistortionEnabled = antiDistortionEnabled;
    }

    public boolean isAntiDistortionEnabled() {
        return antiDistortionEnabled;
    }

    public void setBarrelDistortionConfig(BarrelDistortionConfig barrelDistortionConfig) {
        this.barrelDistortionConfig = barrelDistortionConfig;
    }

    public BarrelDistortionConfig getBarrelDistortionConfig() {
        return barrelDistortionConfig;
    }
}
