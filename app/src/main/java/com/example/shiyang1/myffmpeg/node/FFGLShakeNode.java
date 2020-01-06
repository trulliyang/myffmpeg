package com.example.shiyang1.myffmpeg.node;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.example.shiyang1.myffmpeg.R;
import com.example.shiyang1.myffmpeg.utils.FFGLShaderUtils;
import com.example.shiyang1.myffmpeg.utils.FFGLTextureUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class FFGLShakeNode extends FFGLNode {
    class TextureInfo {
        int mTextureId;
        int mTextureWidth;
        int mTextureHeight;
    };


    private Context mContext;

    private int mShaderProgramID = -111;
    private int mTexture0ID = -200;
    private int mTexture1ID = -211;
    private int mTexture2ID = -222;
    private int mTexture3ID = -233;
    private int mTexture4ID = -244;
    private int mTexture5ID = -255;
    private int mTexture6ID = -266;
    private int mTexture7ID = -277;

    private float mProgress = 0.0f;
    private float mIntensity = 1.0f;

    private FloatBuffer mVerticesCoordinatesBuffer;
    private FloatBuffer mTextureCoordinatesBuffer;
    private ShortBuffer mIndicesBuffer;

    private int mPositionHandle = -111;
    private int mTextureCoordinatesHandle = -222;
    private int mTexture0Handle = -300;
    private int mTexture1Handle = -311;
    private int mTexture2Handle = -322;
    private int mTexture3Handle = -333;
    private int mTexture4Handle = -344;
    private int mTexture5Handle = -355;
    private int mTexture6Handle = -366;
    private int mTexture7Handle = -377;

    private int mProgressHandle = -200;
    private int mIntensityHandle = -200;

    private int mTexture0WidthHandle = -500;
    private int mTexture0HeightHandle = -501;

    private int mTexture1WidthHandle = -500;
    private int mTexture1HeightHandle = -501;

    private int mTexture0OriginalWidth = 0;
    private int mTexture0OriginalHeight = 0;

    private int mTexture1OriginalWidth = 0;
    private int mTexture1OriginalHeight = 0;


    private TextureInfo mTextureInfo0;
    private TextureInfo mTextureInfo1;


    private ByteBuffer mGLReadPixelBuffer;
    private Bitmap mBitmapDump;

    private String mVertexShaderString = " \n" +
            "attribute vec2 aTextureCoordinates; \n" +
            "attribute vec2 aPosition; \n" +
            "varying vec2 vTexCoord; \n" +
            "void main() { \n" +
            "    gl_Position = vec4(aPosition, 0.0, 1.0); \n" +
            "    vTexCoord = vec2(aTextureCoordinates.x, 1.0-aTextureCoordinates.y); \n" +
            "} \n";

    private String mFragmentShaderStringNo = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    gl_FragColor = color0;\n" +
            "} \n";

    private String mFragmentShaderStringShake = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "uniform float uTexture0Width; \n" +
            "uniform float uTexture0Height; \n" +

            "void main() { \n" +
            "    vec4 color = vec4(0.0); \n" +
            "    vec2 tcNew = vTexCoord; \n" +
            "    if (uProgress < 0.5) {; \n" +
            "        float prg = 2.0*uProgress; \n" +
            "        prg = prg*prg*prg; \n" +
            "        vec2 tl = vec2(0.0, -1.0); \n" +
            "        tcNew = vTexCoord - tl + tl*prg; \n" +
            "    } else { \n" +
            "        float prg = 8.0*(uProgress-0.5); \n" +
            "        float alpha = 0.087266462608333; \n" +
            "        float beta = alpha; \n" +
            "        if (prg < 1.0) { \n" +
            "            beta = 3.0*alpha*prg; \n" +
            "        } else if (prg < 2.0) { \n" +
            "            prg -= 1.0; \n" +
            "            beta = mix(3.0*alpha, -2.0*alpha, prg); \n" +
            "        } else if (prg < 3.0) { \n" +
            "            prg -= 2.0; \n" +
            "            beta = mix(-2.0*alpha, alpha, prg); \n" +
            "        } else { \n" +
            "            prg -= 3.0; \n" +
            "            beta = mix(alpha, 0.0, prg); \n" +
            "        } \n" +
            "        float c = cos(beta); \n" +
            "        float s = sin(beta); \n" +
            "        vec2 tc_origin = vTexCoord*vec2(uTexture0Width, uTexture0Height); \n" +
            "        vec2 tc_center_origin = 0.5*vec2(uTexture0Width, uTexture0Height); \n" +
            "        vec2 tc_new_origin = mat2(c, -s, s, c)*(tc_origin-tc_center_origin) + tc_center_origin; \n" +
            "        tcNew = tc_new_origin/vec2(uTexture0Width, uTexture0Height); \n" +
            "    } \n" +
            "    if (0.0 <= tcNew.x && tcNew.x <= 1.0 && 0.0 <= tcNew.y && tcNew.y <= 1.0) { \n" +
            "        vec4 color0 = texture2D(uTexture0, tcNew); \n" +
            "        vec4 color1 = texture2D(uTexture1, tcNew); \n" +
            "        color = mix(color0, color1, uProgress*uProgress*uProgress); \n" +
            "    } else { \n" +
            "        color = vec4(0.0); \n" +
            "    } \n" +
            "    gl_FragColor = color; \n" +
            "} \n";

    public FFGLShakeNode(Context context) {
        mContext = context;
    }

    @Override
    public void init() {
        initTexture();
        initShader();
        initAttribute();
        initUniform();
        initMesh();
    }

//    float dt = 0.0f;

    @Override
    public void update(float dt) {
//        if (mProgress >= 1.0f) mProgress = -0.01f;
//        mProgress += 0.01;
//        if (mProgress > 1.0f) mProgress = 1.0f;
//        else if (mProgress < 0.0f) mProgress = 0.0f;

//        dt += 0.01f;
//        mProgress = (float) Math.abs(Math.sin(dt));

//        mProgress += 0.005;
//        mProgress = mProgress-(int)mProgress;

//        mProgress = 14.0f/15.0f;
//        Log.e("shiyang", "shiyang progress="+mProgress);

//        mIntensity += 0.01;
//        mIntensity = mIntensity-(int)mIntensity;

//        mIntensity = 14.0f/15.0f;


        mProgress = dt;
    }



    @Override
    public void render() {
        if (mShaderProgramID <= 0) {
            return;
        }

        GLES20.glUseProgram(mShaderProgramID);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureInfo0.mTextureId);
        GLES20.glUniform1i(mTexture0Handle, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureInfo1.mTextureId);
        GLES20.glUniform1i(mTexture1Handle, 1);

        GLES20.glUniform1f(mProgressHandle, mProgress);
        GLES20.glUniform1f(mIntensityHandle, mIntensity);

        GLES20.glUniform1f(mTexture0WidthHandle, mTextureInfo0.mTextureWidth);
        GLES20.glUniform1f(mTexture0HeightHandle, mTextureInfo0.mTextureHeight);

        GLES20.glUniform1f(mTexture1WidthHandle, mTextureInfo1.mTextureWidth);
        GLES20.glUniform1f(mTexture1HeightHandle, mTextureInfo1.mTextureHeight);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordinatesHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, mVerticesCoordinatesBuffer);
        GLES20.glVertexAttribPointer(mTextureCoordinatesHandle,  2, GLES20.GL_FLOAT, false, 2 * 4, mTextureCoordinatesBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mIndicesBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinatesHandle);
        GLES20.glUseProgram(0);

