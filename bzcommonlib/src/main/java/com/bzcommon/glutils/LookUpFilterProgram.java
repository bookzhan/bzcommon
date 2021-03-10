package com.bzcommon.glutils;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import static android.opengl.GLES20.glGetUniformLocation;

/**
 * Created by zhandalin on 2021-03-10 15:09.
 * description:
 */
public class LookUpFilterProgram extends BaseProgram {
    private static final String fss
            = "varying highp vec2 vTextureCoord;\n" +
            "\n" +
            "uniform sampler2D sTexture;\n" +
            "uniform sampler2D lookupTexture; // lookup texture\n" +
            "\n" +
            "void main() {\n" +
            "    lowp vec4 textureColor = texture2D(sTexture, vTextureCoord);\n" +
            "\n" +
            "    mediump float blueColor = textureColor.b * 63.0;\n" +
            "\n" +
            "    mediump vec2 quad1;\n" +
            "    quad1.y = floor(floor(blueColor) / 8.0);\n" +
            "    quad1.x = floor(blueColor) - (quad1.y * 8.0);\n" +
            "\n" +
            "    mediump vec2 quad2;\n" +
            "    quad2.y = floor(ceil(blueColor) / 8.0);\n" +
            "    quad2.x = ceil(blueColor) - (quad2.y * 8.0);\n" +
            "\n" +
            "    highp vec2 texPos1;\n" +
            "    texPos1.x = (quad1.x * 0.125) + 0.5 / 512.0 + ((0.125 - 1.0 / 512.0) * textureColor.r);\n" +
            "    texPos1.y = (quad1.y * 0.125) + 0.5 / 512.0 + ((0.125 - 1.0 / 512.0) * textureColor.g);\n" +
            "\n" +
            "    highp vec2 texPos2;\n" +
            "    texPos2.x = (quad2.x * 0.125) + 0.5 / 512.0 + ((0.125 - 1.0 / 512.0) * textureColor.r);\n" +
            "    texPos2.y = (quad2.y * 0.125) + 0.5 / 512.0 + ((0.125 - 1.0 / 512.0) * textureColor.g);\n" +
            "\n" +
            "    lowp vec4 newColor1 = texture2D(lookupTexture, texPos1);\n" +
            "    lowp vec4 newColor2 = texture2D(lookupTexture, texPos2);\n" +
            "\n" +
            "    lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n" +
            "    gl_FragColor = vec4(newColor.rgb, textureColor.w);\n" +
            "}";
    private float[] matrix = {1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1};
    private int lookUpTexture = -1;
    private static final int FLOAT_SZ = Float.SIZE / 8;
    private static final int VERTEX_NUM = 4;
    private static final int VERTEX_SZ = VERTEX_NUM * 2;

    public LookUpFilterProgram(boolean needFlipVertical) {
        super(needFlipVertical);
    }

    public LookUpFilterProgram(int rotation, boolean flipHorizontal, boolean flipVertical) {
        super(rotation, flipHorizontal, flipVertical);
    }


    @Override
    protected int loadShader(String vss, String fss) {
        int program = super.loadShader(vss, LookUpFilterProgram.fss);
        int lookupTextureLocation = glGetUniformLocation(program, "lookupTexture");
        GLES20.glUseProgram(program);
        GLES20.glUniform1i(lookupTextureLocation, 2);
        GLES20.glUseProgram(0);

        return program;
    }

    public void setLookUpBitmap(Bitmap bitmap) {
        if (null == bitmap || bitmap.isRecycled()) {
            return;
        }
        lookUpTexture = BZOpenGlUtils.loadTexture(bitmap);
    }

    @Override
    public void onDrawBefore() {
        super.onDrawBefore();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lookUpTexture);
    }

}
