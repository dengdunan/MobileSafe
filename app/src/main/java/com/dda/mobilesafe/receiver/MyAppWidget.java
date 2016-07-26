package com.dda.mobilesafe.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.dda.mobilesafe.service.KillProcessWidgetService;

/**
 * Created by nuo on 2016/6/24.
 * Created by 14:39.
 * 描述:
 */
public class MyAppWidget extends AppWidgetProvider {

    /**
     * 第一次创建的时候才会调用当前的生命周期的方法
     * 当前广播的生命周期只有10秒钟
     * 不能做耗时的操作
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.startService(intent);
    }

    /**
     * 当桌面上面所有的桌面小控件都删除的时候才会调用
     *
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context, KillProcessWidgetService.class);
        context.stopService(intent);
    }
}
