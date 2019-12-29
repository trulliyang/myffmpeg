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

public class FFGLMoveLeftProNode extends FFGLNode {
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

    private String mFragmentShaderStringFadeInOut = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    gl_FragColor = mix(color0, color1, uProgress);\n" +
            "} \n";

    private String mFragmentShaderStringZoomIn = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "void main() { \n" +
            "    vec4 color = texture2D(uTexture0, vTexCoord);\n" +
            "    vec2 tc = (vTexCoord-vec2(0.5))*uProgress + vec2(0.5);\n" +
            "    if (tc.x >= 0.0 && tc.x <= 1.0 && tc.y >= 0.0 && tc.y <= 1.0) {\n" +
            "        vec4 color1 = texture2D(uTexture1, tc);\n" +
            "        color = mix(color, color1, uProgress);\n" +
            "    }\n" +
            "    gl_FragColor = color;\n" +
            "} \n";

    private String mFragmentShaderStringZoomOut = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "void main() { \n" +
            "    vec4 color = texture2D(uTexture0, vTexCoord);\n" +
            "    vec2 tc = (vTexCoord-vec2(0.5))/uProgress + vec2(0.5);\n" +
            "    if (tc.x >= 0.0 && tc.x <= 1.0 && tc.y >= 0.0 && tc.y <= 1.0) {\n" +
            "        vec4 color1 = texture2D(uTexture1, tc);\n" +
            "        color = mix(color, color1, uProgress);\n" +
            "    }\n" +
            "    gl_FragColor = color;\n" +
            "} \n";

    private String mFragmentShaderStringMoveLeft = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "void main() { \n" +
            "    vec2 tc = vTexCoord + vec2(uProgress, 0.0);\n" +
            "    vec4 color = texture2D(uTexture0, tc);\n" +
            "    if (tc.x >= 1.0) {\n" +
            "        color = texture2D(uTexture1, tc - vec2(1.0, 0.0));\n" +
            "    }\n" +
            "    gl_FragColor = color;\n" +
            "} \n";

    private String mFragmentShaderStringMoveRight = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "void main() { \n" +
            "    vec2 tc = vTexCoord - vec2(uProgress, 0.0);\n" +
            "    vec4 color = texture2D(uTexture0, tc);\n" +
            "    if (tc.x < 0.0) {\n" +
            "        color = texture2D(uTexture1, tc + vec2(1.0, 0.0));\n" +
            "    }\n" +
            "    gl_FragColor = color;\n" +
            "} \n";

    private String mFragmentShaderStringMoveUp = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "void main() { \n" +
            "    vec2 tc = vTexCoord + vec2(0.0, uProgress);\n" +
            "    vec4 color = texture2D(uTexture0, tc);\n" +
            "    if (tc.y > 1.0) {\n" +
            "        color = texture2D(uTexture1, tc - vec2(0.0, 1.0));\n" +
            "    }\n" +
            "    gl_FragColor = color;\n" +
            "} \n";

    private String mFragmentShaderStringMoveDown = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "void main() { \n" +
            "    vec2 tc = vTexCoord - vec2(0.0, uProgress);\n" +
            "    vec4 color = texture2D(uTexture0, tc);\n" +
            "    if (tc.y < 0.0) {\n" +
            "        color = texture2D(uTexture1, tc + vec2(0.0, 1.0));\n" +
            "    }\n" +
            "    gl_FragColor = color;\n" +
            "} \n";

    private String mFragmentShaderStringZoomInPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +

            "vec4 getColor(vec2 tc) { \n" +
            "    float s0 = 1.0-uProgress;\n" +
            "    vec2 tcNew0 = (tc-vec2(0.5)) / s0  + vec2(0.5);\n" +
            "    tcNew0 = abs(tcNew0);\n" +
            "    if (tcNew0.x > 1.0) { tcNew0.x = 2.0 - tcNew0.x; }\n" +
            "    if (tcNew0.y > 1.0) { tcNew0.y = 2.0 - tcNew0.y; }\n" +

            "    float s1 = 2.0-uProgress;\n" +
            "    vec2 tcNew1 = (tc-vec2(0.5)) / s1  + vec2(0.5);\n" +
            "    tcNew1 = abs(tcNew1);\n" +
            "    if (tcNew1.x > 1.0) { tcNew1.x = 2.0 - tcNew1.x; }\n" +
            "    if (tcNew1.y > 1.0) { tcNew1.y = 2.0 - tcNew1.y; }\n" +

