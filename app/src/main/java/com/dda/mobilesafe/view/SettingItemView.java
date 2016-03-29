package com.dda.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mobilesafe.dda.com.activity.R;

/**
 * Created by nuo on 2016/3/26.
 * 设置中心的自定义控件
 */
public class SettingItemView extends RelativeLayout {

    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox cbStatus;
    private String mDescOff;
    private String mDescOn;
    private String mTitle;

    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //根据属性名称获取属性的值
        mTitle = attrs.getAttributeValue("http://schemas.android.com/apk/com.dda.mobilesafe", "titles");
        //根据属性名称获取属性的值
        mDescOn = attrs.getAttributeValue("http://schemas.android.com/apk/com.dda.mobilesafe", "desc_on");
        //根据属性名称获取属性的值
        mDescOff = attrs.getAttributeValue("http://schemas.android.com/apk/com.dda.mobilesafe", "desc_off");

        initView();
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        //将自定义好的布局文件设置给当前的SettingItemView
        View view = View.inflate(getContext(), R.layout.view_settting_item, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        cbStatus = (CheckBox) findViewById(R.id.cb_status);

        setTitle(mTitle);//设置标题
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setDesc(String desc) {
        tvDesc.setText(desc);
    }

    /**
     * 返回勾选状态
     */
    public boolean isChecked() {
        return cbStatus.isChecked();
    }

    public void setChecked(boolean check) {
        cbStatus.setChecked(check);

        //根据选择的状态更新文本描述
        if (check) {
            setDesc(mDescOn);
        } else {
            setDesc(mDescOff);
        }
    }
}
