package mobilesafe.dda.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.dda.mobilesafe.service.KillProcessService;
import com.dda.mobilesafe.utils.SharedPreferencesUtils;
import com.dda.mobilesafe.utils.SystemInfoUtils;

/**
 * 任务管理器的设置界面
 */
public class TaskManagerSettingActivity extends Activity {

    private SharedPreferences sp;
    private CheckBox cb_status_kill_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_task_manager_setting);
        CheckBox cb_status = (CheckBox) findViewById(R.id.cb_status);
        //0表示私有的模式
        //sp = getSharedPreferences("config", 0);

        //设置是否选中
        cb_status.setChecked(SharedPreferencesUtils.getBoolean(TaskManagerSettingActivity.this, "is_show_system", false));

        cb_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_show_system", true);
                } else {

                }
                SharedPreferencesUtils.saveBoolean(TaskManagerSettingActivity.this, "is_show_system", isChecked);
            }
        });

        //定时清理进程
        cb_status_kill_process = (CheckBox) findViewById(R.id.cb_status_kill_process);

        final Intent intent = new Intent(this, KillProcessService.class);

        cb_status_kill_process.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SystemInfoUtils.isServiceRunning(TaskManagerSettingActivity.this, "com.dda.mobilesafe.service.KillProcessService")) {
            cb_status_kill_process.setChecked(true);
        } else {
            cb_status_kill_process.setChecked(false);
        }
    }
}