            "    vec4 color0 = texture2D(uTexture0, tcNew0);\n" +
            "    vec4 color1 = texture2D(uTexture1, tcNew1);\n" +
            "    return mix(color0, color1, uProgress);\n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        vec2 tcNewA = mix(vec2(0.5), vTexCoord, 1.0+0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewA); \n" +
            "        vec2 tcNewB = mix(vec2(0.5), vTexCoord, 1.0-0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
            "} \n";

    private String mFragmentShaderStringZoomOutPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +

            "vec4 getColor(vec2 tc) { \n" +
            "    float s0 = 1.0+uProgress;\n" +
            "    vec2 tcNew0 = (tc-vec2(0.5)) / s0  + vec2(0.5);\n" +
            "    tcNew0 = abs(tcNew0);\n" +
            "    if (tcNew0.x > 1.0) { tcNew0.x = 2.0 - tcNew0.x; }\n" +
            "    if (tcNew0.y > 1.0) { tcNew0.y = 2.0 - tcNew0.y; }\n" +

            "    float s1 = 0.0+uProgress;\n" +
            "    vec2 tcNew1 = (tc-vec2(0.5)) / s1  + vec2(0.5);\n" +
            "    tcNew1 = abs(tcNew1);\n" +
            "    if (tcNew1.x > 1.0) { tcNew1.x = 2.0 - tcNew1.x; }\n" +
            "    if (tcNew1.y > 1.0) { tcNew1.y = 2.0 - tcNew1.y; }\n" +

            "    vec4 color0 = texture2D(uTexture0, tcNew0);\n" +
            "    vec4 color1 = texture2D(uTexture1, tcNew1);\n" +
            "    return mix(color0, color1, uProgress);\n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        vec2 tcNewA = mix(vec2(0.5), vTexCoord, 1.0+0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewA); \n" +
            "        vec2 tcNewB = mix(vec2(0.5), vTexCoord, 1.0-0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
            "} \n";

    private String mFragmentShaderStringMoveLeftPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +

            "vec4 getColor(vec2 tc) { \n" +
            "    vec2 tl0 = vec2(uProgress, 0.0);\n" +
            "    vec2 tcNew0 = tc+tl0;\n" +
            "    if (tcNew0.x > 1.0) { tcNew0.x = 2.0 - tcNew0.x; }\n" +

            "    vec2 tl1 = vec2(uProgress-1.0, 0.0);\n" +
            "    vec2 tcNew1 = tc+tl1;\n" +
            "    tcNew1 = abs(tcNew1);\n" +

            "    vec4 color0 = texture2D(uTexture0, tcNew0);\n" +
            "    vec4 color1 = texture2D(uTexture1, tcNew1);\n" +
            "    return mix(color0, color1, uProgress);\n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        vec2 tcNewA = vTexCoord + mix(vec2(0.0, 0.0), vec2(-0.2, 0.0), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewA); \n" +
            "        vec2 tcNewB = vTexCoord + mix(vec2(0.0, 0.0), vec2(+0.2, 0.0), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
            "} \n";

    private String mFragmentShaderStringMoveRightPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +

            "vec4 getColor(vec2 tc) { \n" +
            "    vec2 tl0 = vec2(-uProgress, 0.0); \n" +
            "    vec2 tcNew0 = tc+tl0; \n" +
            "    tcNew0 = abs(tcNew0); \n" +

            "    vec2 tl1 = vec2(-uProgress+1.0, 0.0); \n" +
            "    vec2 tcNew1 = tc+tl1; \n" +
            "    if (tcNew1.x > 1.0) { tcNew1.x = 2.0 - tcNew1.x; } \n" +

            "    vec4 color0 = texture2D(uTexture0, tcNew0); \n" +
            "    vec4 color1 = texture2D(uTexture1, tcNew1); \n" +
            "    return mix(color0, color1, uProgress); \n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        vec2 tcNewA = vTexCoord + mix(vec2(0.0, 0.0), vec2(-0.2, 0.0), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewA); \n" +
            "        vec2 tcNewB = vTexCoord + mix(vec2(0.0, 0.0), vec2(+0.2, 0.0), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
            "} \n";

    private String mFragmentShaderStringMoveTopPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +

            "vec4 getColor(vec2 tc) { \n" +
            "    vec2 tl0 = vec2(0.0, uProgress); \n" +
            "    vec2 tcNew0 = tc+tl0; \n" +
            "    if (tcNew0.y > 1.0) { tcNew0.y = 2.0 - tcNew0.y; } \n" +

            "    vec2 tl1 = vec2(0.0, uProgress-1.0); \n" +
            "    vec2 tcNew1 = tc+tl1; \n" +
            "    tcNew1 = abs(tcNew1); \n" +

