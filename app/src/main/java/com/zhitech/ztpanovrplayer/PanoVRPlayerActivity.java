package com.zhitech.ztpanovrplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.pano.vrplayer.VRLibrary;
import com.pano.vrplayer.model.VRHotspotBuilder;
import com.pano.vrplayer.model.VRPosition;
import com.pano.vrplayer.model.VRRay;
import com.pano.vrplayer.plugins.IVRHotspot;
import com.pano.vrplayer.plugins.VRAbsPlugin;
import com.pano.vrplayer.plugins.VRHotspotPlugin;
import com.pano.vrplayer.plugins.VRWidgetPlugin;
import com.pano.vrplayer.texture.VR360BitmapTexture;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by taipp on 9/7/2016.
 */
public abstract class PanoVRPlayerActivity extends Activity {

    private static final String TAG = "PanoVRPlayerActivity";

    private static final SparseArray<String> sDisplayMode = new SparseArray<>();
    private static final SparseArray<String> sInteractiveMode = new SparseArray<>();
    private static final SparseArray<String> sProjectionMode = new SparseArray<>();
    private static final SparseArray<String> sAntiDistortion = new SparseArray<>();

    static {
        sDisplayMode.put(VRLibrary.DISPLAY_MODE_NORMAL,"NORMAL");
        sDisplayMode.put(VRLibrary.DISPLAY_MODE_GLASS,"GLASS");

        sInteractiveMode.put(VRLibrary.INTERACTIVE_MODE_MOTION,"MOTION");
        sInteractiveMode.put(VRLibrary.INTERACTIVE_MODE_TOUCH,"TOUCH");
        sInteractiveMode.put(VRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH,"M & T");

        sProjectionMode.put(VRLibrary.PROJECTION_MODE_SPHERE,"SPHERE");
        sProjectionMode.put(VRLibrary.PROJECTION_MODE_DOME180,"DOME 180");
        sProjectionMode.put(VRLibrary.PROJECTION_MODE_DOME230,"DOME 230");
        sProjectionMode.put(VRLibrary.PROJECTION_MODE_DOME180_UPPER,"DOME 180 UPPER");
        sProjectionMode.put(VRLibrary.PROJECTION_MODE_DOME230_UPPER,"DOME 230 UPPER");
        sProjectionMode.put(VRLibrary.PROJECTION_MODE_STEREO_SPHERE,"STEREO SPHERE");
        sProjectionMode.put(VRLibrary.PROJECTION_MODE_PLANE_FIT,"PLANE FIT");
        sProjectionMode.put(VRLibrary.PROJECTION_MODE_PLANE_CROP,"PLANE CROP");
        sProjectionMode.put(VRLibrary.PROJECTION_MODE_PLANE_FULL,"PLANE FULL");
        sProjectionMode.put(VRLibrary.PROJECTION_MODE_MULTI_FISH_EYE_HORIZONTAL,"MULTI FISH EYE HORIZONTAL");
        sProjectionMode.put(VRLibrary.PROJECTION_MODE_MULTI_FISH_EYE_VERTICAL,"MULTI FISH EYE VERTICAL");
        sProjectionMode.put(CustomProjectionFactory.CUSTOM_PROJECTION_FISH_EYE_RADIUS_VERTICAL,"CUSTOM MULTI FISH EYE");

        sAntiDistortion.put(1,"ANTI-ENABLE");
        sAntiDistortion.put(0,"ANTI-DISABLE");
    }

    public static void startVideo(Context context, Uri uri){
        start(context, uri, VideoPlayerActivity.class);
    }

    public static void startBitmap(Context context, Uri uri){
        start(context, uri, BitmapPlayerActivity.class);
    }

    private static void start(Context context, Uri uri, Class<? extends Activity> clz){
        Intent i = new Intent(context,clz);
        i.setData(uri);
        context.startActivity(i);
    }

    private VRLibrary mVRLibrary;

    private List<VRAbsPlugin> plugins = new LinkedList<>();

    private VRPosition logoPosition = VRPosition.newInstance().setY(-8.0f).setYaw(-90.0f);

