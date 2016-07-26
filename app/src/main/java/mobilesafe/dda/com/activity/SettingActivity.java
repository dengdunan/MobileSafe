package mobilesafe.dda.com.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dda.mobilesafe.receiver.AdminReceiver;
import com.dda.mobilesafe.service.AddressService;
import com.dda.mobilesafe.service.CallSafeService;
import com.dda.mobilesafe.service.WatchDogService;
import com.dda.mobilesafe.utils.SystemInfoUtils;
import com.dda.mobilesafe.view.SettingClickView;
import com.dda.mobilesafe.view.SettingItemView;

public class SettingActivity extends Activity {

    private SettingItemView sivUpdate;//设置升级
    private SettingItemView sivAddress;//设置地址
    private SettingItemView sivCallSafe;//设置黑名单更新
    private SettingItemView sivWatchDog;//设置看门狗
    private SettingClickView scvAddressStyle;//设置地址风格
    private SettingClickView scvAddressLocation;//设置地址的显示位置
    private TextView unInstall;
    private TextView tvDevDesc;
    private CheckBox cbDev;
    private SharedPreferences mPref;
    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;
    private final String[] items = new String[]{"半透明", "活力橙", "卫士蓝", "苹果绿", "金属灰"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        initUpdateView();
        initDevView();
        initAddressView();
        initAddressStyle();
        initAddressLoction();
        initBlackView();
        initWatchDog();
        uninstall();
    }

    private void initWatchDog() {
        sivWatchDog = (SettingItemView) findViewById(R.id.siv_watch_dog);

        boolean serviceRunning = SystemInfoUtils.isServiceRunning(this, "com.dda.mobilesafe.service.WatchDogService");

        //根据看门狗服务的运行来更新checkbox
        if (serviceRunning) {
            sivWatchDog.setChecked(true);
        } else {
            sivWatchDog.setChecked(false);
        }

        sivWatchDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivWatchDog.isChecked()) {
                    sivWatchDog.setChecked(false);
                    stopService(new Intent(SettingActivity.this, WatchDogService.class));//停止看门狗服务
                } else {
                    sivWatchDog.setChecked(true);
                    startService(new Intent(SettingActivity.this, WatchDogService.class));//开启看门狗服务
                }
            }
        });
    }

    /**
     * 初始化黑名单
     */
    private void initBlackView() {
        sivCallSafe = (SettingItemView) findViewById(R.id.siv_callsafe);

        boolean serviceRunning = SystemInfoUtils.isServiceRunning(this, "com.dda.mobilesafe.service.CallSafeService");

        //根据黑名单服务的运行来更新checkbox
        if (serviceRunning) {
            sivCallSafe.setChecked(true);
        } else {
            sivCallSafe.setChecked(false);
        }

        sivCallSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivCallSafe.isChecked()) {
                    sivCallSafe.setChecked(false);
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));//停止通讯安全服务
                } else {
                    sivCallSafe.setChecked(true);
                    startService(new Intent(SettingActivity.this, CallSafeService.class));//开启通讯安全服务
                }
            }
        });
    }

    /**
     * 初始化自动更新开关
     */
    private void initUpdateView() {
        sivUpdate = (SettingItemView) findViewById(R.id.siv_update);

        boolean autoUpdate = mPref.getBoolean("auto_update", true);

        if (autoUpdate) {
            sivUpdate.setChecked(true);
        } else {
            sivUpdate.setChecked(false);
        }

        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前的勾选状态
                if (sivUpdate.isChecked()) {
                    //设置不勾选
                    sivUpdate.setChecked(false);
                    //更新sp
                    mPref.edit().putBoolean("auto_update", false).commit();
                } else {
                    sivUpdate.setChecked(true);
                    //更新sp
                    mPref.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }

    /**
     * 初始化归属地开关
     */
    private void initAddressView() {
        sivAddress = (SettingItemView) findViewById(R.id.siv_address);

        boolean serviceRunning = SystemInfoUtils.isServiceRunning(this, "com.dda.mobilesafe.service.AddressService");

        //根据归属地服务的运行来更新checkbox
        if (serviceRunning) {
            sivAddress.setChecked(true);
        } else {
            sivAddress.setChecked(false);
        }

        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivAddress.isChecked()) {
                    sivAddress.setChecked(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));//停止归属地服务
                } else {
                    sivAddress.setChecked(true);
                    startService(new Intent(SettingActivity.this, AddressService.class));//开启归属地服务
                }
            }
        });
    }

    /**
     * 设置设备管理器是否开启
     */
    private void initDevView() {
        tvDevDesc = (TextView) findViewById(R.id.tv_dev_desc);
        cbDev = (CheckBox) findViewById(R.id.cb_dev);

        mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);// 设备管理组件
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);// 获取设备策略服务

        if (cbDev.isChecked() == false && mDPM.isAdminActive(mDeviceAdminSample) == false) {
            tvDevDesc.setText("设备管理器未开启");
            cbDev.setChecked(false);
        } else {
            tvDevDesc.setText("设备管理器已开启");
            cbDev.setChecked(true);
        }

        cbDev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cbDev.isChecked() == true) {
                    ActiveDev();
                    tvDevDesc.setText("设备管理器已开启");
                } else if (cbDev.isChecked() == false) {
                    tvDevDesc.setText("设备管理器未开启");
                    mDPM.removeActiveAdmin(mDeviceAdminSample);
                }
            }
        });
    }

    /**
     * 激活设备管理器
     */
    public void ActiveDev() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "哈哈哈, 我们有了超级设备管理器, 好NB!");
        startActivity(intent);
    }

    /**
     * 检查设备管理器是否激活
     */
    public void check() {
        if (mDPM.isAdminActive(mDeviceAdminSample) == true) {
            cbDev.setChecked(true);
        } else {
            cbDev.setChecked(false);
        }
    }

    /**
     * 修改提示框显示风格
     */
    private void initAddressStyle() {
        scvAddressStyle = (SettingClickView) findViewById(R.id.scv_address_style);

        scvAddressStyle.setTitle("归属地提示显示风格");

        int style = mPref.getInt("address_style", 0);//读取保存的style
        scvAddressStyle.setDesc(items[style]);

        scvAddressStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChooseDialog();
            }
        });
    }


    /**
     * 弹出选择风格的单选框
     */
    private void showSingleChooseDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("归属地提示框风格");

        int style = mPref.getInt("address_style", 0);//读取保存的style

        builder.setSingleChoiceItems(items, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPref.edit().putInt("address_style", which).commit();//保存选择的风格
                dialog.dismiss();//让dialog消失
                scvAddressStyle.setDesc(items[which]);//更新组合控件的文字描述信息
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        check();
    }

    /**
     * 修改归属地显示位置
     */
    private void initAddressLoction() {
        scvAddressLocation = (SettingClickView) findViewById(R.id.scv_address_location);
        scvAddressLocation.setTitle("归属地提示框显示位置");
        scvAddressLocation.setDesc("设置归属地提示框的显示位置");

        scvAddressLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DragViewActivity.class));
            }
        });
    }

    /**
     * 卸载软件
     */
    private void uninstall() {
        unInstall = (TextView) findViewById(R.id.uninstall);
        unInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDPM.isAdminActive(mDeviceAdminSample)) {
                    mDPM.removeActiveAdmin(mDeviceAdminSample);// 取消激活
                }
                // 卸载程序
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });
    }
}