            "    vec4 color0 = texture2D(uTexture0, tcNew0); \n" +
            "    vec4 color1 = texture2D(uTexture1, tcNew1); \n" +
            "    return mix(color0, color1, uProgress); \n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        vec2 tcNewA = vTexCoord + mix(vec2(0.0, 0.0), vec2(0.0, -0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewA); \n" +
            "        vec2 tcNewB = vTexCoord + mix(vec2(0.0, 0.0), vec2(0.0, 0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
            "} \n";


    private String mFragmentShaderStringMoveBottomPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +

            "vec4 getColor(vec2 tc) { \n" +
            "    vec2 tl0 = vec2(0.0, -uProgress); \n" +
            "    vec2 tcNew0 = tc+tl0; \n" +
            "    tcNew0 = abs(tcNew0); \n" +

            "    vec2 tl1 = vec2(0.0, -uProgress+1.0); \n" +
            "    vec2 tcNew1 = tc+tl1; \n" +
            "    if (tcNew1.y > 1.0) { tcNew1.y = 2.0 - tcNew1.y; } \n" +

            "    vec4 color0 = texture2D(uTexture0, tcNew0); \n" +
            "    vec4 color1 = texture2D(uTexture1, tcNew1); \n" +
            "    return mix(color0, color1, uProgress); \n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        vec2 tcNewA = vTexCoord + mix(vec2(0.0, 0.0), vec2(0.0, -0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewA); \n" +
            "        vec2 tcNewB = vTexCoord + mix(vec2(0.0, 0.0), vec2(0.0, 0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
            "} \n";

    private String mFragmentShaderStringMoveLeftTopPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +

            "vec4 getColor(vec2 tc) { \n" +
            "    vec2 tl0 = vec2(uProgress, uProgress); \n" +
            "    vec2 tcNew0 = tc+tl0; \n" +
            "    if (tcNew0.x > 1.0) { tcNew0.x = 2.0 - tcNew0.x; } \n" +
            "    if (tcNew0.y > 1.0) { tcNew0.y = 2.0 - tcNew0.y; } \n" +

            "    vec2 tl1 = vec2(uProgress-1.0, uProgress-1.0); \n" +
            "    vec2 tcNew1 = tc+tl1; \n" +
            "    tcNew1 = abs(tcNew1); \n" +

            "    vec4 color0 = texture2D(uTexture0, tcNew0); \n" +
            "    vec4 color1 = texture2D(uTexture1, tcNew1); \n" +
            "    return mix(color0, color1, uProgress); \n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        vec2 tcNewA = vTexCoord + mix(vec2(0.0, 0.0), vec2(-0.2, -0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewA); \n" +
            "        vec2 tcNewB = vTexCoord + mix(vec2(0.0, 0.0), vec2(0.2, 0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
            "} \n";

    private String mFragmentShaderStringMoveLeftBottomPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +

            "vec4 getColor(vec2 tc) { \n" +
            "    vec2 tl0 = vec2(uProgress, -uProgress); \n" +
            "    vec2 tcNew0 = tc+tl0; \n" +
            "    if (tcNew0.x > 1.0) { tcNew0.x = 2.0 - tcNew0.x; } \n" +
            "    tcNew0.y = abs(tcNew0.y); \n" +

            "    vec2 tl1 = vec2(uProgress-1.0, -uProgress+1.0); \n" +
            "    vec2 tcNew1 = tc+tl1; \n" +
            "    if (tcNew1.y > 1.0) { tcNew1.y = 2.0 - tcNew1.y; } \n" +

            "    vec4 color0 = texture2D(uTexture0, tcNew0); \n" +
            "    vec4 color1 = texture2D(uTexture1, tcNew1); \n" +
            "    return mix(color0, color1, uProgress); \n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        vec2 tcNewA = vTexCoord + mix(vec2(0.0, 0.0), vec2(-0.2, 0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewA); \n" +
            "        vec2 tcNewB = vTexCoord + mix(vec2(0.0, 0.0), vec2(0.2, -0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
            "} \n";

    private String mFragmentShaderStringMoveRightTopPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +

            "vec4 getColor(vec2 tc) { \n" +
            "    vec2 tl0 = vec2(-uProgress, uProgress); \n" +
            "    vec2 tcNew0 = tc+tl0; \n" +
            "    tcNew0.x = abs(tcNew0.x); \n" +
            "    if (tcNew0.y > 1.0) { tcNew0.y = 2.0 - tcNew0.y; } \n" +

