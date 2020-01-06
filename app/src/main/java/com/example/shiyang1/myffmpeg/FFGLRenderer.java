package com.example.shiyang1.myffmpeg;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.example.shiyang1.myffmpeg.manager.NodeManager;
import com.example.shiyang1.myffmpeg.manager.ParseManager;
import com.example.shiyang1.myffmpeg.node.FFGLBackgroundNode;
import com.example.shiyang1.myffmpeg.node.FFGLCharactorNode;
import com.example.shiyang1.myffmpeg.node.FFGLFirstNode;
import com.example.shiyang1.myffmpeg.node.FFGLFrameNode;
import com.example.shiyang1.myffmpeg.node.FFGLLUTNode;
import com.example.shiyang1.myffmpeg.node.FFGLMoveLeftProNode;
import com.example.shiyang1.myffmpeg.node.FFGLMoveTopProNode;
import com.example.shiyang1.myffmpeg.node.FFGLRockNode;
import com.example.shiyang1.myffmpeg.node.FFGLRotateCCWProNode;
import com.example.shiyang1.myffmpeg.node.FFGLShakeNode;
import com.example.shiyang1.myffmpeg.node.FFGLVibrateNode;
import com.example.shiyang1.myffmpeg.node.FFGLWaterRippleNode;
import com.example.shiyang1.myffmpeg.node.FFGLWatermarkNode;
import com.example.shiyang1.myffmpeg.node.FFGLZoomInProNode;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FFGLRenderer implements GLSurfaceView.Renderer {
    private FFGLFirstNode mFirstNode;

    private FFGLBackgroundNode mBackgroundNode;
    private FFGLFrameNode mFrameNode;
    private FFGLWatermarkNode mWatermarkNode;
    private FFGLCharactorNode mCharactorNode;
    private FFGLWaterRippleNode mWaterRippleNode;
    private FFGLRotateCCWProNode mRotateCCWProNode;
    private FFGLZoomInProNode mZoomInProNode;
    private FFGLVibrateNode mVibrateNode;
    private FFGLMoveLeftProNode mMoveLeftProNode;
    private FFGLShakeNode mShakeNode;
    private FFGLMoveTopProNode mMoveTopProNode;
    private FFGLRockNode mRockNode;
    private FFGLLUTNode mLUTNode;

//    private FFGLYUV2RGBANode mYuvNode;

    private NodeManager mNodeManager;
    private ParseManager mParseManager;


    private float mProgress = 0.0f;

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

    void draw2() {
        float dt = 0.0f;
        if (null != mBackgroundNode) {
            mBackgroundNode.update(dt);
            mBackgroundNode.render();
        }
        if (mProgress <= 1.0f) {
            if (mProgress < 0.5f) dt = 0.0f;
            else dt = (mProgress - 0.5f) * 2.0f;
            if (null != mShakeNode) {
                mShakeNode.update(dt);
                mShakeNode.render();
            }
        } else if (mProgress <= 2.0f) {
            if (mProgress < 1.5f) dt = 0.0f;
            else dt = (mProgress - 1.5f) * 2.0f;
            if (null != mWaterRippleNode) {
                mWaterRippleNode.update(dt);
                mWaterRippleNode.render();
            }
        } else if (mProgress <= 3.0f) {
            if (mProgress < 2.5f) dt = 0.0f;
            else dt = (mProgress - 2.5f) * 2.0f;
            if (null != mRotateCCWProNode) {
                mRotateCCWProNode.update(dt);
                mRotateCCWProNode.render();
            }
        } else if (mProgress <= 4.0f) {
            if (mProgress < 3.5f) dt = 0.0f;
            else dt = (mProgress - 3.5f) * 2.0f;
            if (null != mZoomInProNode) {
                mZoomInProNode.update(dt);
                mZoomInProNode.render();
            }
        } else if (mProgress <= 5.0f) {
            if (mProgress < 4.5f) dt = 0.0f;
            else dt = (mProgress - 4.5f) * 2.0f;
            if (null != mVibrateNode) {
                mVibrateNode.update(dt);
                mVibrateNode.render();
            }
        } else if (mProgress <= 6.0f) {
            if (mProgress < 5.5f) dt = 0.0f;
            else dt = (mProgress - 5.5f) * 2.0f;
            if (null != mMoveLeftProNode) {
                mMoveLeftProNode.update(dt);
                mMoveLeftProNode.render();
            }
        } else if (mProgress <= 7.0f) {
            if (mProgress < 6.5f) dt = 0.0f;
            else dt = (mProgress - 6.5f) * 2.0f;
            if (null != mMoveTopProNode) {
                mMoveTopProNode.update(dt);
                mMoveTopProNode.render();
            }
        } else if (mProgress <= 8.0f) {
            if (mProgress < 7.5f) dt = 0.0f;
            else dt = (mProgress - 7.5f) * 2.0f;
            if (null != mLUTNode) {
                mLUTNode.update(dt);
                mLUTNode.render();
            }
//        } else if (mProgress <= 9.0f) {
//            if (mProgress < 8.5f) dt = 0.0f;
//            else dt = (mProgress - 8.5f) * 2.0f;
//            if (null != mLUTNode) {
//                mLUTNode.update(dt);
//                mLUTNode.render();
//            }
        } else {
            if (null != mLUTNode) {
                mLUTNode.update(1.0f);
                mLUTNode.render();
            }
        }
        mProgress += 0.016f;
        if (null != mFrameNode) {
            mFrameNode.update(dt);
            mFrameNode.render();
        }
        if (null != mCharactorNode) {
            mCharactorNode.update(dt);
            mCharactorNode.render();
        }
        if (null != mWatermarkNode) {
            mWatermarkNode.update(dt);
            mWatermarkNode.render();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        if (true) {
            draw2();
        } else {
            if (null != mNodeManager) {
                float dt = mProgress;
                mNodeManager.update(dt);
                mProgress += 0.02f;
                mNodeManager.render();
            }
        }
    }

    private void initManger() {
        if (null == mNodeManager) {
            mNodeManager = new NodeManager();
            mNodeManager.init();
        }
        if (null == mParseManager) {
            mParseManager = new ParseManager();
            mParseManager.init();
        }
    }

    private void initNode() {
        mFirstNode = new FFGLFirstNode(mContext);
        mFirstNode.init();

        mBackgroundNode = new FFGLBackgroundNode(mContext);
        mBackgroundNode.init();
        mFrameNode = new FFGLFrameNode(mContext);
        mFrameNode.init();
        mCharactorNode = new FFGLCharactorNode(mContext);
        mCharactorNode.init();
        mWatermarkNode = new FFGLWatermarkNode(mContext);
        mWatermarkNode.init();
        mWaterRippleNode = new FFGLWaterRippleNode(mContext);
        mWaterRippleNode.init();
        mRotateCCWProNode = new FFGLRotateCCWProNode(mContext);
        mRotateCCWProNode.init();
        mZoomInProNode = new FFGLZoomInProNode(mContext);
        mZoomInProNode.init();
        mVibrateNode = new FFGLVibrateNode(mContext);
        mVibrateNode.init();
        mMoveLeftProNode = new FFGLMoveLeftProNode(mContext);
        mMoveLeftProNode.init();
        mShakeNode = new FFGLShakeNode(mContext);
        mShakeNode.init();
        mMoveTopProNode = new FFGLMoveTopProNode(mContext);
        mMoveTopProNode.init();
        mRockNode = new FFGLRockNode(mContext);
        mRockNode.init();
        mLUTNode = new FFGLLUTNode(mContext);
        mLUTNode.init();
    }

    private void addNode() {
        mNodeManager.addFFGLNode(mFirstNode);
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
