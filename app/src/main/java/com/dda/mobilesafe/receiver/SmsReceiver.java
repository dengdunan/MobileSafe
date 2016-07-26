package com.dda.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.dda.mobilesafe.service.LocationService;
import com.dda.mobilesafe.utils.ToastUtils;

import mobilesafe.dda.com.activity.R;

/**
 * 拦截短信
 * Created by nuo on 2016/4/6.
 */
public class SmsReceiver extends BroadcastReceiver {

    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;
    private SharedPreferences mPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objects = (Object[]) intent.getExtras().get("pdus");

        mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务
        mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);// 设备管理组件

        mPref = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String sim = mPref.getString("sim", null);

        if (!TextUtils.isEmpty(sim)) {
            //获取当前手机的sim卡
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String currentSim = tm.getSimSerialNumber() + "111";//获取当前手机的sim卡

            if (!sim.equals(currentSim)) {
                for (Object object : objects) {//短信最多140字节，超出的话会分成多条短信发送，所以是个数组。
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) object);

                    String originatingAddress = message.getOriginatingAddress();//短信来源的号码
                    String messageBody = message.getMessageBody();//短信内容

                    System.out.println(originatingAddress + ":" + messageBody);

                    if ("#*alarm*#".equals(messageBody)) {
                        abortBroadcast();//中断短信的传递，从而系统短信app就收不到内容了

                        //播放报警音乐，即使手机静音，也能播放音乐，因为使用的是媒体声音的通道和铃声无关。
                        MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                        player.setVolume(1f, 1f);
                        player.setLooping(true);
                        player.start();
                    } else if ("#*location*#".equals(messageBody)) {
                        abortBroadcast();//中断短信的传递，从而系统短信app就收不到内容了

                        //获取经纬度坐标
                        context.startService(new Intent(context, LocationService.class));//开启定位服务

                        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                        String location = sp.getString("location", "getting location...");

                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(originatingAddress, null, location, null, null);

                        System.out.println("location: " + location);
                    } else if ("#*wipedata*#".equals(messageBody)) {
                        abortBroadcast();//中断短信的传递，从而系统短信app就收不到内容了

                        if (mDPM.isAdminActive(mDeviceAdminSample)) {// 判断设备管理器是否已经激活
                            mDPM.wipeData(0);// 清除数据,恢复出厂设置
                        } else {
                            ToastUtils.showToast(context, "必须先激活设备管理器!");
                        }
                    } else if ("#*lockscreen*#".equals(messageBody)) {
                        abortBroadcast();//中断短信的传递，从而系统短信app就收不到内容了

                        if (mDPM.isAdminActive(mDeviceAdminSample)) {// 判断设备管理器是否已经激活
                            mDPM.lockNow();// 立即锁屏
                            mDPM.resetPassword("123456", 0);
                        } else {
                            ToastUtils.showToast(context, "必须先激活设备管理器!");
                        }
                    }
                }
            }
        }
    }
}