            "    vec2 tl1 = vec2(-uProgress+1.0, uProgress-1.0); \n" +
            "    vec2 tcNew1 = tc+tl1; \n" +
            "    if (tcNew1.x > 1.0) { tcNew1.x = 2.0 - tcNew1.x; } \n" +
            "    tcNew1 = abs(tcNew1); \n" +

            "    vec4 color0 = texture2D(uTexture0, tcNew0); \n" +
            "    vec4 color1 = texture2D(uTexture1, tcNew1); \n" +
            "    return mix(color0, color1, uProgress); \n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        vec2 tcNewA = vTexCoord + mix(vec2(0.0, 0.0), vec2(0.2, -0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewA); \n" +
            "        vec2 tcNewB = vTexCoord + mix(vec2(0.0, 0.0), vec2(-0.2, 0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
            "} \n";

    private String mFragmentShaderStringMoveRightBottomPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +

            "vec4 getColor(vec2 tc) { \n" +
            "    vec2 tl0 = vec2(-uProgress, -uProgress); \n" +
            "    vec2 tcNew0 = tc+tl0; \n" +
            "    tcNew0 = abs(tcNew0); \n" +

            "    vec2 tl1 = vec2(-uProgress+1.0, -uProgress+1.0); \n" +
            "    vec2 tcNew1 = tc+tl1; \n" +
            "    if (tcNew1.x > 1.0) { tcNew1.x = 2.0 - tcNew1.x; } \n" +
            "    if (tcNew1.y > 1.0) { tcNew1.y = 2.0 - tcNew1.y; } \n" +

            "    vec4 color0 = texture2D(uTexture0, tcNew0); \n" +
            "    vec4 color1 = texture2D(uTexture1, tcNew1); \n" +
            "    return mix(color0, color1, uProgress); \n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 color0 = texture2D(uTexture0, vTexCoord);\n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord);\n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        vec2 tcNewA = vTexCoord + mix(vec2(0.0, 0.0), vec2(0.2, 0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewA); \n" +
            "        vec2 tcNewB = vTexCoord + mix(vec2(0.0, 0.0), vec2(-0.2, -0.2), 0.02*k*prg); \n" +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
            "} \n";

    private String mFragmentShaderStringRotateCCWPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "uniform float uTexture0Width; \n" +
            "uniform float uTexture0Height; \n" +

            "vec4 getColor(vec2 tc_origin) { \n" +
            "    float theta = -6.2831853071796*uProgress;\n" +
            "    float c = cos(theta);\n" +
            "    float s = sin(theta);\n" +
            "    vec2 tc_center_origin = 0.5*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_rot_origin = mat2(c, -s, s, c)*(tc_origin - tc_center_origin) + tc_center_origin;\n" +
            "    vec2 tc_new = tc_rot_origin/vec2(uTexture0Width, uTexture0Height);\n" +
            "    tc_new = abs(tc_new); \n" +
            "    if (tc_new.x > 1.0) { tc_new.x = 2.0 - tc_new.x; } \n" +
            "    if (tc_new.y > 1.0) { tc_new.y = 2.0 - tc_new.y; } \n" +
            "    vec4 color0 = texture2D(uTexture0, tc_new); \n" +
            "    vec4 color1 = texture2D(uTexture1, tc_new); \n" +
            "    return mix(color0, color1, uProgress); \n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    vec2 tc_origin = vTexCoord*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_center_origin = 0.5*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_delta_origin = tc_origin - tc_center_origin; \n" +
            "    float alpha_origin = atan(tc_delta_origin.y, tc_delta_origin.x); \n" +
            "    float len_origin = length(tc_delta_origin); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        float betaA_origin = alpha_origin + mix(0.0, 0.25, 0.02*k*prg); \n" +
            "        vec2 tcNewA = tc_center_origin + len_origin*vec2(cos(betaA_origin), sin(betaA_origin));\n " +
            "        colorSum += getColor(tcNewA); \n" +
            "        float betaB_origin = alpha_origin + mix(0.0, -0.25, 0.02*k*prg); \n" +
            "        vec2 tcNewB = tc_center_origin + len_origin*vec2(cos(betaB_origin), sin(betaB_origin));\n " +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
//            "    gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0); \n" +
            "} \n";

    private String mFragmentShaderStringRotateCWPro = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "uniform float uTexture0Width; \n" +
            "uniform float uTexture0Height; \n" +

