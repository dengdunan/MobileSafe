package com.dda.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.dda.mobilesafe.receiver.MyAppWidget;
import com.dda.mobilesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

import mobilesafe.dda.com.activity.R;

/**
 * Created by nuo on 2016/6/24.
 * Created by 15:15.
 * 描述: 清理桌面小控件的服务
 */
public class KillProcessWidgetService extends Service {

    private AppWidgetManager widgetManager;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //桌面小控件的管理者
        widgetManager = AppWidgetManager.getInstance(this);

        //每隔五秒钟更新一次桌面
        //初始化定时器
        timer = new Timer();
        //初始化一个定时任务
        timerTask = new TimerTask() {
            @Override
            public void run() {

                //这个是把当前的布局文件添加进行
                /**
                 * 初始化一个远程的View
                 */
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                /**
                 * 需要注意,这个里面findviewByid()方法没有
                 * 设置当前文本里面一共有多少个进程
                 */
                int processCount = SystemInfoUtils.getProcessCount(getApplicationContext());

                views.setTextViewText(R.id.process_count, "正在运行的软件:" + String.valueOf(processCount));

                //获取到当前手机上面的可用内存
                long availMem = SystemInfoUtils.getAvailMem(getApplicationContext());

                views.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(KillProcessWidgetService.this, availMem));

                //第一个参数表示上下文
                //第二个参数表示当前有哪一个广播进行去处理当前的桌面小控件

                Intent intent = new Intent();

                //发送一个隐式意图
                intent.setAction("com.dda.killProcess");

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidget.class);

                widgetManager.updateAppWidget(provider, views);
            }
        };
        //从0开始，每隔5秒钟更新一次
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //优化代码,手动释放内存
        if(timer != null || timerTask != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }
}
