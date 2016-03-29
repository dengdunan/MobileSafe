package com.dda.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 获取焦点的TextView
 * Created by nuo on 2016/3/19.
 */
public class FocusedTextView extends TextView{

    //有style样式的话会走此方法
    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //用代码new对象时，走此方法
    public FocusedTextView(Context context) {
        super(context);
    }

    //有属性时走此方法
    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //表示有没有获取焦点
    //跑马灯要运行，首先要调用此函数判断是否有焦点，是true的话，跑马灯才会有效果
    @Override
    public boolean isFocused() {
        return true;
    }
}