            "vec4 getColor(vec2 tc_origin) { \n" +
            "    float theta = 6.2831853071796*uProgress;\n" +
            "    float c = cos(theta);\n" +
            "    float s = sin(theta);\n" +
            "    vec2 tc_center_origin = 0.5*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_rot_origin = mat2(c, -s, s, c)*(tc_origin - tc_center_origin) + tc_center_origin;\n" +
            "    vec2 tc_new = tc_rot_origin/vec2(uTexture0Width, uTexture0Height);\n" +
            "    tc_new = abs(tc_new); \n" +
            "    if (tc_new.x > 1.0) { tc_new.x = 2.0 - tc_new.x; } \n" +
            "    if (tc_new.y > 1.0) { tc_new.y = 2.0 - tc_new.y; } \n" +
            "    vec4 color0 = texture2D(uTexture0, tc_new); \n" +
            "    vec4 color1 = texture2D(uTexture1, tc_new); \n" +
            "    return mix(color0, color1, uProgress); \n" +
            "} \n" +

            "void main() { \n" +
            "    vec4 colorSum = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +
            "    vec2 tc_origin = vTexCoord*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_center_origin = 0.5*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_delta_origin = tc_origin - tc_center_origin; \n" +
            "    float alpha_origin = atan(tc_delta_origin.y, tc_delta_origin.x); \n" +
            "    float len_origin = length(tc_delta_origin); \n" +
            "    for (int i=0; i<=50; i++) {\n" +
            "        float k = float(i); \n" +
            "        float betaA_origin = alpha_origin + mix(0.0, 0.25, 0.02*k*prg); \n" +
            "        vec2 tcNewA = tc_center_origin + len_origin*vec2(cos(betaA_origin), sin(betaA_origin));\n " +
            "        colorSum += getColor(tcNewA); \n" +
            "        float betaB_origin = alpha_origin + mix(0.0, -0.25, 0.02*k*prg); \n" +
            "        vec2 tcNewB = tc_center_origin + len_origin*vec2(cos(betaB_origin), sin(betaB_origin));\n " +
            "        colorSum += getColor(tcNewB); \n" +
            "    } \n" +
            "    colorSum /= 102.0; \n" +
            "    gl_FragColor = clamp(colorSum, vec4(0.0), vec4(1.0)); \n" +
//            "    gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0); \n" +
            "} \n";

    private String mFragmentShaderStringZoomOutCircle = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "uniform float uTexture0Width; \n" +
            "uniform float uTexture0Height; \n" +
            "void main() { \n" +
            "    vec4 color = texture2D(uTexture0, vTexCoord);\n" +
            "    vec2 tc_origin = vTexCoord*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_center_origin = 0.5*vec2(uTexture0Width, uTexture0Height); \n" +
            "    float distance_origin = length(tc_origin - tc_center_origin); \n" +
            "    float radius_max_origin = 0.5*length(vec2(uTexture0Width, uTexture0Height)); \n" +
            "    float radius_origin = radius_max_origin*uProgress; \n" +
            "    if (distance_origin <= radius_origin) { \n" +
            "        vec2 tc = (vTexCoord-vec2(0.5))/(1.0+uProgress) + vec2(0.5);\n" +
            "        if (tc.x >= 0.0 && tc.x <= 1.0 && tc.y >= 0.0 && tc.y <= 1.0) {\n" +
            "            color = texture2D(uTexture1, tc);\n" +
            "        }\n" +
            "    }\n" +
            "    gl_FragColor = color;\n" +
            "} \n";

    private String mFragmentShaderStringVibrate = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "uniform float uTexture0Width; \n" +
            "uniform float uTexture0Height; \n" +

            "void main() { \n" +
            "    vec4 color = vec4(0.0); \n" +
            "    float prg = 1.0 - abs(1.0 - uProgress*2.0); \n" +

            "    vec2 tcFar = vec2(0.55, 0.45); \n" +
            "    vec2 tcCenter = vec2(0.5); \n" +
            "    vec2 tcCircleCenter = 0.5*(tcCenter+tcFar); \n" +
            "    vec2 tcDelta = tcCenter - tcCircleCenter; \n" +
            "    float alpha = atan(tcDelta.y, tcDelta.x); \n" +
            "    float beta = alpha + 6.2831853071796*uProgress; \n" +
            "    float c = cos(beta); \n" +
            "    float s = sin(beta); \n" +
            "    vec2 tcCenterNew = mat2(c, -s, s, c)*(tcCenter - tcCircleCenter) + tcCircleCenter; \n" +
            "    vec2 tcTl = tcCenterNew - tcCenter; \n" +
            "    vec2 tcNew = vTexCoord + tcTl; \n" +

