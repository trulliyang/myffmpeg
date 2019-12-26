package com.example.shiyang1.myffmpeg;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.shiyang1.myffmpeg.manager.NodeManager;
import com.example.shiyang1.myffmpeg.node.FFGLFifthNode;
import com.example.shiyang1.myffmpeg.node.FFGLFirstNode;
import com.example.shiyang1.myffmpeg.node.FFGLFourthNode;
import com.example.shiyang1.myffmpeg.node.FFGLSecondNode;
import com.example.shiyang1.myffmpeg.node.FFGLThirdNode;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FFGLRenderer implements GLSurfaceView.Renderer {
    private FFGLFirstNode mFirstNode;
    private FFGLSecondNode mSecondNode;
    private FFGLThirdNode mThirdNode;
    private FFGLFourthNode mFourthNode;
    private FFGLFifthNode mFifthNode;
//    private FFGLYUV2RGBANode mYuvNode;

    private NodeManager mNodeManager;

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
        GLES20.glViewport(0, 0, 960, 720);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        if (null != mFirstNode) {
//            mFirstNode.update();
//            mFirstNode.render();
//        }
//        if (null != mSecondNode) {
//            mSecondNode.update();
//            mSecondNode.render();
//        }
//        if (null != mThirdNode) {
//            mThirdNode.update();
//            mThirdNode.render();
//        }
//        if (null != mFourthNode) {
//            mFourthNode.update();
//            mFourthNode.render();
//        }
        if (null != mNodeManager) {
            mNodeManager.update();
            mNodeManager.render();
        }
    }

    private void init() {
//        mFirstNode = new FFGLFirstNode(mContext);
//        mFirstNode.init();
//        mSecondNode = new FFGLSecondNode(mContext);
//        mSecondNode.init();
//        mThirdNode = new FFGLThirdNode(mContext);
//        mThirdNode.init();
//        mFourthNode = new FFGLFourthNode(mContext);
//        mFourthNode.init();
        mFifthNode = new FFGLFifthNode(mContext);
        mFifthNode.init();
        mNodeManager = new NodeManager();
        mNodeManager.init();
//        mNodeManager.addFFGLNode(mFourthNode);
        mNodeManager.addFFGLNode(mFifthNode);
    }

    public void destroy() {
        if (null != mNodeManager) {
            mNodeManager.destroy();
        }
//        if (null != mFirstNode) {
//            mFirstNode.destroy();
//            mFirstNode = null;
//        }
//        if (null != mSecondNode) {
//            mSecondNode.destroy();
//            mSecondNode = null;
//        }
//        if (null != mThirdNode) {
//            mThirdNode.destroy();
//            mThirdNode = null;
//        }
//        if (null != mFourthNode) {
//            mFourthNode.destroy();
//            mFourthNode = null;
//        }
    }
}
