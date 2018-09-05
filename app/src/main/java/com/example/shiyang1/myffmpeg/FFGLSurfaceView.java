package com.example.shiyang1.myffmpeg;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class FFGLSurfaceView extends GLSurfaceView {
    private FFGLRenderer mRenderer;

    public FFGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public FFGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        destroyRenderer();
        super.surfaceDestroyed(holder);
    }

    private void destroyRenderer() {
        mRenderer.destroy();
    }

    private void init() {
        mRenderer = new FFGLRenderer();
        setEGLContextClientVersion(2);
        setRenderer(mRenderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

}