            "    float scale = 1.0 + 0.1*prg; \n" +
            "    tcNew = (tcNew-tcCenterNew)/vec2(scale) + tcCenterNew; \n" +
            "    if (0.0 <= tcNew.x && tcNew.x <= 1.0 && 0.0 <= tcNew.y && tcNew.y <= 1.0) { \n" +
            "        vec4 color0 = texture2D(uTexture0, tcNew); \n" +
            "        vec4 color1 = texture2D(uTexture1, tcNew); \n" +
            "        color = mix(color0, color1, uProgress*uProgress*uProgress); \n" +
            "    } else { \n" +
            "        color = vec4(0.0); \n" +
            "    } \n" +
            "    gl_FragColor = color; \n" +
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

    private String mFragmentShaderStringRock = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n" +
            "uniform sampler2D uTexture1; \n" +
            "uniform float uProgress; \n" +
            "uniform float uTexture0Width; \n" +
            "uniform float uTexture0Height; \n" +

            "void main() { \n" +
            "    vec4 color = vec4(0.0); \n" +
            "    vec2 tc_origin = vTexCoord*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_tl_origin = vec2(0.0, 0.15)*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_new_origin = tc_origin; \n" +
            "    if (uProgress < 0.5) {; \n" +
            "        float prg = 2.0*uProgress; \n" +
            "        vec2 tl_s_origin = vec2(0.0, 0.2)*vec2(uTexture0Width, uTexture0Height); \n" +
            "        vec2 tl_e_origin = vec2(0.0, -0.1)*vec2(uTexture0Width, uTexture0Height); \n" +
            "        tc_new_origin = tc_origin + mix(tl_s_origin, tl_e_origin, prg); \n" +
            "    } else if (uProgress < 0.83) { \n" +
            "        float prg = (uProgress-0.5)/0.33; \n" +
            "        vec2 tl_s_origin = vec2(0.0, -0.1)*vec2(uTexture0Width, uTexture0Height); \n" +
            "        vec2 tl_e_origin = vec2(0.0, 0.01)*vec2(uTexture0Width, uTexture0Height); \n" +
            "        tc_new_origin = tc_origin + mix(tl_s_origin, tl_e_origin, prg); \n" +
            "    } else { \n" +
            "        float prg = (uProgress-0.83)/0.17; \n" +
            "        vec2 tl_s_origin = vec2(0.0, 0.01)*vec2(uTexture0Width, uTexture0Height); \n" +
            "        vec2 tl_e_origin = vec2(0.0, 0.0)*vec2(uTexture0Width, uTexture0Height); \n" +
            "        tc_new_origin = tc_origin + mix(tl_s_origin, tl_e_origin, prg); \n" +
            "    } \n" +
            "    vec2 tc_new_center_origin = tc_new_origin*0.5; \n" +
            "    if (uProgress < 0.25) {; \n" +
            "        float prg = 4.0*uProgress; \n" +
            "        float alpha = 0.087266462608333; \n" +
            "        float beta = alpha*prg; \n" +
            "        float c = cos(beta); \n" +
            "        float s = sin(beta); \n" +
            "        tc_new_origin = mat2(c, -s, s, c)*(tc_new_origin-tc_new_center_origin) + tc_new_center_origin; \n" +
            "    } else if (uProgress < 0.75) { \n" +
            "        float prg = 2.0*(uProgress-0.25); \n" +
            "        float alpha = 0.087266462608333; \n" +
            "        float beta = mix(alpha, -alpha, prg); \n" +
            "        float c = cos(beta); \n" +
            "        float s = sin(beta); \n" +
            "        tc_new_origin = mat2(c, -s, s, c)*(tc_new_origin-tc_new_center_origin) + tc_new_center_origin; \n" +
            "    } else { \n" +
            "        float prg = 4.0*(uProgress-0.75); \n" +
            "        float alpha = 0.087266462608333; \n" +
            "        float beta = mix(-alpha, 0.0, prg); \n" +
            "        float c = cos(beta); \n" +
            "        float s = sin(beta); \n" +
            "        tc_new_origin = mat2(c, -s, s, c)*(tc_new_origin-tc_new_center_origin) + tc_new_center_origin; \n" +
            "    } \n" +
            "    vec2 tcNew = tc_new_origin/vec2(uTexture0Width, uTexture0Height); \n" +
            "    if (0.0 <= tcNew.x && tcNew.x <= 1.0 && 0.0 <= tcNew.y && tcNew.y <= 1.0) { \n" +
            "        vec4 color0 = texture2D(uTexture0, tcNew); \n" +
            "        vec4 color1 = texture2D(uTexture1, tcNew); \n" +
            "        color = mix(color0, color1, uProgress*uProgress*uProgress); \n" +
            "    } else { \n" +
            "        color = vec4(0.0); \n" +
            "    } \n" +
            "    gl_FragColor = color; \n" +
            "} \n";

