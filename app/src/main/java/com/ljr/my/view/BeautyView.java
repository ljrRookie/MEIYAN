package com.ljr.my.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class BeautyView extends GLSurfaceView {
    public BeautyView(Context context) {
        super(context);
    }

    public BeautyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(new BeautyRenderer(this));
        //设置按需渲染 当我们调用requestRender 请求GLThread 回调一次 onDrawFrame
        //连续渲染 就是自动的回调onDrawFrame
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
