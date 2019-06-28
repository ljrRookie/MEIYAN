package com.ljr.my.view;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.ljr.my.filter.CameraFilter;
import com.ljr.my.filter.ScreenFilter;
import com.ljr.my.util.CameraHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class BeautyRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private BeautyView mView;
    private CameraHelper mCameraHelper;
    private int[] mTextures;
    private SurfaceTexture mSurfaceTexture;
    private float[] mtx = new float[16];
    private CameraFilter mCameraFilter;
    private ScreenFilter mScreenFilter;

    public BeautyRenderer(BeautyView beautyView) {
        mView = beautyView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //打开摄像头
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
        //纹理 == 数据的输入
        mTextures = new int[1];
        GLES20.glGenTextures(mTextures.length,mTextures,0);
        //mTextures==mSurfaceTexture-->摄像头采集数据
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        //        矩阵--》   摄像头的数据 不会变形  顶点
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter = new CameraFilter(mView.getContext());
        mScreenFilter = new ScreenFilter(mView.getContext());
        mCameraFilter.setMatrix(mtx);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width,height);
        mScreenFilter.onReady(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //摄像头获取一帧数据，会回调此方法
        GLES20.glClearColor(0,0,0,0);
        //执行清空
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter.setMatrix(mtx);
        //信息  int   类型---》GPU 纹理  Request    ---》返回 一个新的   Request
        int id=mCameraFilter.onDrawFrame(mTextures[0]);
        //id ==效果1.onDrawFrame(id);  帽子
        // id ==效果2.onDrawFrame(id);  眼镜
        // id ==效果2.onDrawFrame(id);  大耳朵
        //        mScreenFilter 将最终的特效运用到SurfaceView中
        mScreenFilter.onDrawFrame(id);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mView.requestRender();
    }
}