    private String mFragmentShaderStringWaterRripple = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n"  +
            "uniform sampler2D uTexture1; \n"  +
            "uniform float uProgress; \n" +
            "uniform float uTexture0Width; \n" +
            "uniform float uTexture0Height; \n" +
            "float pi = 3.1415926535898; \n" +

            "void main() { \n" +
            "    float prg = uProgress; \n" +
//            "    if (uProgress > 0.5) prg = (1.0 - uProgress)*2.0; \n" +
            "    vec2 tc_origin = vTexCoord*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_center_origin = 0.5*vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tc_delta_origin = tc_origin - tc_center_origin; \n" +
            "    float distance_origin = length(tc_delta_origin); \n" +
            "    float distance_longest_origin = length(tc_center_origin); \n" +
            "    float alpha = atan(tc_delta_origin.y, tc_delta_origin.x); \n" +
            "    float percentage = distance_origin/distance_longest_origin; \n" +
            "    float amp0 = sin(percentage*15.0*pi-prg*pi)*(1.0-percentage)*(1.0-percentage); \n" +
            "    float amp1 = cos(percentage*15.0*pi-prg*pi)*(1.0-percentage)*(1.0-percentage); \n" +
//            "    float beta = alpha*amp; \n" +
            "    vec2 tc_new_origin = tc_origin + prg*(amp0+amp1)*tc_delta_origin; \n" +
            "    vec2 tc_new = tc_new_origin/vec2(uTexture0Width, uTexture0Height); \n" +
//            "    vec4 black = vec4(0.0, 0.0, 0.0, 1.0); \n" +
//            "    vec4 white = vec4(1.0, 1.0, 1.0, 1.0); \n" +
//            "    vec4 mixColor = mix(black, white, (amp+1.0)*0.5); \n" +
//            "    gl_FragColor = mixColor;\n" +
            "    vec4 color0 = texture2D(uTexture0, tc_new); \n" +
            "    vec4 color1 = texture2D(uTexture1, vTexCoord); \n" +
            "    gl_FragColor = mix(color0, color1, uProgress*uProgress*uProgress); \n" +

            "} \n";

    private String mFragmentShaderStringLUT = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n"  +
            "uniform sampler2D uTexture1; \n"  +
            "uniform float uProgress; \n" +
            "uniform float uTexture0Width; \n" +
            "uniform float uTexture0Height; \n" +
            "uniform float uIntensity; \n" +
            "void main() { \n" +
            "    highp vec4 textureColor = texture2D(uTexture0, vTexCoord); \n"  +

            "    highp float blueColor = textureColor.b * 63.0; \n"  +

            "    highp vec2 quad1; \n"  +
            "    quad1.y = floor(floor(blueColor) / 8.0); \n"  +
            "    quad1.x = floor(blueColor) - (quad1.y * 8.0); \n"  +

            "    highp vec2 quad2; \n"  +
            "    quad2.y = floor(ceil(blueColor) / 8.0); \n"  +
            "    quad2.x = ceil(blueColor) - (quad2.y * 8.0); \n"  +

            "    highp vec2 texPos1; \n"  +
            "    texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r); \n"  +
            "    texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g); \n"  +

            "    highp vec2 texPos2; \n"  +
            "    texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r); \n"  +
            "    texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g); \n"  +

            "    lowp vec4 newColor1 = texture2D(uTexture1, texPos1); \n"  +
            "    lowp vec4 newColor2 = texture2D(uTexture1, texPos2); \n"  +

            "    lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor)); \n"  +
            "    gl_FragColor = mix(textureColor, vec4(newColor.rgb, textureColor.w), uIntensity); \n"  +

            "} \n";

