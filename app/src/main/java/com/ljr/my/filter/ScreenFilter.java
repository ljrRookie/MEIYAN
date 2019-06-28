package com.ljr.my.filter;

import android.content.Context;

import com.ljr.my.R;

/**
 * 滤镜      他是作为    显示滤镜   CameraFilter 已经渲染好的特效
 */
public class ScreenFilter extends AbstractFilter {
    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_frag);
    }


    @Override
    protected void initCoordinate() {

    }
}
