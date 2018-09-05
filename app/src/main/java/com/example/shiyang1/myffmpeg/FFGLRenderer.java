package com.example.shiyang1.myffmpeg;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.shiyang1.myffmpeg.node.FFGLFirstNode;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FFGLRenderer implements GLSurfaceView.Renderer {
    FFGLFirstNode mFirstNode;
//    FFGLYUV2RGBANode mYuvNode;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mFirstNode.update();
        mFirstNode.render();
    }

    private void init() {
        mFirstNode = new FFGLFirstNode();
        mFirstNode.init();
    }

    public void destroy() {

    }
}
