package com.dda.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dda.mobilesafe.db.AddressDao;

import java.util.ArrayList;
import java.util.List;

import mobilesafe.dda.com.activity.R;

/**
 * 来电提醒的服务
 * Created by nuo on 2016/4/9.
 */
public class AddressService extends Service {

    private TelephonyManager tm;
    private MyListener listener;
    private OutCallReceiver receiver;
    private WindowManager mWM;
    private View view;
    private SharedPreferences mPref;

    private int startX;
    private int startY;
    private WindowManager.LayoutParams params;
    private int winWidth;
    private int winHeight;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        //监听来电
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);//监听来电的状态

        receiver = new OutCallReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);//动态注册广播
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);

        unregisterReceiver(receiver);//注销广播
    }

    /**
     * 自定义归属地的浮窗
     */
    public void showToast(String text) {
        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        // 获取屏幕宽高
        winWidth = mWM.getDefaultDisplay().getWidth();
        winHeight = mWM.getDefaultDisplay().getHeight();

        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;// 电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
        params.gravity = Gravity.LEFT + Gravity.TOP;//将重心位置设置在左上方，也就是（0,0）从左上方开始，而不是默认的重心位置
        params.setTitle("Toast");

        int lastX = mPref.getInt("lastX", 0);
        int lastY = mPref.getInt("lastY", 0);

        //设置浮窗的位置，基于左上方的偏移量
        params.x = lastX;
        params.y = lastY;

        view = View.inflate(this, R.layout.toast_address, null);

        int[] bgs = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange, R.drawable.call_locate_blue, R.drawable.call_locate_green, R.drawable.call_locate_gray};
        int style = mPref.getInt("address_style", 0);//读取保存的style

        view.setBackgroundResource(bgs[style]);//根据存储的style样式更新背景

        TextView tvText = (TextView) view.findViewById(R.id.tv_number);

        tvText.setText(text);
        mWM.addView(view, params);//将view添加到屏幕上(Window)

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        // 计算移动偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;

                        // 更新浮窗位置
                        params.x += dx;
                        params.y += dy;

                        // 防止坐标偏离屏幕
                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        // 防止坐标偏离屏幕
                        if (params.x > winWidth - view.getWidth()) {
                            params.x = winWidth - view.getWidth();
                        }

                        if (params.y > winHeight - view.getHeight()) {
                            params.y = winHeight - view.getHeight();
                        }

                        // System.out.println("x:" + params.x + ";y:" + params.y);

                        mWM.updateViewLayout(view, params);

                        // 重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 记录坐标点
                        SharedPreferences.Editor edit = mPref.edit();
                        edit.putInt("lastX", params.x);
                        edit.putInt("lastY", params.y);
                        edit.commit();
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 监听去电的广播接受者,需要权限：android.permission.PROCESS_OUTGOING_CALLS
     * Created by nuo on 2016/4/11.
     */
    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();//获取去电的电话号码

            String address = AddressDao.getAddress(number);
//            Toast.makeText(context, address, Toast.LENGTH_LONG).show();
            showToast(address);
        }
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://电话铃声响了
                    System.out.println("电话铃响。。");
                    String address = AddressDao.getAddress(incomingNumber);//根据来电号码，查询归属地。
//                    Toast.makeText(AddressService.this, address, Toast.LENGTH_LONG).show();
                    showToast(address);
                    break;

                case TelephonyManager.CALL_STATE_IDLE://电话闲置的状态
                    if (mWM != null && view != null) {
                        mWM.removeViewImmediate(view);//从Window中移除界面
                        view = null;
                    }
                    break;
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_NEW_OUTGOING_CALL);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }
}
