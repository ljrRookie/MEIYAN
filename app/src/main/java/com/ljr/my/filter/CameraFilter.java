package com.ljr.my.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.ljr.my.R;
import com.ljr.my.util.OpenGLUtils;

//主要是获取摄像头 数据   并且 创建FBO    在FBO中添加特效
public class CameraFilter extends AbstractFilter {
    //FBO int类型
    int[] mFrameBuffer;
    //纹理
    int[] mFrameBufferTextures;
    private float[] matrix;

    public CameraFilter(Context context) {
        super(context,  R.raw.camera_vertex, R.raw.camera_frag);
    }

    /**
     * 坐标转换
     */
    @Override
    protected void initCoordinate() {
        mTextureBuffer.clear();
        //摄像头是颠倒的 原始坐标  摄像头是颠倒的（90度） + 镜像
        //        float[] TEXTURE = {
//                0.0f, 0.0f,
//                1.0f, 0.0f,
//                0.0f, 1.0f,
//                1.0f, 1.0f
//        };
//        float[] TEXTURE = {
//                1.0f, 0.0f,
//                0.0f, 0.0f,
//                1.0f, 1.0f,
//                0.0f, 1.0f
//
//        };
//        怎么样修复
        /**
         * 修复代码
         */
        float[] TEXTURE = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };
        mTextureBuffer.put(TEXTURE);
    }

    @Override
    public void onReady(int width, int height) {
        super.onReady(width, height);
        mFrameBuffer = new int[1];
        //生成FBO - 缓冲区  ---》 纹理操作
        GLES20.glGenFramebuffers(1,mFrameBuffer,0);
        //实例化一个纹理  目的： 纹理和FBO进行绑定   纹理操作
        mFrameBufferTextures = new int[1];
        OpenGLUtils.glGenTextures(mFrameBufferTextures);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mFrameBufferTextures[0]);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
//        设置纹理显示纤细  宽度高度
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mWidth, mHeight, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        //        将纹理 与FBO联系
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                mFrameBufferTextures[0], 0);
//        解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
    }

    /**
     * 重写原因：第一个   FBO 纹理  mFrameBufferTextures[0]  丢给下一个处理者
     * @param textureId
     * @return
     */
    @Override
    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0, 0, mWidth, mHeight);
        //不调用的话就是默认的操作glsurfaceview中的纹理了。显示到屏幕上了
        //这里我们还只是把它画到fbo中(缓存)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
        //使用着色器
        GLES20.glUseProgram(mProgram);
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);

        mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);

//        变换矩阵
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);

        //激活图层
        GLES20.glActiveTexture(GLES20.GL_TEXTURE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);

        GLES20.glUniform1i(vTexture, 0);
//        绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

//  解绑
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        return mFrameBufferTextures[0];
    }
    public void setMatrix(float[] mtx) {
        this.matrix = mtx;
    }
}
