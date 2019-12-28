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
import com.example.shiyang1.myffmpeg.node.FFGLSeventhNode;
import com.example.shiyang1.myffmpeg.node.FFGLSixthNode;
import com.example.shiyang1.myffmpeg.node.FFGLThirdNode;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FFGLRenderer implements GLSurfaceView.Renderer {
    private FFGLFirstNode mFirstNode;
    private FFGLSecondNode mSecondNode;
    private FFGLThirdNode mThirdNode;
    private FFGLFourthNode mFourthNode;
    private FFGLFifthNode mFifthNode;
    private FFGLSixthNode mSixthNode;
    private FFGLSeventhNode mSeventhNode;
//    private FFGLYUV2RGBANode mYuvNode;

    private NodeManager mNodeManager;

    private int mBackgroudWidth = 720;
    private int mBackgroundHeight = 1280;

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
        int x = 0;
        int y = 0;
        int w = mBackgroudWidth;
        int h = mBackgroundHeight;
        GLES20.glViewport(x, y, w, h);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        if (null != mNodeManager) {
            mNodeManager.update();
            mNodeManager.render();
        }
    }

    private void initManger() {
        if (null == mNodeManager) {
            mNodeManager = new NodeManager();
            mNodeManager.init();
        }
    }

    private void initNode() {
        mFirstNode = new FFGLFirstNode(mContext);
        mFirstNode.init();
        mSecondNode = new FFGLSecondNode(mContext);
        mSecondNode.init();
        mThirdNode = new FFGLThirdNode(mContext);
        mThirdNode.init();
        mFourthNode = new FFGLFourthNode(mContext);
        mFourthNode.init();
        mFifthNode = new FFGLFifthNode(mContext);
        mFifthNode.init();
        mSixthNode = new FFGLSixthNode(mContext);
        mSixthNode.init();
        mSeventhNode = new FFGLSeventhNode(mContext);
        mSeventhNode.init();

    }

    private void addNode() {
        mNodeManager.addFFGLNode(mFirstNode);
//        mNodeManager.addFFGLNode(mFourthNode);
        mNodeManager.addFFGLNode(mFifthNode);
        mNodeManager.addFFGLNode(mSixthNode);
        mNodeManager.addFFGLNode(mSeventhNode);
    }

    private void init() {
        initManger();
        initNode();
        addNode();
    }

    public void destroy() {
        if (null != mNodeManager) {
            mNodeManager.destroy();
        }
    }
}
