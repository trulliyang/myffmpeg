package com.example.shiyang1.myffmpeg.node;

import android.opengl.GLES20;

import com.example.shiyang1.myffmpeg.utils.FFGLShaderUtils;
import com.example.shiyang1.myffmpeg.utils.FFGLTextureUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class FFGLFirstNode extends FFGLNode {

    private int mShaderProgramID = -111;
    private int mTexture0ID = -200;
    private int mTexture1ID = -211;
    private int mTexture2ID = -222;
    private int mTexture3ID = -233;
    private int mTexture4ID = -244;
    private int mTexture5ID = -255;
    private int mTexture6ID = -266;
    private int mTexture7ID = -277;

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

    private String mVertexShaderString = " \n" +
            "attribute vec2 aTextureCoordinates; \n" +
            "attribute vec2 aPosition; \n" +
            "varying vec2 vTexCoord; \n" +
            "void main() { \n" +
            "    gl_Position = vec4(aPosition, 0.0, 1.0); \n" +
            "    vTexCoord = vec2(aTextureCoordinates.x, 1.0 - aTextureCoordinates.y); \n" +
            "} \n";

    private String mFragmentShaderString = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n"  +
            "void main() { \n" +
//            "    vec4 color = texture2D(uTexture0, vTexCoord); \n" +
            "    gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0); \n" +
            "} \n";

    @Override
    public void init() {
        initTexture();
        initShader();
        initAttribute();
        initUniform();
        initMesh();
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {
        if (mShaderProgramID <= 0) {
            return;
        }

        GLES20.glUseProgram(mShaderProgramID);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture0ID);
        GLES20.glUniform1i(mTexture0Handle, 0);
//        GLES20.glUniform4f(mTargetColorHandle, mTargetColor[0], mTargetColor[1],mTargetColor[2], mTargetColor[3]);

//        updateTexture();
//        updateVertex();
        
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

    }

    @Override
    public void destroy() {

    }

    private void initTexture() {
        mTexture0ID = FFGLTextureUtils.initTexture();
    }

    private void initShader() {
        mShaderProgramID = FFGLShaderUtils.initShader(mVertexShaderString, mFragmentShaderString);
    }

    private void initAttribute(){
        mPositionHandle = GLES20.glGetAttribLocation(mShaderProgramID,"aPosition");
        mTextureCoordinatesHandle = GLES20.glGetAttribLocation(mShaderProgramID,"aTextureCoordinates");
    }

    private void initUniform() {
        mTexture0Handle = GLES20.glGetUniformLocation(mShaderProgramID,"uTexture0");
//        mTargetColorHandle = GLES20.glGetUniformLocation(mShaderProgramID,"uTargetColor");
    }

    private void initMesh() {
        float[] v = {
                -1.0f, -1.0f,
                +1.0f, -1.0f,
                -1.0f, +1.0f,
                +1.0f, +1.0f
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

}
