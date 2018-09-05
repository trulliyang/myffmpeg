package com.example.shiyang1.myffmpeg.utils;

import android.opengl.GLES20;
import android.util.Log;

public class FFGLShaderUtils {
    public static int initShader(String vs, String fs){
//        int vertextShader = loadShader(GLES20.GL_VERTEX_SHADER, vs);
//        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fs);

        int programID = GLES20.glCreateProgram();
        if(programID != 0 ){
            GLES20.glAttachShader(programID, loadShader(GLES20.GL_VERTEX_SHADER, vs));
            GLES20.glAttachShader(programID, loadShader(GLES20.GL_FRAGMENT_SHADER, fs));
            GLES20.glLinkProgram(programID);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programID, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
//                Log.e("shiyang", "shiyang GreenGLColorTopNode Could not link program: ");
//                Log.e("shiyang", GLES20.glGetProgramInfoLog(mShaderProgramID));
                GLES20.glDeleteProgram(programID);
                programID = 0;
            }
        }
        return programID;
    }

    private static int loadShader(int type,String shaderCode){
        int shader = GLES20.glCreateShader(type);
        if(shader != 0 ){
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            String infoLog = GLES20.glGetShaderInfoLog(shader);
            if (!infoLog.isEmpty()) {
                Log.e("shiyang", "shiyang load shader info log: "+infoLog);
            }
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }
}
