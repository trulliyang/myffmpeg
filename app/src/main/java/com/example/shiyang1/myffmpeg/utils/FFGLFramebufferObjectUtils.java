package com.example.shiyang1.myffmpeg.utils;

import android.opengl.GLES20;

public class FFGLFramebufferObjectUtils {
    public static int initFramebufferObject() {
        int[] fbo = new int[1];

        GLES20.glGenFramebuffers(1, fbo, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo[0]);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        return fbo[0];
    }

}
