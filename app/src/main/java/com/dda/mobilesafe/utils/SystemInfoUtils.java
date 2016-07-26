package com.dda.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 服务状态工具类
 * Created by nuo on 2016/4/9.
 */
public class SystemInfoUtils {

    /**
     * 检测服务是否在运行
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);//获取所有系统正在运行的服务，最多运行100个

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();

            if (className.equals(serviceName)) {//服务存在
                return true;
            }
        }
        return false;
    }

    /**
     * 返回进程的总个数
     *
     * @param context
     * @return
     */
    public static int getProcessCount(Context context) {
        //得到进程管理者
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取到当前手机上面所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        //获取手机上面一共有多少个进程
        int size = runningAppProcesses.size();
        return size;
    }

    public static long getAvailMem(Context context) {
        //得到进程管理者
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取到内存的基本信息
        activityManager.getMemoryInfo(memoryInfo);
        //获取到剩余内存
        long availMem = memoryInfo.availMem;
        return availMem;
    }

    public static long getTotalMem(Context context) {
        try {
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));

            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            String readLine = reader.readLine();

            StringBuffer sb = new StringBuffer();

            for (char c : readLine.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }

            long totalMem = Long.parseLong(sb.toString()) * 1024;
            return totalMem;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
