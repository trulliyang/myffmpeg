package com.example.shiyang1.myffmpeg;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


// video(mp4,webm)->data(yuv,rgba)->texture->shader->glsurfaceview

public class MainActivity extends Activity implements Button.OnClickListener{
    static {
        System.loadLibrary("native-lib");
    }

    FFGLSurfaceView mFFView;
    Button mAddBtn;
    Button mRunBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddBtn = findViewById(R.id.button_add);
        mRunBtn = findViewById(R.id.button_run);

        mAddBtn.setOnClickListener(this);
        mRunBtn.setOnClickListener(this);

//        mGlsfv = findViewById(R.id.glsfv);
//        mFFView = new FFGLSurfaceView(this);
        mFFView = findViewById(R.id.ffview);
//        String aaa  = (String) avformatinfo().subSequence(3000,5000);
//        Log.e("shiyang", "shiyang avformatinfo:" + aaa);

//        yuv2mp4();
    }

    public native String stringFromJNI();
    public native String urlprotocolinfo();
    public native String avformatinfo();
    public native String avcodecinfo();
    public native String avfilterinfo();
    public native String yuv2mp4();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add:
                Log.e("shiyang", "shiyang button add");
                String filePath = "/sdcard/DCIM/Camera/aaa.mp4";
                yuv2mp4();
                break;
            case R.id.button_run:
                Log.e("shiyang", "shiyang button run");
                break;
            default:
        }
    }
}