//        mGLReadPixelBuffer = ByteBuffer.allocateDirect(mTexture0OriginalWidth*mTexture0OriginalHeight*4);
//        mGLReadPixelBuffer.order(ByteOrder.nativeOrder());
//        GLES20.glReadPixels(0, 0, mTexture0OriginalWidth, mTexture0OriginalHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mGLReadPixelBuffer);
//
//        byte[] bytes = new byte[mGLReadPixelBuffer.capacity()];
//        mGLReadPixelBuffer.get(bytes, 0, bytes.length);
//        mBitmapDump = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void destroy() {
        if (null != mTextureInfo0) {
            if (mTextureInfo0.mTextureId > 0) {
                int[] tex = new int[1];
                tex[0] = mTextureInfo0.mTextureId;
                GLES20.glDeleteTextures(1, tex, 0);
                mTextureInfo0.mTextureId = -200;
            }
            mTextureInfo0 = null;
        }
        if (null != mTextureInfo1) {
            if (mTextureInfo1.mTextureId > 0) {
                int[] tex = new int[1];
                tex[0] = mTextureInfo1.mTextureId;
                GLES20.glDeleteTextures(1, tex, 0);
                mTextureInfo1.mTextureId = -200;
            }
            mTextureInfo1 = null;
        }
    }

    private void initTexture() {
        mTextureInfo0 = new TextureInfo();
        mTextureInfo0.mTextureId = FFGLTextureUtils.initTexture();
        int rId0 = R.raw.rp0;
        updateTexture(rId0, mTextureInfo0);

        Log.e("shiyang", "shiyang texid="+mTextureInfo0.mTextureId
                        +",w="+mTextureInfo0.mTextureWidth
                        +",h="+mTextureInfo0.mTextureHeight);

        mTextureInfo1 = new TextureInfo();
        mTextureInfo1.mTextureId = FFGLTextureUtils.initTexture();
        int rId1 = R.raw.rp1;
        updateTexture(rId1, mTextureInfo1);

        Log.e("shiyang", "shiyang texid="+mTextureInfo1.mTextureId
                        +",w="+mTextureInfo1.mTextureWidth
                        +",h="+mTextureInfo1.mTextureHeight);

//        mTexture2ID = FFGLTextureUtils.initTexture();
//        int rId2 = R.raw.a002;
//        updateTexture(rId2, mTexture2ID);
//
//        mTexture3ID = FFGLTextureUtils.initTexture();
//        int rId3 = R.raw.a003;
//        updateTexture(rId3, mTexture3ID);
//
//        mTexture4ID = FFGLTextureUtils.initTexture();
//        int rId4 = R.raw.a004;
//        updateTexture(rId4, mTexture4ID);
//
//        mTexture5ID = FFGLTextureUtils.initTexture();
//        int rId5 = R.raw.a005;
//        updateTexture(rId5, mTexture5ID);
//
//        mTexture6ID = FFGLTextureUtils.initTexture();
//        int rId6 = R.raw.a006;
//        updateTexture(rId6, mTexture6ID);
//
//        mTexture7ID = FFGLTextureUtils.initTexture();
//        int rId7 = R.raw.a007;
//        updateTexture(rId7, mTexture7ID);

    }

    private void initShader() {
        String fs14 = mFragmentShaderStringShake;
        mShaderProgramID = FFGLShaderUtils.initShader(mVertexShaderString, fs14);
//        mShaderProgramID = FFGLShaderUtils.initShader(mVertexShaderString1, mFragmentShaderString1);
    }

    private void initAttribute() {
        mPositionHandle = GLES20.glGetAttribLocation(mShaderProgramID,"aPosition");
        mTextureCoordinatesHandle = GLES20.glGetAttribLocation(mShaderProgramID,"aTextureCoordinates");
    }

    private void initUniform() {
        mTexture0Handle = GLES20.glGetUniformLocation(mShaderProgramID,"uTexture0");
        mTexture1Handle = GLES20.glGetUniformLocation(mShaderProgramID,"uTexture1");
        mProgressHandle = GLES20.glGetUniformLocation(mShaderProgramID,"uProgress");
        mTexture0WidthHandle = GLES20.glGetUniformLocation(mShaderProgramID,"uTexture0Width");
        mTexture0HeightHandle = GLES20.glGetUniformLocation(mShaderProgramID,"uTexture0Height");
        mTexture1WidthHandle = GLES20.glGetUniformLocation(mShaderProgramID,"uTexture1Width");
        mTexture1HeightHandle = GLES20.glGetUniformLocation(mShaderProgramID,"uTexture1Height");
        mIntensityHandle = GLES20.glGetUniformLocation(mShaderProgramID,"uIntensity");
    }

    private void initMesh() {
        float a = 0.421875f;
//        float w = 240.0f/720.0f;
//        float h = 76.8f/1280.0f;
//        float[] v = {
//                0.6f, -0.8f,
//                0.6f+w,-0.8f,
//                0.6f, -0.8f+h,
//                0.6f+w, -0.8f+h
//        };
        float[] v = {
                -1f, -1f*a,
                1f,  -1f*a,
                -1f, 1f*a,
                1f,  1f*a
        };
        ByteBuffer vb = ByteBuffer.allocateDirect(v.length * 4);
        vb.order(ByteOrder.nativeOrder());
        mVerticesCoordinatesBuffer = vb.asFloatBuffer();
        mVerticesCoordinatesBuffer.put(v);
        mVerticesCoordinatesBuffer.position(0);

        float[] t = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f
        };
        ByteBuffer tb = ByteBuffer.allocateDirect(t.length * 4);
        tb.order(ByteOrder.nativeOrder());
        mTextureCoordinatesBuffer = tb.asFloatBuffer();
        mTextureCoordinatesBuffer.put(t);
        mTextureCoordinatesBuffer.position(0);

        short[] i = {
                0,1,2,
                1,2,3
        };
        ByteBuffer ib = ByteBuffer.allocateDirect(i.length * 2);
        ib.order(ByteOrder.nativeOrder());
        mIndicesBuffer = ib.asShortBuffer();
        mIndicesBuffer.put(i);
        mIndicesBuffer.position(0);
    }

    private void updateTexture(int resId, TextureInfo texInfo) {
        InputStream is = mContext.getResources().openRawResource(resId);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        texInfo.mTextureWidth = bitmap.getWidth();
        texInfo.mTextureHeight = bitmap.getHeight();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texInfo.mTextureId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        bitmap.recycle();
        if (bitmap.isRecycled()) {
            bitmap = null;
        }
    }

    private void updateVertex() {

    }

    public void setTextureData(Bitmap bitmap) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture0ID);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

}
