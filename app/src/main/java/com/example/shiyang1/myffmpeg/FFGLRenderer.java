package com.example.shiyang1.myffmpeg;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.shiyang1.myffmpeg.node.FFGLFirstNode;
import com.example.shiyang1.myffmpeg.node.FFGLSecondNode;
import com.example.shiyang1.myffmpeg.node.FFGLThirdNode;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FFGLRenderer implements GLSurfaceView.Renderer {
    private FFGLFirstNode mFirstNode;
    private FFGLSecondNode mSecondNode;
    private FFGLThirdNode mThirdNode;
//    private FFGLYUV2RGBANode mYuvNode;

    private Context mContext;

    FFGLRenderer(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e("shiyang", "shiyang vp (w,h)="+width+","+height);
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        if (null != mFirstNode) {
            mFirstNode.update();
            mFirstNode.render();
        }
        if (null != mSecondNode) {
            mSecondNode.update();
            mSecondNode.render();
        }
        if (null != mThirdNode) {
            mThirdNode.update();
            mThirdNode.render();
        }
    }

    private void init() {
        mFirstNode = new FFGLFirstNode(mContext);
        mFirstNode.init();
//        mSecondNode = new FFGLSecondNode(mContext);
//        mSecondNode.init();
//        mThirdNode = new FFGLThirdNode(mContext);
//        mThirdNode.init();
    }

    public void destroy() {
        if (null != mFirstNode) {
            mFirstNode.destroy();
            mFirstNode = null;
        }
        if (null != mSecondNode) {
            mSecondNode.destroy();
            mSecondNode = null;
        }
        if (null != mThirdNode) {
            mThirdNode.destroy();
            mThirdNode = null;
        }
    }
}
