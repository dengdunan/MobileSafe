package com.dda.mobilesafe.receiver;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.dda.mobilesafe.utils.SystemInfoUtils;

import java.util.List;

import mobilesafe.dda.com.activity.R;

public class KillProcessAllReceiver extends BroadcastReceiver {

    private AppWidgetManager widgetManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //得到手机上面正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
            //杀死所有的进程
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
        }

        Toast.makeText(context,"清理完毕",Toast.LENGTH_SHORT).show();

        //这个是把当前的布局文件添加进行
        /**
         * 初始化一个远程的View
         */
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.process_widget);
        /**
         * 需要注意,这个里面findviewByid()方法没有
         * 设置当前文本里面一共有多少个进程
         */
        int processCount = SystemInfoUtils.getProcessCount(context);

        views.setTextViewText(R.id.process_count, "正在运行的软件:" + String.valueOf(processCount));

        //获取到当前手机上面的可用内存
        long availMem = SystemInfoUtils.getAvailMem(context);

        views.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(context, availMem));

        //第一个参数表示上下文
        //第二个参数表示当前有哪一个广播进行去处理当前的桌面小控件

        ComponentName provider = new ComponentName(context, MyAppWidget.class);

        widgetManager.updateAppWidget(provider, views);
    }
}
