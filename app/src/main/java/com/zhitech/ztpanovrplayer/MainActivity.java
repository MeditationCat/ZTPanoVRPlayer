package com.zhitech.ztpanovrplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zhitech.zhilunvrsdk.ZhilunVrService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //start service
        this.startService(new Intent(this, ZhilunVrService.class));
        // hide the view
        TextView textViewUrl, textViewAction;
        EditText editTextUrl;
        Spinner spinner;
        Button ijkButton;
        textViewUrl = (TextView) findViewById(R.id.textViewUrl);
        textViewUrl.setVisibility(View.GONE);
        textViewAction = (TextView) findViewById(R.id.textViewActoin);
        textViewAction.setVisibility(View.GONE);
        editTextUrl = (EditText) findViewById(R.id.edit_text_url);
        editTextUrl.setVisibility(View.GONE);
        spinner = (Spinner) findViewById(R.id.spinner_url);
        spinner.setVisibility(View.GONE);
        ijkButton = (Button) findViewById(R.id.ijk_button);
        ijkButton.setVisibility(View.GONE);

        /*EditText et = (EditText) findViewById(R.id.edit_text_url);

        SparseArray<String> data = new SparseArray<>();

        data.put(data.size(), getDrawableUri(R.drawable.bitmap360).toString());
        data.put(data.size(), getDrawableUri(R.drawable.texture).toString());
        data.put(data.size(), getDrawableUri(R.drawable.dome_pic).toString());
        data.put(data.size(), getDrawableUri(R.drawable.stereo).toString());
        data.put(data.size(), getDrawableUri(R.drawable.multifisheye).toString());
        data.put(data.size(), getDrawableUri(R.drawable.multifisheye2).toString());
        data.put(data.size(), getDrawableUri(R.drawable.fish2sphere180sx2).toString());
        data.put(data.size(), getDrawableUri(R.drawable.fish2sphere180s).toString());

        data.put(data.size(), "file:///mnt/sdcard/vr/video-1920x960a.mp4");
        data.put(data.size(), "http://10.240.131.39/vr/570624aae1c52.mp4");
        data.put(data.size(), "http://192.168.5.106/vr/570624aae1c52.mp4");

        data.put(data.size(), "http://cache.utovr.com/201508270528174780.m3u8");
        data.put(data.size(), "file:///mnt/sdcard/vr/WSGV6301.jpg");

        SpinnerHelper.with(this)
                .setData(data)
                .setClickHandler(new SpinnerHelper.ClickHandler() {
                    @Override
                    public void onSpinnerClicked(int index, int key, String value) {
                        et.setText(value);
                    }
                })
                .init(R.id.spinner_url);*/

        findViewById(R.id.video_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;// = et.getText().toString();
                url = "file:///mnt/sdcard/vr/video-1920x960a.mp4";
                if (TextUtils.isEmpty(url)) {
                    url = "http://10.240.131.39/vr/570624aae1c52.mp4";
                }
                if (!TextUtils.isEmpty(url)){
                    PanoVRPlayerActivity.startVideo(MainActivity.this, Uri.parse(url));
                } else {
                    Toast.makeText(MainActivity.this, "empty url!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.bitmap_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;// = et.getText().toString();
                url = getDrawableUri(R.drawable.bitmap360).toString();
                if (!TextUtils.isEmpty(url)){
                    PanoVRPlayerActivity.startBitmap(MainActivity.this, Uri.parse(url));
                } else {
                    Toast.makeText(MainActivity.this, "empty url!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.ijk_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;// = et.getText().toString();
                url = "file:///mnt/sdcard/vr/video-1920x960a.mp4";
                if (TextUtils.isEmpty(url)) {
                    url = "http://10.240.131.39/vr/570624aae1c52.mp4";
                }
                if (!TextUtils.isEmpty(url)){
                    IjkPlayerActivity.start(MainActivity.this, Uri.parse(url));
                } else {
                    Toast.makeText(MainActivity.this, "empty url!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stop service
        this.stopService(new Intent(this, ZhilunVrService.class));
    }

    private Uri getDrawableUri(@DrawableRes int resId){
        Resources resources = getResources();
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(resId) + '/' + resources.getResourceTypeName(resId) + '/' + resources.getResourceEntryName(resId) );
    }
}