    private VRPosition[] positions = new VRPosition[]{
            VRPosition.newInstance().setZ(-8.0f).setYaw(-45.0f),
            VRPosition.newInstance().setZ(-18.0f).setYaw(15.0f).setAngleX(15),
            VRPosition.newInstance().setZ(-10.0f).setYaw(-10.0f).setAngleX(-15),
            VRPosition.newInstance().setZ(-10.0f).setYaw(30.0f).setAngleX(30),
            VRPosition.newInstance().setZ(-10.0f).setYaw(-30.0f).setAngleX(-30),
            VRPosition.newInstance().setZ(-5.0f).setYaw(30.0f).setAngleX(60),
            VRPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45),
            VRPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45).setAngleY(45),
            VRPosition.newInstance().setZ(-3.0f).setYaw(0.0f).setAngleX(90),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set content view
        setContentView(R.layout.activity_md_using_surface_view);
        // init VR Library
        mVRLibrary = createVRLibrary();

        // hide setting components
        findViewById(R.id.hotspot_point1).setVisibility(View.GONE);
        findViewById(R.id.hotspot_point2).setVisibility(View.GONE);
        findViewById(R.id.spinner_layout).setVisibility(View.GONE);
        findViewById(R.id.button_layout).setVisibility(View.GONE);
        findViewById(R.id.hotspot_text).setVisibility(View.GONE);

        final List<View> hotspotPoints = new LinkedList<>();
        hotspotPoints.add(findViewById(R.id.hotspot_point1));
        hotspotPoints.add(findViewById(R.id.hotspot_point2));
        SpinnerHelper.with(this)
                .setData(sDisplayMode)
                .setDefault(mVRLibrary.getDisplayMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchDisplayMode(PanoVRPlayerActivity.this, key);
                        int i = 0;
                        for (View point : hotspotPoints){
                            point.setVisibility(i < mVRLibrary.getScreenSize() ? View.VISIBLE : View.GONE);
                            i++;
                        }
                    }
                })
                .init(R.id.spinner_display);

        SpinnerHelper.with(this)
                .setData(sInteractiveMode)
                .setDefault(mVRLibrary.getInteractiveMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchInteractiveMode(PanoVRPlayerActivity.this, key);
                    }
                })
                .init(R.id.spinner_interactive);

        SpinnerHelper.with(this)
                .setData(sProjectionMode)
                .setDefault(mVRLibrary.getProjectionMode())
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.switchProjectionMode(PanoVRPlayerActivity.this, key);
                    }
                })
                .init(R.id.spinner_projection);

        SpinnerHelper.with(this)
                .setData(sAntiDistortion)
                .setDefault(mVRLibrary.isAntiDistortionEnabled() ? 1 : 0)
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        mVRLibrary.setAntiDistortionEnabled(key != 0);
                    }
                })
                .init(R.id.spinner_distortion);

        findViewById(R.id.button_add_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int index = (int) (Math.random() * 100) % positions.length;
                VRPosition position = positions[index];
                VRHotspotBuilder builder = VRHotspotBuilder.create()
                        .size(4f,4f)
                        .provider(0, new AndroidDrawableProvider(android.R.drawable.star_off))
                        .provider(1, new AndroidDrawableProvider(android.R.drawable.star_on))
                        .provider(10, new AndroidDrawableProvider(android.R.drawable.checkbox_off_background))
                        .provider(11, new AndroidDrawableProvider(android.R.drawable.checkbox_on_background))
                        .listenClick(new VRLibrary.ITouchPickListener() {
                            @Override
                            public void onHotspotHit(IVRHotspot hitHotspot, VRRay ray) {
                                if (hitHotspot instanceof VRWidgetPlugin){
                                    VRWidgetPlugin widgetPlugin = (VRWidgetPlugin) hitHotspot;
                                    widgetPlugin.setChecked(!widgetPlugin.getChecked());
                                }
                            }
                        })
                        .title("star" + index)
                        .position(position)
                        .status(0,1)
                        .checkedStatus(10,11);

                VRWidgetPlugin plugin = new VRWidgetPlugin(builder);

                plugins.add(plugin);
                getVRLibrary().addPlugin(plugin);
                Toast.makeText(PanoVRPlayerActivity.this, "add plugin position:" + position, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_add_plugin_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VRHotspotBuilder builder = VRHotspotBuilder.create()
                        .size(4f,4f)
                        .provider(new VRLibrary.IBitmapProvider() {
                            @Override
                            public void onProvideBitmap(VR360BitmapTexture.Callback callback) {
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.custom_logo);
                                callback.texture(bitmap);
                            }
                        })
                        .title("logo")
                        .position(logoPosition)
                        .listenClick(new VRLibrary.ITouchPickListener() {
                            @Override
                            public void onHotspotHit(IVRHotspot hitHotspot, VRRay ray) {
                                Toast.makeText(PanoVRPlayerActivity.this, "click logo", Toast.LENGTH_SHORT).show();
                            }
                        });
                VRHotspotPlugin plugin = new VRHotspotPlugin(builder);
                plugins.add(plugin);
                getVRLibrary().addPlugin(plugin);
                Toast.makeText(PanoVRPlayerActivity.this, "add plugin logo" , Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_remove_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plugins.size() > 0){
                    VRAbsPlugin plugin = plugins.remove(plugins.size() - 1);
                    getVRLibrary().removePlugin(plugin);
                }
            }
        });

        findViewById(R.id.button_remove_plugins).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plugins.clear();
                getVRLibrary().removePlugins();
            }
        });

        final TextView hotspotText = (TextView) findViewById(R.id.hotspot_text);
        getVRLibrary().setEyePickChangedListener(new VRLibrary.IEyePickListener() {
            @Override
            public void onHotspotHit(IVRHotspot hotspot, long hitTimestamp) {
                String text = hotspot == null ? "nop" : String.format(Locale.CHINESE, "%s  %fs", hotspot.getTitle(), (System.currentTimeMillis() - hitTimestamp) / 1000.0f );
                hotspotText.setText(text);

                if (System.currentTimeMillis() - hitTimestamp > 5000){
                    getVRLibrary().resetEyePick();
                }
            }
        });
    }

    abstract protected VRLibrary createVRLibrary();

    public VRLibrary getVRLibrary() {
        return mVRLibrary;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVRLibrary.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVRLibrary.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVRLibrary.onDestroy();
    }

    protected Uri getUri() {
        Intent i = getIntent();
        if (i == null || i.getData() == null){
            return null;
        }
        return i.getData();
    }

    public void cancelBusy(){
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    private class AndroidDrawableProvider implements VRLibrary.IBitmapProvider{

        private int res;

        public AndroidDrawableProvider(int res) {
            this.res = res;
        }

        @Override
        public void onProvideBitmap(VR360BitmapTexture.Callback callback) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), this.res);
            callback.texture(bitmap);
        }
    }

}
