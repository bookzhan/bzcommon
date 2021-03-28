package com.bzcommon;

import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.glutils.BZOpenGlUtils;
import com.bzcommon.glutils.BaseProgram;
import com.bzcommon.glutils.FrameBufferUtil;
import com.bzcommon.glutils.LookUpFilterProgram;
import com.bzcommon.glutils.MixTextureProgram;
import com.bzcommon.utils.BZAssetsFileManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLImageActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    private GLSurfaceView gl_surface_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_l_image);
        gl_surface_view = findViewById(R.id.gl_surface_view);
        gl_surface_view.setEGLContextClientVersion(2);
        gl_surface_view.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        gl_surface_view.setRenderer(this);
        gl_surface_view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void start(View view) {
        gl_surface_view.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

        BaseProgram baseProgram = new BaseProgram(true);
        int texture = BZOpenGlUtils.loadTexture(BitmapFactory.decodeResource(getResources(), R.drawable.test));

        gl.glViewport(0, 0, 540, 960);
        FrameBufferUtil frameBufferUtil = new FrameBufferUtil(540, 960);
        frameBufferUtil.bindFrameBuffer();
        LookUpFilterProgram lookUpFilterProgram = new LookUpFilterProgram(true);
        lookUpFilterProgram.setLookUpBitmap(BitmapFactory.decodeFile(BZAssetsFileManager.getFinalPath(this, "lookup.png")));
        lookUpFilterProgram.draw(texture);
        frameBufferUtil.unbindFrameBuffer();


        gl.glViewport(0, 0, 540 / 2, 960 / 2);
        baseProgram.draw(texture);


        gl.glViewport(540 / 2, 0, 540 / 2, 960 / 2);
        MixTextureProgram mixTextureProgram = new MixTextureProgram(true);
        mixTextureProgram.setMixturePercent(0);
        mixTextureProgram.setMixTexture(frameBufferUtil.getFrameBufferTextureID());
        mixTextureProgram.draw(texture);
    }
}