    private String mFragmentShaderStringCharactor = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n"  +
            "uniform sampler2D uTexture1; \n"  +
            "uniform float uProgress; \n" +
            "uniform float uTexture0Width; \n" +
            "uniform float uTexture0Height; \n" +
            "uniform float uTexture1Width; \n" +
            "uniform float uTexture1Height; \n" +
            "uniform float uIntensity; \n" +
            "void main() { \n" +
            "    vec4 textureColor = texture2D(uTexture0, vTexCoord); \n"  +
            "    vec2 whPercentage = vec2(uTexture1Width/uTexture0Width, uTexture1Height/uTexture0Height); \n" +
            "    float newW_origin = 0.5*uTexture0Width; \n" +
            "    float newH_origin = newW_origin*uTexture1Height/uTexture1Width; \n" +
            "    vec2 scale = vec2(newW_origin/uTexture0Width, newH_origin/uTexture0Height); \n" +
            "    vec2 newCenter0_origin= vec2(newW_origin*0.5, 0.5*newH_origin); \n" +
            "    vec2 newCenter1_origin= vec2(uTexture0Width-newW_origin*0.5, uTexture0Height-0.5*newH_origin); \n" +
            "    vec2 newCenter0 = newCenter0_origin/vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 newCenter1 = newCenter1_origin/vec2(uTexture0Width, uTexture0Height); \n" +
            "    vec2 tl0 = 0.5 - newCenter0; \n" +
            "    vec2 tl1 = 0.5 - newCenter1; \n" +
            "    vec2 newCenter = mix(newCenter0, newCenter1, uProgress); \n" +
            "    vec2 tl = mix(tl0, tl1, uProgress); \n" +
            "    vec2 tc_charactor = vTexCoord; \n"  +
            "    tc_charactor = vTexCoord+tl; \n"  +
            "    tc_charactor = (tc_charactor-0.5)/scale + 0.5; \n"  +
            "    if (0.0 <= tc_charactor.x && tc_charactor.x <= 1.0 && 0.0 <= tc_charactor.y && tc_charactor.y <= 1.0) { \n" +
            "        textureColor = texture2D(uTexture1, tc_charactor); \n"  +
            "    }\n " +
            "    gl_FragColor = textureColor; \n"  +
            "} \n";


    private String mVertexShaderString1 = " \n" +
            "attribute vec2 aTextureCoordinates; \n" +
            "attribute vec2 aPosition; \n" +
            "varying vec2 vTexCoord; \n" +
            "void main() { \n" +
            "    gl_Position = vec4(aPosition, 0.0, 1.0); \n" +
            "    vTexCoord = vec2(aTextureCoordinates.x, 1.0 - aTextureCoordinates.y); \n" +
            "} \n";

    private String mFragmentShaderString1 = " \n" +
            "precision mediump float; \n" +
            "varying vec2 vTexCoord; \n" +
            "uniform sampler2D uTexture0; \n"  +
            "void main() { \n" +
            "    vec4 color = texture2D(uTexture0, vTexCoord); \n" +
            "    vec3 invert = vec3(1.0) - color.rgb;\n" +
            "    gl_FragColor = vec4(invert, color.a);\n" +
            "} \n";

    public FFGLMoveLeftProNode(Context context) {
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
        int rId0 = R.raw.rp5;
        updateTexture(rId0, mTextureInfo0);

        Log.e("shiyang", "shiyang texid="+mTextureInfo0.mTextureId
                        +",w="+mTextureInfo0.mTextureWidth
                        +",h="+mTextureInfo0.mTextureHeight);

        mTextureInfo1 = new TextureInfo();
        mTextureInfo1.mTextureId = FFGLTextureUtils.initTexture();
        int rId1 = R.raw.rp6;
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
        String fs = mFragmentShaderStringFadeInOut;
//        fs = mFragmentShaderStringZoomIn;
//        fs = mFragmentShaderStringZoomOut;
//        fs = mFragmentShaderStringMoveLeft;
//        fs = mFragmentShaderStringMoveRight;
//        fs = mFragmentShaderStringMoveUp;
//        fs = mFragmentShaderStringMoveDown;
        String fsm1 = mFragmentShaderStringNo;
        String fs0 = mFragmentShaderStringZoomInPro;
        String fs1 = mFragmentShaderStringZoomOutPro;
        String fs2 = mFragmentShaderStringMoveLeftPro;
        String fs3 = mFragmentShaderStringMoveRightPro;
        String fs4 = mFragmentShaderStringMoveTopPro;
        String fs5 = mFragmentShaderStringMoveBottomPro;
        String fs6 = mFragmentShaderStringMoveLeftTopPro;
        String fs7 = mFragmentShaderStringMoveLeftBottomPro;
        String fs8 = mFragmentShaderStringMoveRightTopPro;
        String fs9 = mFragmentShaderStringMoveRightBottomPro;
        String fs10 = mFragmentShaderStringRotateCCWPro;
        String fs11 = mFragmentShaderStringRotateCWPro;
        String fs12 = mFragmentShaderStringZoomOutCircle;
        String fs13 = mFragmentShaderStringVibrate;
        String fs14 = mFragmentShaderStringShake;
        String fs15 = mFragmentShaderStringRock;
        String fs16 = mFragmentShaderStringWaterRripple;
        String fs17 = mFragmentShaderStringLUT;
        String fs18 = mFragmentShaderStringCharactor;
        mShaderProgramID = FFGLShaderUtils.initShader(mVertexShaderString, fs2);
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
