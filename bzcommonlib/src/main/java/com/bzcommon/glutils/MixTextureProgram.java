package com.bzcommon.glutils;

import android.opengl.GLES20;

import static android.opengl.GLES20.glGetUniformLocation;

/**
 * Created by zhandalin on 2021-03-28 16:10.
 * description:
 */
public class MixTextureProgram extends BaseProgram {
    private static final String vss
            = "attribute vec4 aPosition;\n" +
            "attribute vec2 aTextureCoord;\n" +
            "attribute vec2 aTextureCoord2;\n" +
            "\n" +
            "varying vec2 vTextureCoord;\n" +
            "varying vec2 vTextureCoord2;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position =aPosition;\n" +
            "    vTextureCoord = aTextureCoord;\n" +
            "    vTextureCoord2 = aTextureCoord2;\n" +
            "}";
    private static final String fss
            = "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "varying vec2 vTextureCoord2;\n" +
            "\n" +
            "uniform sampler2D sTexture;\n" +
            "uniform sampler2D sTexture2;\n" +
            "\n" +
            "uniform lowp float mixturePercent;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "  lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);\n" +
            "  lowp vec4 textureColor2 = texture2D(sTexture2, vTextureCoord2);\n" +
            "\n" +
            "  gl_FragColor = vec4(mix(textureColor.rgb, textureColor2.rgb, textureColor2.a * mixturePercent), textureColor.a);\n" +
            "}";
    private int maTextureCoord2Loc;
    private int mixturePercentLocation;
    private float mixturePercent = 1;
    private int mixTexture;

    public MixTextureProgram(boolean needFlipVertical) {
        super(needFlipVertical);
    }

    public MixTextureProgram(int rotation, boolean flipHorizontal, boolean flipVertical) {
        super(rotation, flipHorizontal, flipVertical);
    }

    public float getMixturePercent() {
        return mixturePercent;
    }

    public void setMixturePercent(float mixturePercent) {
        this.mixturePercent = mixturePercent;
    }

    public void setMixTexture(int mixTexture) {
        this.mixTexture = mixTexture;
    }

    @Override
    protected int loadShader(String vss, String fss) {
        int program = super.loadShader(MixTextureProgram.vss, MixTextureProgram.fss);
        maTextureCoord2Loc = GLES20.glGetAttribLocation(program, "aTextureCoord2");

        mixturePercentLocation = glGetUniformLocation(program, "mixturePercent");
        int sTexture2Location = glGetUniformLocation(program, "sTexture2");
        GLES20.glUseProgram(program);
        GLES20.glUniform1i(sTexture2Location, 2);
        GLES20.glUseProgram(0);
        return program;
    }

    @Override
    public void onDrawBefore() {
        super.onDrawBefore();
        GLES20.glEnableVertexAttribArray(maTextureCoord2Loc);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, coordinateBuffer[0]);
        GLES20.glVertexAttribPointer(maTextureCoord2Loc, 2, GLES20.GL_FLOAT, false, VERTEX_SZ, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        GLES20.glUniform1f(mixturePercentLocation, mixturePercent);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mixTexture);
    }
}
