package com.dda.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mobilesafe.dda.com.activity.R;

/**
 * Created by nuo on 2016/3/26.
 * 设置中心的自定义控件
 */
public class SettingClickView extends RelativeLayout {

    private TextView tvTitle;
    private TextView tvDesc;

    public SettingClickView(Context context) {
        super(context);
        initView();
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDesc(String desc) {
        tvDesc.setText(desc);
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //将自定义好的布局文件设置给当前的SettingClickView
        View.inflate(getContext(), R.layout.view_settting_click, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);

    }
}
