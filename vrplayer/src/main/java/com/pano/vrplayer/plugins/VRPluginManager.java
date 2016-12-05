package com.pano.vrplayer.plugins;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by taipp on 9/7/2016.
 */
public class VRPluginManager {

    private static final String TAG = "VRPluginManager";

    private List<VRAbsPlugin> mList;

    public VRPluginManager() {
        mList = new CopyOnWriteArrayList<>();
    }

    public void add(VRAbsPlugin plugin){
        mList.add(plugin);
    }

    public List<VRAbsPlugin> getPlugins() {
        return mList;
    }

    public void remove(VRAbsPlugin plugin) {
        if (plugin != null){
            mList.remove(plugin);
        }
    }

    public void removeAll() {
        Iterator<VRAbsPlugin> iterator = mList.iterator();
        while (iterator.hasNext()){
            VRAbsPlugin plugin = iterator.next();
            if (plugin.removable()){
                mList.remove(plugin);
            }
        }
    }
}
